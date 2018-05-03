package us.codecraft.webmagic.pipeline;

import org.apache.http.annotation.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.util.DBFS12307DAOImpl;
import us.codecraft.webmagic.pipeline.util.DateUtil;
import us.codecraft.webmagic.pipeline.util.DownloadImage;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Store results in files.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
@ThreadSafe
public class MysqlFS12307Pipeline implements Pipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    DBFS12307DAOImpl dao = new DBFS12307DAOImpl();

    @Override
    public void process(ResultItems resultItems, Task task) {
    	//insert into addressbook(fname,lname,phone,fax,email)
        //-> values('Rob',Rabbit,'674 1536','382 8364','rob@some.domain');
    	
    	//post_author post_date post_date_gmt post_content post_title post_name(编码后) post_modified post_modified_gmt guid 
        //2        now()     now()         content     title                            now                now    http://www.milletblog.com/?p=744
        	
    	String title = String.valueOf(resultItems.getAll().get("title"));
    	String content = String.valueOf(resultItems.getAll().get("content"));
    	List<String> pic_links = (List<String>) resultItems.getAll().get("img_links");



    	//纯文103  图片102
    	int flag=103;
    	if(null == pic_links || pic_links.size()==0){
    		flag =103;
    	}else{
    		flag=102;
    	}
    	
    	
    	this.savetomysql(title, content,flag, pic_links);
    	
    }
    
    //插入到数据库中
    private void savetomysql(String title,String content,int flag,List<String> pic_links){
    	title = title.trim();

    	try {
    		//content=content.replaceAll("\"", "\\\"");
    		String postname= this.StringFilter(title);

    		//校验是否有当前文章
			String countsql="select count(1) from wp_posts where post_title like '%"+title+"%'";
			List teml = dao.findMapList(countsql,null);
			if(teml.size()>0){
				System.out.println("已有重复的了:  "+title);
			}



    		//String sql = "insert into wp_posts (post_title,post_name,post_content,post_author,post_date,post_date_gmt,post_modified,post_modified_gmt,post_excerpt,to_ping,pinged,post_content_filtered)"
    		//		+"values ('"+title+"','"+postname+"',?,3,now(),now(),now(),now(),'','','','')";
    		String sql = "insert into wp_posts (post_title,post_name,post_content,post_author,post_date,post_date_gmt,post_modified,post_modified_gmt,post_excerpt,to_ping,pinged,post_content_filtered)"
    				+"values ('"+title+"','"+postname+"',?,3,CONCAT('2016-03-01 ',SUBSTRING(NOW(),12,9)),CONCAT('2016-03-01 ',SUBSTRING(NOW(),12,9)),CONCAT('2016-03-01 ',SUBSTRING(NOW(),12,9)),CONCAT('2016-03-01 ',SUBSTRING(NOW(),12,9)),'','','','')";
    		
    		
    		 
    		String[] params = {content};//html 代码保存不能拼接sql
			dao.update(sql, params);
			String sql2 = "SELECT max(id) maxid from wp_posts ; ";
			//更新guid字段
			List<Map<String, Object>> lst = dao.findMapList(sql2, null);
			String maxid = String.valueOf(lst.get(0).get("maxid"));
			String sql3 = "update wp_posts set guid='http://www.fs12307.com/?p="+maxid+"' where id="+maxid;
			dao.update(sql3, null);
			//添加 纯文 图片的栏目
			String sql4 = "insert into wp_term_relationships values ("+maxid+","+flag+",0);" ; 
			dao.update(sql4, null);
			//添加 搞笑的标签 
			//String sql5 = "insert into wp_term_relationships values ("+maxid+",21,0);" ; 
			//dao.update(sql5, null);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		//下载图片  前面校验重复了,确认没有重复的文章才下载图片
		Set<String> set_links = new HashSet<String>();
		for(String s: pic_links){//去重
			set_links.add(s);
		}

		for(String s: set_links){
			String datestr=DateUtil.getDateStr(new Date(),"yyyyMMdd");
			int flint = (int)(Math.random()*100000);
			String[] tempstr = s.split("\\.");
			String last_ = tempstr[tempstr.length-1];//图片结尾 .jpg .gif
			String new_img_name=System.currentTimeMillis()+"_"+flint+"."+last_;
			try {
				DownloadImage.download(s, new_img_name, "/Users/wangt/wtdata0/temp/spider2/"+datestr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println("begin**********************");
			//System.out.println(s);
			//System.out.println("http://www.milletblog.com/wp-content/uploads/spider/"+datestr+"/"+new_img_name);
			content=content.replaceAll(s, "http://www.fs12307.com/wp-content/uploads/spider/"+datestr+"/"+new_img_name);
			//System.out.println();System.out.println();System.out.println();

		}//end for


    }//end insert
    
    public  String StringFilter(String   str)      {     
        // 只允许字母和数字       
        // String   regEx  =  "[^a-zA-Z0-9]";                     
           // 清除掉所有特殊字符  
	  String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";  
	  Pattern   p   =   Pattern.compile(regEx);     
	  Matcher   m   =   p.matcher(str);     
	  String temp =  m.replaceAll("").trim();
	  temp = temp.replaceAll(" ", "");
	  temp=temp.replaceAll("---","m");//  ---  会导致404
	  String result = "";
	  try {
		  result =  URLEncoder.encode(temp, "utf-8");
		  int len = result.length()+3;
		  if(len > 190){
			  //System.out.println(temp.substring(0,10));
			  result =  URLEncoder.encode(temp.substring(0,10), "utf-8");
			  
		  }
	  } catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	  }
	  return result+"-"+(int)(Math.random()*100);
	}   
    
    
    
    public static void main(String[] args) throws Exception {
    	String sql = "select id, post_title, post_name  from wp_posts where id > 166 ";
    	//String sql = "select id, post_title, post_name  from wp_posts where id = 1130 ";
    	DBFS12307DAOImpl dao = new DBFS12307DAOImpl();
    	List<Map<String, Object>> lst = dao.findMapList(sql, null);
    	
    	for(Map<String, Object> map : lst){
    		String name = String.valueOf(map.get("post_name"));
    		String title = String.valueOf(map.get("post_title"));
    		int id = Integer.valueOf(String.valueOf(map.get("ID")));
    		System.out.println(name);
    		System.out.println(title);
    		System.out.println(id);
    		int len = name.length();
    		String result = "";
    		if(len > 190){
  			  //System.out.println(temp.substring(0,10));
  			  result =  URLEncoder.encode(title.substring(0,10), "utf-8")+(int)(Math.random()*100);
  			  System.out.println("进来了***********");
  			  System.out.println(result);
  			System.out.println(result.length());
  			  String sql2 = "update wp_posts set post_name='"+result+"' where id ="+id ;
  			System.out.println(sql2);
  			  dao.update(sql2, null);
  		  	}
    		
    		
    	}
    	
    	
    	
    }
}
