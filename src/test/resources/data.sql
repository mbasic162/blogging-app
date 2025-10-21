INSERT INTO users (email, username, password, created_on, is_private, is_enabled, is_deleted, description) VALUES ('first@example.com', 'first_user', '$2a$10$jFLL1Pk.f8CUaPiL85ET4OQbE1IrcnNpY8aG3t6N5tjWlXmE6/iUG', '2025-09-09', false, true, false, '');
INSERT INTO users (email, username, password, created_on, is_private, is_enabled, is_deleted, description) VALUES ('second@example.com', 'second_user', '$2a$10$nqkCN8g5Fr.iLg/jD4cMJOm25jSyYKVpS7PdWlFx3qJ8SM7i1/YeW', '2025-09-09', false, true, false, 'Hi, I''m second user');
INSERT INTO users (email, username, password, created_on, is_private, is_enabled, is_deleted, description) VALUES ('third@example.com', 'third_user', '$2a$10$P3t4gs7e13mmmR5WDILQK.llYi0v0U2iIDr0Ziuy1T8f6SYpUp5AG', '2025-09-09', false, true, false, '');
INSERT INTO users (email, username, password, created_on, is_private, is_enabled, is_deleted, description) VALUES ('fourth@example.com', 'fourth_user', '$2a$10$GANtU6Abf/RXb3vW2/Fq/OgQ76igCi6KAg.99hKbjeifg/C/JBWUK', '2025-09-15', false, true, false, '');
INSERT INTO users (email, username, password, created_on, is_private, is_enabled, is_deleted, description) VALUES ('fifth@example.com', 'fifth_user', '$2a$10$sA686ihLgMlT.U45JYyIRO90FKkR5aP8Vx1eZQsLV.yI56J7YJj8C', '2025-09-15', false, true, false, '');
INSERT INTO users(email, username, password, created_on, is_private, is_enabled, is_deleted, description) VALUES ('private@example.com', 'private_user','$2a$10$l4dxLKbxtdfVfNYxH0swfeOnbP3/0htJ/IVMjw7LMsSOCS.AhinHO','2025-09-15',true,true,false,'Private');
INSERT INTO users(email, username, password, created_on, is_private, is_enabled, is_deleted, description) VALUES ('disabled@example.com', 'disabled_user','$2a$10$9tWQYPEQ1tPaUKSTto22BuxZAjZ.m/7f5Q4J39e7gs9pFn9sJWY7y','2025-09-15',false,false,false,'Disabled');
INSERT INTO users(email, username, password, created_on, is_private, is_enabled, is_deleted, description) VALUES ('deleted@example.com', 'deleted_user','$2a$10$M/YyvnZF18ES6dFXtP.xZel2LjhMUtv.RG4AyuhQ0RHcqaKftzvla','2025-09-15',false,true,true,'Deleted');
INSERT INTO users(email, username, password, created_on, is_private, is_enabled, is_deleted, description) VALUES ('admin@example.com', 'admin_user','$2a$10$9lG6cb5f3SRp9DvAipSiU.vPqtFxF.FKqFooe6Cm5IcQySq1M7bsK','2025-09-15',false,true,false,'Admin');

INSERT INTO user_roles (user_id, role) VALUES (1, 'USER');
INSERT INTO user_roles (user_id, role) VALUES (2, 'USER');
INSERT INTO user_roles (user_id, role) VALUES (3, 'USER');
INSERT INTO user_roles (user_id, role) VALUES (4, 'USER');
INSERT INTO user_roles (user_id, role) VALUES (5, 'USER');
INSERT INTO user_roles (user_id, role) VALUES (6, 'USER');
INSERT INTO user_roles (user_id, role) VALUES (7, 'USER');
INSERT INTO user_roles (user_id, role) VALUES (8, 'USER');
INSERT INTO user_roles (user_id, role) VALUES (9, 'USER');
INSERT INTO user_roles (user_id, role) VALUES (9, 'ADMIN');


