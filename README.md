# api-manager for mo

This program is designing for iOS application named Mo. That's an intelligent assistant. But Mo failed when we just finished some versions in vertical search. In my opinion, Mo is a good product. But it's not a popular one.

This program named api-manager is charging for managing background APIs of Mo exculdes user system and web background. Mo can do vertical search in many applications or websites such as Zhihu, Taobao, Douban and so on. All of these functions are finished by some APIs mentioned above. What this program do is running these APIs in Docker and supply functional RESTful APIs to a website.

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
