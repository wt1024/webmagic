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
public class CSDNBlogPageProcessor implements PageProcessor {

    private Site site = Site.me().setDomain("blog.csdn.net");

    @Override
    public void process(Page page) {
    	//
    	//*****************************************todo
    	List<String> links = page.getHtml().links().regex("http://blog.csdn.net/zolalad/article/details/\\d+").all();
        page.addTargetRequests(links);
        
        page.putField("title", page.getHtml().xpath("//div[@id='container']/div[@id='body']/div[@id='main']/div[@class='main']//div[@class='article_title']//span[@class='link_title']/a/text()").toString());
        String content=page.getHtml().xpath("//div[@id='container']/div[@id='body']/div[@id='main']/div[@class='main']/div[@id='article_details']/div[@class='article_content']/html()").toString();
        //page.putField("content", page.getHtml().xpath("//div[@id='container']/div[@id='body']/div[@id='main']/div[@class='main']/div[@id='article_details']/div[@class='article_content']/html()").toString());
        
        page.putField("img_links", page.getHtml().xpath("//div[@id='container']/div[@id='body']/div[@id='main']/div[@class='main']/div[@id='article_details']/div[@class='article_content']//img/@src").all());
        
        
        
        if(content!=null && !"".equals(content)){
        	//去掉版权信息
        	//content =  content.replaceFirst("<p><span style=\"font-size:14px\">转载请注明出处： 转载自&nbsp; Thinkgamer的CSDN博客：blog.csdn.net/gamer_gyt</span></p> ", "");
          //版权信息添加到最后
          //content+="<span style=\"font-size: 10pt;\">转载自&amp; http://blog.csdn.net/ochangwen <span>";
          page.putField("content",content);
        }
        
        
        
        if (page.getResultItems().get("title") == null || "".equals(page.getResultItems().get("title"))
        		|| 	page.getResultItems().get("content") == null|| "".equals(page.getResultItems().get("content"))
        		) {
            //skip this page
        	System.out.println("skip``````");
            page.setSkip(true);
        }
    }

    @Override
    public Site getSite() {
        return site;

    }

    public static void main(String[] args) {
    	//***csdn有目录视图(显示文章数量多) 和 摘要视图(显示文章数少), 默认是摘要视图
    	Spider s = Spider.create(new CSDNBlogPageProcessor());
    	//todo
    	for(int i=8   ; i>=1;i--){

    		s.addUrl("http://blog.csdn.net/zolalad/article/list/"+i);
    	}
    	//s.addUrl("http://blog.csdn.net/zolalad/article/details/11593153");
        //s.addUrl("http://blog.csdn.net/ljcitworld?viewmode=list");
        
        s.addPipeline(new ConsolePipeline())
                .addPipeline(new MysqlWtblogPipeline())
        .run();

        System.out.println("success");
    }
    //zolalad
    //qq_37267015
    //Peng_Hong_fu
    //ochangwen
    //reliveit
    //ochangwen
    //qfanmingyiq
    //xfg0218
    //leonwei
    //wang631106979
    //hejjunlin
    //u010293698
    //gamer_gyt
    //qq_26787115
    //kkkloveyou
    //han_xiaoyang
    //seu_calvin
    //poem_qianmo
    //wangyangzhizhou
    //u011240877
    //jiangshouzhuang
    //qq_26525215
    //future_challenger
}
