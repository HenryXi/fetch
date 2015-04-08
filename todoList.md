# Goobbe todo list

---

* <s>make id in page meaningful</s>
* <s>make search with ajax and progress bar</s>
* translation function, using js is the best way(press button then translate move mouse button disappear)
* similar question list in content page(Lucene)
* make a ecosystem for goobbe(1.fetching all data 2.update db by searching result )
* return state code and page for 404 500 503 and so on
* add contact us link(email) and version at the bottom
* add original link for each question
* replace system.out.put with log4j to collect log.
* replace sof question link in content json
* css file is too long, remove useless css
* i18n
* security in spring mvc(forbid access with illegal url)
* get total page number by another table, show lastest question in first page
<pre><code>
    insert into tcounter(table_name,count) select 'tb_content', count(*) from tb_content ;
    CREATE OR REPLACE FUNCTION ex_count()  
    RETURNS trigger AS  
    $BODY$  
    BEGIN  
    IF (TG_OP='INSERT') THEN  
      UPDATE tcounter set count = count + 1 where table_name = TG_TABLE_NAME::TEXT;  
    ELSIF  (TG_OP='DELETE') THEN  
      UPDATE tcounter set count = count - 1 where table_name = TG_TABLE_NAME::TEXT;  
    END IF;  
    RETURN NEW;  
    END$BODY$  
    LANGUAGE plpgsql VOLATILE  
    COST 100;  
    CREATE TRIGGER tg_counter AFTER INSERT OR DELETE ON my_schema.my_table  FOR EACH ROW  EXECUTE PROCEDURE ex_count();  
</pre></code>
