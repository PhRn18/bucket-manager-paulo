CREATE TABLE logs (
    id SERIAL PRIMARY KEY,
    bucket VARCHAR(100),
    username VARCHAR(100) NOT NULL,
    operation VARCHAR(100) NOT NULL,
    time VARCHAR(100) NOT NULL,
    exception VARCHAR(100)
);