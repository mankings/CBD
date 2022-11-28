CREATE KEYSPACE IF NOT EXISTS cbd_103341_ex4 WITH replication = {'class': 'SimpleStrategy','replication_factor': 1};
USE cbd_103341_ex4;

DROP TABLE IF EXISTS pokemon;
CREATE TABLE pokemon (
    id int,
    name text,
    types set<text>,
    generation int,
    stats list<int>,
    moves set<text>,
    primary key((generation), id)
);

DROP TABLE IF EXISTS attacks;
CREATE TABLE attacks (
    name text,
    type text,
    power int,
    accuracy int,
    description text,
    generation int,
    primary key((generation), name)
);

DROP TABLE IF EXISTS items;
CREATE TABLE items (
    name text,
    description text,
    generation int,
    primary key((generation), name)
);

DROP TABLE IF EXISTS routes;
CREATE TABLE routes (
    name text,
    connections map<text, text>,
    generation int,
    primary key((generation), name)
);