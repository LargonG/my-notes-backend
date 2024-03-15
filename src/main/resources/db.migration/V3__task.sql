CREATE TABLE task (
    id              uuid PRIMARY KEY,
    board           uuid,
    "group"         uuid,
    title           VARCHAR(256),
    assigns         uuid[],
    status          VARCHAR(256),
    content_text    text,
    content_files   uuid[],
    comments        uuid[],
    createdAt       TIME,
    updatedAt       TIME
)