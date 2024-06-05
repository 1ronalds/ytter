ALTER TABLE users
    ADD is_verified BOOL NOT NULL,
    ADD is_admin BOOL NOT NULL;

CREATE TABLE verifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    verification_key varchar(30) NOT NULL UNIQUE,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);