INSERT INTO posts (title, content, rating, user_id, created_at, is_deleted, is_hidden, is_deleted_by_admin) VALUES ('First user''s post', 'testing for 100 characters testing for 100 characters testing for 100 characters testing for 100 characters testing for 100 characters testing for 100 characters testing for 100 characters testing for 100 characters', 0, 1, '2025-09-09 21:51:41', false, false, false);
INSERT INTO posts (title, content, rating, user_id, created_at, is_deleted, is_hidden, is_deleted_by_admin) VALUES ('Second user''s post', 'testing for 100 characters testing for 100 characters testing for 100 characters testing for 100 characters', 0, 2, '2025-09-09 21:52:22', false, false, false);
INSERT INTO posts (title, content, rating, user_id, created_at, is_deleted, is_hidden, is_deleted_by_admin) VALUES ('Third user''s post', 'testing for 100 characters testing for 100 characters testing for 100 characters testing for 100 characters', 0, 3, '2025-09-09 21:52:57', false, false, false);

INSERT INTO comments (content, rating, user_id, created_at, parent_comment_id, parent_post_id, is_deleted, is_hidden, is_deleted_by_admin) VALUES ('Comment on post 1', 0, 3, '2025-09-09 21:54:33', null, 1, false, false, false);
INSERT INTO comments (content, rating, user_id, created_at, parent_comment_id, parent_post_id, is_deleted, is_hidden, is_deleted_by_admin) VALUES ('Comment on post 2', 0, 2, '2025-09-09 21:54:44', null, 2, false, false, false);
INSERT INTO comments (content, rating, user_id, created_at, parent_comment_id, parent_post_id, is_deleted, is_hidden, is_deleted_by_admin) VALUES ('Comment on post 3', 0, 3, '2025-09-09 21:54:55', null, 3, false, false, false);
INSERT INTO comments (content, rating, user_id, created_at, parent_comment_id, parent_post_id, is_deleted, is_hidden, is_deleted_by_admin) VALUES ('Comment on post 3', 0, 2, '2025-09-09 21:55:13', null, 3, false, false, false);
INSERT INTO comments (content, rating, user_id, created_at, parent_comment_id, parent_post_id, is_deleted, is_hidden, is_deleted_by_admin) VALUES ('Another comment on post 1', 0, 1, '2025-09-09 21:56:27', null, 1, false, false, false);
INSERT INTO comments (content, rating, user_id, created_at, parent_comment_id, parent_post_id, is_deleted, is_hidden, is_deleted_by_admin) VALUES ('Comment on comment 2', 0, 2, '2025-09-09 21:54:44', 2, 2, false, false, false);
INSERT INTO comments (content, rating, user_id, created_at, parent_comment_id, parent_post_id, is_deleted, is_hidden, is_deleted_by_admin) VALUES ('Another comment on comment 2', 0, 2, '2025-09-09 21:54:44', 2, 2, false, false, false);
INSERT INTO comments (content, rating, user_id, created_at, parent_comment_id, parent_post_id, is_deleted, is_hidden, is_deleted_by_admin) VALUES ('Comment on comment 5', 0, 1, '2025-09-09 21:57:26', 5, 1, false, false, false);
INSERT INTO comments (content, rating, user_id, created_at, parent_comment_id, parent_post_id, is_deleted, is_hidden, is_deleted_by_admin) VALUES ('Another comment on comment 5', 0, 2, '2025-09-09 21:57:26', 5, 1, false, false, false);
INSERT INTO comments (content, rating, user_id, created_at, parent_comment_id, parent_post_id, is_deleted, is_hidden, is_deleted_by_admin) VALUES ('Another comment on post 2', 0, 7, '2025-09-09 21:57:26', 5, 1, false, false, false);
INSERT INTO comments (content, rating, user_id, created_at, parent_comment_id, parent_post_id, is_deleted, is_hidden, is_deleted_by_admin) VALUES ('Another comment on post 2', 0, 8, '2025-09-09 21:57:26', 5, 1, false, false, false);
INSERT INTO comments (content, rating, user_id, created_at, parent_comment_id, parent_post_id, is_deleted, is_hidden, is_deleted_by_admin) VALUES ('Another comment on post 2', 0, 9, '2025-09-09 21:57:26', 5, 1, false, false, false);
INSERT INTO comments (content, rating, user_id, created_at, parent_comment_id, parent_post_id, is_deleted, is_hidden, is_deleted_by_admin) VALUES ('Another comment on comment 10', 0, 1, '2025-09-09 21:57:26', 5, 1, false, false, false);