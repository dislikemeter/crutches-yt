package pw.crutchtools.hisau.component.mapping.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import pw.crutchtools.hisau.component.mapping.ExistentObjectMapper;
import pw.crutchtools.hisau.controller.exceptions.RequestValidationException;
import pw.crutchtools.hisau.domain.security.Account;

@Component
public class OwnProfileMapper implements ExistentObjectMapper<Account> {
	private static final String REGEXP_NAME_REQUIRED = ("^[a-zA-Zа-яА-Я]{4,}$");
	private static final String REGEXP_NAME = ("(^[a-zA-Zа-яА-Я]{4,}$)|(^$)");
	private static final String REGEXP_PHONE = ("(^\\+?\\d{6,}$)|(^$)");

	@Override
	public Account mapToObject(Account existentObject, String input) throws RequestValidationException {
		Set<String> errors = new HashSet<>();
		JsonObject inputObject = Json.parse(input).asObject();
		
		String firstName = inputObject.getString("firstName", null);
		String lastName = inputObject.getString("lastName", null);
		String middleName = inputObject.getString("middleName", null);
		String phone = inputObject.getString("phone", null);
		
		if (firstName == null || !firstName.matches(REGEXP_NAME_REQUIRED)) {
			errors.add("firstName");
		}
		if (lastName == null || !lastName.matches(REGEXP_NAME)) {
			errors.add("lastName");
		}
		if (middleName == null || !middleName.matches(REGEXP_NAME)) {
			errors.add("middleName");
		}
		if (phone == null || !phone.matches(REGEXP_PHONE)) {
			errors.add("phone");
		}
		
		if (errors.size() > 0) {
			throw new RequestValidationException(errors);
		} else {
			existentObject.getUserProfile().setFirstName(firstName);
			existentObject.getUserProfile().setLastName(lastName);
			existentObject.getUserProfile().setMiddleName(middleName);
			existentObject.getUserProfile().setPhone(phone);
			return existentObject;
		}
	}

}
