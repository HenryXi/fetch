select count(*) from tb_content2;
select count(*) from tb_content2 where content is not null;
select * from tb_content2 order by url limit 200 ;
select * from tb_content2 where id between 1990 and 5000 and content is not null;
select * from tb_content2 where url='fontawesome-f18c-fa-pagelines-symbol-is-not-shown-but-most-of-the-others-work';
select * from tb_content2 where url like '%fontawesome-f18c-fa-pagelines-symbol-is-not-shown-but-most-of-the-others-work%';
select * from tb_content2 where content like '%Passing state%';
select * from tb_content2 where content is not null;
select * from tb_content2 where id =9282740 or id=10;
update tb_content2 set content=null where url='http://stackoverflow.com/questions/28185009/in-the-mapper-iam-using-following-code-but-i-have-gone-through-documentation-but';
select * from tb_content2 where url='http://stackoverflow.com/questions/27413995/how-to-connect-to-your-prod-dev-database-using-phpmyadmin';
select count(*) from tb_content;
select count(distinct title) from tb_content;
select count(*) from tb_503content;
select * from tb_503content where url like '%354148%';
select url from tb_503content order by url;
select * from tb_content where title like '%How do I work with multiple git branches of a python module%' order by url asc;
select * from tb_content2 where url like '%quick%develop%';
select * from tb_content ;
select pg_database_size('page');
select pg_size_pretty(pg_database_size('page'));
select count(*) from pg_stat_activity;
select index from tb_content order by index desc;
select max(index) from tb_content;
select count(*) from tb_content2 ;
select * from tb_content2 where url not like '%http://stackoverflow.com/questions/%'

--todo delete short title in url , format title by my self
CREATE OR REPLACE FUNCTION hs_demo()
RETURNS text
LANGUAGE plpgsql AS
$$
DECLARE
  i integer;
  r record;
BEGIN
  FOR r IN select * from tb_content
  LOOP
 select count(*) into i from tb_content where url=substring(r.url from '[0-9]+');
 if(i=0) then
 update tb_content set url=substring(r.url from '[0-9]+') where url=r.url;
 else
 delete from tb_content where url=r.url;
 update tb_content set url=substring(r.url from '[0-9]+') where url=r.url;
 end if;
  END LOOP;
  return 'success';
END;
$$;


select hs_demo();

select count(*) from tb_content;
delete from tb_content where url='2571402'
update tb_content set url=substring(url from '[0-9]+') where id=14611084;
select * from tb_content where url ='2706500';
select substring('14484550/java-variables-not-initialized-error' from '[0-9]+')
select max(id) from tb_content;
select * from tb_content where url like '%http%';
select * from tb_content where id=8876;

CREATE UNIQUE INDEX unique_url
  ON tb_content
  USING btree
  (url COLLATE pg_catalog."default");


  select id, max(char_length(url)) max from tb_content group by id order by max desc limit 100;
  select id,max(char_length(url)) from tb_content order by max(char_length(url)) limit 1
  select char_length(url) from tb_content where char_length >8 limit 10

  -- select count(*) from tb_content;
-- select pg_size_pretty(pg_database_size('goobbe'));--4828M
