CREATE TABLE "group" (
    id              uuid PRIMARY KEY,
    parent_board    uuid,
    title           VARCHAR(256),
    tasks           uuid[]
)