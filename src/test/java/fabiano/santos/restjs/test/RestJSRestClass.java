/**
 * 
 */
package fabiano.santos.restjs.test;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * @author fabiano.santos
 * 
 */
@Path("/restjs/test")
public class RestJSRestClass {

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/get/{pparam}/method")
	public void getMethod(@PathParam("pparam") String pparam,
			@QueryParam("qparam") Long qparam) {
		System.out.println("Param: " + qparam);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/post/method")
	public String postMethod(@FormParam("fparam") List<String> fparam) {
		System.out.println("fpram: " + fparam);
		return "OK";
	}
}
