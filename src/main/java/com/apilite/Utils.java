package com.apilite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.bson.Document;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.apilite.service.API;
import com.apilite.settings.DockerSettings;
import com.apilite.settings.ManagerSettings;

public class Utils {
	/**
	 * 从String类型存储的JSONArray类型的API得到List<API>
	 * ，整个List有任何地方出现格式错误将返回null
	 */
	public static List<API> getAPIs(String apisJSON) {
		List<API> apis = new ArrayList<API>();
		if(Utils.isValid(apisJSON)) {
			JSONArray apisArray = JSON.parseArray(apisJSON);
			for(int i = 0; i < apisArray.size(); i ++) {
				JSONObject apiObject = apisArray.getJSONObject(i);
				try {
					API api = apiObject.toJavaObject(API.class);
					if(api.canPost()) {
						apis.add(api);
					}
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		
		if(!apis.isEmpty()) {
			return apis;
		}
		else {
			return null;
		}
	}
	/**
	 * 解压Zip文件到当前目录并删除Zip文件，解压后的路径与Zip文件名相同
	 * 出错或Zip内容为空时返回false
	 */
	public static final boolean unZip(String zipFilePath) {
		ZipFile zipFile = null;
		String unZipFilePath = zipFilePath + "_temp";
		try {
			zipFile = new ZipFile(zipFilePath);
			Enumeration<ZipEntry> entries = zipFile.getEntries();
			if (entries == null || !entries.hasMoreElements()) {
				// 空ZIP包
				return false;
			}

			// 创建目标文件目录
			FileUtils.forceMkdir(new File(unZipFilePath));

			// 遍历所有文件
			while (entries.hasMoreElements()) {
				ZipEntry zipEntry = entries.nextElement();
				String fname = zipEntry.getName();

				// 创建目录
				if (zipEntry.isDirectory()) {
					String fpath = FilenameUtils.normalize(unZipFilePath + "/" + fname);
					FileUtils.forceMkdir(new File(fpath));
					continue;
				}

				// 复制文件目录
				if (StringUtils.contains(fname, "/")) {
					String tpath = StringUtils.substringBeforeLast(fname, "/");
					String fpath = FilenameUtils.normalize(unZipFilePath + "/" + tpath);
					FileUtils.forceMkdir(new File(fpath));
				}

				// 复制文件内容
				InputStream input = null;
				OutputStream output = null;
				try {
					input = zipFile.getInputStream(zipEntry);

					String file = FilenameUtils.normalize(unZipFilePath + "/" + fname);
					output = new FileOutputStream(file);

					IOUtils.copy(input, output);
				} finally {
					IOUtils.closeQuietly(input);
					IOUtils.closeQuietly(output);
				}
			}
			// 删除Zip并重命名路径成功时返回true
			if(deleteFile(zipFilePath) && moveFile(unZipFilePath, zipFilePath)) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (zipFile != null) {
				ZipFile.closeQuietly(zipFile);
			}
		}
		return false;
	}
	/**
	 * 将输入的InputStream以文件形式存放在filePath
	 * ※※※ 注意 ※※※ 存储完毕后会关闭inputStream，就不能再通过inputSteam得到内容
	 */
	private static boolean saveInputStream(InputStream inputStream, String filePath) {
		try {
			File file = new File(filePath);
			// 路径不存在时创建路径
			if(!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
				return false;
			}
			OutputStream outputStream = new FileOutputStream(file);
			// 对循环进行计数，超过83,886,080（80*1024*1024，80M）次
			long bytesNum = 0;
			int len = 0;
			byte[] bytes = new byte[1024];
			while((len = inputStream.read(bytes)) != -1) {
				bytesNum += len;
				outputStream.write(bytes, 0, len);
				// 文件超出80M时停止写文件并返回false
				if(bytesNum > 83886080) {
					outputStream.close();
					return false;
				}
			}
			outputStream.flush();
			outputStream.close();
			
			// 关闭inputStream
			inputStream.close();
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 根据语言不同存储InputStream为文件
	 * 语言为Python时需要解压Zip文件
	 * 失败时尝试删除文件
	 */
	public static boolean saveInputStreamByLang(InputStream inputStream, String filePath
			, String language) {
		if(saveInputStream(inputStream, filePath)) {
			// 单独处理语言为python时解压的问题
			if(language.startsWith("python")) {
				if(unZip(filePath)) {
					return true;
				}
			}
			else {
				return true;
			}
		}
		deleteFile(filePath);
		return false;
	}
	/**
	 * 移动源文件至目标文件，若目标文件已存在则先删除
	 */
	public static boolean moveFile(String srcFilePath, String destFilePath) {
		try {
			// 目标文件存在时删除失败则进入判断
			if(existFile(destFilePath) && !deleteFile(destFilePath)) {
				return false;
			}
			File srcFile = new File(srcFilePath);
			return srcFile.renameTo(new File(destFilePath));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 复制单个文件至目标文件，若目标文件已存在则覆盖
	 */
	public static boolean copySingleFile(String srcFilePath, String destFilePath) {
		InputStream in = null;
		OutputStream out = null;
		try {
			// 目标文件存在时删除失败则进入判断
			if (existFile(destFilePath) && !deleteFile(destFilePath)) {
				return false;
			}
			File srcFile = new File(srcFilePath);
			File destFile = new File(destFilePath);
			// 开始拷贝
			// 读取的位数
			int byteread = 0;
			// 打开原文件
			in = new FileInputStream(srcFile);
			// 打开连接到目标文件的输出流
			out = new FileOutputStream(destFile);
			byte[] buffer = new byte[1024];
			// 一次读取1024个字节，当byteread为-1时表示文件已经读完
			while ((byteread = in.read(buffer)) != -1) {
				// 将读取的字节写入输出流
				out.write(buffer, 0, byteread);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	/**
	 * 删除文件
	 */
	public static boolean deleteFile(String filePath) {
		try {
			File file = new File(filePath);
			if(file.isDirectory()) {
				File[] delFiles = file.listFiles();
				for(File delFile : delFiles) {
					deleteFile(delFile.getAbsolutePath());
				}
			}
			return file.delete();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 检查文件是否存在
	 */
	public static boolean existFile(String filePath) {
		if(filePath != null && !filePath.isEmpty()) {
			File file = new File(filePath);
			return file.exists();
		}
		return false;
	}
	/**
	 * 查看默认服务文件存储路径下文件是否存在并且可读
	 */
	public static boolean canReadServiceFile(String filePath) {
		try {
			File file = new File(filePath);
			return file.exists() && file.canRead();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 当String数组中的每个元素不为null且有值时为真
	 */
	public static boolean isValid(String ... strings) {
		for(String str : strings) {
			if(str == null || str.isEmpty()) {
				return false;
			}
		}
		return true;
	}
	/**
	 * jsonArray为null或keys数组的每一个值在JSONArray的每一个JSONObject中都存在，则返回true
	 */
	public static boolean isValid(JSONArray jsonArray, String ... keys) {
		if(jsonArray == null) {
			return true;
		}
		else {
			for(int i = 0; i < jsonArray.size(); i ++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				for(String key : keys) {
					if(!jsonObject.containsKey(key)) {
						return false;
					}
				}
			}
			return true;
		}
	}
	/**
	 * 根据用户名、服务名生成临时存放路径
	 */
	public static String genTempServiceStoragePath(String author
			, String serviceName) {
		return genServiceStoragePath(author, serviceName) + "_temp";
	}
	/**
	 * 根据用户名、服务名生成正式存放路径
	 */
	public static String genServiceStoragePath(String author
			, String serviceName) {
		return ManagerSettings.HOST_WORK_PATH + "/" + author + "/" 
			+ serviceName + "/" + author + "_" + serviceName;
	}
	/**
	 * 根据用户名、服务名生成Container中的运行路径
	 */
	public static String genServiceRunningPath(String author
			, String serviceName) {
		return DockerSettings.CONTAINER_WORK_PATH + "/" + author + "/" 
			+ serviceName + "/" + author + "_" + serviceName;
	}
	/**
	 * 生成不重复的容器名
	 */
	public static String genContainerName(String language
			, String author, String serviceName) {
		if(Utils.isValid(language, author, serviceName)) {
			return language + "_" + author + "_" + serviceName;
		}
		else {
			return null;
		}
	}
	/**
	 * 得到一个随机生成的UUID，中间的“-”已被去掉
	 */
	public static String genUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}
	/**
	 * 将List<Document>转换为JSONArray
	 * 可能抛出错误异常
	 */
	public static JSONArray getJSONArray(List<Document> documents) {
		JSONArray ret = new JSONArray();
		for(Document document : documents) {
			ret.add(JSON.parseObject(document.toJson()));
		}
		return ret;
	}
	
	/**
	 * 根据HTTP状态码和Object生成Response
	 * Object支持String，JSONObject，JSONArray
	 * 返回body为String类型的Object
	 * 其中String会将body处理为{ "message" : "xxx" }
	 */
	public static Response genResponse(int httpCode, Object object) {
		ResponseBuilder rb = Response.status(httpCode);
		
		rb.header("Content-Type", "application/json; charset=UTF-8");
		rb.header("Content-Encoding", "UTF-8");
		
		// 过滤器已处理跨域问题故此处屏蔽
		// 跨域访问
//		rb.header("Access-Control-Allow-Origin", "*");
//		rb.header("Access-Control-Allow-Credentials", "true");
//		rb.header("Access-Control-Allow-Methods", "POST, GET"
//				+ ", OPTIONS, PUT, DELETE, HEAD");
//		rb.header("Access-Control-Allow-Headers", "X-PINGOTHER, Origin"
//				+ ", X-Requested-With, Content-Type, Accept");
		
		if(object instanceof String) {
			JSONObject body = new JSONObject();
			body.put("message", (String) object);
			rb.entity(body.toJSONString());
		}
		else if(object instanceof JSONObject) {
			rb.entity(((JSONObject) object).toJSONString());
		}
		else if(object instanceof JSONArray) {
			rb.entity(((JSONArray) object).toJSONString());
		}
		else {
			rb.status(500);
			JSONObject body = new JSONObject();
			body.put("message", "Response Generation Error");
			rb.entity(body.toJSONString());
		}
		
		return rb.build();
	}
}