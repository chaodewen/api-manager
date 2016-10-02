package com.apilite.db;

import com.apilite.settings.MongoSettings;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoDatabase;

/**
 * 部分实现有误，不推荐使用
 */
@Deprecated
public class AsyncMongoConnectionFactory {
	private static MongoClient mongoClient = null;
	
	private AsyncMongoConnectionFactory() {
		super();
	}
	
	public static synchronized MongoClient getMongoClient() {
		if (mongoClient == null) {
			mongoClient = MongoClients.create(MongoSettings.CONNECTION_STRING);
		}
		return mongoClient;
	}
	
	public static MongoDatabase getDatabase(String databaseName) {
		return AsyncMongoConnectionFactory.getMongoClient().getDatabase(databaseName);
	}
}