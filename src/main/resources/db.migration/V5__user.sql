CREATE TABLE "user" (
    id              uuid PRIMARY KEY,
    name            VARCHAR(256),
    password        VARCHAR(256),
    registered_in   time
)