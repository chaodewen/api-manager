package com.apilite.api;

import static com.mongodb.client.model.Projections.include;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.apilite.Utils;
import com.apilite.db.MongoUtils;
import com.apilite.docker.DockerJavaConnectionFactory;
import com.apilite.docker.DockerJavaUtils;
import com.apilite.service.API;
import com.apilite.service.Service;
import com.apilite.settings.MongoSettings;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;

public class ServiceAPIImpl implements IServiceAPI {
	@Override
	public Response getServiceState(String _id) {
		Response response;
		Service service = MongoUtils.getService(_id);
		if(service != null && Utils.isValid(service.container_id)) {
			InspectContainerResponse containerInfo = DockerJavaUtils
					.inspectContainer(service.container_id).exec();
			JSONObject body = new JSONObject();
			body.put("message", "Success");
			if(containerInfo.getState().isRunning()) {
				body.put("state", "running");
			}
			else {
				body.put("state", "stopped");
			}
			response = Utils.genResponse(200, body);
		}
		else if(service == null) {
			response = Utils.genResponse(400, "Param Error");
		}
		else {
			JSONObject body = new JSONObject();
			body.put("message", "Success");
			body.put("state", "stopped");
			response = Utils.genResponse(200, body);
		}
		return response;
	}
	
	@Override
	public Response killService(String _id) {
		Response response;
		Service service = MongoUtils.getService(_id);
		if(service != null && Utils.isValid(service.container_id)) {
			try {
				DockerJavaConnectionFactory.getDockerClient().killContainerCmd(
						service.container_id).exec();
				response = Utils.genResponse(200, "Success");
			} catch (Exception e) {
				// 创建失败
				e.printStackTrace();
				response = Utils.genResponse(500, "Container Killing Error");
			}
		}
		else {
			response = Utils.genResponse(400, "Param Error");
		}
		return response;
	}
	
	@Override
	public Response startService(String _id) {
		Response response;
		Service service = MongoUtils.getService(_id);
		if(service != null) {
			// 检查Service文件是否存在与可读性
			if(!Utils.canReadServiceFile(Utils.genServiceStoragePath(
					service.author, service.name))) {
				// 不存在或不可读
				response = Utils.genResponse(500, "No Service File or Can Not Read");
				return response;
			}
			// 数据库中不存在相应信息则创建
			if(!Utils.isValid(service.container_id)) {
				CreateContainerResponse createContainer = DockerJavaUtils
						.createContainer(service);
				if(createContainer == null) {
					// 创建失败
					response = Utils.genResponse(500, "Container Creation Error");
					return response;
				}
				else {
					// 创建成功
					service.container_id = createContainer.getId();
				}
			}
			// 启动
			try {
				DockerJavaConnectionFactory.getDockerClient().startContainerCmd(
						service.container_id).exec();
				InspectContainerResponse containerInfo = DockerJavaConnectionFactory
						.getDockerClient().inspectContainerCmd(service.container_id).exec();
				// 获得所有对外暴露的端口
				JSONArray ports = new JSONArray();
				for(ExposedPort port : containerInfo.getConfig().getExposedPorts()) {
					ports.add(port.toString());
				}
				if(ports.size() > 0) {
					// 暂时只存储一个
					for(API api : service.apis) {
						api.lan_port = ports.getString(0);
					}
				}
				// 存储修改的信息至数据库
				if(MongoUtils.updateDocument(MongoSettings.DATABESE_NAME
						, MongoSettings.SERVICE_COLLECTION_NAME,
						Filters.eq("_id", service._id),
						service.genServiceDocumentUpdates())) {
					response = Utils.genResponse(200, "Success");
				}
				else {
					response = Utils.genResponse(500, "SQL Updating Error");
					return response;
				}
			} catch (Exception e) {
				// 创建失败
				e.printStackTrace();
				response = Utils.genResponse(500, "Container Starting Error");
				return response;
			}
			
			return response;
		}
		response = Utils.genResponse(400, "Param Error");
		return response;
	}
	
