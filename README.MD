# BOOK SUGGESTION PLUGIN WITH OLLAMA AND SPRING AI

# Prerequsities

## Install PostgreSQL

**MacOS/Linux**

```bash
brew install postgresql
```

**Windows**

[Check out this link](https://community.chocolatey.org/packages/postgresql)

```
choco install postgresql
```

### To start postgresql now and restart at login:

**MacOS/Linux**

```bash
brew services start postgresql@14
```

**Windows**

It should start automatically.


### To install pgvector extension in PostgreSQL

In this repo https://github.com/pgvector/pgvector you can find other OS.

**MacOS/Linux**.

```bash
cd /tmp
git clone --branch v0.7.4 https://github.com/pgvector/pgvector.git
cd pgvector
make
make install # may need sudo
```

**Windows**

Ensure C++ support in Visual Studio is installed, and run:

```
call "C:\Program Files\Microsoft Visual Studio\2022\Community\VC\Auxiliary\Build\vcvars64.bat"
```

Note: The exact path will vary depending on your Visual Studio version and edition

Then use `nmake` to build:

```
set "PGROOT=C:\Program Files\PostgreSQL\16"
cd %TEMP%
git clone --branch v0.7.4 https://github.com/pgvector/pgvector.git
cd pgvector
nmake /F Makefile.win
nmake /F Makefile.win install
```

Note: Postgres 17 is not supported yet due to an upstream issue

### Enable the extension

Do this once in each database where you want to use it.

```SQL
CREATE EXTENSION vector;
```

### Create user and give grants

Choose more powerful password for prod use.

```SQL
CREATE USER app_user WITH PASSWORD '123456';
GRANT ALL PRIVILEGES ON DATABASE postgres TO app_user;
GRANT ALL PRIVILEGES ON TABLE book TO app_user;
GRANT ALL PRIVILEGES ON TABLE book_id_seq TO app_user;
```

### Create the book table

```SQL
CREATE TABLE book
(
    id                 SERIAL PRIMARY KEY,
    title              TEXT,
    author             TEXT,
    rating             REAL,
    voters             INT,
    price              REAL,
    currency           TEXT,
    description        TEXT,
    description_vector VECTOR(768), -- Adjust the dimension based on the embedding model
    publisher          TEXT,
    page_count         INT,
    generes            TEXT,
    ISBN               TEXT,
    language           TEXT,
    published_date     DATE
);
```

## To set up python virtual environment

**MacOS/Linux**

```bash
 python -m venv path/to/venv
 source path/to/venv/bin/activate
```

**Windows**
```
 python -m venv path/to/venv
.\path\to\venv\bin\activate
```

>[!NOTE]
If `.\path\to\venv\bin\activate` doesn't work, then try ` \path\to\venv\Scripts\activate`.

### Deactivate virtual environment

```
deactivate
```

## Install dependencies

```bash
pip install kagglehub pandas psycopg2 sentence-transformers
```

# Project Files

## 1. Download Dataset

With [data_fetcher.py](./dataset/data_fetcher.py) script, you can download dataset to your user local.

On Mac, it is /Users/`{user}`/.cache/kagglehub/datasets

## 2. Convert and Save to Database

Make sure you have the following prerequisites installed:

- pandas for reading the CSV
- psycopg2 for database connection
- sentence-transformers to generate vector embeddings

The script converts the description column into a vector embedding using a simple pre-trained embedding model.

This is the [transform_and_store.py](./dataset/transform_and_store.py)

### If you have an issue with numpy, try to downgrade numpy.

```bash
pip install "numpy<2"
```