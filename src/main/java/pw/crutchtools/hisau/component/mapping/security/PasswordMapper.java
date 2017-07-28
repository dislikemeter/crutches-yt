package pw.crutchtools.hisau.component.mapping.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import pw.crutchtools.hisau.app.SecurityConfig;
import pw.crutchtools.hisau.component.mapping.ExistentObjectMapper;
import pw.crutchtools.hisau.controller.exceptions.RequestValidationException;
import pw.crutchtools.hisau.domain.security.Account;

@Component
public class PasswordMapper implements ExistentObjectMapper<Account> {
	private static final String REGEXP_PASSWORD = "^[\\w:;.,!@#$%^&*(){}]{8,64}$";
	
	@Override
	public Account mapToObject(Account existentObject, String input) throws RequestValidationException {
		Set<String> errors = new HashSet<>();
		JsonObject inputObject = Json.parse(input).asObject();
		String oldPassword = inputObject.getString("password", "");
		String newPassword = inputObject.getString("newPassword", null);
		
		if (oldPassword.length() == 0 || !SecurityConfig.passwordEncoder().matches(oldPassword, existentObject.getPassword())) {
			errors.add("password");
		}
		
		if (newPassword == null || !newPassword.matches(REGEXP_PASSWORD) || oldPassword.equals(newPassword)) {
			errors.add("newPassword");
		}
		
		if (errors.size() == 0) {
			existentObject.changePassword(newPassword);
			return existentObject;
		} else {
			throw new RequestValidationException(errors);
		}
	}

}
