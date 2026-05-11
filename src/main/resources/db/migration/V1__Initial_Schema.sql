CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       user_name VARCHAR(100) NOT NULL,
                       email VARCHAR(150) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       phone_number VARCHAR(20),
                       role VARCHAR(50) NOT NULL,
                       status VARCHAR(50) NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP
);

CREATE TABLE job (
                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                     job_type VARCHAR(255),
                     salary_type VARCHAR(255),
                     salary_amount DOUBLE PRECISION,
                     location VARCHAR(255),
                     job_description TEXT,
                     job_requirement TEXT,
                     status VARCHAR(50),
                     employer_id BIGINT NOT NULL,
                     created_at TIMESTAMP NOT NULL,
                     updated_at TIMESTAMP
);

CREATE TABLE application (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             apply_at TIMESTAMP,
                             cv_form VARCHAR(255),
                             status VARCHAR(50),
                             user_id BIGINT NOT NULL,
                             job_id BIGINT NOT NULL,
                             created_at TIMESTAMP NOT NULL,
                             updated_at TIMESTAMP
);

CREATE TABLE profile (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         profile_image VARCHAR(255),
                         business_name VARCHAR(255),
                         location VARCHAR(255),
                         company_background TEXT,
                         crn VARCHAR(255),
                         skill VARCHAR(255),
                         age INT,
                         address VARCHAR(255),
                         education_background TEXT,
                         experience TEXT,
                         nrc_front VARCHAR(255),
                         nrc_back VARCHAR(255),
                         user_id BIGINT NOT NULL UNIQUE,
                         created_at TIMESTAMP NOT NULL,
                         updated_at TIMESTAMP,
                         CONSTRAINT usr_profile_user_id UNIQUE (user_id)
);

CREATE TABLE saved_job (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           save_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           user_id BIGINT NOT NULL,
                           job_id BIGINT NOT NULL,
                           created_at TIMESTAMP NOT NULL,
                           updated_at TIMESTAMP
);