
CREATE EXTENSION IF NOT EXISTS CITEXT;

DROP TABLE IF EXISTS post CASCADE ;
DROP TABLE IF EXISTS forum CASCADE ;
DROP TABLE IF EXISTS thread CASCADE ;
DROP TABLE IF EXISTS m_user CASCADE ;
DROP TABLE IF EXISTS voice;
DROP TABLE IF EXISTS m_users_forums CASCADE ;

DROP INDEX IF EXISTS voice_thread_id;
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

CREATE TABLE IF NOT EXISTS m_user (
  id       SERIAL        NOT NULL PRIMARY KEY,
  nickname VARCHAR(100) NOT NULL,
  fullname VARCHAR(100),
  abbout   TEXT,
  email    VARCHAR(100)  NOT NULL
);

CREATE UNIQUE INDEX unique_email ON m_user(LOWER(email));
CREATE UNIQUE INDEX unique_nickname ON m_user (LOWER(nickname COLLATE "ucs_basic"));

CREATE TABLE IF NOT EXISTS forum (
  id      SERIAL NOT NULL PRIMARY KEY,
  title   VARCHAR(100)  NOT NULL,
  slug    VARCHAR(100),
  posts   BIGINT        NOT NULL DEFAULT 0,
  threads BIGINT        NOT NULL DEFAULT 0,
  user_id INT REFERENCES m_user(id) NOT NULL
);


CREATE INDEX IF NOT EXISTS forum_user ON forum(user_id);
CREATE UNIQUE INDEX IF NOT EXISTS unique_slug_forum ON forum (LOWER(slug));


CREATE TABLE IF NOT EXISTS thread (
  id      SERIAL PRIMARY KEY,
  title   VARCHAR(128) NOT NULL,
  user_id INT REFERENCES m_user(id) NOT NULL,
  forum_id INT REFERENCES forum(id) NOT NULL,
  message TEXT         NOT NULL,
  votes   BIGINT       NOT NULL DEFAULT 0,
  slug    VARCHAR(128),
  __nickname CITEXT NOT NULL,
  created TIMESTAMP    NOT NULL DEFAULT current_timestamp
);

CREATE INDEX IF NOT EXISTS thread_user ON thread(user_id);
CREATE INDEX IF NOT EXISTS thread_forum ON thread(forum_id);
CREATE UNIQUE INDEX IF NOT EXISTS unique_slug_thread ON thread (LOWER(slug));

CREATE TABLE IF NOT EXISTS post (
  id        SERIAL PRIMARY KEY,
  parent_id INT ,
  message   TEXT,
  isEdited  BOOLEAN DEFAULT FALSE,
  user_id INT NOT NULL,
  forum_id INT NOT NULL,
  thread    INT NOT NULL,
  created   TIMESTAMP DEFAULT current_timestamp,
  __nickname CITEXT NOT NULL,
  path INT ARRAY,
  FOREIGN KEY (user_id) REFERENCES m_user (id) ON DELETE CASCADE,
  FOREIGN KEY (forum_id) REFERENCES forum (id) ON DELETE CASCADE,
  FOREIGN KEY (thread) REFERENCES thread (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS post_user ON post(user_id);
CREATE INDEX IF NOT EXISTS post_forum_id ON post(forum_id);
CREATE INDEX IF NOT EXISTS post_thread_id ON post(thread);
CREATE INDEX IF NOT EXISTS post_created on post(created);
CREATE INDEX if NOT EXISTS post_thread_path on post(thread, path);
CREATE INDEX IF NOT EXISTS post_path_thread on post((path[1]), thread);
CREATE INDEX IF NOT EXISTS post_path ON post((path[1]));
CREATE INDEX IF NOT EXISTS post_parent_thread ON post(parent_id, thread);
CREATE INDEX IF NOT EXISTS post_parent_thread_id on post (parent_id, thread, id);
CREATE INDEX IF NOT EXISTS post_id_thread_id ON post(id, thread);
CREATE INDEX if NOT EXISTS post_thread_created_id on post(thread, created, id);


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

CREATE INDEX IF NOT EXISTS uf_user ON m_users_forums (user_id);
CREATE INDEX IF NOT EXISTS uf_forum ON m_users_forums (forum_id);

CREATE OR REPLACE FUNCTION m_users_forums_add() RETURNS TRIGGER AS '
  BEGIN
    INSERT INTO m_users_forums (user_id, forum_id) VALUES (NEW.user_id, NEW.forum_id);
    RETURN NEW;
  END;
' LANGUAGE plpgsql;


DROP TRIGGER IF EXISTS post_insert_trigger ON post;
CREATE TRIGGER post_insert_trigger AFTER INSERT ON post
FOR EACH ROW EXECUTE PROCEDURE m_users_forums_add();

DROP TRIGGER IF EXISTS thread_insert_trigger ON thread;
CREATE TRIGGER thread_insert_trigger AFTER INSERT ON thread
FOR EACH ROW EXECUTE PROCEDURE m_users_forums_add();
