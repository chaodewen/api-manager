package com.apilite.test;

import java.util.List;
import java.util.Map.Entry;

import org.bson.Document;
import org.junit.Test;

import com.apilite.db.MongoUtils;
import com.mongodb.client.model.Filters;

public class ServiceTest {
	@Test
	public void testBot() {
		// 从数据库取得Bot
		List<Document> documents = MongoUtils.findDocument("mop", "bot", Filters.eq("id", 1000));
		Document doc = documents.get(0);
		for(Entry<String, Object> entry : doc.entrySet()) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}
		System.out.println(doc);
		System.out.println(doc.toJson());
	}
	@Test
	public void testService() {
//		Service service = MongoUtils.getService("5718e87f87c748b15ee7425e");
//		System.out.println(service.getDocument());
	}
}