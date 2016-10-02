package com.apilite.service;

import java.lang.reflect.Field;

import javax.ws.rs.FormParam;

import org.bson.Document;

import com.alibaba.fastjson.JSONObject;
import com.apilite.Utils;

public class Arg {
	@FormParam("name")
	public String name;
	
	@FormParam("type")
	public String type;
	
	@FormParam("description")
	public String description;
	
	@FormParam("location")
	public String location;
	
	@FormParam("required")
	public boolean required;
	
	@FormParam("default_value")
	public String default_value;
	
	/**
	 * FastJson需要默认构造函数帮助解析
	 */
	public Arg() {}
	
	/**
	 * 以Document类型返回对象，出错时返回空的Document
	 */
	public Document getDocument() {
		Document ret = new Document();
		try {
			Field[] fields = Arg.class.getDeclaredFields();
			for(Field field : fields) {
				if(field.get(this) != null) {
					if(field.getName() == "required") {
						ret.put(field.getName(), (boolean) field.get(this));
					}
					else {
						ret.put(field.getName(), field.getType().cast(field.get(this)));
					}
				}
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
				if(field.get(this) != null) {
					ret.put(field.getName(), field.getType().cast(field.get(this)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new JSONObject();
		}
		return ret;
	}
	
	/**
	 * 确保name, type, location, required有非空值
	 */
	public boolean canPost() {
		// required一定有值所以无需检查
		return Utils.isValid(name, type, location);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getDefault_value() {
		return default_value;
	}

	public void setDefault_value(String default_value) {
		this.default_value = default_value;
	}
}