package service

import (
	"errors"
	"fmt"
	"time"

	"github.com/golang-jwt/jwt/v5"
	"google.golang.org/grpc/codes"
)

type Claims struct {
	UserId         int64
	UserRole       UserRoleDb
	UserSurname    string
	UserPersonName string
	UserPatronymic string
	Username       string
	Password       string
	jwt.RegisteredClaims
}

func generateJWT(user *UserDb, jwtSecret []byte) (string, error) {
	claims := Claims{
		UserId:         user.Id,
		UserRole:       user.Role,
		UserSurname:    user.Surname,
		UserPersonName: user.PersonName,
		UserPatronymic: user.Patronymic,
		Username:       user.Username,
		Password:       user.Password,
		RegisteredClaims: jwt.RegisteredClaims{
			ExpiresAt: jwt.NewNumericDate(time.Now().Add(24 * time.Hour)),
			IssuedAt:  jwt.NewNumericDate(time.Now()),
		},
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	return token.SignedString(jwtSecret)
}

func parseJWT(tokenStr string, jwtSecret []byte) (*Claims, *ServiceError) {
	token, err := jwt.ParseWithClaims(tokenStr, &Claims{}, func(token *jwt.Token) (interface{}, error) {
		if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
			return nil, fmt.Errorf("unexpected signing method")
		}
		return jwtSecret, nil
	})
	if errors.Is(err, jwt.ErrTokenExpired) {
		return nil, NewServiceError(err, codes.Canceled, "token expired")
	} else if err != nil {
		return nil, NewServiceError(err, codes.Internal, "internal error")
	}

	claims, ok := token.Claims.(*Claims)
	if !ok || !token.Valid {
		return nil, NewServiceError(fmt.Errorf("invalid token"), codes.Unauthenticated, "invalid token")
	}

	return claims, nil
}
