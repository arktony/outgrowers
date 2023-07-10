package com.farm_erp.fileresources.controllers.services;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;

import com.farm_erp.fileresources.domain.FileResource;

@ApplicationScoped
public class FileService {

	public FileResource create(FileRequest file) {
		String systemName = UUID.randomUUID().toString();
		byte[] data = Base64.getDecoder().decode(file.data.getBytes());
		String url = "/file/url/" + systemName + "." + file.fileType;

		FileResource newfile = new FileResource(systemName, url, file.fileType, data);
		newfile.commonName = file.name;
		newfile.persist();

		return newfile;
	}

	public List<FileResource> createBulk(List<FileRequest> file) {
		List<FileResource> newfiles = new ArrayList<>();
		for (FileRequest f : file) {

			newfiles.add(create(f));
		}
		return newfiles;
	}

	public FileResource update(Long id, FileRequest file) {

		FileResource entity = FileResource.findById(id);

		if (entity == null) {
			throw new WebApplicationException("FileResource with these details does not exist.", 404);
		}

		String systemName = UUID.randomUUID().toString();
		byte[] data = Base64.getDecoder().decode(file.data.getBytes());

		entity.url = "/file/url/" + systemName + "." + file.fileType;
		entity.fileType = file.fileType;
		entity.commonName = file.name;
		entity.systemName = systemName;
		entity.data = data;
		entity.persist();

		return entity;
	}

	public Boolean delete(Long id) {

		FileResource entity = FileResource.findById(id);

		if (entity == null) {
			throw new WebApplicationException("FileResource with this Id does not exist.", 404);
		}

		entity.delete();

		return Boolean.TRUE;
	}

}
