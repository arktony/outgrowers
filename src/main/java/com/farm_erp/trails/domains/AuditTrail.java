package com.farm_erp.trails.domains;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.farm_erp.auth.domain.User;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class AuditTrail extends PanacheEntity {

	@Column(nullable = false)
	public String action;

	@Column(nullable = false)
	public String section;

	@Column(nullable = false)
	public Long identifier;

	@Column(nullable = false)
	public LocalDateTime entryDate = LocalDateTime.now();

	@JsonbTransient
	@Column(columnDefinition = "LONGTEXT")
	public String oldData;

	@JsonbTransient
	@Column(columnDefinition = "LONGTEXT")
	public String newData;

	// Mapping
	@ManyToOne
	public User user;

	public String accessKey;

	public AuditTrail() {
	}

	public AuditTrail(String action, String section, Long identifier, String oldData, String newData, User user) {
		this.action = action;
		this.section = section;
		this.identifier = identifier;
		this.oldData = oldData;
		this.newData = newData;
		this.user = user;
	}

	public AuditTrail(String action, String section, Long identifier, String oldData, String newData,
			String accessKey) {
		this.action = action;
		this.section = section;
		this.identifier = identifier;
		this.oldData = oldData;
		this.newData = newData;
		this.accessKey = accessKey;
	}

	public static List<AuditTrail> getByIdentifier(Long Identifier) {
		List<AuditTrail> data = AuditTrail.find("identifier=?1 order by entryDate asc", Identifier).list();

		return data;
	}

	public static AuditTrail getByIdentifierByAction(Long identifier, String action) {
		Optional<AuditTrail> trail = find("identifier = ?1 and action = ?2", identifier, action).singleResultOptional();

		if (trail.isPresent()) {
			return trail.get();

		} else {
			return null;
		}

	}

}
