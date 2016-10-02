package com.apilite.docker;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;

import com.apilite.Utils;
import com.apilite.db.MongoUtils;
import com.apilite.service.API;
import com.apilite.service.Service;
import com.apilite.settings.DockerSettings;
import com.apilite.settings.ManagerSettings;
import com.apilite.settings.MongoSettings;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerCmd;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

/**
 * 使用docker-java包进行操作的工具类
 */
public class DockerJavaUtils {
	/**
	 * 更新容器ID至数据库
	 */
	public static boolean updateContainerID(String _id, String containerID) {
		return MongoUtils.updateDocument(MongoSettings.DATABESE_NAME
				, MongoSettings.SERVICE_COLLECTION_NAME,
				Filters.eq("_id", new ObjectId(_id)),
				Updates.set("container_id", containerID));
	}
	
	/**
	 * 创建容器并将container_id写入数据库，出错时返回null
	 * @param name 容器名
	 * @param localWorkPath 服务器工作路径
	 * @param vmWorkPath 虚拟机工作路径
	 * @param language 镜像名
	 * @param containerPorts 容器暴露的端口
	 * @param workingDir 进入容器后的工作目录
	 * @param cmd 进入容器后执行的命令 null代表不执行命令
	 * @return
	 */
	public static CreateContainerResponse createContainer(String serviceID
			, String name, String localWorkPath, String vmWorkPath
			, String image, Set<String> containerPorts, String workingDir
			, String env, String ... cmd) {
		try {
			DockerClient dockerClient = DockerJavaConnectionFactory.getDockerClient();
			// 保存容器要暴露的端口号
			int portNum = containerPorts.size();
			ExposedPort[] exposedPorts = new ExposedPort[portNum];
			int i = 0;
			for(String containerPort : containerPorts) {
				exposedPorts[i ++] = ExposedPort.tcp(Integer.valueOf(containerPort));
			}
			
			// 创建容器
			CreateContainerCmd create = dockerClient
					.createContainerCmd(image)
					.withName(name)
					.withExposedPorts(exposedPorts)
					.withBinds(new Bind(localWorkPath, new Volume(vmWorkPath)))
					.withWorkingDir(workingDir);
			CreateContainerResponse response;
			
			// python3
			if(env != null) {
				create.withEnv(env);
			}
			
			if(cmd == null) {
				// python3
				response = create.exec();
			}
			else {
				// java
				response = create.withCmd(cmd).exec();
			}
			
			if(response != null) {
				updateContainerID(serviceID, response.getId());
			}
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 通过Service创建容器，包括Service中所有API，将container_id写入数据库，失败时返回null
	 */
	public static CreateContainerResponse createContainer(Service service) {
		if(service != null && service.apis != null && service.apis.size() > 0) {
			String containerName = Utils.genContainerName(service.language
					, service.author, service.name);
			String image = getImage(service);
			// 确保镜像存在且镜像名获取成功
			if(image != null) {
				Set<String> lanPorts = getLanPorts(service.apis);
				// 没有API时返回null
				if(lanPorts.size() != 0) {
					String[] cmd = getCmd(service);
					String env = getEnv(service);
					String workingDir = getWorkingDir(service);
					CreateContainerResponse response = createContainer(
							service._id.toHexString()
							, containerName, ManagerSettings.HOST_WORK_PATH
							, DockerSettings.CONTAINER_WORK_PATH, image, lanPorts
							, workingDir, env, cmd);
					return response;
				}
			}
		}
		return null;
	}
	
	/**
	 * 根据dockerFile创建镜像，并命名为imageTag
	 */
	public static boolean buildImage(String servicePath, String image) {
		try {
			// 存放Dockerfile至Service目录
			if(!Utils.copySingleFile(DockerSettings.PYTHON3_DOCKER_FILE
					, servicePath + "/Dockerfile")) {
				return false;
			}
			DockerClient dockerClient = DockerJavaConnectionFactory
					.getDockerClient();
			File dockerFile = new File(servicePath + "/Dockerfile");
			dockerClient.buildImageCmd(dockerFile)
			.withTag(image).exec(new BuildImageResultCallback() {})
			.awaitCompletion();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 根据容器ID返回容器信息，出错时返回null
	 */
	public static InspectContainerCmd inspectContainer(String containerID) {
		if(Utils.isValid(containerID)) {
			try {
				return DockerJavaConnectionFactory.getDockerClient().inspectContainerCmd(containerID);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 查看镜像是否存在
	 * 例如查看"java:6-jdk"是否存在
	 * "java"默认代表"java:latest"
	 */
	public static boolean existImage(String image) {
		try {
			DockerClient dockerClient = DockerJavaConnectionFactory
					.getDockerClient();
			dockerClient.inspectImageCmd(image).exec();
			return true;
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 根据language返回imageTag
	 * 当语言为Python2这种需要创建新镜像的情况时会主动创建
	 * 出错返回null
	 */
	public static String getImage(Service service) {
		if(service.language != null) {
			if(service.language.equals("java")) {
				return DockerSettings.JAVA_IMAGE;
			}
			else if(service.language.equals("java8")) {
				return DockerSettings.JAVA8_IMAGE;
			}
			else if(service.language.equals("python3")) {
				// 生成Python3的镜像名
				String image = service.author + service.name 
						+ service.tag;
				image = image.toLowerCase();
				// 镜像存在时返回结果，不存在时创建镜像并返回结果
				if(DockerJavaUtils.existImage(image)) {
					return image;
				}
				else if(DockerJavaUtils.buildImage(
						Utils.genServiceStoragePath(
						service.author, service.name), image)) {
					return image;
				}
			}
		}
		return null;
	}
	
	/**
	 * 根据容器中服务运行位置生成进入容器时的操作目录
	 * 若服务是一个文件,操作目录为其父目录下
	 * 若服务是一个路径,操作目录为该路径内
	 */
	public static String getWorkingDir(Service service) {
		// 莫名Bug:判断serviceFile是否为目录(isDirectory)在容器中会出错
		File serviceFile = new File(Utils.genServiceRunningPath(
				service.author, service.name));
		if(service.language.equals("python3")) {
			return serviceFile.getAbsolutePath();
		}
		else {
			return serviceFile.getParent();
		}
	}
	
	/**
	 * 根据language和文件路径获得cmd参数
	 * 不需要命令时返回null
	 */
	public static String[] getCmd(Service service) {
		if(service.language != null) {
			if(service.language.startsWith("java")) {
				String vmFilePath = Utils.genServiceRunningPath(service.author
						, service.name);
				return new String[] { "java", "-jar" , vmFilePath };
			}
			else if(service.language.equals("python3")) {
//				return new String[] { getImage(service) };
				return null;
			}
		}
		return null;
	}
	
	/**
	 * 根据language和文件路径获得env参数
	 * 不需要环境变量时返回null
	 */
	public static String getEnv(Service service) {
		if(service.language != null) {
			if(service.language.equals("python3")) {
				String vmFilePath = Utils.genServiceRunningPath(
						service.author, service.name);
				return DockerSettings.PYTHON3_SERVICE_DOCKER_ENV 
						+ "=" + vmFilePath;
			}
			else {
				return null;
			}
		}
		return null;
	}
	
	/**
	 * 根据API列表得到用户服务需要暴露端口号的集合
	 */
	public static Set<String> getLanPorts(List<API> apis) {
		Set<String> lanPorts = new HashSet<String>();
		if(apis == null || apis.isEmpty()) {
			return lanPorts;
		}
		else {
			for(API api : apis) {
				if(Utils.isValid(api.lan_port)) {
					lanPorts.add(api.lan_port);
				}
			}
			return lanPorts;
		}
	}
}