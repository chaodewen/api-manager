package com.apilite.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import com.apilite.service.Service;

@Path("/services")
public interface IServiceAPI {
	/**
	 * GET /services/state?id="xxx"
	 * 返回JSONObject，包括state字段
	 * 只含有running和stopped两种情况
	 */
	@GET
	@Path("state")
	@Produces("application/json")
	public Response getServiceState(@QueryParam("id") String _id);
	/**
	 * POST /services/killed
	 */
	@POST
	@Path("killed")
	@Produces("application/json")
	public Response killService(@FormParam("id") String _id);
	/**
	 * POST /services/started
	 */
	@POST
	@Path("started")
	@Produces("application/json")
	public Response startService(@FormParam("id") String _id);
	/**
	 * POST /services
	 */
	@POST
	@Path("")
	@Produces("application/json")
	@Consumes("multipart/form-data")
	public Response postService(@MultipartForm Service service);
	/**
	 * GET /services?author="xxx"
	 */
	@GET
	@Path("")
	@Produces("application/json")
	public Response getService(@QueryParam("author") String author);
	/**
	 * GET /services?author="xxx"&draw=123&length=1234&skip=10
	 * 为框架定制的API
	 */
	@POST
	@Path("")
	@Produces("application/json")
	public Response getService(@QueryParam("author") String author
			, @FormParam("draw") int draw, @FormParam("length") int length
			, @FormParam("start") int skip);
	/**
	 * GET /services?author="xxx"&serviceName="xxx"
	 */
	@GET
	@Path("")
	@Produces("application/json")
	public Response getService(@QueryParam("author") String author
			, @QueryParam("name") String serviceName);
	/**
	 * PUT /services
	 */
	@PUT
	@Path("/update")
	@Produces("application/json")
	@Consumes("multipart/form-data")
	public Response putService(@MultipartForm Service service);
	/**
	 * DELETE /services?id="xxx"
	 */
	@DELETE
	@Path("")
	@Produces("application/json")
	public Response deleteService(@QueryParam("id") String _id);
	/**
	 * POST /services/apis
	 */
	@POST
	@Path("/apis")
	@Produces("application/json")
	@Consumes("multipart/form-data")
	public Response postAPI(@MultipartForm Service service);
	/**
	 * GET /services/apis?author="xxx"&&serviceName="xxx"
	 */
	@GET
	@Path("/apis")
	@Produces("application/json")
	public Response getAPI(@QueryParam("id") String _id);
}