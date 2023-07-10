package com.farm_erp.fileresources.controllers.endpoints;

import java.io.IOException;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.farm_erp.fileresources.controllers.services.FileRequest;
import com.farm_erp.fileresources.controllers.services.FileService;
import com.farm_erp.fileresources.domain.FileResource;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.jaxrs.PathParam;


@Path("file")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "File Resources", description = "")
public class FileResourceController {

	@Inject
	FileService fileService;

	@GET
	@Path("/id/{id}")
	public byte[] getSingleId(@PathParam Long id, @HeaderParam("activeUser") String user) throws IOException {

		FileResource entity = FileResource.findById(id);
		if (entity != null) {
			return entity.data;
		} else {
			return null;
		}

	}

	@GET
	@Path("/url/{url}")
	public byte[] getSingleUrl(@PathParam String url) throws IOException {

		System.out.println(".........: " + url);
		FileResource entity = FileResource.findByUrl("/file/url/" + url);
		if (entity != null) {
			return entity.data;
		} else {
			return null;
		}
	}

	@POST
	@Transactional
	public Response create(FileRequest file, @HeaderParam("activeUser") String user) {

		FileResource entity = fileService.create(file);

		return Response.ok(entity).build();
	}

	@PUT
	@Path("{id}")
	@Transactional
	public Response update(@PathParam Long id, FileRequest file, @HeaderParam("activeUser") String user) {

		FileResource entity = fileService.update(id, file);
		entity.persist();
		return Response.ok(entity).build();
	}

	@DELETE
	@Path("{id}")
	@Transactional
	public Response delete(@PathParam Long id, @HeaderParam("activeUser") String user) {

		if (fileService.delete(id)) {
			return Response.ok().build();
		}
		throw new WebApplicationException("There was an error deleting this record!", 500);
	}

}
