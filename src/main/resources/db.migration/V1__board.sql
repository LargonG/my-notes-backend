CREATE TABLE board (
    id      uuid PRIMARY KEY NOT NULL,
    title   VARCHAR(256) NOT NULL,
    owner   uuid NOT NULL
)