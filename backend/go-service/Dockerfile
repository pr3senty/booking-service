FROM golang:1.23.6

WORKDIR /app
COPY . .

RUN go mod tidy && go build -o go-service .

CMD ["./go-service"]
