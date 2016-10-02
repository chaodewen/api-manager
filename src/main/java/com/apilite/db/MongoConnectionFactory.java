package com.apilite.db;

import com.apilite.settings.MongoSettings;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class MongoConnectionFactory {
	private static MongoClient mongoClient = null;
	
	private MongoConnectionFactory() {
		super();
	}
	
	public static synchronized MongoClient getMongoClient() {
		if (mongoClient == null) {
			mongoClient = new MongoClient(new MongoClientURI(MongoSettings.CONNECTION_STRING));
		}
		return mongoClient;
	}
	
	public static MongoDatabase getDatabase(String databaseName) {
		return MongoConnectionFactory.getMongoClient().getDatabase(databaseName);
	}
}
