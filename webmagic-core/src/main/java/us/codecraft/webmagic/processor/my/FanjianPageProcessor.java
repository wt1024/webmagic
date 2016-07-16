package us.codecraft.webmagic.processor.my;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.pipeline.MysqlWlheiheiPipeline;
import us.codecraft.webmagic.pipeline.MysqlWtblogPipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

/**
 * @author code4crafter@gmail.com <br>
 */
public class FanjianPageProcessor implements PageProcessor {

    private Site site = Site.me().setDomain("my.oschina.net");

    @Override
    public void process(Page page) {
    	//
    	//*****************************************
        //List<String> links = page.getHtml().links().regex("http://my\\.oschina\\.net/wangt10/blog/\\d+").all();

    	List<String> links = page.getHtml().links().regex("http://www\\.fanjian\\.net/post/\\d+").all();
        page.addTargetRequests(links);
        page.putField("title", page.getHtml().xpath("//div[@class='main']/div[@class='box-pm']/div[@class='view-box']/h1/@title").toString());
        page.putField("content", page.getHtml().xpath("//div[@class='main']/div[@class='box-pm']/div[@class='view-box']/div[@class='view-main']/html()").toString());
        
        page.putField("img_links", page.getHtml().xpath("//div[@class='main']/div[@class='box-pm']/div[@class='view-box']/div[@class='view-main']//img/@src").all());
        
        if (page.getResultItems().get("title") == null) {
            //skip this page
            page.setSkip(true);
        }
    }

    @Override
    public Site getSite() {
        return site;

    }

    public static void main(String[] args) {
    	//*********************
    	String[] urls = new String[800];
    	for(int i=1; i<=800;i++){
    		urls[i-1]="http://www.fanjian.net/hot-"+i;
    	}
    	
    	
    	
        Spider.create(new FanjianPageProcessor())//.addUrl("http://www.fanjian.net/jianwen-1","http://www.fanjian.net/jianwen-2")
        .addUrl(urls)
        .addPipeline(new ConsolePipeline()).addPipeline(new MysqlWlheiheiPipeline())
        .run();
    }
}
