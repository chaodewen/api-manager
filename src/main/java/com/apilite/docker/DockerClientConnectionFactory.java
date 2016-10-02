package com.apilite.docker;

//import java.net.URI;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import com.apilite.settings.DockerSettings;
//import com.spotify.docker.client.DefaultDockerClient;
//import com.spotify.docker.client.DockerCertificateException;
//import com.spotify.docker.client.DockerCertificates;
//import com.spotify.docker.client.DockerClient;
//import com.spotify.docker.client.messages.ContainerConfig;
//import com.spotify.docker.client.messages.HostConfig;
//import com.spotify.docker.client.messages.PortBinding;

/**
 * 使用docker-client包进行操作的工厂类
 * 因为jackson相关的包在docker-client和docker-java之间可能产生冲突
 * 所以屏蔽内容
 */
@Deprecated
public class DockerClientConnectionFactory {
//	private static DockerClient dockerClient = null;
//	
//	public static synchronized DockerClient getDockerClient() {
//		if(dockerClient == null) {
//			try {
//				dockerClient = DefaultDockerClient.builder()
//						.uri(URI.create(DockerSettings.DOCKER_DAEMON_URI))
//					    .dockerCertificates(new DockerCertificates(
//					    		Paths.get(DockerSettings.CER_PATH)))
//					    .build();
//			} catch (DockerCertificateException e) {
//				e.printStackTrace();
//				dockerClient = null;
//			}
//		}
//		return dockerClient;
//	}
//	
//	/**
//	 * 返回Host设置，包括主机提供外部访问的端口号，以及绑定服务器工作路径至虚拟机工作路径（确保文件同步）
//	 * @param exposedPort 主机暴露的用于外部访问的端口号
//	 * @param localWorkPath 服务器工作路径
//	 * @param vmWorkPath 虚拟机工作路径
//	 * @return
//	 */
//	public static HostConfig getHostConfig(String exposedPort, String localWorkPath, String vmWorkPath) {
//		// 绑定端口
//		List<PortBinding> portBinding = new ArrayList<PortBinding>();
//		portBinding.add(PortBinding.randomPort("0.0.0.0"));
//		Map<String, List<PortBinding>> portBindings = new HashMap<String, List<PortBinding>>();
//		portBindings.put(exposedPort, portBinding);
//		
//		return HostConfig.builder()
//				.portBindings(portBindings)
//				.binds(localWorkPath + ":" + vmWorkPath)
//				.build();
//	}
//	
//	/**
//	 * 返回容器设置，包括镜像名、容器名、
//	 * @param image 镜像名
//	 * @param exposedPorts 容器暴露的用于主机访问的端口
//	 * @param workingDir 进入容器后的工作目录
//	 * @param hostConfig 主机设置
//	 * @param cmd 进入容器后执行的命令
//	 * @return
//	 */
//	public static ContainerConfig getContainerConfig(String image
//			, Set<String> exposedPorts, String workingDir
//			, HostConfig hostConfig, String ... cmd) {
//		return ContainerConfig.builder()
//				.image(image)
//				.exposedPorts(exposedPorts)
//				.workingDir(workingDir)
//				.hostConfig(hostConfig)
//				.cmd(cmd)
//				.build();
//	}
}