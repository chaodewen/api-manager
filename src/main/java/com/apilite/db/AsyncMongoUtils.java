package com.apilite.db;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.Block;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.FindIterable;
import com.mongodb.async.client.MongoDatabase;

/**
 * 部分实现有误，不推荐使用
 */
@Deprecated
public class AsyncMongoUtils {
	/**
	 * 插入一个Document
	 */
	public static boolean insertDocumentAsync(String databaseName
			, String collectionName, Document document) {
		try {
			MongoDatabase mongodb = AsyncMongoConnectionFactory
					.getDatabase(databaseName);
			mongodb.getCollection(collectionName).insertOne(
					document, null);
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
	public static List<Document> findDocumentAsync(String databaseName
			, String collectionName, Bson filter, int limit
			, int skip) {
		MongoDatabase database = AsyncMongoConnectionFactory
				.getDatabase(databaseName);
		FindIterable<Document> iterable = database
				.getCollection(collectionName)
				.find(filter)
				.skip(skip)
				.limit(limit);
		return genListAsync(iterable);
	}
	/**
	 * 根据filter条件返回List集合，并用projection过滤
	 * 例如filter为Filters.eq("author", author)
	 * projection为Projections.fields(include("api")))
	 */
	public static List<Document> findDocumentAsync(String databaseName
			, String collectionName, Bson filter, Bson projection) {
		MongoDatabase database = AsyncMongoConnectionFactory
				.getDatabase(databaseName);
		FindIterable<Document> iterable = database
				.getCollection(collectionName).find(filter)
				.projection(projection);
		return genListAsync(iterable);
	}
	/**
	 * 根据filter条件返回List集合
	 * 例如filter为Filters.eq("author", author)
	 */
	public static List<Document> findDocumentAsync(String databaseName
			, String collectionName, Bson filter) {
		MongoDatabase database = AsyncMongoConnectionFactory
				.getDatabase(databaseName);
		FindIterable<Document> iterable = database
				.getCollection(collectionName).find(filter);
		return genListAsync(iterable);
	}
	/**
	 * 对所有符合filter的字段进行update操作
	 */
	public static boolean updateDocumentAsync(String databaseName
			, String collectionName, Bson filter, Bson update) {
		try {
			MongoDatabase database = AsyncMongoConnectionFactory
					.getDatabase(databaseName);
			database.getCollection(collectionName).updateMany(
					filter, update, null);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	/**
	 * 删除所有符合filter的字段
	 */
	public static boolean deleteDocumentAsync(String databaseName
			, String collectionName, Bson filter) {
		try {
			MongoDatabase database = AsyncMongoConnectionFactory
					.getDatabase(databaseName);
			database.getCollection(collectionName).deleteMany(
					filter, null);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	/**
	 * ※※※ 方法有误，因为异步操作所以find的结果无法直接传送至此 ※※※
	 * ※※※ 现象 ※※※ iterable没有值
	 * ※※※ 现象 ※※※ 断点调试因为有等待时间，此时正常返回结果
	 * ※※※ 改进 ※※※ MongoDB原生驱动无法使用Future，第三方如allanbank的就可以
	 * 根据FindIterable生成List
	 */
	@Deprecated
	public static <T> List<T> genListAsync(FindIterable<T> iterable) {
		List<T> results = new ArrayList<T>();
		iterable.forEach(new Block<T> () {
			@Override
			public void apply(T t) {
				results.add(t);
			}
		}, new SingleResultCallback<Void> () {
			@Override
			public void onResult(Void result, Throwable t) {
				// TODO Auto-generated method stub
				return;
			}
		});
		return results;
	}
}
