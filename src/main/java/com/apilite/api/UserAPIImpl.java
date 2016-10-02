package com.apilite.api;

import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;

import com.alibaba.fastjson.JSONArray;
import com.apilite.Utils;
import com.apilite.db.MongoUtils;
import com.apilite.settings.MongoSettings;
import com.apilite.user.User;
import com.mongodb.client.model.Filters;

public class UserAPIImpl implements IUserAPI {
	@Override
	public Response postUser(User user) {
		Response response;
		if (user.canPost()) {
			System.out.println(user.getDocument());
			if (MongoUtils.insertDocument(MongoSettings.DATABESE_NAME,
					MongoSettings.USER_COLLECTION_NAME, user.getDocument())) {
				response = Utils.genResponse(200, "Success");
			} else {
				response = Utils.genResponse(500, "Server Runtime Error");
			}
		} else {
			response = Utils.genResponse(400, "Param Error");
		}
		return response;
	}

	@Override
	public Response getUser(String account) {
		System.out.println("here");
		Response response;
		try {
			JSONArray user = Utils.getJSONArray(MongoUtils.findDocument(
					MongoSettings.DATABESE_NAME,
					MongoSettings.USER_COLLECTION_NAME,
					Filters.eq("account", account)));
			response = Utils.genResponse(200, user);
		} catch (Exception e) {
			e.printStackTrace();
			response = Utils.genResponse(500, "Server Runtime Error");
		}

		return response;
	}

	@Override
	public Response putUser(User user) {
		Response response;
		if (user.canPut()) {
			// 更新数据库字段
			try {
				if (MongoUtils.updateDocument(MongoSettings.DATABESE_NAME,
						MongoSettings.USER_COLLECTION_NAME,
						Filters.eq("_id", user._id),
						user.genUserDocumentUpdates())) {
					// 此时处理已全部正常完成
					response = Utils.genResponse(200, "Success");
				} else {
					response = Utils.genResponse(500, "Updating Fields Error");
				}
			} catch (Exception e) {
				e.printStackTrace();
				response = Utils.genResponse(500, "Server Runtime Error");
			}
		} else {
			response = Utils.genResponse(400, "Updating Args Error");
		}
		return response;
	}

	@Override
	public Response deleteUser(String _id) {
		Response response;
		if (MongoUtils.deleteDocument(MongoSettings.DATABESE_NAME,
				MongoSettings.USER_COLLECTION_NAME, Filters.eq("_id", new ObjectId(_id)))) {
			response = Utils.genResponse(200, "Success");
		} else {
			response = Utils.genResponse(500, "SQL Error");
		}
		return response;
	}

	@Override
	public Response verifyUser(String account, String password) {
		Response response;
		try {
			JSONArray userDB = Utils.getJSONArray(MongoUtils.findDocument(
					MongoSettings.DATABESE_NAME,
					MongoSettings.USER_COLLECTION_NAME,
					Filters.eq("account", account)));
			if (userDB.size() == 1
					&& Utils.isValid(userDB, "account", "password")) {
				if (MongoUtils.verfyHashPassword(password, userDB
						.getJSONObject(0).getString("password"))) {
					response = Utils.genResponse(200, userDB.getJSONObject(0));
				} else {
					response = Utils.genResponse(400, "Name or Password Error");
				}
			} else {
				response = Utils.genResponse(401, "Args Error");
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = Utils.genResponse(500, "Server Internal Error");
		}
		return response;
	}
}
