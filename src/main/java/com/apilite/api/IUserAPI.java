package com.apilite.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import com.apilite.user.User;

@Path("/users")
public interface IUserAPI {
	/**
	 * POST /users
	 */
	@POST
	@Path("")
	@Produces("application/json")
	@Consumes("multipart/form-data")
	public Response postUser(@MultipartForm User user);

	/**
	 * GET /users
	 */
	@GET
	@Path("")
	@Produces("application/json")
	public Response getUser(@QueryParam("account") String account);

	/**
	 * PUT /users
	 */
	@PUT
	@Path("")
	@Produces("application/json")
	@Consumes("multipart/form-data")
	public Response putUser(@MultipartForm User user);

	/**
	 * DELETE /users
	 */
	@PUT
	@Path("")
	@Produces("application/json")
	public Response deleteUser(@QueryParam("id") String _id);

	/**
	 * POST /users/verified 需要传入的参数包括account和password
	 */
	@GET
	@Path("/verified")
	@Produces("application/json")
	public Response verifyUser(@QueryParam("account") String account,
			@QueryParam("password") String password);

}