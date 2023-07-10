/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.farm_erp.fileresources.controllers.services;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class FileRequest {

	@Schema(example = "default_image")
	public String name; // common name of the file as seen in client filesystem

	@Schema(required = true, example = "png")
	public String fileType;

	@Schema(required = true, example = "Base 64 string")
	public String data; // base 64 String

	public FileRequest() {
	}

	public FileRequest(String name, String fileType, String data) {
		this.name = name;
		this.fileType = fileType;
		this.data = data;
	}

}
