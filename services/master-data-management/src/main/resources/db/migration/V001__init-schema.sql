create table if not exists data_type (
    code varchar(128) not null,
    name varchar(256) not null,
    metadata text,
    created_at timestamp not null default now(),
    updated_at timestamp,
    constraint pk_data_type_code primary key(code)
);

create table if not exists category (
    code varchar(128) not null,
    name varchar(256) not null,
    description text,
    created_at timestamp not null default now(),
    updated_at timestamp,
    constraint pk_category_code primary key(code)
);


create table if not exists properties (
    id bigserial not null,
    key varchar(128) not null,
    value text,
    data_type_code varchar(128) not null,
    category_code varchar(128) not null,
    is_enabled bool not null default true,
    created_at timestamp not null default now(),
    updated_at timestamp,
    constraint pk_properties_id primary key(id),
    constraint uk_properties_key unique (key)
);


