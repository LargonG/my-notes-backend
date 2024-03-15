CREATE TABLE board (
    id      uuid PRIMARY KEY,
    title   VARCHAR(256),
    owner   uuid,
    groups  uuid[]
)