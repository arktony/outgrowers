package com.farm_erp.auth.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.farm_erp.statics._StatusTypes_Enum;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.Where;

@Entity
@Where(clause = "DELETED = 0")
public class Role extends PanacheEntity {

	@NotBlank
	public String name;

	@NotNull
	public String type;

	@Column(name = "DELETED")
	public Integer deleted = 0;

	public String description;

	public Boolean isApproved = false;

	public String status = _StatusTypes_Enum.ACTIVE.toString();

	@Column(nullable = false)
	public String reference = UUID.randomUUID().toString();

	@OneToMany(mappedBy = "role")
    @LazyCollection(LazyCollectionOption.FALSE)
//	@JsonbTransient
    public List<RoleCategory> categories = new ArrayList<>();

	public Role() {
		super();
	}
	
	public static Role findByNameByType(String name, String type) {

		Optional<Role> T = Role.find("name = ?1 and type = ?2", name, type).singleResultOptional();
		if (T.isPresent()) {
			return T.get();
		} else {
			return null;
		}
	}

}
