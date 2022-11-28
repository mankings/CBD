USE cbd_103341_ex2;

-- ex b)
select * from users;
select * from videos;
select * from videos_by_author;
select * from comments;
select * from followers;
select * from events;
select * from ratings;
select * from comments_by_user;
select * from comments_by_video;

-- ex d) 1
select * from comments_by_video where id_video=14 limit 3;

-- ex d) 2
select id, tags from videos where id=5;

-- ex d) 3
-- without allowing filtering, this query is not possible
-- because the tags are not part of the primary key

-- ex d) 4
select * from events where username='lucas' and id_video=7 limit 5;

-- ex d) 5
select * from videos_by_author where author='marychannel' and ts > '2022-11-01';

-- ex d) 6
-- query is not possible because clustering key restricts the query
-- also cassandra does not allow global querying

-- ex d) 7
select users from followers where id_video=1;

-- ex d) 8
-- not possible because cassandra doesn't support joins

-- ex d) 9
-- not possible because cassandra does not allow global querying

-- ex d) 10
select * from videos;

-- ex d) 11
-- not possible, would need to create another table with tags as primary keys
-- and a set of video ids as a field

-- ex d) 12
-- get 4 latest comments
select * from comments limit 4;

-- ex d) 13
-- get all ratings for a certain video
select * from ratings where id_video=1;