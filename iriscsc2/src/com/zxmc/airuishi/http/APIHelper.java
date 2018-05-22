package com.zxmc.airuishi.http;

import java.util.HashMap;
import java.util.Map;

import com.xzmc.airuishi.base.C;
import com.xzmc.airuishi.https.WebService;
import com.xzmc.airuishi.utils.Utils;


public class APIHelper {
	Map<String,String> param;


	public APIHelper() {
		param = new HashMap<String, String>();
	}

	public String getNewsCatergory() {
		param.clear();
		return new WebService(C.GETCATEGORY, param).getReturnInfo();
	}


	public String getTopPosts() {
		param.clear();
		return new WebService(C.GETTOPNEWS, param).getReturnInfo();

	}

	public String searchPosts(String keyWord, int page, int count) {
		return "";
	}

	public String getPostsByCategory(String id, int page, int count) {
		param.clear();
		param.put("userID",Utils.getID());
		param.put("category", id);
		param.put("page", page+"");
		param.put("count", count+"");
		return new WebService(C.GETNEWSLIST, param).getReturnInfo();
	}
	
	public String replyComment(String id,String content,String newsId,String touserID){
		param.clear();
		param.put("newsID", newsId);
		param.put("userID", Utils.getID());
		param.put("touserID", touserID);
		param.put("commentID", id);
		param.put("content", content);
		return new WebService(C.REPLYCOMMENT, param).getReturnInfo();
	}

}
