DROP TABLE IF EXISTS post CASCADE ;
DROP TABLE IF EXISTS forum CASCADE ;
DROP TABLE IF EXISTS thread CASCADE ;
DROP TABLE IF EXISTS m_user CASCADE ;
DROP TABLE if EXISTS voice;

DROP INDEX IF EXISTS voice_thread_id;


DROP INDEX if EXISTS post_parent_id_thread;
DROP INDEX IF EXISTS post_parent_id;
DROP INDEX IF EXISTS post_path_thread;

DROP INDEX IF EXISTS thread_forum;
DROP INDEX IF EXISTS thread_slug;

DROP INDEX IF EXISTS forum_slug;

DROP EXTENSION citext CASCADE;

CREATE EXTENSION IF NOT EXISTS citext;

CREATE TABLE IF NOT EXISTS m_user (
  id       SERIAL        NOT NULL PRIMARY KEY,
  nickname CITEXT UNIQUE NOT NULL,
  fullname VARCHAR(128)  NOT NULL,
  abbout   TEXT          NOT NULL,
  email    CITEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS post (
  id        SERIAL PRIMARY KEY,
  parent_id BIGINT    NOT NULL DEFAULT 0,
  author    CITEXT    NOT NULL,
  message   TEXT      NOT NULL,
  isEdited  BOOLEAN   NOT NULL DEFAULT FALSE,
  forum     CITEXT    NOT NULL,
  thread    BIGINT    NOT NULL,
  created   TIMESTAMP NOT NULL DEFAULT current_timestamp,
  path INT ARRAY
);

CREATE INDEX post_thread_parent_id on post (thread, parent_id);
CREATE INDEX post_parent_id ON post (parent_id);
CREATE INDEX post_path_thread ON post ((path[1]), thread);



CREATE TABLE IF NOT EXISTS voice (
  id        SERIAL PRIMARY KEY,
  author  CITEXT NOT NULL,
  count     INT    NOT NULL,
  thread_id INT    NOT NULL
);

CREATE INDEX voice_thread_id ON voice (author, thread_id) ;

CREATE TABLE IF NOT EXISTS thread (
  id      SERIAL PRIMARY KEY,
  title   VARCHAR(128) NOT NULL,
  author  CITEXT       NOT NULL,
  forum   CITEXT       NOT NULL,
  message TEXT         NOT NULL,
  votes   BIGINT       NOT NULL DEFAULT 0,
  slug    CITEXT UNIQUE,
  created TIMESTAMP    NOT NULL DEFAULT current_timestamp,
  FOREIGN KEY (author) REFERENCES m_user (nickname)
);

CREATE UNIQUE INDEX thread_slug ON thread (LOWER(slug));
CREATE INDEX thread_forum ON thread (forum);

CREATE TABLE IF NOT EXISTS forum (
  title   VARCHAR(128)  NOT NULL,
  admin   CITEXT        NOT NULL,
  slug    CITEXT UNIQUE NOT NULL,
  posts   BIGINT        NOT NULL DEFAULT 0,
  threads BIGINT        NOT NULL DEFAULT 0,
  FOREIGN KEY (admin) REFERENCES m_user (nickname)
);

CREATE INDEX forum_slug ON forum (slug)






