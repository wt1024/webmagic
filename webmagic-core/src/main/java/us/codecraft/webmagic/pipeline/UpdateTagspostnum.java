package us.codecraft.webmagic.pipeline;

import java.util.List;
import java.util.Map;

import us.codecraft.webmagic.pipeline.util.DBWtblogDAOImpl;

//更新每个标签包含的文章个数
public class UpdateTagspostnum {

	
	public void updatetag(){
		DBWtblogDAOImpl dao = new DBWtblogDAOImpl();
		
//		//更新当前标签对应的文章的个数
		String getc="SELECT COUNT(1)  cc,t2.term_id,t2.term_taxonomy_id "
				+"FROM wt_term_relationships  t1 "
				+"LEFT JOIN wt_term_taxonomy t2 ON  t1.term_taxonomy_id=t2.term_taxonomy_id "
				+"GROUP BY t2.term_id,t2.term_taxonomy_id ";
		List<Map<String, Object>> lst;
		try {
			lst = dao.findMapList(getc, null);
		
			for(int i=0; i<lst.size(); i++){
				String count = String.valueOf(lst.get(i).get("cc"));
				String term_id = String.valueOf(lst.get(i).get("term_id"));
				System.out.println(count+"-----------"+term_id);
				String updatesSQL="UPDATE wt_term_taxonomy SET COUNT='"+count+"' WHERE term_id="+term_id;
				dao.update(updatesSQL, null);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		
		new UpdateTagspostnum().updatetag();
		
	}
}
