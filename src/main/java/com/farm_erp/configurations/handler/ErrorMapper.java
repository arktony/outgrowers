/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.farm_erp.configurations.handler;

import javax.json.Json;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author aiden
 */
public class ErrorMapper {

	@Provider
	public static class ErrorMapperx implements ExceptionMapper<Exception> {

		@Override
		public Response toResponse(Exception exception) {
			int code = 500;
			if (exception instanceof WebApplicationException) {
				code = ((WebApplicationException) exception).getResponse().getStatus();
			}

			exception.printStackTrace();

			return Response.status(code)
					.entity(Json.createObjectBuilder().add("error", exception.getMessage()).add("code", code).build())
					.build();
		}

	}
}
