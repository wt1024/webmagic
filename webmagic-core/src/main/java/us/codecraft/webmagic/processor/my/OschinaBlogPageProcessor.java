package us.codecraft.webmagic.processor.my;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.pipeline.MysqlWtblogPipeline;
import us.codecraft.webmagic.pipeline.util.StringUtil;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

/**
 * @author code4crafter@gmail.com <br>
 */
public class OschinaBlogPageProcessor implements PageProcessor {

    private Site site = Site.me().setDomain("my.oschina.net");

    @Override
    public void process(Page page) {
    	//
    	//*****************************************
        //List<String> links = page.getHtml().links().regex("http://my\\.oschina\\.net/wangt10/blog/\\d+").all();
    	List<String> links = page.getHtml().links().regex("http://my.oschina.net/yangbajing/blog/\\d+").all();
        page.addTargetRequests(links);
        page.putField("title", page.getHtml().xpath("//div[@class='container']/div[@class='blog-content']/div[@class='blog-heading']/div[@class='title']/text()").toString());
        page.putField("content", page.getHtml().xpath("//div[@class='container']/div[@class='blog-content']/div[@class='blog-body']/div[@class='BlogContent']/html()").toString());
        //page.putField("content", page.getHtml().xpath("//div[@class='container']/div[@class='blog-content']/div[@class='blog-body']/html()").toString());
        
        page.putField("img_links", page.getHtml().xpath("//div[@class='container']/div[@class='blog-content']/div[@class='blog-body']/div[@class='BlogContent']//img/@src").all());
        
        if (page.getResultItems().get("title") == null
        		|| page.getResultItems().get("content") == null || "".equals(page.getResultItems().get("content") == null)
        		) {
            //skip this page
        	System.out.println("skip: "+page.getResultItems().get("title"));
            page.setSkip(true);
        }
    }

    @Override
    public Site getSite() {
        return site;

    }

    public static void main(String[] args) {
    	//*********************
        
        
    	Spider s = Spider.create(new CSDNBlogPageProcessor());
    	
    	for(int i=40;i>=1;i--){
    		s.addUrl("http://blog.csdn.net/testcs_dn/article/list/"+i);
    	}
        //.addUrl("http://blog.csdn.net/yerenyuan_pku/article/list/2","http://blog.csdn.net/yerenyuan_pku/article/list/1")
        
        s.addPipeline(new ConsolePipeline()).addPipeline(new MysqlWtblogPipeline())
        .run();
    }
}
