CREATE TABLE "group" (
    id              uuid PRIMARY KEY NOT NULL,
    board_id        uuid NOT NULL,
    title           VARCHAR(256) NOT NULL
)