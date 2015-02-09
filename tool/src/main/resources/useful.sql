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


