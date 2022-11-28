CREATE KEYSPACE IF NOT EXISTS cbd_103341_ex2 WITH replication = {'class': 'SimpleStrategy','replication_factor': 1};
USE cbd_103341_ex2;


DROP TABLE IF EXISTS users;
CREATE TABLE users(
    username text primary key,
    name text,
    email text,
    ts timestamp
);

DROP TABLE IF EXISTS videos;
CREATE TABLE videos(
    id int,
    author text,
    name text,
    description text,
    tags set<text>,
    ts timestamp,
    primary key ((id), ts)
);

DROP TABLE IF EXISTS videos_by_author;
CREATE TABLE videos_by_author(
    id int,
    author text,
    name text,
    description text,
    tags set<text>,
    ts timestamp,
    primary key((author), ts)
);

DROP TABLE IF EXISTS comments;
CREATE TABLE comments(
    id int primary key,
    id_video int,
    author text,
    comment text,
    ts timestamp
);

DROP TABLE IF EXISTS followers;
CREATE TABLE followers(
    id_video int primary key,
    users set<text>
);

DROP TABLE IF EXISTS events;
CREATE TABLE events(
    id_video int,
    username text,
    type text,
    ts timestamp,
    instant timestamp,
    primary key((id_video, username), ts)
);

DROP TABLE IF EXISTS ratings;
CREATE TABLE ratings(
    id_video int,
    rate int,
    primary key((id_video), rate)
);

DROP TABLE IF EXISTS comments_by_user;
CREATE TABLE comments_by_user(
    username text,
    id_video int,
    comment text,
    ts timestamp,
    primary key(username, ts)) WITH CLUSTERING ORDER BY (ts DESC);

DROP TABLE IF EXISTS comments_by_video;
CREATE TABLE comments_by_video(
    username text,
    id_video int,
    author text,
    comment text,
    ts timestamp,
    primary key(id_video, ts)) WITH CLUSTERING ORDER BY (ts DESC);