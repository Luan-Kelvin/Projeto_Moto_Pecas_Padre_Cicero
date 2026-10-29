CREATE TABLE employees(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL,
    jobType VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    verified BOOLEAN NOT NULL  DEFAULT FALSE,
    verificationToken VARCHAR(255),
    verificationTokenExpiration TIMESTAMP,
    creationDate DATE DEFAULT (CURRENT_DATE),


    CONSTRAINT chk_email
    CHECK (email REGEXP '^[A-Za-zÀ-ÖØ-öø-ÿ0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$')

)