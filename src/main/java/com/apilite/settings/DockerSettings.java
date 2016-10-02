package com.apilite.settings;

/**
 * Docker设置 注意：路径不要以斜杠结尾
 */
public class DockerSettings {
	public static final String CONTAINER_WORK_PATH = 
			"/var/app";
	// public static final String CER_PATH =
	// "/Users/Cc/.docker/machine/machines/default";

//	public static final String DOCKER_DAEMON_URI = 
//			"https://127.0.0.1:2376";
	// 宿主机的docker.sock挂在到容器中使用
//	public static final String DOCKER_DAEMON_URI = 
//			"unix:///var/run_host/docker.sock";
	public static final String DOCKER_DAEMON_URI = 
			"unix:///var/run/docker.sock";

	public static final String JAVA_IMAGE = "java";
	public static final String JAVA8_IMAGE = "java:8";
	// public static final String PYTHON2_IMAGE = "python:2.7";

	public static final String PYTHON3_DOCKER_FILE = 
			"/var/devops/api-manager/Dockerfile";
	
	public static final String PYTHON3_SERVICE_DOCKER_ENV =
			"SERVICE_DIR";

//	public static final int CMDEXEC_READ_TIMEOUT = 10000;
//	public static final int CMDEXEC_CONNECT_TIMEOUT = 5000;
//	public static final int CMDEXEC_MAX_CONNECTIONS = 10000;
//	public static final int CMDEXEC_MAX_PER_ROUTE_CONNECTIONS = 10;
}