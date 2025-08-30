package main

import (
	"context"
	"fmt"
	"os"
	"os/signal"
	"syscall"

	"edu.centraluniversity/pkg/server"
)

func main() {

	serverHost := os.Getenv("SERVER_HOST")
	serverPort := os.Getenv("SERVER_PORT")

	dbHost := os.Getenv("DB_HOST")
	dbPort := os.Getenv("DB_PORT")
	user := os.Getenv("DB_USER")
	password := os.Getenv("DB_PASSWORD")
	dbName := os.Getenv("DB_NAME")

	jwtSecret := os.Getenv("JWT_SECRET")

	dsnUrl := fmt.Sprintf("postgres://%s:%s@%s:%s/%s", user, password, dbHost, dbPort, dbName)
	ip := fmt.Sprintf("%s:%s", serverHost, serverPort)

	ctxCancel, cancel := context.WithCancel(context.Background())

	ctxSignal, stop := signal.NotifyContext(ctxCancel, os.Interrupt, syscall.SIGTERM)

	go func() {
		defer cancel()

		server.Start(ctxSignal, stop, ip, dsnUrl, jwtSecret)
	}()

	<-ctxCancel.Done()
}
