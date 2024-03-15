CREATE TABLE "user" (
    id              uuid PRIMARY KEY NOT NULL,
    name            VARCHAR(256) NOT NULL,
    password        VARCHAR(256) NOT NULL,
    registered_in   time NOT NULL
)