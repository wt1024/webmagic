package us.codecraft.webmagic.pipeline;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.annotation.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.util.DBWtblogDAOImpl;
import us.codecraft.webmagic.pipeline.util.DateUtil;
import us.codecraft.webmagic.pipeline.util.DownloadImage;
import us.codecraft.webmagic.utils.FilePersistentBase;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
public class MysqlWtblogPipeline extends FilePersistentBase implements Pipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    DBWtblogDAOImpl dao = new DBWtblogDAOImpl();
    
    String[][] guizeArr =  null;
    //String[][] gg={{"",""},{"",""}};

    /*
	栏目 -2it技术  5java 3javascript  8hadoop 20linux 29mapreduce  10php 32wordpress
	89编程语言 23数据库 11前端 197移动端

	标签
	53spring 54mybatis 58shell 61hadoop 65eclipse  80jsp 82webservice 84多线程 83 struts2 87c语言
	99设计模式 107算法 111maven 113 android 114微信 117java 123数据库 119hibernate  124jquery 126html 
	129tomcat 139xml 135ubuntu 27centos 108python 21linux */
    
    public MysqlWtblogPipeline() {
    	guizeArr = new String[][]{
    			{"5","java,mybatis,spring,hibernate,webservice,struts,设计模式,maven"},
    			{"3","javascript"},
    			{"8","hadoop,mapreduce"},
    			{"20","linux,centos,shell,ubuntu,redhat,sudo"},
    			{"10","php"},
    			{"32","wordpress"},
    			{"89","java,php,python"},
    			{"23","mysql,oracle,数据库,sqlserver,hbase,redis"},
    			{"11","javascript,js,jquery,html,div"},
    			{"197","android,安卓,ios,app"},
    			{"53","spring"},{"54","mybatis"},{"58","shell"},{"61","hadoop"},
    			{"65","eclipse"},{"80","jsp"},{"82","webservice"},{"84","线程"},{"83","struts"},
    			{"57","c语言"},{"99","设计模式"},{"107","算法"},{"111","maven"},{"113","android"},
    			{"114","微信"},{"117","java,mybatis,spring,hibernate,webservice,struts,设计模式,maven"},
    			{"123","mysql,oracle,数据库,sqlserver,hbase,redis"},
    			{"119","hibernate"},{"124","jquery"},{"126","html"},{"129","tomcat"},{"139","xml"},
    			{"135","ubuntu"},{"127","centos"},{"198","mac,xcode"},{"199","xcode"},{"108","python"},
    			{"21","linux"}
    			
    			};
	}
    

    @Override
    public void process(ResultItems resultItems, Task task) {
    	//insert into addressbook(fname,lname,phone,fax,email)
        //-> values('Rob',Rabbit,'674 1536','382 8364','rob@some.domain');
    	
    	//post_author post_date post_date_gmt post_content post_title post_name(编码后) post_modified post_modified_gmt guid 
        //2        now()     now()         content     title                            now                now    http://www.milletblog.com/?p=744
        	
    	String title = String.valueOf(resultItems.getAll().get("title"));
    	String content = String.valueOf(resultItems.getAll().get("content"));
    	List<String> pic_links = (List<String>) resultItems.getAll().get("img_links");
    	
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
    			System.out.println("downloadpic: "+s);
				DownloadImage.download(s, new_img_name, "d:/spider/"+datestr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		//System.out.println("begin**********************");
    		//System.out.println(s);
    		//System.out.println("http://www.milletblog.com/wp-content/uploads/spider/"+datestr+"/"+new_img_name);
    		content=content.replaceAll(s, "http://www.milletblog.com/wp-content/uploads/spider/"+datestr+"/"+new_img_name);
    		//System.out.println();System.out.println();System.out.println();
    		
    	}//end for
    	
    	
    	
    	
    	this.savetomysql(title, content);
    	
    	
    }
    
    //插入到数据库中
    private void savetomysql(String title,String content){
    	title = title.trim();

    	try {
    		//content=content.replaceAll("\"", "\\\"");
    		String postname= this.StringFilter(title);
    		String sql = "insert into wt_posts (post_title,post_name,post_content,post_author,post_date,post_date_gmt,post_modified,post_modified_gmt,post_excerpt,to_ping,pinged,post_content_filtered)"
    				+"values ('"+title+"','"+postname+"',?,3,now(),now(),now(),now(),'','','','')";
    		
    		String[] params = {content};//html 代码保存不能拼接sql
			dao.update(sql, params);
			String sql2 = "SELECT max(id) maxid from wt_posts ; ";
			//更新guid字段
			List<Map<String, Object>> lst = dao.findMapList(sql2, null);
			String maxid = String.valueOf(lst.get(0).get("maxid"));
			String sql3 = "update wt_posts set guid='http://www.milletblog.com/?p="+maxid+"' where id="+maxid;
			dao.update(sql3, null);
			//添加it技术的栏目
			String sql4 = "insert into wt_term_relationships values ("+maxid+",2,0);" ; 
			dao.update(sql4, null);
			//添加it(102)的标签  reprint(103)标签
			String sql5 = "insert into wt_term_relationships values ("+maxid+",103,0);" ; 
			dao.update(sql5, null);
			
			
			///匹配栏目标签个规则
			System.out.println("设置栏目和标签"+"********************************************************************");
			this.setBiaoqianAndLanmu(maxid, title);
			
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }//end insert
    
    
    
    //对标题的处理
    public  String StringFilter(String   str)      {     
        // 只允许字母和数字       
        // String   regEx  =  "[^a-zA-Z0-9]";                     
           // 清除掉所有特殊字符  
	  String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";  
	  Pattern   p   =   Pattern.compile(regEx);     
	  Matcher   m   =   p.matcher(str);     
	  String temp =  m.replaceAll("").trim();
	  temp = temp.replaceAll(" ", "");
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
    
    //自动设置标签和栏目
    public void setBiaoqianAndLanmu(String postid,String title) throws Exception{
    	//String title="linuxshangshell操作脚本和mybatis";
    	
    	title=title.toLowerCase();
    	System.out.println(title+"********************************************************************");
    	String sql="";
    	for(String[] arr :this.guizeArr){
    		String term_id=arr[0];
    		String[] keywords = arr[1].split(",",-1);
    		for(String keyword : keywords){
    			if(title.indexOf(keyword)>-1){
    				sql="insert into wt_term_relationships values ("+postid+","+term_id+",0);";
    				System.out.println(sql);
    				this.dao.update(sql, null);
    				System.out.println("********************************************************************");
    				break;
    			}//end if
    		}//end for
    	}//end for
    	System.out.println(title);
    	System.out.println("设置完毕********************************************************************");
    	//System.out.println(sql);
    }
    
    
    public static void main(String[] args) throws Exception {

    	String sql = "insert into wt_term_relationships values ("+10002+","+10000+",0);insert into wt_term_relationships values ("+10001+","+10000+",0);";
    	MysqlWtblogPipeline w = new MysqlWtblogPipeline();
    	w.dao.update(sql, null);
    	
    	
    	
    	if(1==1)return;
    	String s  = "文件操作中 fgets、fputs 函数详解";
    	MysqlWtblogPipeline mw = new MysqlWtblogPipeline();
    	System.out.println(mw.StringFilter(s));
    }
}
