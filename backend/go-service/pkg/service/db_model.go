package service

import (
	model "edu.centraluniversity/internal/modelpb"
)

type UserRoleDb string

const (
	TYPE_UNSPECIFIED UserRoleDb = "TYPE_UNSPECIFIED"
	GUEST            UserRoleDb = "GUEST"
	STUDENT          UserRoleDb = "STUDENT"
	STAFF            UserRoleDb = "STAFF"
	ADMIN            UserRoleDb = "ADMIN"
)

type UserDb struct {
	Id         int64      `db:"id"`
	Role       UserRoleDb `db:"role"`
	Surname    string     `db:"surname"`
	PersonName string     `db:"person_name"`
	Patronymic string     `db:"patronymic"`
	Username   string     `db:"username"`
	Password   string     `db:"password"`
}

func (r UserRoleDb) ToProto() model.UserRole {
	switch r {
	case GUEST:
		return model.UserRole_GUEST
	case STUDENT:
		return model.UserRole_STUDENT
	case STAFF:
		return model.UserRole_STAFF
	case ADMIN:
		return model.UserRole_ADMIN
	}

	return model.UserRole_TYPE_UNSPECIFIED
}

func UserRoleFromProto(r model.UserRole) UserRoleDb {
	switch r {
	case model.UserRole_GUEST:
		return GUEST
	case model.UserRole_STUDENT:
		return STUDENT
	case model.UserRole_STAFF:
		return STAFF
	case model.UserRole_ADMIN:
		return ADMIN
	}

	return TYPE_UNSPECIFIED
}

func (u *UserDb) ToProto() *model.User {
	return &model.User{
		Id:         u.Id,
		Role:       u.Role.ToProto(),
		Surname:    u.Surname,
		PersonName: u.PersonName,
		Patronymic: u.Patronymic,
		Username:   u.Username,
		Password:   u.Password,
	}
}

func (u *UserDb) GetInfo() *model.UserInfo {
	return &model.UserInfo{
		Id:         u.Id,
		Role:       u.Role.ToProto(),
		Surname:    u.Surname,
		PersonName: u.PersonName,
		Patronymic: u.Patronymic,
	}
}

func UserDbFromProto(u *model.User) *UserDb {
	return &UserDb{
		Id:         u.Id,
		Role:       UserRoleFromProto(u.Role),
		Surname:    u.Surname,
		PersonName: u.PersonName,
		Patronymic: u.Patronymic,
		Username:   u.Username,
		Password:   u.Password,
	}
}
