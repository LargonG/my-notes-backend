CREATE TABLE comment (
    id              uuid PRIMARY KEY NOT NULL,
    task            uuid NOT NULL,
    author          uuid NOT NULL,
    content_text    text NOT NULL,
    content_files   uuid[] NOT NULL,
    created_at      time NOT NULL
)