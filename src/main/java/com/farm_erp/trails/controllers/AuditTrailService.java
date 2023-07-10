/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.farm_erp.trails.controllers;


import com.farm_erp.trails.domains.AuditTrail;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class AuditTrailService {

	public List<AuditTrail> get(Long identifier) {

		List<AuditTrail> data = AuditTrail.getByIdentifier(identifier);

		return data;

	}

}
