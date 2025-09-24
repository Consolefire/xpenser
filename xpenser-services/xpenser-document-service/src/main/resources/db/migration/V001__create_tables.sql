--- Sequences
CREATE SEQUENCE seq_receipt_documents_id
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;


CREATE SEQUENCE seq_files_id
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;


-- Tables
CREATE TABLE IF NOT EXISTS files (
    id BIGINT PRIMARY KEY DEFAULT nextval('seq_files_id') NOT NULL,
    identifier VARCHAR(120) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    location VARCHAR(1024) NOT NULL,
    checksum VARCHAR(120) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    record_version INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS receipt_documents (
    id BIGINT PRIMARY KEY DEFAULT nextval('seq_receipt_documents_id') NOT NULL,

    receipt_id VARCHAR(120) NOT NULL,
    file_id BIGINT NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    record_version INT NOT NULL DEFAULT 0,
    CONSTRAINT uk_receipt_file_id UNIQUE (receipt_id, file_id),
    CONSTRAINT fk_receipt_file FOREIGN KEY (file_id) REFERENCES files(id)
);


