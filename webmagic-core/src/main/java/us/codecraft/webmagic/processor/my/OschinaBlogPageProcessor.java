package us.codecraft.webmagic.processor.my;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.pipeline.MysqlWtblogPipeline;
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
    	List<String> links = page.getHtml().links().regex("http://my.oschina.net/airship/blog/\\d+").all();
        page.addTargetRequests(links);
        page.putField("title", page.getHtml().xpath("//div[@class='container']/div[@class='blog-content']/div[@class='blog-heading']/div[@class='title']/text()").toString());
        page.putField("content", page.getHtml().xpath("//div[@class='container']/div[@class='blog-content']/div[@class='blog-body']/div[@class='BlogContent']/html()").toString());
        
        page.putField("img_links", page.getHtml().xpath("//div[@class='container']/div[@class='blog-content']/div[@class='blog-body']/div[@class='BlogContent']//img/@src").all());
        
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
        Spider.create(new OschinaBlogPageProcessor())
        .addUrl("http://my.oschina.net/airship/blog?sort=time&p=1")
        .addUrl("http://my.oschina.net/airship/blog?sort=time&p=2")
        //.addUrl("http://my.oschina.net/codeismygirl/blog?sort=time&p=2","http://my.oschina.net/codeismygirl/blog?sort=time&p=3")
        .addPipeline(new ConsolePipeline()).addPipeline(new MysqlWtblogPipeline())
        .run();
    }
}
