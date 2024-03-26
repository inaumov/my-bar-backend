INSERT INTO roles (ROLE_NAME, DESCRIPTION) VALUES ('ROLE_SUPER', 'Full-powered administrator. Audit. View reports.');
INSERT INTO roles (ROLE_NAME, DESCRIPTION) VALUES ('ROLE_ADMIN', 'User administrator role.');
INSERT INTO roles (ROLE_NAME, DESCRIPTION) VALUES ('ROLE_USER', 'Regular user role. Create/manage individual cocktails. Manage personal shelf.');

INSERT INTO user_has_roles (USERNAME, ROLE_NAME) VALUES ('super', 'ROLE_SUPER');
INSERT INTO user_has_roles (USERNAME, ROLE_NAME) VALUES ('admin', 'ROLE_ADMIN');
