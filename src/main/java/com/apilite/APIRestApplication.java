package com.apilite;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Application;

import com.apilite.api.ServiceAPIImpl;
import com.apilite.api.UserAPIImpl;

@ApplicationPath("")
public class APIRestApplication extends Application {
	Set<Object> singletons = new HashSet<Object>();
	
	public APIRestApplication() {
		// 本机测试时OPTIONS不能返回跨域内容，找不到bug换成下面的方法
		// 两种方法理论上没有区别，CorsFilter实现了ContainerResponseFilter接口
//		CorsFilter filter = new CorsFilter();
//		filter.setAllowedHeaders("origin, content-type, accept, authorization");
//		filter.setAllowedMethods("GET, POST, PUT, DELETE, OPTIONS, HEAD");
//		filter.getAllowedOrigins().add("*");
//		filter.setCorsMaxAge(1209600);
		
		// 增加跨域内容
    	singletons.add(new ContainerResponseFilter() {
			@Override
			public void filter(ContainerRequestContext requestContext
					, ContainerResponseContext responseContext)
					throws IOException {
				responseContext.getHeaders().add("Access-Control-Allow-Origin"
						, "*");
				responseContext.getHeaders().add("Access-Control-Allow-Headers"
						, "origin, content-type, accept, authorization");
				responseContext.getHeaders().add("Access-Control-Allow-Methods"
						, "GET, POST, PUT, DELETE, OPTIONS, HEAD");
				responseContext.getHeaders().add("Access-Control-Max-Age"
						, "1209600");
			}
    	});
    	
    	// 增加资源实例
    	singletons.add(new ServiceAPIImpl());
    	singletons.add(new UserAPIImpl());
	}

    @Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> clazzes = new HashSet<>();
        return clazzes;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}