INSERT INTO users (USERNAME, PASSWORD, NAME, SURNAME, EMAIL, ACTIVE)
VALUES ('super', 'super', 'super', 'super', 'super@mybar.com', '1');
INSERT INTO users (USERNAME, PASSWORD, NAME, SURNAME, EMAIL, ACTIVE)
VALUES ('admin', 'admin', 'admin', 'admin', 'admin@mybar.com', '1');
INSERT INTO users (USERNAME, PASSWORD, NAME, SURNAME, EMAIL, ACTIVE)
VALUES ('analyst', 'analyst', 'analyst', 'analyst', 'analyst@mybar.com', '1');
INSERT INTO users (USERNAME, PASSWORD, NAME, SURNAME, EMAIL, ACTIVE)
VALUES ('client', 'client', 'client', 'client', 'client@mybar.com', '0');
INSERT INTO users (USERNAME, PASSWORD, NAME, SURNAME, EMAIL, ACTIVE)
VALUES ('JohnDoe', 'JhnD', 'John', 'Doe', 'john.doe@mybar.com', '1');
INSERT INTO users (USERNAME, PASSWORD, NAME, SURNAME, EMAIL, ACTIVE)
VALUES ('test', 'user', 'Test', 'User', 'test.user@mybar.com', '1');

INSERT INTO roles (ROLE_NAME, DESCRIPTION) VALUES ('ROLE_SUPER', 'Full-powered administrator.');
INSERT INTO roles (ROLE_NAME, DESCRIPTION) VALUES ('ROLE_ADMIN', 'User administrator role.');
INSERT INTO roles (ROLE_NAME, DESCRIPTION) VALUES ('ROLE_ANALYST', 'Audit Management view/read-only access. View reports.');
INSERT INTO roles (ROLE_NAME, DESCRIPTION) VALUES ('ROLE_USER', 'Regular user role. Create/manage individual cocktails. Manage personal shelf.');

INSERT INTO user_has_roles (USERNAME, ROLE_NAME) VALUES ('super', 'ROLE_SUPER');
INSERT INTO user_has_roles (USERNAME, ROLE_NAME) VALUES ('admin', 'ROLE_ADMIN');
INSERT INTO user_has_roles (USERNAME, ROLE_NAME) VALUES ('analyst', 'ROLE_ANALYST');
INSERT INTO user_has_roles (USERNAME, ROLE_NAME) VALUES ('client', 'ROLE_USER');
INSERT INTO user_has_roles (USERNAME, ROLE_NAME) VALUES ('test', 'ROLE_USER');

INSERT INTO user_has_roles (USERNAME, ROLE_NAME) VALUES ('JohnDoe', 'ROLE_SUPER');
INSERT INTO user_has_roles (USERNAME, ROLE_NAME) VALUES ('JohnDoe', 'ROLE_ADMIN');
INSERT INTO user_has_roles (USERNAME, ROLE_NAME) VALUES ('JohnDoe', 'ROLE_USER');