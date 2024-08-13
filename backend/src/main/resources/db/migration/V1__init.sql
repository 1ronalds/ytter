CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    hashed_password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    is_verified BOOL NOT NULL,
    is_admin BOOL NOT NULL
);

CREATE TABLE verifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    verification_key VARCHAR(30) NOT NULL UNIQUE,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE posts (
    id BIGINT PRIMARY KEY,
    author BIGINT NOT NULL,
    text VARCHAR(300) NOT NULL,
    reply_count BIGINT NOT NULL,
    image_id BIGINT NULL,
    timestamp_ DATETIME,
    like_count BIGINT NOT NULL,
    reyeet_count BIGINT NOT NULL,
    FOREIGN KEY (author) REFERENCES users(id)
);

CREATE TABLE comments (
    id BIGINT PRIMARY KEY,
    author BIGINT NOT NULL,
    root_post BIGINT NOT NULL,
    reply_to_comment BIGINT NULL,
    comment VARCHAR(300),
    reply_count BIGINT NOT NULL,
    like_count BIGINT NOT NULL,
    comment_count BIGINT NOT NULL,
    reyeet_count BIGINT NOT NULL,
    FOREIGN KEY (root_post) REFERENCES posts(id),
    FOREIGN KEY (reply_to_comment) REFERENCES comments(id)
    FOREIGN KEY (author) REFERENCES users(id)

);

CREATE TABLE likes (
    id BIGINT PRIMARY KEY,
    post_id BIGINT NULL,
    comment_id BIGINT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (post_id) REFERENCES posts(id),
    FOREIGN KEY (comment_id) REFERENCES comments(id)
);