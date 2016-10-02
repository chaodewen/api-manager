package com.apilite.docker;

import com.apilite.settings.DockerSettings;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;

/**
 * 使用docker-java包进行操作的工厂类
 */
public class DockerJavaConnectionFactory {
	private static DockerClient dockerClient = null;
	
	public static synchronized DockerClient getDockerClient() {
		if(dockerClient == null) {
			try {
//				DockerClientConfig config = DockerClientConfig
//						.createDefaultConfigBuilder()
////						.withDockerCertPath(DockerSettings.CER_PATH)
//						.withUri(DockerSettings.DOCKER_DAEMON_URI)
//						.build();
//				@SuppressWarnings("resource")
//				DockerCmdExecFactoryImpl dockerCmdExecFactory = 
//						new DockerCmdExecFactoryImpl()
//						.withReadTimeout(DockerSettings.CMDEXEC_READ_TIMEOUT)
//						.withConnectTimeout(DockerSettings.CMDEXEC_CONNECT_TIMEOUT)
//						.withMaxTotalConnections(DockerSettings.CMDEXEC_MAX_CONNECTIONS)
//						.withMaxPerRouteConnections(DockerSettings
//								.CMDEXEC_MAX_PER_ROUTE_CONNECTIONS);
//				dockerClient = DockerClientBuilder
//						.getInstance(config)
//						.withDockerCmdExecFactory(dockerCmdExecFactory)
//						.build();
				dockerClient = DockerClientBuilder.getInstance(
						DockerSettings.DOCKER_DAEMON_URI).build();
			} catch (Exception e) {
				e.printStackTrace();
				dockerClient = null;
			}
		}
		return dockerClient;
	}
}