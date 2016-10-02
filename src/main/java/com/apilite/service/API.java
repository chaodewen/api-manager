package com.apilite.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.FormParam;

import org.bson.Document;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.apilite.Utils;

public class API {
	@FormParam("id")
	public String id;
	
	@FormParam("name")
	public String name;
	
	@FormParam("description")
	public String description;
	
	@FormParam("lan_path")
	public String lan_path;
	
	@FormParam("lan_host")
	public String lan_host;
	
	@FormParam("lan_port")
	public String lan_port;
	
	@FormParam("method")
	public String method;
	
	@FormParam("args")
	public List<Arg> args;
	
	@FormParam("status")
	public List<Status> status;
	
	/**
	 * FastJson需要默认构造函数帮助解析
	 */
	public API() {}
	
	/**
	 * 以Document类型返回对象，出错时返回空的Document
	 */
	public Document getDocument() {
		Document ret = new Document();
		try {
			Field[] fields = API.class.getDeclaredFields();
			for(Field field : fields) {
				String fieldName = field.getName();
				if(field.get(this) != null && fieldName != "args" && fieldName != "status") {
					ret.put(field.getName(), field.getType().cast(field.get(this)));
				}
			}
			// 加入args
			if(args != null && !args.isEmpty()) {
				List<Document> argsArray = new ArrayList<Document>();
				for(Arg arg : args) {
					argsArray.add(arg.getDocument());
				}
				ret.put("args", argsArray);
			}
			// 加入status
			if(status != null && !status.isEmpty()) {
				List<Document> statusArray = new ArrayList<Document>();
				for(Status stat : status) {
					statusArray.add(stat.getDocument());
				}
				ret.put("status", statusArray);
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
				if(field.get(this) != null && fieldName != "args" && fieldName != "status") {
					ret.put(field.getName(), field.getType().cast(field.get(this)));
				}
			}
			// 加入args
			if(args != null && !args.isEmpty()) {
				JSONArray argsArray = new JSONArray();
				for(Arg arg : args) {
					argsArray.add(arg.getJSONObject());
				}
				ret.put("args", argsArray);
			}
			// 加入status
			if(status != null && !status.isEmpty()) {
				JSONArray statusArray = new JSONArray();
				for(Status stat : status) {
					statusArray.add(stat.getJSONObject());
				}
				ret.put("status", statusArray);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new JSONObject();
		}
		return ret;
	}
	
	/**
	 * id不存在时生成id
	 * 确保name, lan_path, lan_port, method有非空值
	 * 确保args, status存在时内容格式正确
	 */
	public boolean canPost() {
		if(id == null || id.isEmpty()) {
			id = Utils.genUUID();
		}
		// 检查args是否都填写完整
		if(args != null && !args.isEmpty()) {
			for(Arg arg : args) {
				if(!arg.canPost()) {
					return false;
				}
			}
		}
		// 检查status是否都填写完整
		if(status != null && !status.isEmpty()) {
			for(Status stat : status) {
				if(!stat.canPost()) {
					return false;
				}
			}
		}
		// 最后检查剩下的项
		return Utils.isValid(name, lan_path, lan_port, method);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getLan_path() {
		return lan_path;
	}

	public void setLan_path(String lan_path) {
		this.lan_path = lan_path;
	}

	public String getLan_host() {
		return lan_host;
	}

	public void setLan_host(String lan_host) {
		this.lan_host = lan_host;
	}

	public String getLan_port() {
		return lan_port;
	}

	public void setLan_port(String lan_port) {
		this.lan_port = lan_port;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public List<Arg> getArgs() {
		return args;
	}

	public void setArgs(List<Arg> args) {
		this.args = args;
	}

	public List<Status> getStatus() {
		return status;
	}

	public void setStatus(List<Status> status) {
		this.status = status;
	}
}