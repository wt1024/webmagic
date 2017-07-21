package us.codecraft.webmagic.pipeline.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;


public class DownloadImage {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		 //download("http://www.milletblog.com/wp-content/uploads/2016/06/1-1.png", "51bi.gif","D:\\imag22e\\");

		//[http://img.blog.csdn.net/20131120175026921?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvem9sYWxhZA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast, http://img.blog.csdn.net/20131120175222734?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvem9sYWxhZA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast, http://img.blog.csdn.net/20131120175544953?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvem9sYWxhZA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast, http://img.blog.csdn.net/20131120175434406?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvem9sYWxhZA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast, http://img.blog.csdn.net/20131120175126562?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvem9sYWxhZA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast]

		String s= "http://img.blog.csdn.net/20131120175026921?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvem9sYWxhZA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast";
		String new_img_name="temp";

		DownloadImage.download(s, new_img_name, "/Users/wangt/wtdata0/temp/spider/"+"riqi");


	}
	
	public static void download(String urlString, String filename,String savePath) throws Exception {
	    // 构造URL
	    URL url = new URL(urlString);
	    // 打开连接
	    URLConnection con = url.openConnection();
	    //设置请求超时为5s
	    con.setConnectTimeout(5*1000);
	    // 输入流
	    InputStream is = con.getInputStream();
	
	    // 4K的数据缓冲
	    byte[] bs = new byte[4096];
	    // 读取到的数据长度
	    int len;
	    // 输出的文件流
	   File sf=new File(savePath);
	   if(!sf.exists()){
		   sf.mkdirs();
	   }
	   OutputStream os = new FileOutputStream(sf.getPath()+"/"+filename);
	    // 开始读取
	    while ((len = is.read(bs)) != -1) {
	      os.write(bs, 0, len);
	    }
	    // 完毕，关闭所有链接
	    os.close();
	    is.close();
	} 

}
