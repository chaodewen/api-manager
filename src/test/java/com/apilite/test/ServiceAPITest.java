package com.apilite.test;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.apilite.Utils;
import com.apilite.docker.DockerJavaConnectionFactory;
import com.apilite.settings.DockerSettings;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.BuildImageResultCallback;

//import com.spotify.docker.client.DefaultDockerClient;
//import com.spotify.docker.client.DockerCertificates;
//import com.spotify.docker.client.DockerClient;
//import com.spotify.docker.client.messages.ContainerConfig;
//import com.spotify.docker.client.messages.ContainerCreation;
//import com.spotify.docker.client.messages.ContainerInfo;
//import com.spotify.docker.client.messages.HostConfig;
//import com.spotify.docker.client.messages.PortBinding;

@SuppressWarnings("unused")
public class ServiceAPITest {
	@Test
	public void testAsyncMongoDB() {
//		AsyncMongoConnectionFactory.getMongoClient().getDatabase(
//				MongoSettings.DATABESE_NAME).getCollection(
//						MongoSettings.USER_COLLECTION_NAME).count(
//								new SingleResultCallback<Long>() {
//							@Override
//							public void onResult(Long result, Throwable t) {
//								// TODO Auto-generated method stub
//								System.out.println("result : " + result);
//							}
//						});
//		AsyncMongoConnectionFactory.getMongoClient().getDatabase(
//				MongoSettings.DATABESE_NAME).getCollection(
//						MongoSettings.USER_COLLECTION_NAME)
//				.find().into(new ArrayList<Document>()
//						, new SingleResultCallback<List<Document>>() {
//							@Override
//							public void onResult(List<Document> result, Throwable t) {
//								System.out.println("MongoDB Content : " + result);
//							}
//				});
	}
	
	@Test
	public void testService() {
//		Service service = new Service();;
//		service.name = "cc";
//		service.author = "vivo";
//		service.tag = "2.2";
//		System.out.println(service);
	}
	
	/**
	 * 因为jackson相关的包在docker-client和docker-java之间可能产生冲突
	 * 所以屏蔽内容
	 */
	@Test
	public void testDockerClient() {
//		try {
//			DockerClient docker = DefaultDockerClient.builder()
//				    .uri(URI.create(DockerSettings.DOCKER_DAEMON_URI))
//				    .dockerCertificates(new DockerCertificates(
//				    		Paths.get(DockerSettings.CER_PATH)))
//				    .build();
//			
//			List<PortBinding> portBinding = new ArrayList<PortBinding>();
//			portBinding.add(PortBinding.randomPort("0.0.0.0"));
//			Map<String, List<PortBinding>> portBindings = new HashMap<String, List<PortBinding>>();
//			portBindings.put(DockerSettings.EXPOSED_PORT, portBinding);
//			HostConfig hostConfig = HostConfig.builder()
//					.portBindings(portBindings)
//					.binds(ManagerSettings.WORK_PATH + ":" + DockerSettings.VM_WORK_PATH)
//					.build();
//			
//			ContainerConfig containerConfig = ContainerConfig.builder()
//					.image("java")
//					.cmd("java", "-jar", "Zhihu.jar")
//					.hostname("api_mo_baidu")
//					.exposedPorts(DockerSettings.EXPOSED_PORT)
//					.hostConfig(hostConfig)
//					.workingDir(DockerSettings.VM_WORK_PATH)
//					.build();
//			
//			ContainerCreation containCreation = docker.createContainer(containerConfig);
//			
//			System.out.println(containCreation.id());
//			System.out.println();
//			
//			docker.startContainer(containCreation.id());
//			
////			final List<Container> containers = docker.listContainers();
////			System.out.println(containers);
//			
//			ContainerInfo containerInfo = docker.inspectContainer(
//					containCreation.id());
//			System.out.println(containerInfo.toString());
//			
//			if(containerInfo.state().running()) {
//				System.out.println("running");
////				docker.killContainer(containCreation.id());
//			}
//			
//			docker.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
	@Test
	public void testDockerJava() throws InterruptedException {
		DockerClient dockerClient = DockerClientBuilder.getInstance(
				"unix:///var/run/docker.sock").build();
		
		List<Image> images = dockerClient.listImagesCmd().exec();
		for(Image image : images) {
			System.out.println(image.getId());
		}
		
		File dockerFile = new File("/media/lxf/ORICO/163/Dockerfile");
		System.out.println(dockerFile.isFile());
		dockerClient.buildImageCmd(dockerFile)
			.withTag("rabbitlxfyuedu1")
			.exec(new BuildImageResultCallback() {
				 @Override
				    public void onNext(BuildResponseItem item) {
				        System.out.println(item.getStatus());
				        super.onNext(item);
				    }
			})
			.awaitCompletion();
	}
	
	@Test
	public void testBCrypt() {
//		String salt = BCrypt.gensalt(12);
//		System.out.println(salt);
//		String result = BCrypt.hashpw("12345", salt);
//		System.out.println(result);
//		System.out.println(BCrypt.checkpw("12345", result));
	}
	
	@Test
	public void testZip() {
//		Utils.unZip("/Users/Cc/Desktop/MOVIST1317");
//		System.out.println(Utils.deleteFile("/Users/Cc/Desktop/MOVIST1317"));
	}
}