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
    	List<String> links = page.getHtml().links().regex("https://my.oschina.net/jxcdwangtao/blog/\\d+").all();
        //List<String> links = page.getHtml().links().regex("https://my.oschina.net/LinBigR/blog/846211").all();
        page.addTargetRequests(links);
        System.out.println(links.size());
        //   /html/body/div[1]/div[1]/div/div[4]/div[1]/div[1]/span
        page.putField("title", page.getHtml().xpath("//div[@class='title']/text()").toString());
        
      //*[@id="blogBody"]/div  //*[@id="blogBody"]
        //page.putField("content", page.getHtml().xpath("//div[@id='blogBody']/div[@class='BlogContent']/html()").toString());

        page.putField("content", page.getHtml().xpath("//*[@id='blogBody']/html()").toString());
        //page.putField("img_links", page.getHtml().xpath("//div[@id='blogBody']/div[@class='BlogContent']//img/@src").all());
        page.putField("img_links", page.getHtml().xpath("//*[@id='blogBody']//img/@src").all());


//        System.out.println("magic infossssssssssssssssssssssssssssssss");
//        System.out.println(page.getResultItems().get("title"));
//        System.out.println(page.getResultItems().get("content"));
//        System.out.println(page.getResultItems().get("img_links"));
//        System.out.println("magic infoeeeeeeeeeeeeeeeeeeeeeeeee");


        if (page.getResultItems().get("title") == null || "".equals(page.getResultItems().get("title") )
        		|| page.getResultItems().get("content") == null || "".equals(page.getResultItems().get("content") )
        		) {
            //skip this page
        	System.out.println("skip: "+page.getResultItems().get("title"));
            System.out.println("content: "+page.getResultItems().get("content"));


            page.setSkip(true);
        }
    }

    @Override
    public Site getSite() {
        return site;

    }

    public static void main(String[] args) {
    	//*********************
//    	Spider.create(new OschinaBlogPageProcessor())
//        .addUrl("http://my.oschina.net/JoeyZ/blog?sort=time&p=1")
//        .addUrl("http://my.oschina.net/JoeyZ/blog?sort=time&p=2")
//
//        .addPipeline(new ConsolePipeline()).addPipeline(new MysqlWtblogPipeline())
//        .run();
    	
    	
        
    	Spider s = Spider.create(new OschinaBlogPageProcessor());
    	
    	for(int i=4;i>=1;i--){

    		s.addUrl("https://my.oschina.net/jxcdwangtao/blog?sort=time&p="+i);
    	}
        //s.addUrl("https://my.oschina.net/LinBigR/blog?sort=time");
        s.addPipeline(new ConsolePipeline()).addPipeline(new MysqlWtblogPipeline())
        .run();
        //s.addPipeline(new ConsolePipeline()).run();

        System.out.println("success");


    }
}
