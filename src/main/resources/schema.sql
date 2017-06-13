DROP TABLE IF EXISTS post CASCADE ;
DROP TABLE IF EXISTS forum CASCADE ;
DROP TABLE IF EXISTS thread CASCADE ;
DROP TABLE IF EXISTS m_user CASCADE ;
DROP TABLE IF EXISTS voice;
DROP TABLE IF EXISTS users_forum;

DROP INDEX if EXISTS unique_email;
DROP INDEX IF EXISTS unique_slug_thread;
DROP INDEX IF EXISTS unique_slug_forum;
DROP INDEX IF EXISTS unique_nickname;
DROP INDEX IF EXISTS forum_user;
DROP INDEX IF EXISTS thread_user;
DROP INDEX IF EXISTS thread_forum;
DROP INDEX IF EXISTS post_user;
DROP INDEX IF EXISTS post_forum_id;
DROP INDEX IF EXISTS uf_forum;
DROP INDEX IF EXISTS post_path;
DROP INDEX IF EXISTS uf_user;
DROP INDEX IF EXISTS post_thread_id;
DROP INDEX IF EXISTS post_parent_thread;
DROP INDEX IF EXISTS post_parent;
DROP INDEX IF EXISTS post_id_thread_id;

DROP EXTENSION citext CASCADE;

CREATE EXTENSION IF NOT EXISTS citext;

CREATE TABLE IF NOT EXISTS m_user (
  id       SERIAL        NOT NULL PRIMARY KEY,
  nickname CITEXT NOT NULL,
  fullname VARCHAR(128),
  abbout   TEXT,
  email    VARCHAR(50) NOT NULL
);

CREATE UNIQUE INDEX unique_email ON m_user (LOWER(email));
CREATE UNIQUE INDEX unique_nickname ON m_user (LOWER(nickname COLLATE "ucs_basic"));

CREATE TABLE IF NOT EXISTS forum (
  id      SERIAL PRIMARY KEY,
  title   VARCHAR(128)  NOT NULL,
  slug    CITEXT UNIQUE NOT NULL,
  posts   BIGINT        NOT NULL DEFAULT 0,
  threads BIGINT        NOT NULL DEFAULT 0,
  user_id INT REFERENCES m_user(id) NOT NULL
);


CREATE INDEX forum_user ON forum(user_id);
CREATE UNIQUE INDEX unique_slug_forum ON forum (LOWER(slug));


CREATE TABLE IF NOT EXISTS thread (
  id      SERIAL PRIMARY KEY,
  title   VARCHAR(128) NOT NULL,
  user_id INT REFERENCES m_user(id) NOT NULL,
  forum_id INT REFERENCES forum(id) NOT NULL,
  message TEXT         NOT NULL,
  votes   BIGINT       NOT NULL DEFAULT 0,
  slug    CITEXT UNIQUE,
  created TIMESTAMP    NOT NULL DEFAULT current_timestamp
);

CREATE INDEX thread_user ON thread(user_id);
CREATE INDEX thread_forum ON thread(forum_id);
CREATE UNIQUE INDEX unique_slug_thread ON thread (LOWER(slug));

CREATE TABLE IF NOT EXISTS post (
  id        SERIAL PRIMARY KEY,
  parent_id INT ,
  message   TEXT,
  isEdited  BOOLEAN DEFAULT FALSE,
  user_id INT REFERENCES m_user(id) NOT NULL,
  forum_id INT REFERENCES forum(id) NOT NULL,
  thread    INT  REFERENCES thread(id) NOT NULL,
  created   TIMESTAMP DEFAULT current_timestamp,
  path INT ARRAY
);

CREATE INDEX post_user ON post(user_id);
CREATE INDEX post_forum_id ON post(forum_id);
CREATE INDEX post_thread_id ON post(thread);
CREATE INDEX post_path ON post((path[1]));
CREATE INDEX post_parent ON post(parent_id);
CREATE INDEX post_parent_thread ON post(parent_id, id, thread);
CREATE INDEX post_id_thread_id ON post(id, thread);

CREATE TABLE IF NOT EXISTS voice (
  id        SERIAL PRIMARY KEY,
  user_id INT REFERENCES m_user(id) NOT NULL,
  count     SMALLINT,
  thread_id INT  REFERENCES thread(id) NOT NULL,
  UNIQUE (user_id, thread_id)
);

CREATE TABLE IF NOT EXISTS m_users_forums (
  user_id INT REFERENCES m_user(id) NOT NULL,
  forum_id INT REFERENCES forum(id) NOT NULL);

CREATE INDEX uf_user ON m_users_forums (user_id);
CREATE INDEX uf_forum ON m_users_forums (forum_id);

CREATE OR REPLACE FUNCTION m_users_forums_add() RETURNS TRIGGER AS '
  BEGIN
    INSERT INTO m_users_forums (user_id, forum_id) VALUES (NEW.user_id, NEW.forum_id);
    RETURN NEW;
  END;
' LANGUAGE plpgsql;


CREATE TRIGGER post_insert_trigger AFTER INSERT ON post
FOR EACH ROW EXECUTE PROCEDURE m_users_forums_add();

CREATE TRIGGER thread_insert_trigger AFTER INSERT ON thread
FOR EACH ROW EXECUTE PROCEDURE m_users_forums_add();
