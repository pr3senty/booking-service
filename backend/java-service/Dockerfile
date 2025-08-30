
# Используем образ Gradle для сборки приложения. Даём ему имя "build"
FROM gradle:8.12.1-jdk17 AS build

# Копируем исходный код проекта в директорию /app
WORKDIR /app
COPY ./backend/java-service .

COPY ./proto ./proto

# Собираем проект с помощью gradle 
RUN gradle bootJar --no-daemon

# Используем образ JRE для выполнения нашего кода
FROM eclipse-temurin:17-jre-jammy

# Копируем в него собранную ранее JAR-ку из образа "build"
WORKDIR /app

COPY --from=build /app/build/libs/app-0.0.1-SNAPSHOT.jar app.jar

# Указываем команду для запуска приложения
CMD ["java", "-jar", "app.jar"]
