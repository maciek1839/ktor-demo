# Contributing

- Follow [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/).


## Local setup

### Insomnia requests

Import requests from `Insomnia_ktor-demo.yaml`.

## Local development

## Start server

```text
./gradlew run
```

## ktlint

```text
./gradlew ktlintCheck
```

```text
./gradlew ktlintCheck
```

### Database

#### Flyway

In order to change a database, add another migration to apply changes.

#### Local database connection

By default, H2 is enabled.

You can change H2 to PostgreSQL by uncommenting the configuration in `application.conf`, but remember to set up PostgreSQL locally before.
