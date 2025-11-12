-- users table
CREATE TABLE IF NOT EXISTS user_entity (
    user_id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    phone VARCHAR(50),
    street VARCHAR(255),
    city VARCHAR(255),
    zip_code VARCHAR(50)
);

-- roles for each user
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL REFERENCES user_entity(user_id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL
);

-- policies table
CREATE TABLE IF NOT EXISTS policy_entity (
    policy_id BIGSERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    coverage_amount BIGINT NOT NULL,
    subject VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    policy_holder_id BIGINT NOT NULL REFERENCES user_entity(user_id),
    insured_user_id BIGINT NOT NULL REFERENCES user_entity(user_id)
);

-- events table
CREATE TABLE IF NOT EXISTS event_entity (
    event_id BIGSERIAL PRIMARY KEY,
    policy_id BIGINT NOT NULL REFERENCES policy_entity(policy_id),
    event_date DATE NOT NULL,
    event_type VARCHAR(50),
    event_description TEXT,
    event_status VARCHAR(50),
    original_claim_amount BIGINT,
    amount_paid BIGINT
);

-- audit log
CREATE TABLE IF NOT EXISTS audit_log (
    id BIGSERIAL PRIMARY KEY,
    action_type VARCHAR(100),
    entity_name VARCHAR(255),
    entity_id BIGINT,
    timestamp TIMESTAMP,
    description TEXT
);
