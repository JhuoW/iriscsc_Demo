package com.xzmc.airuishi.https;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.xzmc.airuishi.base.App;
import com.xzmc.airuishi.bean.Answer;
import com.xzmc.airuishi.bean.Comment;
import com.xzmc.airuishi.bean.ContactUser;
import com.xzmc.airuishi.bean.Doc;
import com.xzmc.airuishi.bean.Lesson;
import com.xzmc.airuishi.bean.Notify;
import com.xzmc.airuishi.bean.PostModel;
import com.xzmc.airuishi.bean.QXUser;
import com.xzmc.airuishi.bean.Question;
import com.xzmc.airuishi.bean.QuestionTitle;
import com.xzmc.airuishi.bean.Reply;
import com.xzmc.airuishi.bean.RequestUser;
import com.xzmc.airuishi.bean.Student;
import com.xzmc.airuishi.service.PreferenceMap;

public class GetObjectFromService {
	static PreferenceMap preference = new PreferenceMap(App.ctx);
	static Map<String, String> result = new HashMap<String, String>();

	/**
	 * 获取简单的json结果
	 * 
	 * @param jsonstr
	 * @return
	 */
	public static Boolean getSimplyResult(String jsonstr) {
		Boolean flag = false;
		try {
			JSONObject json = new JSONObject(jsonstr);
			if (!json.getString("ret").equals("success")) {
				return flag;
			} else {
				flag = true;
			}
		} catch (Exception e) {
		}
		return flag;

	}

	/**
	 * 获取登录结果
	 * 
	 * @param jsonstr
	 * @param number
	 * @param password
	 * @return
	 */
	public static Map<String, Boolean> getLoginResult(String jsonstr) {
		final Map<String, Boolean> result = new HashMap<String, Boolean>();
		result.put("result", true);
		try {
			final JSONObject json = new JSONObject(jsonstr);
			if (json.get("ret").equals("success")) {
				final String id = json.getString("ID");
				final String name = json.getString("nickName");
				final String image = json.getString("imgUrl");
				final String sex = json.getString("sex");
				final String address = json.getString("address");
				final String authority = json.getString("authority");
				final boolean IsCustomer = json.getBoolean("IsCustomer");
				final boolean IsOpto = json.getBoolean("IsOpto");
				final boolean IsBusiness = json.getBoolean("IsBusiness");
				final boolean IsStudent = json.getBoolean("IsStudent");
				final boolean IsSuperOpto = json.getBoolean("IsSuperOpto");
				if (id.isEmpty() || name.isEmpty()) {
					result.put("result", false);
					return result;
				} else {
					QXUser user = new QXUser();
					user.setAddress(address);
					user.setAuthority(authority);
					user.setID(id);
					user.setImage(image);
					user.setName(name);
					user.setSex(sex);
					user.setIsCustomer(IsCustomer);
					user.setIsOpto(IsOpto);
					user.setIsBusiness(IsBusiness);
					user.setIsStudent(IsStudent);
					user.setIsSuperOpto(IsSuperOpto);
					preference.setUser(user);
				}
			} else {
				result.put("result", false);
			}
		} catch (Exception e) {
			result.put("result", false);
		}
		return result;
	}

