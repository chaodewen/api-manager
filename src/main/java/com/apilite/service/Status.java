package com.apilite.service;

import java.lang.reflect.Field;

import javax.ws.rs.FormParam;

import org.bson.Document;

import com.alibaba.fastjson.JSONObject;
import com.apilite.Utils;

public class Status {
	@FormParam("code")
	public int code;
	
	@FormParam("message")
	public String message;
	
	/**
	 * FastJson需要默认构造函数帮助解析
	 */
	public Status() {}
	
	/**
	 * 以Document类型返回对象，出错时返回空的Document
	 */
	public Document getDocument() {
		Document ret = new Document();
		try {
			Field[] fields = Status.class.getDeclaredFields();
			for(Field field : fields) {
				if(field.get(this) != null) {
					if(field.getName() == "code") {
						ret.put(field.getName(), (int) field.get(this));
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
	 * 确保message, code有非空值，且code在[100, 600)内
	 */
	public boolean canPost() {
		return Utils.isValid(message) && (code >= 100 && code < 600);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}