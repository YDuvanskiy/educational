# Используем официальный образ OpenJDK
FROM alpine:latest

# Устанавливаем Maven
RUN apk update && apk add maven openjdk11
# Копируем файлы проекта в образ
COPY pom.xml /usr/src/app/
COPY src /usr/src/app/src/

# Задаем рабочую директорию
WORKDIR /usr/src/app

# Собираем проект с помощью Maven
RUN mvn clean package

# Определяем команду для запуска приложения

CMD ["java", "-jar", "target/aibot-jar-with-dependencies.jar"]