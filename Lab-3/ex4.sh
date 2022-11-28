#! /bin/bash
cd 103341/ex4
echo "Executing DDL script."
docker run --rm --network cassandra -v "$(pwd)/CBD_103341_EX4_DDL.cql:/scripts/data.cql" -e CQLSH_HOST=cassandra -e CQLSH_PORT=9042 -e CQLVERSION=3.4.5 nuvo/docker-cqlsh
echo "Executing SEEDDATA script."
docker run --rm --network cassandra -v "$(pwd)/CBD_103341_EX4_SEEDDATA.cql:/scripts/data.cql" -e CQLSH_HOST=cassandra -e CQLSH_PORT=9042 -e CQLVERSION=3.4.5 nuvo/docker-cqlsh
echo "Executing QUERIES script."
docker run --rm --network cassandra -v "$(pwd)/CBD_103341_EX4_QUERIES.cql:/scripts/data.cql" -e CQLSH_HOST=cassandra -e CQLSH_PORT=9042 -e CQLVERSION=3.4.5 nuvo/docker-cqlsh

docker kill cassandra
docker run --rm -d --name cassandra --hostname cassandra --network cassandra -p 9042:9042 cassandra