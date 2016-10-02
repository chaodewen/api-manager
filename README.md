#mo-api-java

### Docker Instruction ###
```shell
docker build -t dyonghan/api_java .
docker run -d --name api_java -v `pwd`:/usr/src/myapp --expose 8088 dyonghan/api_java
docker inspect --format='{{ .NetworkSettings.IPAddress }}' api_java
```

### Container Run Instruction ###
```shell
docker run -d --name java8_chaodewen_zhihu -v /home/lxf/Desktop/Test/service_store:/var/app -w /var/app --expose=8080 java:8 java -jar /var/app/chaodewen/zhihu/chaodewen_zhihu.jar
```

### API-Manager Run Instruction ###
```shell
docker run -d --name api-manager -v /root/api-manager/api-manager-jar:/var/api-manager/api-manager-jar -v /root/api-manager/service_store:/var/api-manager/workspace -v /var/run:/var/run_host -w /var/api-manager/api-manager-jar -p 7070:7070 --expose=7070 java:8 java -jar api-manager.jar
```