CREATE TABLE task (
    id              uuid PRIMARY KEY NOT NULL,
    board           uuid NOT NULL,
    "group"         uuid NOT NULL,
    title           VARCHAR(256) NOT NULL,
    assigns         uuid[] NOT NULL,
    status          VARCHAR(256) NOT NULL,
    content_text    text NOT NULL,
    content_files   uuid[] NOT NULL,
    createdAt       TIME NOT NULL,
    updatedAt       TIME NOT NULL
)