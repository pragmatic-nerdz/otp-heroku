[![](https://github.com/pragmatic-nerdz/otp-heroku/workflows/otp-heroku-master.yml/badge.svg)](https://github.com/pragmatic-nerdz/otp-heroku/workflows/otp-heroku-master.yml)

[![JDK](https://img.shields.io/badge/jdk-17-brightgreen.svg)](https://jdk.java.net/17/)
![](https://img.shields.io/badge/language-kotlin-brightgreen.svg)

[![spring3](https://img.shields.io/badge/springboot-3.x-blue.svg)](https://spring.io/projects/spring-boot)
[![JDK](https://img.shields.io/badge/redis-blue.svg)](https://redis.io/)
[![JDK](https://img.shields.io/badge/rabbitmq-blue.svg)](https://www.rabbitmq.com/)

One-Time-Password API.

# Setup your local environment

## Installations

- Install [Maven](https://maven.apache.org/install.html)
- Install [Redis](https://redis.io/docs/getting-started/installation/)
- Install [RabbitMQ](https://www.rabbitmq.com/download.html)

## Build

```
mvn clean install
```

## Run the server

```
mvn spring-boot:run
```

Browse the [API](http://localhost:8080/swagger-ui.html)

# Deployment to Heroku

TODO
