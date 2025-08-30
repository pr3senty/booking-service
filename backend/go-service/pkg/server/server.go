package server

import (
	"context"
	"fmt"
	"net"
	"strings"
	"sync"
	"time"

	_ "github.com/jackc/pgx/v5/stdlib"
	"github.com/jmoiron/sqlx"
	"google.golang.org/grpc"
	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/status"

	model "edu.centraluniversity/internal/modelpb"
	"edu.centraluniversity/internal/serverpb"
	"edu.centraluniversity/pkg/service"
)

func isEmpty(v string) bool {
	return len(strings.Trim(v, " \n")) == 0
}

func loggingInterceptor(
	ctx context.Context,
	req interface{},
	info *grpc.UnaryServerInfo,
	handler grpc.UnaryHandler,
) (resp interface{}, err error) {
	defer func() {
		if r := recover(); r != nil {
			fmt.Println("Panic:", r)

			err = status.Error(codes.Internal, "internal error")
		}
	}()

	fmt.Printf("-> Unary call: %s\n", info.FullMethod)
	start := time.Now()

	resp, err = handler(ctx, req)
	dur := time.Since(start)
	if err != nil {
		fmt.Printf("<- Completed with error: %v (method %s, %v)\n", err, info.FullMethod, dur)
	} else {
		fmt.Printf("<- Completed: method %s, duration=%v\n", info.FullMethod, dur)
	}
	return resp, err
}

type authServer struct {
	serverpb.UnimplementedAuthServer
	db        *sqlx.DB
	mu        sync.RWMutex
	jwtSecret []byte
}

func (s *authServer) SignUp(ctx context.Context, user *model.User) (*model.Result, error) {
	s.mu.RLock()
	defer s.mu.RUnlock()

	if isEmpty(user.Username) || isEmpty(user.Password) || isEmpty(user.Patronymic) || isEmpty(user.PersonName) || isEmpty(user.Surname) {
		return nil, status.Error(codes.Canceled, "user has empty fields")
	}

	if user.Role == model.UserRole_TYPE_UNSPECIFIED {
		return nil, status.Error(codes.InvalidArgument, "invalid user role")
	}

	result, err := service.SignUp(ctx, s.db, user, s.jwtSecret)
	if err != nil {
		fmt.Println(err.Message, err.Err)
		return result, status.Error(err.StatusCode, err.Message)
	}

	return result, nil
}

func (s *authServer) SignIn(ctx context.Context, credentials *model.UserCredentials) (*model.Result, error) {
	s.mu.RLock()
	defer s.mu.RUnlock()

	if isEmpty(credentials.Username) || isEmpty(credentials.Password) {
		return nil, status.Error(codes.Canceled, "credentials has empty fields")
	}

	result, err := service.SignIn(ctx, s.db, credentials, s.jwtSecret)
	if err != nil {
		fmt.Println(err.Message, err.Err)
		return result, status.Error(err.StatusCode, err.Message)
	}

	return result, nil
}

func (s *authServer) SignInByJwt(ctx context.Context, jwtToken *model.JwtToken) (*model.Result, error) {
	s.mu.RLock()
	defer s.mu.RUnlock()

	if isEmpty(jwtToken.Value) {
		return nil, status.Error(codes.Canceled, "has a empty field")
	}

	result, err := service.SignInByJwt(ctx, s.db, jwtToken.GetValue(), s.jwtSecret)
	if err != nil {
		fmt.Println(err.Message, err.Err)
		return result, status.Error(err.StatusCode, err.Message)
	}

	return result, nil
}

func (s *authServer) UpdateUser(ctx context.Context, user *model.User) (*model.Result, error) {
	s.mu.RLock()
	defer s.mu.RUnlock()

	if user.Id == 0 {
		return nil, status.Error(codes.Canceled, "request must have a valid user id")
	}

	if isEmpty(user.Password) && user.Role == model.UserRole_TYPE_UNSPECIFIED && isEmpty(user.Username) &&
		isEmpty(user.Surname) && isEmpty(user.PersonName) && isEmpty(user.Patronymic) {
		return nil, status.Error(codes.Canceled, "request has no any changed values")
	}

	result, err := service.UpdateUser(ctx, s.db, user, s.jwtSecret)
	if err != nil {
		fmt.Println(err.Message, err.Err)
		return result, status.Error(err.StatusCode, err.Message)
	}

	return result, nil
}

