[![](https://github.com/pragmatic-nerdz/otp-heroku/workflows/otp-heroku-master.yml/badge.svg)](https://github.com/pragmatic-nerdz/otp-heroku/workflows/otp-heroku-master.yml)

[![JDK](https://img.shields.io/badge/jdk-17-brightgreen.svg)](https://jdk.java.net/17/)
![](https://img.shields.io/badge/language-kotlin-brightgreen.svg)

[![spring3](https://img.shields.io/badge/springboot-3.x-blue.svg)](https://spring.io/projects/spring-boot)
[![JDK](https://img.shields.io/badge/redis-blue.svg)](https://redis.io/)
[![JDK](https://img.shields.io/badge/rabbitmq-blue.svg)](https://www.rabbitmq.com/)

This is the project is based on the following blog posts:

- [Conception d'un systeme de gestion de One Time Password sur le Cloud](https://www.wutsi.com/read/65042/conception-d-un-systeme-de-gestion-de-one-time-password-sur-le-cloud)

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

----

# Testing the API

Goto to the [Swagger API](https://otp-heroku-test-0ba93376585a.herokuapp.com/swagger-ui.html)

**IMPORTANT NOTE**
This server is used for testing and educational purpose only.

- We are using [Mailgun](https://www.mailgun.com/) API sandbox for sending emails. This Mailgun sandbox delivers
  emails *only* to addresses that have been whitelisted.

  To whitelist the email address you'll like to use for testing the API:
    - Create a [new issue](https://github.com/pragmatic-nerdz/otp-heroku/issues/new) containing the email address you
      want to whitelist
    - We will whitelist your email address within 24 hours.
    - You'll receive a confirmation email to accept receiving email from the Mailgun.

- SMS delivery doesn't work. The MTN account used is not production ready.
