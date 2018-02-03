
    create table ROLES (
        ROLE_NAME varchar(255) not null,
        DESCRIPTION varchar(255),
        primary key (ROLE_NAME)
    );

    create table USERS (
        USERNAME varchar(255) not null,
        ACTIVE tinyint,
        EMAIL varchar(255),
        NAME varchar(255),
        PASSWORD varchar(255),
        SURNAME varchar(255),
        primary key (USERNAME)
    );

    create table USER_HAS_ROLES (
        USERNAME varchar(255) not null,
        ROLE_NAME varchar(255) not null
    );

    alter table USER_HAS_ROLES
        add constraint FK_7axkude5t552rbjbq5pe09f86 
        foreign key (ROLE_NAME)
        references ROLES (ROLE_NAME);

    alter table USER_HAS_ROLES 
        add constraint FK_io1baw1imdvxdsvhpprytxsxk 
        foreign key (USERNAME) 
        references USERS (USERNAME);
