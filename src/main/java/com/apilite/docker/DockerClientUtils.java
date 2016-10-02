package com.apilite.docker;

//import java.util.Set;
//
//import org.bson.types.ObjectId;
//
//import com.apilite.Utils;
//import com.apilite.db.MongoUtils;
//import com.apilite.service.Service;
//import com.apilite.settings.DockerSettings;
//import com.apilite.settings.ManagerSettings;
//import com.apilite.settings.MongoSettings;
//import com.mongodb.client.model.Filters;
//import com.mongodb.client.model.Updates;
//import com.spotify.docker.client.DockerClient;
//import com.spotify.docker.client.messages.ContainerConfig;
//import com.spotify.docker.client.messages.ContainerCreation;
//import com.spotify.docker.client.messages.ContainerInfo;
//import com.spotify.docker.client.messages.HostConfig;

/**
 * 使用docker-client包进行操作的工具类
 * 因为jackson相关的包在docker-client和docker-java之间可能产生冲突
 * 所以屏蔽内容
 */
@Deprecated
public class DockerClientUtils {
//	/**
//	 * 更新容器ID至数据库
//	 */
//	public static boolean updateContainerID(String _id, String containerID) {
//		return MongoUtils.updateDocument(MongoSettings.DATABESE_NAME
//				, MongoSettings.SERVICE_COLLECTION_NAME,
//				Filters.eq("_id", new ObjectId(_id)),
//				Updates.setOnInsert("container_id", containerID));
//	}
//	/**
//	 * 创建容器并将container_id写入数据库
//	 * @param name 容器名
//	 * @param hostPort 主机暴露的用于外部访问的端口号
//	 * @param localWorkPath 服务器工作路径
//	 * @param vmWorkPath 虚拟机工作路径
//	 * @param image 镜像名
//	 * @param containerPort 容器暴露的用于主机访问的端口
//	 * @param workingDir 进入容器后的工作目录
//	 * @param cmd 进入容器后执行的命令
//	 * @return
//	 */
//	public static ContainerCreation createContainer(String serviceID, String name, String hostPort
//			, String localWorkPath, String vmWorkPath, String image
//			, Set<String> containerPorts, String workingDir, String ... cmd) {
//		DockerClient dockerClient = DockerClientConnectionFactory.getDockerClient();
//		// 设置HostConfig
//		HostConfig hostConfig = DockerClientConnectionFactory.getHostConfig(
//				hostPort, localWorkPath, vmWorkPath);
//		// 设置ContainerConfig
//		ContainerConfig containerConfig = DockerClientConnectionFactory.getContainerConfig(
//				image, containerPorts, workingDir, hostConfig, cmd);
//		try {
//			ContainerCreation containerCreation = dockerClient.createContainer(containerConfig, name);
//			if(containerCreation != null) {
//				updateContainerID(serviceID, containerCreation.id());
//			}
//			return containerCreation;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//	/**
//	 * 通过容器名和容器参数创建容器并将container_id写入数据库，失败时返回null
//	 * @param name 容器名
//	 * @param containerConfig 容器设置
//	 * @return
//	 */
//	public static ContainerCreation createContainer(String serviceID, String name
//			, ContainerConfig containerConfig) {
//		DockerClient dockerClient = DockerClientConnectionFactory.getDockerClient();
//		try {
//			ContainerCreation containerCreation = dockerClient.createContainer(containerConfig, name);
//			if(containerCreation != null) {
//				// 创建成功写数据库
//				updateContainerID(serviceID, containerCreation.id());
//			}
//			return containerCreation;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//	/**
//	 * 通过Service创建容器，包括Service中所有API，将container_id写入数据库，失败时返回null
//	 */
//	public static ContainerCreation createContainer(Service service) {
//		if(service != null && service.apis != null && service.apis.size() > 0) {
//			String containerName = Utils.genContainerName(service.language
//					, service.author, service._id.toHexString());
//			HostConfig hostConfig = DockerClientConnectionFactory.getHostConfig(
//					DockerSettings.EXPOSED_PORT, ManagerSettings.WORK_PATH
//					, DockerSettings.VM_WORK_PATH);
//			String image = Utils.getImage(service.language);
//			Set<String> lanPorts = Utils.getLanPorts(service.apis);
//			String[] cmd = Utils.getCMD(service.language
//					, Utils.genServiceStoragePath(service.author, service.name));
//			ContainerConfig containerConfig = DockerClientConnectionFactory.getContainerConfig(
//					image, lanPorts, DockerSettings.VM_WORK_PATH, hostConfig, cmd);
//			return DockerClientUtils.createContainer(service._id.toHexString()
//					, containerName, containerConfig);
//		}
//		else {
//			return null;
//		}
//	}
//	/**
//	 * 根据容器ID返回容器信息
//	 */
//	public static ContainerInfo inspectContainer(String containerID) {
//		if(Utils.isValid(containerID)) {
//			try {
//				return DockerClientConnectionFactory.getDockerClient().inspectContainer(containerID);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return null;
//	}
}