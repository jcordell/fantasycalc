## Installation

#### Postgres

Start new docker container.
Setup based on https://hackernoon.com/dont-install-postgres-docker-pull-postgres-bee20e200198.
```
docker run --rm --name fantasycalc_db -e POSTGRES_PASSWORD=password -d -p 5432:5432 postgres
```
```
psql -h localhost -U postgres -d postgres
CREATE DATABASE fantasycalc_db;
```