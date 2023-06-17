create TABLE sample_user (
    id serial primary key,
    email varchar(100) unique,
    nick varchar(100) unique,
    password varchar(100),
    active boolean,
    created_at TIMESTAMP
)
