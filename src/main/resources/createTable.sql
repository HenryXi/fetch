drop table if exists tb_content;
drop table if exists tb_503content;
CREATE TABLE tb_content
(
  id serial NOT NULL,
  title character varying(1000),
  url character varying(1000),
  content text,
  index bigint,
  CONSTRAINT tb_content_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tb_content
  OWNER TO postgres;

-- Index: unique_title

-- DROP INDEX unique_title;

CREATE UNIQUE INDEX unique_title
  ON tb_content
  USING btree
  (title COLLATE pg_catalog."default");
  -- Table: tb_503content

-- DROP TABLE tb_503content;

CREATE TABLE tb_503content
(
  id serial NOT NULL,
  url character varying(1000),
  CONSTRAINT tb_503content_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tb_503content
  OWNER TO postgres;


