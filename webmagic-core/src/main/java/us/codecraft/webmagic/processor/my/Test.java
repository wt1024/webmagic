package us.codecraft.webmagic.processor.my;

import java.util.ArrayList;
import java.util.List;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

public class Test {

	public void addUrl(String... urls) {
        for (String url : urls) {
            System.out.println(url);
        }
    }
	public static void main(String[] args) {
		String t = "<p><span style=\"font-size:14px\">转载请注明出处： 转载自&nbsp; Thinkgamer的CSDN博客：blog.csdn.net/gamer_gyt</span></p> ";
		System.out.println(t.replaceFirst("请注明出处： 转载自&nbsp; Thinkgamer的CSDN博客：blog.csdn.net", "a"));
		System.out.println(t);
		System.out.println("%E4%B8%80%E4%BD%8D%E5%B1%8C%E4%B8%9D%E7%9B%B4%E6%92%AD%E4%BB%96%E5%92%8C%E5%A5%B3%E7%A5%9E%E7%9A%84%E4%B8%80%E4%BA%9B%E4%BA%8B%E7%9C%9F%E6%98%AF%E9%97%B7%E5%A3%B0%E5%A4%87%E5%BE%97%E4%B8%80%E6%89%8B%E".length());
		
		System.out.println("一位屌丝直播他和女神的一些事 。。。真是闷声备得一手好胎！[多图]".length());
		
	}
}