	public static List<QXUser> getSearchResult(String jsonstr) {
		List<QXUser> result = new ArrayList<QXUser>();
		try {
			JSONObject json = new JSONObject(jsonstr);
			if (json.get("ret").equals("success")) {
				JSONArray jsonarray = json.getJSONArray("userSearchList");
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject child = jsonarray.getJSONObject(i);
					QXUser user = new QXUser();
					user.setID(child.getString("ID"));
					user.setName(child.getString("nickName"));
					user.setImage(child.getString("imgUrl"));
					user.setAddress(child.getString("address"));
					user.setSex(child.getString("sex"));
					result.add(user);
				}
			}
		} catch (Exception e) {
		}
		return result;
	}

	public static Notify getNotificationDetail(String jsonstr) {
		Notify notify = new Notify();
		try {
			JSONObject json = new JSONObject(jsonstr);
			String type = json.getString("type");
			notify.setType(type);

			if (type.equals("RequestAddFriend")) {
				notify.setTitle("好友请求");
				notify.setContent(json.getString("nickname") + "想要添加你为好友");
			} else if (type.equals("PushNews")) {
				notify.setTitle(json.getString("title"));
				notify.setContent(json.getString("newsID"));
			}else if(type.equals("110")){
				notify.setTitle("有新的课程");
				notify.setContent("");
			}

		} catch (Exception e) {
		}
		return notify;
	}

	public static List<RequestUser> getRequestUser(String jsonstr) {
		List<RequestUser> data = new ArrayList<RequestUser>();
		try {
			JSONObject json = new JSONObject(jsonstr);
			if (json.get("ret").equals("success")) {
				JSONArray jsonarray = json.getJSONArray("newFriendList");
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject child = jsonarray.getJSONObject(i);
					RequestUser user = new RequestUser();
					user.setID(child.getString("ID"));
					user.setImage(child.getString("imgUrl"));
					user.setName(child.getString("nickName"));
					user.setSex(child.getString("sex"));
					user.setStatus(child.getString("status"));
					user.setRemark(child.getString("remarks"));
					// child.getString("address")
					data.add(user);
				}
			}
		} catch (Exception e) {
		}
		return data;
	}

	public static List<QXUser> getFriend(String jsonstr) {
		List<QXUser> list = new ArrayList<QXUser>();
		try {
			JSONObject json = new JSONObject(jsonstr);
			if (json.get("ret").equals("success")) {
				JSONArray jsonarray = json.getJSONArray("userFriendList");
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject child = jsonarray.getJSONObject(i);
					QXUser user = new QXUser();
					user.setID(child.getString("ID"));
					user.setSex(child.getString("sex"));
					user.setImage(child.getString("imgUrl"));
					user.setName(child.getString("nickName"));
					if (child.getString("isProfessor").equals("true")) {
						user.setName("【专家】" + child.getString("nickName"));
					}
					list.add(user);
				}
			}
		} catch (Exception e) {
		}
		return list;
	}

	public static List<ContactUser> getContactUser(String jsonstr) {
		List<ContactUser> contactuser = new ArrayList<ContactUser>();
		try {
			JSONObject json = new JSONObject(jsonstr);
			if (json.get("ret").equals("success")) {
				JSONArray array = json.getJSONArray("contactsList");
				for (int i = 0; i < array.length(); i++) {
					JSONObject child = array.getJSONObject(i);
					ContactUser user = new ContactUser();
					user.setId(child.getString("ID"));
					user.setImage("");
					user.setName(child.getString("name"));
					user.setPhone(child.getString("phone"));
					user.setStatue(child.getString("status"));
					contactuser.add(user);
				}
			}
		} catch (Exception e) {
		}
		return contactuser;
	}

	public static QXUser getQXUser(String jsonstr) {
		QXUser user = new QXUser();
		try {
			JSONObject json = new JSONObject(jsonstr);
			if (json.get("ret").equals("success")) {
				user.setAddress(json.getString("address"));
				user.setID(json.getString("ID"));
				user.setImage(json.getString("imgUrl"));
				user.setName(json.getString("nickName"));
				user.setSex(json.getString("sex"));
				user.setIsProfessor(json.getString("isProfessor"));
				user.setProfessorInfo(json.getString("professorInfo"));
				user.setOnlineTime(json.getString("onlineTime"));
			} else {
				return null;
			}
		} catch (Exception e) {
		}
		return user;
	}

	public static List<Comment> getCommentList(String jsonstr) {
		List<Comment> data = new ArrayList<Comment>();
		try {
			JSONObject json = new JSONObject(jsonstr);
			if (json.get("ret").equals("success")) {
				JSONArray jsonarray = json.getJSONArray("comments");
				for (int i = 0; i < jsonarray.length(); i++) {
					Comment comment = new Comment();
					JSONObject obj = jsonarray.getJSONObject(i);
					comment.setUserId(obj.getString("userID"));
					comment.setNickname(obj.getString("nickName"));
					comment.setPicture(obj.getString("picture"));
					comment.setAuthority(obj.getString("authority"));
					comment.setContent(obj.getString("content"));
					comment.setTime(obj.getString("time"));
					comment.setId(obj.getString("id"));
					comment.setStatus(obj.getString("status"));
					comment.setAcceptId(obj.getString("acceptID"));
					comment.setTonickname(obj.getString("tonickname"));
					comment.setTopicture(obj.getString("topicture"));
					comment.setContentId(obj.getString("contentID"));
					comment.setToauthority(obj.getString("toauthority"));
					data.add(comment);
				}
				// JSONArray jsonarray2 = json.getJSONArray("tocomments");
				// for (int i = 0; i < jsonarray2.length(); i++) {
				// Comment comment = new Comment();
				// JSONObject obj = jsonarray2.getJSONObject(i);
				// comment.setUserId(obj.getString("userID"));
				// comment.setNickname(obj.getString("nickName"));
				// comment.setPicture(obj.getString("picture"));
				// comment.setAuthority(obj.getString("authority"));
				// comment.setContent(obj.getString("content"));
				// comment.setTime(obj.getString("time"));
				// comment.setId(obj.getString("id"));
				// comment.setStatus(obj.getString("status"));
				// comment.setAcceptId(obj.getString("acceptID"));
				// comment.setTonickname(obj.getString("tonickname"));
				// comment.setTopicture(obj.getString("topicture"));
				// comment.setContentId(obj.getString("contentID"));
				// comment.setToauthority(obj.getString("toauthority"));
				// data.add(comment);
				// }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	public static List<PostModel> getCollectionNews(String jsonstr) {
		List<PostModel> datas = new ArrayList<PostModel>();
		try {
			JSONObject json = new JSONObject(jsonstr);
			if (json.get("ret").equals("success")) {
				JSONArray jsonarray = json.getJSONArray("collectionList");
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject obj = jsonarray.getJSONObject(i);
					PostModel post = new PostModel();
					post.setCollection("true");
					post.setComment_count(obj.getString("commentCount"));
					post.setContent(obj.getString("description"));
					post.setId(obj.getString("newsID"));
					post.setImageurl(obj.getString("imgUrl"));
					post.setSource(obj.getString("source"));
					post.setTime(obj.getString("time"));
					post.setTitle(obj.getString("title"));
					post.setView_count(obj.getString("viewCount"));
					datas.add(post);
				}
			}
		} catch (Exception e) {
		}
		return datas;
	}

	public static List<QXUser> getClubData(String jsonstr) {
		List<QXUser> data = null;
		try {
			JSONObject json = new JSONObject(jsonstr);
			if (json.get("ret").equals("success")) {
				data = new ArrayList<QXUser>();
				JSONArray jsonarray = json.getJSONArray("userFriendList");
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject child = jsonarray.getJSONObject(i);
					QXUser user = new QXUser();
					user.setID(child.getString("ID"));
					user.setName(child.getString("nickName"));
					user.setImage(child.getString("imgUrl"));
					user.setAddress(child.getString("address"));
					data.add(user);
				}
			}
		} catch (Exception e) {
			data = null;
		}
		return data;
	}

	public static List<Reply> getReplyList(String jsonStr) {
		List<Reply> datas = null;
		try {
			JSONObject json = new JSONObject(jsonStr);
			if (json.get("ret").equals("success")) {
				datas = new ArrayList<Reply>();
				JSONArray jsonarray = json.getJSONArray("replyList");
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject child = jsonarray.getJSONObject(i);
					Reply reply = new Reply();
					reply.setContent(child.getString("content"));
					reply.setId(child.getString("id"));
					reply.setReplyName(child.getString("replyName"));
					reply.setReplyuserId(child.getString("replyUserId"));
					reply.setSendName(child.getString("sendName"));
					reply.setSenduserId(child.getString("sendUserId"));
					datas.add(reply);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return datas;
	}

	public static List<Reply> getReplyListByJsonArray(String replyList) {
		List<Reply> datas = new ArrayList<Reply>();
		try {
			JSONArray jsonarray = new JSONArray(replyList);
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject child = jsonarray.getJSONObject(i);
				Reply reply = new Reply();
				reply.setContent(child.getString("content"));
				reply.setId(child.getString("id"));
				reply.setReplyName(child.getString("replyName"));
				reply.setReplyuserId(child.getString("replyUserId"));
				reply.setSendName(child.getString("sendName"));
				reply.setSenduserId(child.getString("sendUserId"));
				datas.add(reply);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return datas;
	}

	public static List<QuestionTitle> getQuestionList(String jsonstr) {
		List<QuestionTitle> datas = new ArrayList<QuestionTitle>();
		try {
			JSONObject json = new JSONObject(jsonstr);
			if (json.get("ret").equals("success")) {
				JSONArray jsonarray = json.getJSONArray("questionList");
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject child = jsonarray.getJSONObject(i);
					QuestionTitle item = new QuestionTitle();
					String id = child.getString("questionId");
					String title = child.getString("title");
					boolean isDone = child.getBoolean("isAnswered");
					item.setId(id);
					item.setTitle(title);
					item.setDone(isDone);
					datas.add(item);
				}
			}
		} catch (Exception e) {
			datas = null;
		}
		return datas;
	}

	public static List<Question> getQuestion(String jsonstr) {
		List<Question> datas = new ArrayList<Question>();
		try {
			JSONObject json = new JSONObject(jsonstr);
			if (json.get("ret").equals("success")) {
				JSONArray jsonarray = json.getJSONArray("problemList");
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject child = jsonarray.getJSONObject(i);
					Question question = new Question();
					List<Answer> answerlist = new ArrayList<Answer>();
					question.setId(child.getString("problemId"));
					question.setQuestion(child.getString("problem"));
					question.setType(child.getInt("type"));
					question.setAnswer(false);
					JSONArray resultarray = child.getJSONArray("resultList");
					for (int j = 0; j < resultarray.length(); j++) {
						JSONObject resultobj = resultarray.getJSONObject(j);
						Answer answer = new Answer();
						answer.setId(resultobj.getString("resultId"));
						answer.setSelection(resultobj.getString("select"));
						answer.setSelectionContent(resultobj
								.getString("answer"));
						answer.setSelected(false);
						answerlist.add(answer);
					}
					question.setAnswers(answerlist);

					datas.add(question);
				}
			}
		} catch (Exception e) {
			datas = null;
		}
		return datas;
	}

	public static List<Doc> getMyOptDoc(JSONArray jsonarray) {
		List<Doc> datas = new ArrayList<Doc>();
		try {
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject json = jsonarray.getJSONObject(i);
				Doc d = new Doc();
				d.setOptometryId(json.getString("optometryId"));
				d.setTime(json.getString("time"));
				d.setSL_L(json.getString("SL_L"));
				d.setSL_R(json.getString("SL_R"));
				d.setCL_L(json.getString("CL_L"));
				d.setCL_R(json.getString("CL_R"));
				d.setAxial_L(json.getString("Axial_L"));
				d.setAxial_R(json.getString("Axial_R"));
				d.setNv_L(json.getString("Nv_L"));
				d.setNv_R(json.getString("Nv_R"));
				d.setCv_L(json.getString("Cv_L"));
				d.setCv_R(json.getString("Cv_R"));
				d.setAD(json.getString("AD"));
				d.setPD(json.getString("PD"));
				datas.add(d);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return datas;
	}

	public static List<PostModel> getPostModel(JSONArray jsonarray) {
		List<PostModel> datas = new ArrayList<PostModel>();
		try {
			for (int i = 0; i < jsonarray.length(); i++) {
				PostModel pm = new PostModel();
				JSONObject obj = jsonarray.getJSONObject(i);
				String id = obj.getString("newsID");
				String source = obj.getString("source");
				String title = obj.getString("title");
				String content = obj.getString("description");
				String imgurl = obj.getString("imgUrl");
				String time = obj.getString("time");
				String comment_count = obj.getString("commentCount");
				String view_count = obj.getString("viewCount");
				String collection_count = obj.getString("collectionCount");
				String iscollection = obj.getString("collection");
				pm.setId(id);
				pm.setSource(source);
				pm.setTitle(title);
				pm.setContent(content);
				pm.setImageurl(imgurl);
				pm.setTime(time);
				pm.setComment_count(comment_count);
				pm.setView_count(view_count);
				pm.setCollection_count(collection_count);
				pm.setCollection(iscollection);
				datas.add(pm);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return datas;
	}

	// 获取我发起的课程
	public static List<Lesson> getLessonList(JSONArray jsonarray) {
		List<Lesson> datas = new ArrayList<Lesson>();
		try {
			for (int i = 0; i < jsonarray.length(); i++) {
				Lesson l = new Lesson();
				JSONObject obj = jsonarray.getJSONObject(i);
				l.setCourseId(obj.getString("courseId"));
				l.setReleaseTime(obj.getString("releaseTime"));
				l.setLectureTime(obj.getString("lectureTime"));
				l.setTitle(obj.getString("title"));
				l.setDescription(obj.getString("description"));
				l.setUserCount(obj.getString("userCount"));
				l.setPoint(obj.getString("point"));
				l.setLink(obj.getString("link"));
				l.setTag(obj.getString("tag"));
				l.setStatus(obj.getString("status"));
				if (l.getTag().equals("111")) {
					datas.add(l);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return datas;
	}
	
	// 获取我收到的课程
	public static List<Lesson> getLessonList_receive(JSONArray jsonarray) {
		List<Lesson> datas = new ArrayList<Lesson>();
		try {
			for (int i = 0; i < jsonarray.length(); i++) {
				Lesson l = new Lesson();
				JSONObject obj = jsonarray.getJSONObject(i);
				l.setCourseId(obj.getString("courseId"));
				l.setReleaseTime(obj.getString("releaseTime"));
				l.setLectureTime(obj.getString("lectureTime"));
				l.setTitle(obj.getString("title"));
				l.setDescription(obj.getString("description"));
				l.setUserCount(obj.getString("userCount"));
				l.setPoint(obj.getString("point"));
				l.setLink(obj.getString("link"));
				l.setTag(obj.getString("tag"));
				l.setStatus(obj.getString("status"));
				if (l.getTag().equals("112")) {
					datas.add(l);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return datas;
	}
	
	public static List<Student> getCustome(JSONArray jsonarray){
		List<Student> datas = new ArrayList<Student>();
		try {
			for (int i = 0; i < jsonarray.length(); i++) {
				Student s = new Student();
				JSONObject obj = jsonarray.getJSONObject(i);
				s.setUserId(obj.getString("userId"));
				s.setNickName(obj.getString("nickName"));
				s.setImgUrl(obj.getString("imgUrl"));
				s.setSex(obj.getString("sex"));
				s.setPhone(obj.getString("phone"));
				s.setStatue(obj.getString("status"));
				s.setId(obj.getString("id"));
				datas.add(s);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return datas;
	}
}
