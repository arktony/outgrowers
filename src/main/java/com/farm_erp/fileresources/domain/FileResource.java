package com.farm_erp.fileresources.domain;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.Where;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = "url") })
public class FileResource extends PanacheEntity {

	@Column(nullable = false)
	public String systemName;

	public String commonName;

	@Column(nullable = false)
	public String url;

	@Column(nullable = false)
	public String fileType;

	@Lob
	@JsonbTransient
	@Column(nullable = false)
	public byte[] data;

	
	public LocalDateTime entryDate = LocalDateTime.now();

	public FileResource() {
	}

	public FileResource(String systemName, String url, String fileType, byte[] data) {
		this.systemName = systemName;
		this.url = url;
		this.fileType = fileType;
		this.data = data;
	}

	public static FileResource findByUrl(String url) {
		Optional<FileResource> file = FileResource.find("url", url).singleResultOptional();
		if (file.isPresent()) {
			return file.get();
		} else {
			return null;
		}
	}
}
