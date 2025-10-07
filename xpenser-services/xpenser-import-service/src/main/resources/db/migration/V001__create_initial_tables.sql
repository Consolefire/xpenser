
--- Sequences
CREATE SEQUENCE seq_templates_id
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

-- Tables
CREATE TABLE IF NOT EXISTS templates (
    id BIGINT PRIMARY KEY DEFAULT nextval('seq_templates_id') NOT NULL,
    template_name VARCHAR(120) NOT NULL,
    template_data TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    record_version INT NOT NULL DEFAULT 0
);