	@Override
	public Response postService(Service service) {
		Response response;

		// 帮助创建apis
		service.apis = Utils.getAPIs(service.apisJSON);
		
		// 判断时增加_id字段
		if (service.canPost()) {
			// 存储inputStream
			if(Utils.saveInputStreamByLang(service.fileStream
					, Utils.genServiceStoragePath(service.author, service.name)
					, service.language)) {
				// 进行插入操作
				if (MongoUtils.insertDocument(MongoSettings.DATABESE_NAME
						, MongoSettings.SERVICE_COLLECTION_NAME
						, service.getDocument())) {
					JSONObject body = new JSONObject();
					body.put("message", "Success");
					body.put("id", service._id.toHexString());
					response = Utils.genResponse(200, body);
				}
				else {
					Utils.deleteFile(Utils.genServiceStoragePath(
							service.author, service.name));
					response = Utils.genResponse(500, "Server Runtime Error");
				}
			}
			else {
				response = Utils.genResponse(500, "File Saving Error");
			}
		} else {
			response = Utils.genResponse(400, "Param Error");
		}
		return response;
	}

	@Override
	public Response getService(String author) {
		Response response;
		try {
			JSONArray services = Utils.getJSONArray(MongoUtils.findDocument(
					MongoSettings.DATABESE_NAME,
					MongoSettings.SERVICE_COLLECTION_NAME,
					Filters.eq("author", author)));
			response = Utils.genResponse(200, services);
		} catch (Exception e) {
			e.printStackTrace();
			response = Utils.genResponse(500, "Server Runtime Error");
		}
		return response;
	}
	
	@Override
	public Response getService(String author, int draw, int length, int skip) {
		Response response;
		try {
			JSONObject responseJSON = new JSONObject();
			JSONArray services = Utils.getJSONArray(MongoUtils.findDocument(
					MongoSettings.DATABESE_NAME, MongoSettings.SERVICE_COLLECTION_NAME
					, Filters.eq("author", author), length, skip));
			
			responseJSON.put("draw", draw);
			responseJSON.put("recordsTotal", 1);
			responseJSON.put("recordsFiltered", 1);
			responseJSON.put("data", services);
			
			response = Utils.genResponse(200, responseJSON);
		} catch (Exception e) {
			e.printStackTrace();
			response = Utils.genResponse(500, "Server Runtime Error");
		}
		return response;
	}
	
	@Override
	public Response getService(String author, String serviceName) {
		Response response;
		try {
			JSONArray services = Utils.getJSONArray(
					MongoUtils.findDocument(MongoSettings.DATABESE_NAME
							, MongoSettings.SERVICE_COLLECTION_NAME
							, Filters.and(Filters.eq("author", author)
									, Filters.eq("name", serviceName))));
			response = Utils.genResponse(200, services);
		} catch (Exception e) {
			e.printStackTrace();
			response = Utils.genResponse(500, "Server Runtime Error");
		}
		return response;
	}
	
