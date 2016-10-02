package com.apilite.service;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ws.rs.FormParam;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.apilite.Utils;
import com.mongodb.client.model.Updates;

public class Service {
	@FormParam("id")
	public ObjectId _id;
	
	@FormParam("name")
	public String name;
	
	@FormParam("description")
	public String description;
	
	@FormParam("logo_url")
	public String logo_url;
	
	@FormParam("tag")
	public String tag;
	
	@FormParam("author")
	public String author;
	
	public Date created_time;
	
	public Date updated_time;
	
	@FormParam("language")
	public String language;
	
	@FormParam("subscriber_num")
	public String subscriber_num;
	
	@FormParam("requests_num")
	public String requests_num;
	
	@FormParam("star")
	public String star;
	
	@FormParam("category_id")
	public String category_id;
	
	@FormParam("container_id")
	public String container_id;
	
	@FormParam("apis")
	@JSONField(serialize=false)
	public String apisJSON;
	
	public List<API> apis;
	
	@FormParam("file")
	public InputStream fileStream;
	
	/**
	 * FastJson需要默认构造函数帮助解析
	 */
	public Service() {}
	
	/**
	 * 返回更新需要的Bson值，出错或无更新时返回null
	 * 不包括author, name, fileStream, apis
	 */
	public Bson genServiceDocumentUpdates() {
		try {
			updated_time = new Date();
			// 利用反射得到所有域
			List<Bson> updates = new ArrayList<Bson>();
			Field[] fields = Service.class.getDeclaredFields();
			for(Field field : fields) {
				String fieldName = field.getName();
				// 内容不空且不是无需更新的域
				if(field.get(this) != null && fieldName != "_id" 
						&& fieldName != "apisJSON"  
						&& fieldName != "fileStream" 
						&& fieldName != "apis" 
						&& fieldName != "author" 
						&& fieldName != "name") {
					updates.add(Updates.set(field.getName()
							, field.getType().cast(field.get(this))));
				}
			}
			return Updates.combine(updates);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 以Document类型返回对象，不包括文件流，出错时返回空的Document
	 */
	public Document getDocument() {
		Document ret = new Document();
		try {
			Field[] fields = Service.class.getDeclaredFields();
			for(Field field : fields) {
				String fieldName = field.getName();
				if(field.get(this) != null && fieldName != "apisJSON" 
						&& fieldName != "apis" && fieldName != "fileStream") {
					ret.put(field.getName(), field.getType().cast(field.get(this)));
				}
			}
			// 加入apis
			if(apis != null && !apis.isEmpty()) {
				List<Document> apisArray = new ArrayList<Document>();
				for(API api : apis) {
					apisArray.add(api.getDocument());
				}
				ret.put("apis", apisArray);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new Document();
		}
		return ret;
	}
	
	@Deprecated
	public JSONObject getJSONObject() {
		JSONObject ret = new JSONObject();
		try {
			Field[] fields = Service.class.getDeclaredFields();
			for(Field field : fields) {
				String fieldName = field.getName();
				if(field.get(this) != null && fieldName != "apis" && fieldName != "fileStream") {
					ret.put(field.getName(), field.getType().cast(field.get(this)));
				}
			}
			if(apis != null && !apis.isEmpty()) {
				JSONArray apisArray = new JSONArray();
				for(API api : apis) {
					apisArray.add(api.getJSONObject());
				}
				ret.put("apis", apisArray);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new JSONObject();
		}
		return ret;
	}
	
	/**
	 * 确保name, tag, author, language有非空值
	 * 确保文件流有值
	 * _id、created_time和updated_time不存在时自动生成
	 */
	public boolean canPost() {
		if(_id == null) {
			_id = new ObjectId();
		}
		if(created_time == null) {
			created_time = new Date();
			updated_time = created_time;
		}
		else if(updated_time == null){
			updated_time = new Date();
		}
		// 检查apis是否正确填写
		if(apis != null && !apis.isEmpty()) {
			for(API api : apis) {
				if(!api.canPost()) {
					return false;
				}
			}
		}
		// 检查必填项且上传文件不为空
		return Utils.isValid(name, tag, author, language) && (fileStream != null);
	}
	
	/**
	 * 确保_id有值且apis存在时格式正确
	 * 更新updated_time
	 */
	public boolean canPut() {
		updated_time = new Date();
		// 检查apis是否正确填写
		if(apis != null && !apis.isEmpty()) {
			for(API api : apis) {
				if(!api.canPost()) {
					return false;
				}
			}
		}
		// 检查必填项
		return _id != null;
	}
	
	/**
	 * 确保_id有值
	 */
	public boolean canDelete() {
		return _id != null;
	}

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLogo_url() {
		return logo_url;
	}

	public void setLogo_url(String logo_url) {
		this.logo_url = logo_url;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getCreated_time() {
		return created_time;
	}

	public void setCreated_time(Date created_time) {
		this.created_time = created_time;
	}

	public Date getUpdated_time() {
		return updated_time;
	}

	public void setUpdated_time(Date updated_time) {
		this.updated_time = updated_time;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getSubscriber_num() {
		return subscriber_num;
	}

	public void setSubscriber_num(String subscriber_num) {
		this.subscriber_num = subscriber_num;
	}

	public String getRequests_num() {
		return requests_num;
	}

	public void setRequests_num(String requests_num) {
		this.requests_num = requests_num;
	}

	public String getStar() {
		return star;
	}

	public void setStar(String star) {
		this.star = star;
	}

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	public String getContainer_id() {
		return container_id;
	}

	public void setContainer_id(String container_id) {
		this.container_id = container_id;
	}

	public String getApisJSON() {
		return apisJSON;
	}

	public void setApisJSON(String apisJSON) {
		this.apisJSON = apisJSON;
	}

	public List<API> getApis() {
		return apis;
	}

	public void setApis(List<API> apis) {
		this.apis = apis;
	}

	public InputStream getFileStream() {
		return fileStream;
	}

	public void setFileStream(InputStream fileStream) {
		this.fileStream = fileStream;
	}
}