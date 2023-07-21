CREATE TABLE execution_time (
                      id SERIAL PRIMARY KEY,
                      method_name VARCHAR(100) NOT NULL,
                      execution_time decimal NOT NULL
);