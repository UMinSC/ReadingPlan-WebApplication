
-- user table---------------------------
CREATE TABLE sec_user (
  userId            BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  userName          VARCHAR(75) NOT NULL UNIQUE,
  email             VARCHAR(75) NOT NULL UNIQUE,
  encryptedPassword VARCHAR(128) NOT NULL,
  enabled           BIT NOT NULL 
);

CREATE TABLE sec_role(
  roleId   BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  roleName VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE user_role(
  id     BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  userId BIGINT NOT NULL,
  roleId BIGINT NOT NULL
);

ALTER TABLE user_role
  ADD CONSTRAINT user_role_uk UNIQUE (userId, roleId);

ALTER TABLE user_role
  ADD CONSTRAINT user_role_fk1 FOREIGN KEY (userId)
  REFERENCES sec_user (userId);
 
ALTER TABLE user_role
  ADD CONSTRAINT user_role_fk2 FOREIGN KEY (roleId)
  REFERENCES sec_role (roleId);
  
  
  
  
-- LIBRARY table---------------------------
CREATE TABLE LIBRARY (
	id       BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	title        VARCHAR(255) NOT NULL,
	author       VARCHAR(255) NOT NULL,
	isbn         BIGINT NOT NULL UNIQUE,
	price        DECIMAL(10,2) NOT NULL,
	description  TEXT,
    totalPage INT CHECK (totalPage > 0),
   	imagePath VARCHAR(255)
   	
);





-- bookList table  ---------------------------  
CREATE TABLE BookStatus (
statusId   BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
statusName VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE bookList (
    userId     BIGINT NOT NULL,
    bookId     BIGINT NOT NULL,
    statusId   BIGINT NOT NULL,
    PRIMARY KEY (userId, bookId),
    FOREIGN KEY (bookId) REFERENCES LIBRARY(id),
    FOREIGN KEY (userId) REFERENCES sec_user (userId),
    FOREIGN KEY (statusId) REFERENCES bookStatus (statusId)
);



-- RECORDS table  ---------------------------  
CREATE TABLE RECORDS (
    userId     BIGINT NOT NULL,
    bookId     BIGINT NOT NULL,
    recordPage INT CHECK (recordPage >= 0),
    updateDate Date,
    PRIMARY KEY (userId, bookId, updateDate),
    FOREIGN KEY (bookId) REFERENCES LIBRARY (id),
    FOREIGN KEY (userId) REFERENCES sec_user (userId)
);


