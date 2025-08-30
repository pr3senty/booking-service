package service

import (
	"context"
	"database/sql"
	"errors"
	"fmt"
	"strings"

	model "edu.centraluniversity/internal/modelpb"
	"github.com/jmoiron/sqlx"
	"golang.org/x/crypto/bcrypt"
	"google.golang.org/grpc/codes"
)

type ServiceError struct {
	Err        error
	StatusCode codes.Code
	Message    string
}

func (e *ServiceError) Error() string {
	return fmt.Sprintf("service error - %d: %s. error: %s", e.StatusCode, e.Message, e.Err)
}

func NewServiceError(err error, statusCode codes.Code, message string) *ServiceError {
	return &ServiceError{
		Err:        err,
		StatusCode: statusCode,
		Message:    message,
	}
}

func isEmpty(v string) bool {
	return len(strings.Trim(v, " \n")) == 0
}

func checkUsernameExists(ctx context.Context, db *sqlx.DB, username string) *ServiceError {
	var exists bool
	err := db.GetContext(ctx, &exists, "SELECT EXISTS (SELECT 1 FROM users WHERE username = $1)", username)
	if err != nil {
		return NewServiceError(err, codes.Internal, "internal error")
	}
	if exists {
		return NewServiceError(err, codes.AlreadyExists, "user with such username already exists")
	}

	return nil
}

func SignUp(ctx context.Context, db *sqlx.DB, user *model.User, jwtSecret []byte) (*model.Result, *ServiceError) {

	if err := checkUsernameExists(ctx, db, user.Username); err != nil {
		return nil, err
	}

	hashPassword, err := bcrypt.GenerateFromPassword([]byte(strings.Trim(user.Password, " \n")), bcrypt.DefaultCost)
	if err != nil {
		return nil, NewServiceError(err, codes.Internal, "internal error")
	}

	userDb := UserDbFromProto(user)
	userDb.Password = string(hashPassword)

	query := `INSERT INTO users (role, surname, person_name, patronymic, username, password) VALUES (:role, :surname, :person_name, :patronymic, :username, :password) RETURNING id`
	stmt, err := db.PrepareNamedContext(ctx, query)
	if err != nil {
		return nil, NewServiceError(err, codes.Internal, "internal error while preparing statement")
	}
	defer stmt.Close()

	err = stmt.QueryRowContext(ctx, userDb).Scan(&userDb.Id)
	if err != nil {
		return nil, NewServiceError(err, codes.Internal, "internal error while creating user")
	}

	jwtToken, err := generateJWT(userDb, jwtSecret)
	if err != nil {
		return nil, NewServiceError(err, codes.Internal, "internal error")
	}

	return &model.Result{UserData: userDb.ToProto(), JwtToken: &model.JwtToken{Value: jwtToken}}, nil
}

func SignInByJwt(ctx context.Context, db *sqlx.DB, jwtToken string, jwtSecret []byte) (*model.Result, *ServiceError) {

	claims, err := parseJWT(jwtToken, jwtSecret)
	if err != nil {
		return nil, err
	}

	user := &model.User{
		Id:         claims.UserId,
		Role:       claims.UserRole.ToProto(),
		Surname:    claims.UserSurname,
		PersonName: claims.UserPersonName,
		Patronymic: claims.UserPatronymic,
		Username:   claims.Username,
		Password:   claims.Password,
	}

	return &model.Result{UserData: user, JwtToken: &model.JwtToken{Value: jwtToken}}, nil
}

func SignIn(ctx context.Context, db *sqlx.DB, credentials *model.UserCredentials, jwtSecret []byte) (*model.Result, *ServiceError) {

	user := &UserDb{}
	err := db.GetContext(ctx, user, "SELECT * FROM users WHERE username = $1", credentials.Username)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, NewServiceError(err, codes.NotFound, "user with such username not found")
		}
		return nil, NewServiceError(err, codes.Internal, "internal error")
	}

	err = bcrypt.CompareHashAndPassword([]byte(user.Password), []byte(credentials.Password))
	if err != nil {
		return nil, NewServiceError(err, codes.InvalidArgument, "invalid password")
	}

	jwtToken, err := generateJWT(user, jwtSecret)
	if err != nil {
		return nil, NewServiceError(err, codes.Internal, "internal error")
	}

	return &model.Result{UserData: user.ToProto(), JwtToken: &model.JwtToken{Value: jwtToken}}, nil
}

