CREATE TABLE PERMISSIONS(
   ID UUID PRIMARY KEY,
   NAME VARCHAR (100) UNIQUE NOT NULL,
   CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);