	@Override
	public Response putService(Service service) {
		Response response;
		
		// 帮助创建apis
		service.apis = Utils.getAPIs(service.apisJSON);
		
		if(service.canPut()) {
			Service serviceOrigin = MongoUtils.getService(service._id.toHexString());
			if(serviceOrigin != null) {
				boolean fileUploaded = false;
				// 移动原文件至临时目录做临时保存并且上传新文件至正式存储目录
				if(service.fileStream != null) {
					if(Utils.moveFile(Utils.genServiceStoragePath(
							serviceOrigin.author, serviceOrigin.name)
							, Utils.genTempServiceStoragePath(serviceOrigin.author
									, serviceOrigin.name))
							&& Utils.saveInputStreamByLang(service.fileStream
									, Utils.genServiceStoragePath(serviceOrigin.author
											, serviceOrigin.name), service.language)) {
						fileUploaded = true;
					}
					else {
						response = Utils.genResponse(500, "File Saving Error");
						return response;
					}
				}
				// 更新数据库字段
				try {
					if (MongoUtils.updateDocument(MongoSettings.DATABESE_NAME
							, MongoSettings.SERVICE_COLLECTION_NAME,
							Filters.eq("_id", service._id),
							service.genServiceDocumentUpdates())) {
						// 原文件保存至临时位置同时新文件上传至正式位置
						if(fileUploaded) {
							// 删除临时位置的原文件
							if(!Utils.deleteFile(Utils.genTempServiceStoragePath(
									serviceOrigin.author, serviceOrigin.name))) {
								// 移动文件时出错
								response = Utils.genResponse(500, "File Moving Error");
								return response;
							}
						}
						
						// 此时处理已全部正常完成
						response = Utils.genResponse(200, "Success");
					}
					else {
						// 字段更新失败时把原文件放回正式目录，替换新上传的文件，恢复未上传时的状态
						if(Utils.moveFile(Utils.genTempServiceStoragePath(
								serviceOrigin.author, serviceOrigin.name)
								, Utils.genTempServiceStoragePath(serviceOrigin.author
										, serviceOrigin.name))) {
							response = Utils.genResponse(500, "Updating Fields Error");
						}
						else {
							response = Utils.genResponse(500
									, "Updating Fields Error and Origin File Damaged");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					response = Utils.genResponse(500, "Server Runtime Error");
				}
				return response;
			}
		}
		response = Utils.genResponse(400, "Updating Args Error");
		return response;
	}
	
	public Response deleteService(String _id) {
		Response response;
		Service service = MongoUtils.getService(_id);
		if(service != null) {
			// 存在容器ID时要删除容器
			if(Utils.isValid(service.container_id)) {
				try {
					InspectContainerResponse containerInfo = DockerJavaConnectionFactory
							.getDockerClient().inspectContainerCmd(service.container_id).exec();
					// 容器处于运行状态则kill
					if(containerInfo.getState().isRunning()) {
						DockerJavaConnectionFactory.getDockerClient().killContainerCmd(
								service.container_id).exec();
					}
					// remove容器
					DockerJavaConnectionFactory.getDockerClient().removeContainerCmd(
							service.container_id).exec();
				} catch (Exception e) {
					e.printStackTrace();
					response = Utils.genResponse(500, "Container Removed Error");
					return response;
				}
			}
			
			// 删除Service文件
			File file = new File(Utils.genServiceStoragePath(service.author
					, service.name));
			String delFileString = file.getParent();
			if(!Utils.deleteFile(delFileString)) {
				response = Utils.genResponse(500, "Service File Deleting Error");
			}
			
			// 删除数据库内容
			if(MongoUtils.deleteDocument(MongoSettings.DATABESE_NAME
					, MongoSettings.SERVICE_COLLECTION_NAME
					, Filters.eq("_id", new ObjectId(_id)))) {
				response = Utils.genResponse(200, "Success");
			}
			else {
				response = Utils.genResponse(500, "SQL Deleting Error");
			}
		}
		else {
			response = Utils.genResponse(500, "Param Error");
		}
		return response;
	}

	@Override
	public Response postAPI(Service service) {
		Response response;
		
		// 帮助创建apis
		service.apis = Utils.getAPIs(service.apisJSON);
		
		// 检查格式是否正确
		if(service.canPut() && service.apis != null && !service.apis.isEmpty()) {
			// 开始处理创建请求
			List<Bson> updates = new ArrayList<Bson>();
			List<Document> documents = new ArrayList<Document>();
			for(API api : service.apis) {
				documents.add(api.getDocument());
			}
			updates.add(Updates.pushEach("apis", documents));
			// 调用canPut()时生成了新的更新时间
			updates.add(Updates.setOnInsert("updated_time", service.updated_time));
						
			if(MongoUtils.updateDocument(MongoSettings.DATABESE_NAME
					, MongoSettings.SERVICE_COLLECTION_NAME, Filters.eq(
							"_id", service._id)
					, Updates.combine(updates))) {
				response = Utils.genResponse(200, "Success");
			}
			else {
				response = Utils.genResponse(500, "Server Runtime Error");
			}
		}
		else {
			response = Utils.genResponse(400, "Param Error");
		}
		return response;
	}

	@Override
	public Response getAPI(String _id) {
		Response response;
		try {
			JSONArray services = Utils.getJSONArray(
					MongoUtils.findDocument(MongoSettings.DATABESE_NAME
							, MongoSettings.SERVICE_COLLECTION_NAME
							, Filters.eq("_id", new ObjectId(_id))
							, Projections.fields(include("apis"))));
			if (services.isEmpty()) {
				response = Utils.genResponse(404, "Service Not Found");
			}
			else if (services.size() != 1) {
				response = Utils.genResponse(400, "Args Error");
			}
			else {
				response = Utils.genResponse(200, services.getJSONObject(0)
						.getJSONArray("apis").toJSONString());
			}
		} catch (Exception e) {
			response = Utils.genResponse(500, "Server Runtime Error");
		}
		return response;
	}
}
