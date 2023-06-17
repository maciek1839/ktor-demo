# KTOR demo

This application was generated using https://start.ktor.io/

| Branch |                                                                               Pipeline                                                                               |                                                                            Code coverage                                                                             |                                 Gradle test report                                  |                                 Jacoco test report                                  |
|:------:|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------:|
|  main  | [![pipeline status](https://gitlab.com/ShowMeYourCodeYouTube/ktor-demo/badges/main/pipeline.svg)](https://gitlab.com/ShowMeYourCodeYouTube/ktor-demo/-/commits/main) | [![coverage report](https://gitlab.com/ShowMeYourCodeYouTube/ktor-demo/badges/main/coverage.svg)](https://gitlab.com/ShowMeYourCodeYouTube/ktor-demo/-/commits/main) | [link](https://gitlab.com/ShowMeYourCodeYouTube/ktor-demo/gradle-report/index.html) | [link](https://gitlab.com/ShowMeYourCodeYouTube/ktor-demo/jacoco-report/index.html) |

---

The `KTOR demo` shows a simple project build with KTOR framework and related plugins.

The service aims to provide tokens to registered users. As a user you can:
- log in,
  - {{baseUrl}}api/v1/users/login
- register,
  - {{baseUrl}}/api/v1/users
- request a new access token (using refresh token).

The service doesn't use any external providers e.g. Auth0.

## Technology

- Kotlin
- KTOR
- Gradle
- JDK 11
- Netty
- Flyway
- PostgreSQL / H2
- Exposed (ORM)
- Logback
- Kotlin Coroutines
- com.auth0 » java-jwt
  - Java implementation of JSON Web Token (JWT)
- org.jetbrains.kotlin » kotlin-test-junit

### KTOR

Ktor is a Kotlin framework that allow developers to write asynchronous clients and servers applications, in Kotlin. KTOR Github: https://github.com/ktorio/ktor

It was developed with Kotlin by Jetbrains, and it's already present in a web benchmark: https://www.techempower.com/benchmarks/

#### Advantages

1. Simple and light
    - Ktor is simple, everything is explicit, it has a great API, it’s built on coroutines, so it’s asynchronous from the ground up.
2. Not having to use services like Firebase.
    - Ever tried to build a large application that requires a backend? Well, if you don’t know backend development you probably used a service like Firebase, and it was probably very limited.
    - So, at some point, the pricing and scalability also become a problem. What do you do then? You either pay a lot of money, or you hire a backend developer to build your custom backend for you. Both ways are very expensive.
    - There is an easier and cheaper way, though. You can always learn Ktor and backend development, and build your own backend for free. Of course, hosting will still need some money, but it will be a lot cheaper than the previous two ways.

## Project's technology limitations

- `Exposed` framework uses JDBC which is blocking I/O. There is no active development for `R2DBC`. See: https://github.com/JetBrains/Exposed/issues/456

## Best practises

### Token Best Practices

https://auth0.com/docs/secure/tokens/token-best-practices

### Storing passwords in database

#### HMAC hashing

HMAC (ang. keyed-Hash Message Authentication Code, Hash-based Message Authentication Code)

In cryptography, an HMAC is a specific type of message authentication code (MAC) involving a cryptographic hash function and a secret cryptographic key. As with any MAC, it may be used to simultaneously verify both the data integrity and authenticity of a message.

## ID token vs Access token

Ref: https://auth0.com/blog/id-token-access-token-what-is-the-difference/

![ID TOKEN vs ACCESS TOKEN](docs/id-token-vs-access-token.jpg)
