create table roles
(
    ROLE_NAME   varchar(255) not null,
    DESCRIPTION varchar(255),
    primary key (ROLE_NAME)
);

create table users
(
    USERNAME varchar(255) not null,
    ACTIVE   tinyint,
    EMAIL    varchar(255),
    NAME     varchar(255),
    PASSWORD varchar(255),
    SURNAME  varchar(255),
    primary key (USERNAME)
);

create table user_has_roles
(
    USERNAME  varchar(255) not null,
    ROLE_NAME varchar(255) not null
);

alter table user_has_roles
    add constraint FK_USER_HAS_ROLES_ROLE_NAME
        foreign key (ROLE_NAME)
            references roles (ROLE_NAME);

alter table user_has_roles
    add constraint FK_USER_HAS_ROLES_USERNAME
        foreign key (USERNAME)
            references users (USERNAME);
