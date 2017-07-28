package pw.crutchtools.hisau.component.mapping.security;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import pw.crutchtools.hisau.component.mapping.ExistentObjectMapper;
import pw.crutchtools.hisau.controller.exceptions.RequestValidationException;
import pw.crutchtools.hisau.domain.security.Account;
import pw.crutchtools.hisau.domain.security.Role;
import pw.crutchtools.hisau.service.security.RoleService;

@Component
public class AccountMapper implements ExistentObjectMapper<Account> {
	private static final String REGEXP_NAME_REQUIRED = ("^[a-zA-Zа-яА-Я]{4,}$");
	private static final String REGEXP_NAME = ("(^[a-zA-Zа-яА-Я]{4,}$)|(^$)");
	private static final String REGEXP_EMAIL = ("^[a-zA-Z\\-_0-9]{3,}@([a-zA-Z\\-_0-9]{2,}\\.)+\\w{2,}$");
	private static final String REGEXP_PHONE = ("(^\\+?\\d{6,}$)|(^$)");

	@Autowired
	private RoleService roleService;
	
	@Override
	public Account mapToObject(Account existentObject, String input) throws RequestValidationException {
		Set<String> errors = new HashSet<>();
		JsonObject inputObject = Json.parse(input).asObject();
		
		String firstName = inputObject.getString("firstName", null);
		String lastName = inputObject.getString("lastName", null);
		String middleName = inputObject.getString("middleName", null);
		String email = inputObject.getString("email", null);
		String phone = inputObject.getString("phone", null);
		long roleId = inputObject.getLong("roleId", 0L);
		Role accountRole = roleId != 0L ? roleService.getById(roleId) : null;
		boolean accountEnabled = inputObject.getBoolean("enabled", true);
		JsonValue accountExpireDate = inputObject.get("expireDate");
		
		if (firstName == null || !firstName.matches(REGEXP_NAME_REQUIRED)) {
			errors.add("firstName");
		}
		if (lastName == null || !lastName.matches(REGEXP_NAME)) {
			errors.add("lastName");
		}
		if (middleName == null || !middleName.matches(REGEXP_NAME)) {
			errors.add("middleName");
		}
		if (email == null || !email.matches(REGEXP_EMAIL)) {
			errors.add("email");
		}
		if (phone != null && !phone.matches(REGEXP_PHONE)) {
			errors.add("phone");
		}
		if (accountRole == null) {
			errors.add("roleId");
		}
		//invalid only if accountExpireDate is not null/number of its before tomorrow
		if ((accountExpireDate == null) ||
			(!accountExpireDate.isNull() && !accountExpireDate.isNumber()) ||
			(accountExpireDate.isNumber() && accountExpireDate.asLong() < System.currentTimeMillis())) {
			errors.add("expireDate");
		}
		
		if (errors.size() > 0) {
			throw new RequestValidationException(errors);
		} else {
			existentObject.setUsername(email);
			existentObject.getUserProfile().setFirstName(firstName);
			existentObject.getUserProfile().setLastName(lastName);
			existentObject.getUserProfile().setMiddleName(middleName);
			existentObject.getUserProfile().setPhone(phone);
			existentObject.setRole(accountRole);
			existentObject.setEnabled(accountEnabled);
			existentObject.setAccountExpireDate(accountExpireDate.isNull() ? null : new Date(accountExpireDate.asLong()));
			return existentObject;
		}
	}
	
}
