CREATE SEQUENCE base_entity_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE users (
    id BIGINT NOT NULL,
    version BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_modified_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(50) NOT NULL,
    last_modified_by VARCHAR(50) NOT NULL,
    email VARCHAR(320) NOT NULL,
    password VARCHAR(60) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    bar_number VARCHAR(20),
    role VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE clients (
    id BIGINT NOT NULL,
    version BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_modified_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(50) NOT NULL,
    last_modified_by VARCHAR(50) NOT NULL,
    name VARCHAR(200) NOT NULL,
    client_type VARCHAR(20) NOT NULL,
    email VARCHAR(320),
    phone VARCHAR(30),
    document_number VARCHAR(20) NOT NULL,
    address VARCHAR(500),
    notes TEXT,
    PRIMARY KEY (id),
    CONSTRAINT uk_clients_document_number UNIQUE (document_number)
);

CREATE TABLE contracts (
    id BIGINT NOT NULL,
    version BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_modified_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(50) NOT NULL,
    last_modified_by VARCHAR(50) NOT NULL,
    title VARCHAR(300) NOT NULL,
    original_file_name VARCHAR(500) NOT NULL,
    file_type VARCHAR(10) NOT NULL,
    extracted_content TEXT NOT NULL,
    file_path VARCHAR(500),
    file_hash VARCHAR(64) NOT NULL,
    status VARCHAR(20) NOT NULL,
    uploaded_by_id BIGINT NOT NULL,
    client_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_contracts_uploaded_by FOREIGN KEY (uploaded_by_id) REFERENCES users(id),
    CONSTRAINT fk_contracts_client FOREIGN KEY (client_id) REFERENCES clients(id)
);

CREATE TABLE contract_clauses (
    id BIGINT NOT NULL,
    version BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_modified_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(50) NOT NULL,
    last_modified_by VARCHAR(50) NOT NULL,
    number INTEGER NOT NULL,
    title VARCHAR(300) NOT NULL,
    content TEXT NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    category VARCHAR(100),
    contract_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_contract_clauses_contract FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE CASCADE
);

CREATE TABLE contract_parties (
    id BIGINT NOT NULL,
    version BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_modified_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(50) NOT NULL,
    last_modified_by VARCHAR(50) NOT NULL,
    name VARCHAR(200) NOT NULL,
    role_in_contract VARCHAR(100) NOT NULL,
    document_number VARCHAR(20),
    email VARCHAR(320),
    is_signatory BOOLEAN NOT NULL DEFAULT FALSE,
    contract_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_contract_parties_contract FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE CASCADE
);

CREATE TABLE extracted_keywords (
    id BIGINT NOT NULL,
    version BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_modified_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(50) NOT NULL,
    last_modified_by VARCHAR(50) NOT NULL,
    keyword VARCHAR(100) NOT NULL,
    keyword_value TEXT,
    type VARCHAR(30) NOT NULL,
    confidence DOUBLE PRECISION NOT NULL,
    contract_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_extracted_keywords_contract FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE CASCADE
);

CREATE TABLE risk_assessments (
    id BIGINT NOT NULL,
    version BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_modified_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(50) NOT NULL,
    last_modified_by VARCHAR(50) NOT NULL,
    type VARCHAR(30) NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    description TEXT NOT NULL,
    recommendation TEXT,
    assessed_at TIMESTAMP WITH TIME ZONE NOT NULL,
    contract_id BIGINT NOT NULL,
    contract_clause_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_risk_assessments_contract FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE CASCADE,
    CONSTRAINT fk_risk_assessments_clause FOREIGN KEY (contract_clause_id) REFERENCES contract_clauses(id) ON DELETE CASCADE
);