func (s *authServer) DeleteUser(ctx context.Context, userId *model.UserId) (*model.Successful, error) {
	s.mu.RLock()
	defer s.mu.RUnlock()

	if userId.Value == 0 {
		return nil, status.Error(codes.Canceled, "request must have a valid user id")
	}

	result, err := service.DeleteUser(ctx, s.db, userId)
	if err != nil {
		fmt.Println(err.Message, err.Err)
		return result, status.Error(err.StatusCode, err.Message)
	}

	return result, nil
}

func (s *authServer) GetUserInfo(ctx context.Context, userId *model.UserId) (*model.UserInfo, error) {
	s.mu.RLock()
	defer s.mu.RUnlock()

	if userId.Value == 0 {
		return nil, status.Error(codes.Canceled, "request must have a valid user id")
	}

	result, err := service.GetUserInfo(ctx, s.db, userId)
	if err != nil {
		fmt.Println(err.Message, err.Err)
		return result, status.Error(err.StatusCode, err.Message)
	}

	return result, nil
}

func connectWithContextRetry(ctx context.Context, dsn string, retryDelay time.Duration) (*sqlx.DB, error) {
	var db *sqlx.DB
	var err error

	for {
		select {
		case <-ctx.Done():

			return nil, fmt.Errorf("контекст завершён: %w", ctx.Err())
		default:
			db, err = sqlx.ConnectContext(ctx, "pgx", dsn)
			if err == nil {
				fmt.Println("Успешное подключение к базе данных")
				return db, nil
			}
			fmt.Println("Ошибка подключения к базе данных, повтор через", retryDelay)
			time.Sleep(retryDelay)
		}
	}
}

func Start(ctx context.Context, stop context.CancelFunc, ip string, dsnUrl string, jwtSecret string) {

	withTimeout, releaseTimeout := context.WithTimeout(ctx, 30*time.Second)
	defer releaseTimeout()

	db, err := connectWithContextRetry(withTimeout, dsnUrl, 3*time.Second)
	if err != nil {
		fmt.Println("Ошибка подключения к базе данных:", err)
		return
	}
	defer db.Close()

	query := `
	DO $$
	BEGIN
		IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_role') THEN
			CREATE TYPE user_role AS ENUM ('GUEST', 'STUDENT', 'STAFF', 'ADMIN');
		END IF;
	END$$;

	CREATE TABLE IF NOT EXISTS users (
		id SERIAL PRIMARY KEY,
		role user_role NOT NULL,
		surname VARCHAR(255) NOT NULL,
		person_name VARCHAR(255) NOT NULL,
		patronymic VARCHAR(255) NOT NULL,
		username VARCHAR(255) NOT NULL UNIQUE,
		password VARCHAR(255) NOT NULL
	);`

	_, err = db.ExecContext(ctx, query)
	if err != nil {
		fmt.Println("Ошибка создания таблицы:", err)
		return
	}

	lis, err := net.Listen("tcp", ip)
	if err != nil {
		fmt.Println("Ошибка открытия соединения:", err)
		return
	}

	grpcServer := grpc.NewServer(
		grpc.UnaryInterceptor(loggingInterceptor),
	)
	serverpb.RegisterAuthServer(grpcServer, &authServer{db: db, jwtSecret: []byte(jwtSecret)})

	var listenErr error
	go func() {

		fmt.Printf("gRPC Сервер успешно запущен по адресу %s...\n", ip)
		if err := grpcServer.Serve(lis); err != nil {
			listenErr = err
			stop()
		}
	}()

	<-ctx.Done()

	if listenErr != nil {
		fmt.Println("Произошла ошибка:", listenErr)
	}

	fmt.Printf("Начинаем закрывать сервер...\n")
	grpcServer.GracefulStop()

	fmt.Printf("Сервер успешно выключен!\n")
}
