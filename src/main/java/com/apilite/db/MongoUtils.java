package com.apilite.db;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.mindrot.jbcrypt.BCrypt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.apilite.service.Service;
import com.apilite.settings.MongoSettings;
import com.apilite.user.User;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class MongoUtils {
	/**
	 * 插入一个Document
	 */
	public static boolean insertDocument(String databaseName
			, String collectionName, Document document) {
		try {
			MongoDatabase mongodb = MongoConnectionFactory
					.getDatabase(databaseName);
			mongodb.getCollection(collectionName).insertOne(
					document);
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 根据filter条件返回List集合，并用projection过滤
	 * 例如filter为Filters.eq("author", author)
	 * projection为Projections.fields(include("api")))
	 */
	public static List<Document> findDocument(String databaseName
			, String collectionName, Bson filter, int limit
			, int skip) {
		MongoDatabase database = MongoConnectionFactory
				.getDatabase(databaseName);
		FindIterable<Document> iterable = database
				.getCollection(collectionName)
				.find(filter)
				.skip(skip)
				.limit(limit);
		return genList(iterable);
	}
	
	/**
	 * 根据filter条件返回List集合，并用projection过滤
	 * 例如filter为Filters.eq("author", author)
	 * projection为Projections.fields(include("api")))
	 */
	public static List<Document> findDocument(String databaseName
			, String collectionName, Bson filter, Bson projection) {
		MongoDatabase database = MongoConnectionFactory
				.getDatabase(databaseName);
		FindIterable<Document> iterable = database
				.getCollection(collectionName).find(filter)
				.projection(projection);
		return genList(iterable);
	}
	
	/**
	 * 根据filter条件返回List集合
	 * 例如filter为Filters.eq("author", author)
	 */
	public static List<Document> findDocument(String databaseName
			, String collectionName, Bson filter) {
		MongoDatabase database = MongoConnectionFactory
				.getDatabase(databaseName);
		FindIterable<Document> iterable = database
				.getCollection(collectionName).find(filter);
		return genList(iterable);
	}
	
	/**
	 * 对所有符合filter的字段进行update操作
	 */
	public static boolean updateDocument(String databaseName
			, String collectionName, Bson filter, Bson update) {
		try {
			MongoDatabase database = MongoConnectionFactory
					.getDatabase(databaseName);
			database.getCollection(collectionName).updateMany(
					filter, update);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * 删除所有符合filter的字段
	 */
	public static boolean deleteDocument(String databaseName
			, String collectionName, Bson filter) {
		try {
			MongoDatabase database = MongoConnectionFactory
					.getDatabase(databaseName);
			database.getCollection(collectionName).deleteMany(
					filter);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * 根据FindIterable生成List
	 */
	public static <T> List<T> genList(FindIterable<T> iterable) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<T> results = new ArrayList<T>();
		iterable.forEach(new Block<T> () {
			@SuppressWarnings("unchecked")
			@Override
			public void apply(T t) {
				if(t instanceof Document) {
					Document doc = (Document) t;
					if(doc.containsKey("_id")) {
						final ObjectId id = (ObjectId)doc.get("_id");
						doc.put("_id", id.toHexString());
					}
					if(doc.containsKey("created_time")) {
						Date date = (Date) doc.get("created_time");
						doc.put("created_time", dateFormat.format(date));
					}
					if(doc.containsKey("updated_time")) {
						Date date = (Date) doc.get("updated_time");
						doc.put("updated_time", dateFormat.format(date));
					}
					results.add((T) doc);
				}
				else 
					results.add(t);
			}
		});
		return results;
	}
	
	/**
	 * 生成用于数据库中存放的Hash后的密码
	 */
	public static String genHashPassword(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt(12));
	}
	
	/**
	 * 检查输入password是否正确
	 */
	public static boolean verfyHashPassword(String password, String hashPassword) {
		return BCrypt.checkpw(password, hashPassword);
	}
	
	/**
	 * 通过_id得到Service对象，若对象为多个或不存在则返回null
	 */
	public static Service getService(String _id) {
		// 从数据库取得Service
		List<Document> documents = MongoUtils.findDocument(
				MongoSettings.DATABESE_NAME, MongoSettings.SERVICE_COLLECTION_NAME
				, Filters.eq("_id", new ObjectId(_id)));
		if(documents.size() == 1) {
			Document doc = documents.get(0);
			System.out.println(doc);
			System.out.println(doc.toJson());
			JSONObject json = JSON.parseObject(documents.get(0).toJson());
			json.put("_id", new ObjectId(json.getString("_id")));
			Service service = JSON.parseObject(json.toJSONString(), Service.class);
			service._id = new ObjectId(json.getString("_id"));
			return service;
		}
		else {
			return null;
		}
	}
	
	/**
	 * 通过_id得到User对象，若对象为多个或不存在则返回null
	 */
	public static User getUser(String _id) {
		// 从数据库取得Service
		List<Document> documents = MongoUtils.findDocument(
				MongoSettings.DATABESE_NAME, MongoSettings.SERVICE_COLLECTION_NAME
				, Filters.eq("_id", new ObjectId(_id)));
		if(documents.size() == 1) {
			JSONObject json = JSON.parseObject(documents.get(0).toJson());
			return JSON.parseObject(json.toJSONString(), User.class);
		}
		else {
			return null;
		}
	}
}