func UpdateUser(ctx context.Context, db *sqlx.DB, user *model.User, jwtSecret []byte) (*model.Result, *ServiceError) {

	dbUser := &UserDb{}
	err := db.GetContext(ctx, dbUser, "SELECT * FROM users WHERE id = $1", user.Id)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, NewServiceError(err, codes.NotFound, "user with such id not found")
		}
		return nil, NewServiceError(err, codes.Internal, "internal error")
	}

	if serviceError := updateUserDb(ctx, db, dbUser, user); serviceError != nil {
		return nil, serviceError
	}

	query := `UPDATE users SET role = :role, surname = :surname, person_name = :person_name, patronymic = :patronymic, username = :username, password = :password WHERE id = :id`
	_, err = db.NamedExecContext(ctx, query, dbUser)
	if err != nil {
		return nil, NewServiceError(err, codes.Internal, "internal error")
	}

	jwtToken, err := generateJWT(dbUser, jwtSecret)
	if err != nil {
		return nil, NewServiceError(err, codes.Internal, "internal error")
	}

	return &model.Result{UserData: dbUser.ToProto(), JwtToken: &model.JwtToken{Value: jwtToken}}, nil
}

func DeleteUser(ctx context.Context, db *sqlx.DB, userId *model.UserId) (*model.Successful, *ServiceError) {

	result, err := db.ExecContext(ctx, `DELETE FROM users WHERE id=$1`, userId.Value)
	if err != nil {
		return &model.Successful{Value: false}, NewServiceError(err, codes.Internal, "internal error")
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		return &model.Successful{Value: false}, NewServiceError(err, codes.Internal, "internal error")
	}

	if rowsAffected == 0 {
		return &model.Successful{Value: false}, NewServiceError(err, codes.NotFound, "not found users with such id")
	}

	return &model.Successful{Value: true}, nil
}

func GetUserInfo(ctx context.Context, db *sqlx.DB, userId *model.UserId) (*model.UserInfo, *ServiceError) {

	var user UserDb
	err := db.GetContext(ctx, &user, `SELECT id, role, surname, person_name, patronymic FROM users WHERE id = $1`, userId.Value)
	if errors.Is(err, sql.ErrNoRows) {
		return nil, NewServiceError(err, codes.NotFound, "user with such id not found")
	} else if err != nil {
		return nil, NewServiceError(err, codes.Internal, "internal error")
	}

	return user.GetInfo(), nil
}

func updateUserDb(ctx context.Context, db *sqlx.DB, user *UserDb, modifiedUser *model.User) *ServiceError {
	if len(strings.Trim(modifiedUser.Password, " \n")) != 0 {
		hashPassword, err := bcrypt.GenerateFromPassword([]byte(strings.Trim(modifiedUser.Password, " \n")), bcrypt.DefaultCost)
		if err != nil {
			return NewServiceError(err, codes.Internal, "internal error")
		}

		user.Password = string(hashPassword)
	}

	if modifiedUser.Role != model.UserRole_TYPE_UNSPECIFIED {
		user.Role = UserRoleFromProto(modifiedUser.Role)
	}

	if !isEmpty(modifiedUser.Username) {
		if err := checkUsernameExists(ctx, db, modifiedUser.Username); err != nil {
			return err
		}
		user.Username = strings.Trim(modifiedUser.Username, " \n")
	}

	if !isEmpty(modifiedUser.Surname) {
		user.Surname = strings.Trim(modifiedUser.Surname, " \n")
	}

	if !isEmpty(modifiedUser.PersonName) {
		user.PersonName = strings.Trim(modifiedUser.PersonName, " \n")
	}

	if !isEmpty(modifiedUser.Patronymic) {
		user.Patronymic = strings.Trim(modifiedUser.Patronymic, " \n")
	}

	return nil
}
