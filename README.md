# OTP Heroku

[![master](https://github.com/pragmatic-nerdz/otp-heroku/actions/workflows/master.yml/badge.svg)](https://github.com/pragmatic-nerdz/otp-heroku/actions/workflows/master.yml)
[![pull request](https://github.com/pragmatic-nerdz/otp-heroku/actions/workflows/pull_request.yml/badge.svg)](https://github.com/pragmatic-nerdz/otp-heroku/actions/workflows/pull_request.yml)
[![codecov](https://codecov.io/gh/pragmatic-nerdz/otp-heroku/settings/badge.svg)](https://codecov.io/gh/pragmatic-nerdz/otp-heroku)

[![JDK](https://img.shields.io/badge/jdk-17-brightgreen.svg)](https://jdk.java.net/17/)
[![spring3](https://img.shields.io/badge/springboot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
![](https://img.shields.io/badge/kotlin-brightgreen.svg)

OTP-Heroku is a sample API created using the [12-Factor-App](https://12factor.net/) methodology.

The design of this API has been documented in the
following [blog post](https://www.wutsi.com/read/65042/conception-d-un-systeme-de-gestion-de-one-time-password-sur-le-cloud) (
in french)

# Get Started

## Pre-requisites

To build the API, you must install the following softwares:

- Install [Maven](https://maven.apache.org/install.html)
- Install [Redis](https://redis.io/docs/getting-started/installation/)
- Install [RabbitMQ](https://www.rabbitmq.com/download.html)

## Build and Run

```
mvn clean install
mvn spring-boot:run
```

# Test the API on Heroku

## Important Notice

This server is used for testing and educational purpose only.

### Email

We are using [Mailgun](https://www.mailgun.com/) as mail server for email delivery. This server sends emails *only* to
addresses that have been whitelisted.

To whitelist a email address you'll like to use for testing the API:

- Create a [new issue](https://github.com/pragmatic-nerdz/otp-heroku/issues/new) containing the email address you want
  to whitelist
- We will whitelist your email address within 24 hours.
- You'll receive a confirmation email to accept receiving email from the Mailgun.

### SMS

We are using [MTN SMS](https://developers.mtn.com/products/sms-v3-api) for SMS delivery. But SMS delivery doesn't work
because the account we are using is not production ready.

## Use the API

- Goto to the online [API playgoung](https://otp-heroku-test-0ba93376585a.herokuapp.com/swagger-ui.html) on Heroku

# Links

- [Conception d'un système de gestion de One Time Password sur le Cloud](https://www.wutsi.com/read/65042/conception-d-un-systeme-de-gestion-de-one-time-password-sur-le-cloud)
- [Guide simplifié des Twelve-Factor-Apps.](https://www.wutsi.com/read/63900/guide-simplifie-des-twelve-factor-apps)
- [Comment créer une Twelve-Factor-App avec Spring Boot, Heroku et Github](https://www.wutsi.com/read/65387/comment-creer-une-twelve-factor-app-avec-spring-boot-heroku-et-github)
