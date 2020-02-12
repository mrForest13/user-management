CREATE TABLE USERS(
   ID UUID PRIMARY KEY,
   EMAIL VARCHAR (50) UNIQUE NOT NULL,
   HASH VARCHAR (50) NOT NULL,
   SALT UUID NOT NULL,
   FIRST_NAME VARCHAR (50) NOT NULL,
   LAST_NAME VARCHAR (50) NOT NULL,
   CITY VARCHAR (50) NOT NULL,
   COUNTRY VARCHAR (50) NOT NULL,
   PHONE VARCHAR (15) NOT NULL,
   CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);