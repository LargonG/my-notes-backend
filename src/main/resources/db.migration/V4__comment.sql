CREATE TABLE comment (
    id              uuid PRIMARY KEY,
    parent          uuid,
    author          uuid,
    content_text    text,
    content_files   uuid[],
    created_at      time
)