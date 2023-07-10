package com.farm_erp.system_initializer;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;

import com.farm_erp.auth.domain.Category;
import com.farm_erp.auth.domain.CategoryItem;
import com.farm_erp.auth.domain.CategoryPrivilege;
import com.farm_erp.auth.domain.Role;
import com.farm_erp.auth.domain.RoleCategory;
import com.farm_erp.auth.domain.RoleCategoryItem;
import com.farm_erp.auth.domain.RoleCategoryPrivilege;
import com.farm_erp.auth.domain.User;
import com.farm_erp.auth.statics.CategoryEnum;
import com.farm_erp.auth.statics.CategoryItemEnum;
import com.farm_erp.auth.statics.Privilege;
import com.farm_erp.outgrowers.domains.Farmer;
import com.farm_erp.settings.domains.Business;
import com.farm_erp.settings.domains.CropType;
import com.farm_erp.settings.domains.FarmerType;
import com.farm_erp.settings.domains.GeneralBusinessSettings;
import com.farm_erp.settings.domains.Village;
import com.farm_erp.settings.statics._SettingParameter_Enums;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.narayana.jta.runtime.TransactionConfiguration;
import io.quarkus.panache.common.Sort;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class Initializer {

	public void run(@Observes StartupEvent ev) {

		if (User.listAll().isEmpty()) {
			initiateDashboardCategories();
			initiateReportsCategories();
			initiateSettingsCategories();
			initiateOutGrowerCategories();
			initializeGeneralSettings();

			Business business = createBusiness();
			Role role = createSuperAdministratorRole();
			createSuperAdmin(role, business);

			createCropTypes();

			initiateNewCategories();

			updateFarmers();
		}

		// rectifyVillageCodes();
	}

	@Transactional
	public void rectifyVillageCodes() {
		List<Village> vills = Village.listAll(Sort.ascending("id"));
		int i = 1;
		for (Village v : vills) {
			v.code = v.district.code + String.format("%01d", i);
			i++;
		}
	}

	@Transactional
	public void initializeGeneralSettings() {
		GeneralBusinessSettings s1 = new GeneralBusinessSettings(
				_SettingParameter_Enums.PERMIT_GRACE_PERIOD.toString(), "3");
		s1.persist();

		GeneralBusinessSettings s2 = new GeneralBusinessSettings(
				_SettingParameter_Enums.AVERAGE_TONNES_PER_DAY.toString(), "10");
		s2.persist();

		GeneralBusinessSettings s3 = new GeneralBusinessSettings(
				_SettingParameter_Enums.FARMER_CODE_PREFIX.toString(), "GM");
		s3.persist();

		GeneralBusinessSettings s4 = new GeneralBusinessSettings(
				_SettingParameter_Enums.MATURITY_PERIOD.toString(), "18");
		s4.persist();

		GeneralBusinessSettings s5 = new GeneralBusinessSettings(
				_SettingParameter_Enums.COMPULSORY_DEDUCTION.toString(), "5");
		s5.persist();

		GeneralBusinessSettings s6 = new GeneralBusinessSettings(
				_SettingParameter_Enums.PAYMENT_PER_TONNE.toString(), "100000");
		s6.persist();
	}

	@Transactional
	public void createCropTypes() {
		CropType r1 = new CropType("Plant", "PL", 40.0, 1);
		r1.persist();

		CropType r2 = new CropType("Ratoon 1", "R1", 35.0, 2);
		r2.persist();

		CropType r3 = new CropType("Ratoon 2", "R2", 30.0, 3);
		r3.persist();

		CropType r4 = new CropType("Ratoon 3", "R2", 35.0, 4);
		r4.persist();
	}

	@Transactional
	public void initiateDashboardCategories() {
		/**
		 * Dashboard
		 */
		Category dashboard = new Category(CategoryEnum.DASHBOARD.label, CategoryEnum.DASHBOARD.toString(),
				CategoryEnum.DASHBOARD.label);
		dashboard.persist();

		CategoryItem dashboardI = new CategoryItem(CategoryItemEnum.DASHBOARD.label,
				CategoryItemEnum.DASHBOARD.toString(), CategoryItemEnum.DASHBOARD.label, dashboard);
		dashboardI.persist();

		CategoryPrivilege W1 = new CategoryPrivilege(Privilege.VIEW.label, Privilege.VIEW.toString(),
				dashboardI);
		W1.persist();

	}

	@Transactional
	public void initiateNewCategories() {
		/**
		 * Payments
		 */
		Category payd = Category.find("nameEnum", CategoryEnum.PAYMENTS.toString()).firstResult();
		if (payd == null) {
			Category pay = new Category(CategoryEnum.PAYMENTS.label, CategoryEnum.PAYMENTS.toString(),
					CategoryEnum.DASHBOARD.label);
			pay.persist();

			// sessions
			CategoryItem p1 = new CategoryItem(CategoryItemEnum.SESSIONS.label,
					CategoryItemEnum.SESSIONS.toString(), CategoryItemEnum.SESSIONS.label, pay);
			p1.persist();

			CategoryPrivilege p12 = new CategoryPrivilege(Privilege.VIEW.label, Privilege.VIEW.toString(),
					p1);
			p12.persist();

			CategoryPrivilege p121 = new CategoryPrivilege(Privilege.CREATE.label,
					Privilege.CREATE.toString(), p1);
			p121.persist();

			CategoryPrivilege p122 = new CategoryPrivilege(Privilege.UPDATE.label,
					Privilege.UPDATE.toString(), p1);
			p122.persist();

			CategoryPrivilege p123 = new CategoryPrivilege(Privilege.CLOSE.label,
					Privilege.CLOSE.toString(), p1);
			p123.persist();

			CategoryPrivilege p124 = new CategoryPrivilege(Privilege.DELETE.label,
					Privilege.DELETE.toString(), p1);
			p124.persist();

			// transactions
			CategoryItem p1q = new CategoryItem(CategoryItemEnum.SESSIONS.label,
					CategoryItemEnum.SESSIONS.toString(), CategoryItemEnum.SESSIONS.label, pay);
			p1q.persist();

			CategoryPrivilege p12q = new CategoryPrivilege(Privilege.VIEW.label, Privilege.VIEW.toString(),
					p1q);
			p12q.persist();

			CategoryPrivilege p121q = new CategoryPrivilege(Privilege.CREATE.label,
					Privilege.CREATE.toString(), p1q);
			p121q.persist();

			CategoryPrivilege p122q = new CategoryPrivilege(Privilege.UPDATE.label,
					Privilege.UPDATE.toString(), p1q);
			p122q.persist();

			CategoryPrivilege p123q = new CategoryPrivilege(Privilege.APPROVE.label,
					Privilege.APPROVE.toString(), p1q);
			p123q.persist();

			CategoryPrivilege p125q = new CategoryPrivilege(Privilege.REJECT.label,
					Privilege.REJECT.toString(), p1q);
			p125q.persist();

			CategoryPrivilege p124q = new CategoryPrivilege(Privilege.DELETE.label,
					Privilege.DELETE.toString(), p1q);
			p124q.persist();

			/// adjust
			p1q.privileges.add(p12q);
			p1q.privileges.add(p121q);
			p1q.privileges.add(p122q);
			p1q.privileges.add(p123q);
			p1q.privileges.add(p124q);
			p1q.privileges.add(p125q);
			pay.items.add(p1q);

			p1.privileges.add(p12);
			p1.privileges.add(p121);
			p1.privileges.add(p122);
			p1.privileges.add(p123);
			p1.privileges.add(p124);
			pay.items.add(p1);

			/// add to existing roles
			List<Role> roles = Role.listAll();
			for (Role role : roles) {
				RoleCategory rcat = new RoleCategory(pay.name, pay.nameEnum, pay.description, role,
						pay);
				rcat.persist();

				for (CategoryItem itm : pay.items) {
					RoleCategoryItem ritm = new RoleCategoryItem(itm.name, itm.nameEnum,
							itm.description, rcat);
					ritm.persist();

					for (CategoryPrivilege p : itm.privileges) {
						RoleCategoryPrivilege priv = new RoleCategoryPrivilege(p.name,
								p.nameEnum, ritm);
						priv.persist();
					}
				}
			}
		}

		int t = FarmerType.listAll().size();
		if (t == 0) {
			FarmerType t1 = new FarmerType("Outgrower Farmer", "OF");
			t1.persist();

			FarmerType t2 = new FarmerType("Nuclear Farmer", "NF");
			t2.persist();
		}
	}

	@Transactional
	@TransactionConfiguration(timeout = 24000000)
	public void updateFarmers() {
		List<Farmer> farmers = Farmer.listAll();

		farmers.forEach(farmer -> {
			if (farmer.type == null) {
				FarmerType type = FarmerType.find("name", "Outgrower Farmer").firstResult();
				if (type == null)
					throw new WebApplicationException("Types not yet created", 404);

				farmer.type = type;

				GeneralBusinessSettings set = GeneralBusinessSettings
						.single(_SettingParameter_Enums.FARMER_CODE_PREFIX.toString());
				String ledgerT = farmer.registrationNumber.replace(set.settingValue, "");
				farmer.registrationNumber = set.settingValue + farmer.districtOffice.district.code
						+ type.code + ledgerT;
			}

			GeneralBusinessSettings set = GeneralBusinessSettings
					.single(_SettingParameter_Enums.FARMER_CODE_PREFIX.toString());
			String ledgerT = farmer.registrationNumber.replace(
					set.settingValue + farmer.districtOffice.district.code + farmer.type.code, "");

			if (ledgerT.length() < 4) {
				farmer.registrationNumber = set.settingValue + farmer.districtOffice.district.code
						+ farmer.type.code + String.format("%04d", Integer.parseInt(ledgerT));
			}
		});
	}

	@Transactional
	public void initiateOutGrowerCategories() {
		Category outgrower = new Category(CategoryEnum.OUTGROWER_MANAGEMENT.label,
				CategoryEnum.OUTGROWER_MANAGEMENT.toString(),
				CategoryEnum.OUTGROWER_MANAGEMENT.label);
		outgrower.persist();

		// PERMITS
		CategoryItem permit = new CategoryItem(CategoryItemEnum.PERMIT.label,
				CategoryItemEnum.PERMIT.toString(),
				CategoryItemEnum.PERMIT.label, outgrower);
		permit.persist();

		CategoryPrivilege Z1r = new CategoryPrivilege(Privilege.CREATE.label, Privilege.CREATE.toString(),
				permit);
		Z1r.persist();

		CategoryPrivilege Z4r = new CategoryPrivilege(Privilege.END.label, Privilege.END.toString(), permit);
		Z4r.persist();

		CategoryPrivilege Z3r = new CategoryPrivilege(Privilege.VIEW.label, Privilege.VIEW.toString(), permit);
		Z3r.persist();

		CategoryPrivilege Z2r = new CategoryPrivilege(Privilege.EXTEND.label, Privilege.EXTEND.toString(),
				permit);
		Z2r.persist();

		// AID BLOCK
		CategoryItem aidBlock = new CategoryItem(CategoryItemEnum.AID_BLOCK.label,
				CategoryItemEnum.AID_BLOCK.toString(),
				CategoryItemEnum.AID_BLOCK.label, outgrower);
		aidBlock.persist();

		CategoryPrivilege Z1 = new CategoryPrivilege(Privilege.CREATE.label, Privilege.CREATE.toString(),
				aidBlock);
		Z1.persist();

		CategoryPrivilege Z4 = new CategoryPrivilege(Privilege.UPDATE.label, Privilege.UPDATE.toString(),
				aidBlock);
		Z4.persist();

		CategoryPrivilege Z3 = new CategoryPrivilege(Privilege.VIEW.label, Privilege.VIEW.toString(), aidBlock);
		Z3.persist();

		CategoryPrivilege Z2 = new CategoryPrivilege(Privilege.DELETE.label, Privilege.DELETE.toString(),
				aidBlock);
		Z2.persist();

		// BLOCKS
		CategoryItem blocks = new CategoryItem(CategoryItemEnum.BLOCKS.label,
				CategoryItemEnum.BLOCKS.toString(),
				CategoryItemEnum.BLOCKS.label, outgrower);
		blocks.persist();

		CategoryPrivilege C1 = new CategoryPrivilege(Privilege.CREATE.label, Privilege.CREATE.toString(),
				blocks);
		C1.persist();

		CategoryPrivilege C4 = new CategoryPrivilege(Privilege.UPDATE.label, Privilege.UPDATE.toString(),
				blocks);
		C4.persist();

		CategoryPrivilege C3 = new CategoryPrivilege(Privilege.VIEW.label, Privilege.VIEW.toString(), blocks);
		C3.persist();

		CategoryPrivilege C2 = new CategoryPrivilege(Privilege.VERIFY.label, Privilege.VERIFY.toString(),
				blocks);
		C2.persist();

		CategoryPrivilege C6 = new CategoryPrivilege(Privilege.DELETE.label, Privilege.DELETE.toString(),
				blocks);
		C6.persist();

		// BLOCK CYCLES
		CategoryItem blockCycles = new CategoryItem(CategoryItemEnum.BLOCK_CYCLE.label,
				CategoryItemEnum.BLOCK_CYCLE.toString(),
				CategoryItemEnum.BLOCK_CYCLE.label, outgrower);
		blockCycles.persist();

		CategoryPrivilege V1 = new CategoryPrivilege(Privilege.CREATE.label, Privilege.CREATE.toString(),
				blockCycles);
		V1.persist();

		CategoryPrivilege V3 = new CategoryPrivilege(Privilege.VIEW.label, Privilege.VIEW.toString(),
				blockCycles);
		V3.persist();

		CategoryPrivilege V2 = new CategoryPrivilege(Privilege.DELETE.label, Privilege.DELETE.toString(),
				blockCycles);
		V2.persist();

		// FARMERS
		CategoryItem farmers = new CategoryItem(CategoryItemEnum.FARMERS.label,
				CategoryItemEnum.FARMERS.toString(),
				CategoryItemEnum.FARMERS.label, outgrower);
		farmers.persist();

		CategoryPrivilege p1 = new CategoryPrivilege(Privilege.CREATE.label, Privilege.CREATE.toString(),
				farmers);
		p1.persist();

		CategoryPrivilege p2 = new CategoryPrivilege(Privilege.UPDATE.label, Privilege.UPDATE.toString(),
				farmers);
		p2.persist();

		CategoryPrivilege p3 = new CategoryPrivilege(Privilege.VERIFY.label, Privilege.VERIFY.toString(),
				farmers);
		p3.persist();

		CategoryPrivilege p4 = new CategoryPrivilege(Privilege.ACTIVATE.label, Privilege.ACTIVATE.toString(),
				farmers);
		p4.persist();

		CategoryPrivilege p5 = new CategoryPrivilege(Privilege.SUSPEND.label, Privilege.SUSPEND.toString(),
				farmers);
		p5.persist();

		CategoryPrivilege p6 = new CategoryPrivilege(Privilege.REINSTATE.label, Privilege.REINSTATE.toString(),
				farmers);
		p6.persist();

		CategoryPrivilege p10 = new CategoryPrivilege(Privilege.VIEW.label, Privilege.VIEW.toString(), farmers);
		p10.persist();

		CategoryPrivilege p11 = new CategoryPrivilege(Privilege.DELETE.label, Privilege.DELETE.toString(),
				farmers);
		p11.persist();

		/**
		 * Settings
		 */
		Category settings = new Category(CategoryEnum.OUTGROWER_SETTINGS.label,
				CategoryEnum.OUTGROWER_SETTINGS.toString(),
				CategoryEnum.OUTGROWER_SETTINGS.label);
		settings.persist();

		// CROP_TYPES
		CategoryItem ratoon = new CategoryItem(CategoryItemEnum.CROP_TYPES.label,
				CategoryItemEnum.CROP_TYPES.toString(),
				CategoryItemEnum.CROP_TYPES.label, settings);
		ratoon.persist();

		CategoryPrivilege N11 = new CategoryPrivilege(Privilege.CREATE.label, Privilege.CREATE.toString(),
				ratoon);
		N11.persist();

		CategoryPrivilege N41 = new CategoryPrivilege(Privilege.UPDATE.label, Privilege.UPDATE.toString(),
				ratoon);
		N41.persist();

		CategoryPrivilege N31 = new CategoryPrivilege(Privilege.VIEW.label, Privilege.VIEW.toString(), ratoon);
		N31.persist();

		CategoryPrivilege N21 = new CategoryPrivilege(Privilege.DELETE.label, Privilege.DELETE.toString(),
				ratoon);
		N21.persist();

		// AID
		CategoryItem aid = new CategoryItem(CategoryItemEnum.AID.label, CategoryItemEnum.AID.toString(),
				CategoryItemEnum.AID.label, settings);
		aid.persist();

		CategoryPrivilege X111 = new CategoryPrivilege(Privilege.CREATE.label, Privilege.CREATE.toString(),
				aid);
		X111.persist();

		CategoryPrivilege X41 = new CategoryPrivilege(Privilege.UPDATE.label, Privilege.UPDATE.toString(), aid);
		X41.persist();

		CategoryPrivilege X31 = new CategoryPrivilege(Privilege.VIEW.label, Privilege.VIEW.toString(), aid);
		X31.persist();

		CategoryPrivilege X21 = new CategoryPrivilege(Privilege.DELETE.label, Privilege.DELETE.toString(), aid);
		X21.persist();

		// CANE VARIETY
		CategoryItem caneTypes = new CategoryItem(CategoryItemEnum.CANE_VARIETY.label,
				CategoryItemEnum.CANE_VARIETY.toString(),
				CategoryItemEnum.CANE_VARIETY.label, settings);
		caneTypes.persist();

		CategoryPrivilege B11 = new CategoryPrivilege(Privilege.CREATE.label, Privilege.CREATE.toString(),
				caneTypes);
		B11.persist();

		CategoryPrivilege B41 = new CategoryPrivilege(Privilege.UPDATE.label, Privilege.UPDATE.toString(),
				caneTypes);
		B41.persist();

		CategoryPrivilege B31 = new CategoryPrivilege(Privilege.VIEW.label, Privilege.VIEW.toString(),
				caneTypes);
		B31.persist();

		CategoryPrivilege B21 = new CategoryPrivilege(Privilege.DELETE.label, Privilege.DELETE.toString(),
				caneTypes);
		B21.persist();
	}

	@Transactional
	public void initiateReportsCategories() {
		/**
		 * Reports
		 */
		Category reports = new Category(CategoryEnum.REPORTS.label, CategoryEnum.REPORTS.toString(),
				CategoryEnum.REPORTS.label);
		reports.persist();

		CategoryItem financial = new CategoryItem(CategoryItemEnum.OUTGROWER_REPORTS.label,
				CategoryItemEnum.OUTGROWER_REPORTS.toString(), CategoryItemEnum.OUTGROWER_REPORTS.label,
				reports);
		financial.persist();

		CategoryPrivilege A3 = new CategoryPrivilege(Privilege.VIEW.label, Privilege.VIEW.toString(),
				financial);
		A3.persist();

		// CategoryItem inventoryReport = new
		// CategoryItem(CategoryItemEnum.INVENTORY_REPORTS.label,
		// CategoryItemEnum.INVENTORY_REPORTS.toString(),
		// CategoryItemEnum.INVENTORY_REPORTS.label, reports);
		// inventoryReport.persist();
		//
		// CategoryPrivilege S3 = new CategoryPrivilege(Privilege.VIEW.label,
		// Privilege.VIEW.toString(), inventoryReport);
		// S3.persist();
		//
		// CategoryItem jobReport = new CategoryItem(CategoryItemEnum.JOB_REPORTS.label,
		// CategoryItemEnum.JOB_REPORTS.toString(), CategoryItemEnum.JOB_REPORTS.label,
		// reports);
		// jobReport.persist();
		//
		// CategoryPrivilege D3 = new CategoryPrivilege(Privilege.VIEW.label,
		// Privilege.VIEW.toString(), jobReport);
		// D3.persist();

		// REPORT ACCESS
		// CategoryItem reportAccess = new
		// CategoryItem(CategoryItemEnum.REPORT_ACCESS.label,
		// CategoryItemEnum.REPORT_ACCESS.toString(),
		// CategoryItemEnum.REPORT_ACCESS.label, reports);
		// reportAccess.persist();
		//
		// CategoryPrivilege F3 = new CategoryPrivilege(Privilege.VIEW.label,
		// Privilege.VIEW.toString(), reportAccess);
		// F3.persist();
	}

	@Transactional
	public void initiateSettingsCategories() {
		/**
		 * Settings
		 */
		Category settings = new Category(CategoryEnum.SETTINGS.label, CategoryEnum.SETTINGS.toString(),
				CategoryEnum.SETTINGS.label);
		settings.persist();

		// USERS
		CategoryItem users = new CategoryItem(CategoryItemEnum.USERS.label, CategoryItemEnum.USERS.toString(),
				CategoryItemEnum.USERS.label, settings);
		users.persist();

		CategoryPrivilege Z1 = new CategoryPrivilege(Privilege.CREATE.label, Privilege.CREATE.toString(),
				users);
		Z1.persist();

		CategoryPrivilege Z4 = new CategoryPrivilege(Privilege.UPDATE.label, Privilege.UPDATE.toString(),
				users);
		Z4.persist();

		CategoryPrivilege Z3 = new CategoryPrivilege(Privilege.VIEW.label, Privilege.VIEW.toString(), users);
		Z3.persist();

		CategoryPrivilege Z2 = new CategoryPrivilege(Privilege.DELETE.label, Privilege.DELETE.toString(),
				users);
		Z2.persist();

		// ROLES
		CategoryItem roles = new CategoryItem(CategoryItemEnum.ROLES.label, CategoryItemEnum.ROLES.toString(),
				CategoryItemEnum.ROLES.label, settings);
		roles.persist();

		CategoryPrivilege X1 = new CategoryPrivilege(Privilege.CREATE.label, Privilege.CREATE.toString(),
				roles);
		X1.persist();

		CategoryPrivilege X4 = new CategoryPrivilege(Privilege.UPDATE.label, Privilege.UPDATE.toString(),
				roles);
		X4.persist();

		CategoryPrivilege X3 = new CategoryPrivilege(Privilege.VIEW.label, Privilege.VIEW.toString(), roles);
		X3.persist();

		CategoryPrivilege X2 = new CategoryPrivilege(Privilege.DELETE.label, Privilege.DELETE.toString(),
				roles);
		X2.persist();

		// DISTRICTS
		CategoryItem zones = new CategoryItem(CategoryItemEnum.DISTRICTS.label,
				CategoryItemEnum.DISTRICTS.toString(),
				CategoryItemEnum.DISTRICTS.label, settings);
		zones.persist();

		CategoryPrivilege C1 = new CategoryPrivilege(Privilege.CREATE.label, Privilege.CREATE.toString(),
				zones);
		C1.persist();

		CategoryPrivilege C4 = new CategoryPrivilege(Privilege.UPDATE.label, Privilege.UPDATE.toString(),
				zones);
		C4.persist();

		CategoryPrivilege C3 = new CategoryPrivilege(Privilege.VIEW.label, Privilege.VIEW.toString(), zones);
		C3.persist();

		CategoryPrivilege C2 = new CategoryPrivilege(Privilege.DELETE.label, Privilege.DELETE.toString(),
				zones);
		C2.persist();

		// DISTRICT OFFICES
		CategoryItem subzones = new CategoryItem(CategoryItemEnum.DISTRICT_OFFICES.label,
				CategoryItemEnum.DISTRICT_OFFICES.toString(),
				CategoryItemEnum.DISTRICT_OFFICES.label, settings);
		subzones.persist();

		CategoryPrivilege V1 = new CategoryPrivilege(Privilege.CREATE.label, Privilege.CREATE.toString(),
				subzones);
		V1.persist();

		CategoryPrivilege V4 = new CategoryPrivilege(Privilege.UPDATE.label, Privilege.UPDATE.toString(),
				subzones);
		V4.persist();

		CategoryPrivilege V3 = new CategoryPrivilege(Privilege.VIEW.label, Privilege.VIEW.toString(), subzones);
		V3.persist();

		CategoryPrivilege V2 = new CategoryPrivilege(Privilege.DELETE.label, Privilege.DELETE.toString(),
				subzones);
		V2.persist();

		// VILLAGES
		CategoryItem village = new CategoryItem(CategoryItemEnum.VILLAGES.label,
				CategoryItemEnum.VILLAGES.toString(),
				CategoryItemEnum.VILLAGES.label, settings);
		village.persist();

		CategoryPrivilege B1 = new CategoryPrivilege(Privilege.CREATE.label, Privilege.CREATE.toString(),
				village);
		B1.persist();

		CategoryPrivilege B4 = new CategoryPrivilege(Privilege.UPDATE.label, Privilege.UPDATE.toString(),
				village);
		B4.persist();

		CategoryPrivilege B3 = new CategoryPrivilege(Privilege.VIEW.label, Privilege.VIEW.toString(), village);
		B3.persist();

		CategoryPrivilege B2 = new CategoryPrivilege(Privilege.DELETE.label, Privilege.DELETE.toString(),
				village);
		B2.persist();

		// GENERAL BUSINESS SETTINGS
		CategoryItem gbusiness = new CategoryItem(CategoryItemEnum.SETTINGS.label,
				CategoryItemEnum.SETTINGS.toString(),
				CategoryItemEnum.SETTINGS.label, settings);
		gbusiness.persist();

		CategoryPrivilege N1 = new CategoryPrivilege(Privilege.CREATE.label, Privilege.CREATE.toString(),
				gbusiness);
		N1.persist();

		CategoryPrivilege N4 = new CategoryPrivilege(Privilege.UPDATE.label, Privilege.UPDATE.toString(),
				gbusiness);
		N4.persist();

		CategoryPrivilege N3 = new CategoryPrivilege(Privilege.VIEW.label, Privilege.VIEW.toString(),
				gbusiness);
		N3.persist();

		CategoryPrivilege N2 = new CategoryPrivilege(Privilege.DELETE.label, Privilege.DELETE.toString(),
				gbusiness);
		N2.persist();

		// BUSINESS
		CategoryItem business = new CategoryItem(CategoryItemEnum.BUSINESS.label,
				CategoryItemEnum.BUSINESS.toString(),
				CategoryItemEnum.BUSINESS.label, settings);
		business.persist();

		CategoryPrivilege B111 = new CategoryPrivilege(Privilege.UPDATE.label, Privilege.UPDATE.toString(),
				business);
		B111.persist();
	}

	@Transactional
	public Role createSuperAdministratorRole() {
		Role newrole = new Role();
		newrole.name = "Super Administrator";
		newrole.type = "Super Administrator";
		newrole.reference = UUID.randomUUID().toString();
		newrole.isApproved = Boolean.TRUE;
		newrole.persist();

		return newrole;
	}

	@Transactional
	public void createSuperAdmin(Role role, Business business) {
		String genpassword = "123";
		// String genpassword = RandomGenerator.randomString(6);
		String password = BcryptUtil.bcryptHash(genpassword);

		User newuser = new User();
		newuser.email = "admin@gmail.com";
		newuser.password = password;
		newuser.firstname = "Super";
		newuser.lastname = "Admin";
		newuser.role = role;
		newuser.business = business;
		newuser.persist();

	}

	@Transactional
	public Business createBusiness() {
		Business business = new Business("GM Sugar");
		business.persist();

		return business;
	}
}
