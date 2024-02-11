# Используем официальный образ OpenJDK
FROM alpine:latest

# Устанавливаем Maven
RUN apk update && apk add maven openjdk17
# Копируем файлы проекта в образ
COPY pom.xml /usr/src/app/
COPY src /usr/src/app/src/

# Задаем рабочую директорию
WORKDIR /usr/src/app

# Собираем проект с помощью Maven
RUN mvn clean package

EXPOSE 8080

# Определяем команду для запуска приложения

CMD ["java", "-jar", "target/education-jar-with-dependencies.jar"]