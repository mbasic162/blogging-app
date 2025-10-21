create table users
(
    id          bigint auto_increment
        primary key,
    email       varchar(50)  not null,
    username    varchar(20)  not null,
    password    varchar(255) not null,
    created_on  date         null,
    is_private  bit          not null,
    is_enabled  bit          not null,
    is_deleted  bit          not null,
    description varchar(200) null
);

create table posts
(
    id                  bigint auto_increment
        primary key,
    title               varchar(200)   not null,
    content             varchar(15000) not null,
    rating              int            not null,
    user_id             bigint         not null,
    created_at          datetime       null,
    is_deleted          bit            not null,
    is_hidden           bit            not null,
    is_deleted_by_admin bit            not null,
    constraint FK5lidm6cqbc7u4xhqpxm898qme
        foreign key (user_id) references users (id)
            on delete cascade
);

create table comments
(
    id                  bigint auto_increment
        primary key,
    content             varchar(1000) not null,
    rating              int           not null,
    user_id             bigint        not null,
    created_at          datetime      null,
    parent_comment_id   bigint        null,
    parent_post_id      bigint        null,
    is_deleted          bit           not null,
    is_hidden           bit           not null,
    is_deleted_by_admin bit           not null,
    constraint FK8omq0tc18jd43bu5tjh6jvraq
        foreign key (user_id) references users (id)
            on delete cascade,
    constraint FKj6uhjlpxlx3n41sa51yqcvcgg
        foreign key (parent_post_id) references posts (id)
            on delete cascade,
    constraint parent_comment_id_fk
        foreign key (parent_comment_id) references comments (id)
            on delete cascade
);

create table blocked_users
(
    user_id   bigint not null,
    parent_id bigint not null,
    constraint blocked_users_parent_id_fk
        foreign key (parent_id) references users (id)
            on delete cascade,
    constraint blocked_users_user_id_fk
        foreign key (user_id) references users (id)
            on delete cascade
);

create table followers
(
    user_id   bigint not null,
    parent_id bigint not null,
    constraint followers_users_id_fk
        foreign key (user_id) references users (id)
            on delete cascade,
    constraint followers_users_id_fk_2
        foreign key (parent_id) references users (id)
            on delete cascade
);

create table user_roles
(
    user_id bigint                 not null,
    role    enum ('ADMIN', 'USER') not null,
    constraint FKhfh9dx7w3ubf1co1vdev94g3f
        foreign key (user_id) references users (id)
            on delete cascade
);

create table post_likes
(
    user_id bigint not null,
    post_id bigint not null,
    constraint post_id_post_like_fk
        foreign key (post_id) references posts (id)
            on delete cascade,
    constraint user_id_post_like_fk
        foreign key (user_id) references users (id)
            on delete cascade
);

create table post_dislikes
(
    user_id bigint not null,
    post_id bigint not null,
    constraint post_id_post_dislike_fk
        foreign key (post_id) references posts (id)
            on delete cascade,
    constraint user_id_post_dislike_fk
        foreign key (user_id) references users (id)
            on delete cascade
);

create table comment_likes
(
    user_id    bigint not null,
    comment_id bigint not null,
    constraint comment_id_comment_like_fk
        foreign key (comment_id) references comments (id),
    constraint user_id_comment_like_fk
        foreign key (user_id) references users (id)
);

create table comment_dislikes
(
    user_id    bigint not null,
    comment_id bigint not null,
    constraint comment_id_comment_dislike_fk
        foreign key (comment_id) references comments (id)
            on delete cascade,
    constraint user_id_comment_dislike_fk
        foreign key (user_id) references users (id)
            on delete cascade
);