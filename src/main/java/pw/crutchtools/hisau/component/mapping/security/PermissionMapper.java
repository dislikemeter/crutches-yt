package pw.crutchtools.hisau.component.mapping.security;

import org.springframework.stereotype.Component;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import pw.crutchtools.hisau.component.mapping.ExistentObjectMapper;
import pw.crutchtools.hisau.controller.exceptions.RequestValidationException;
import pw.crutchtools.hisau.domain.security.Permission;

@Component
public class PermissionMapper implements ExistentObjectMapper<Permission> {
	private static final String REGEX_PERMISSION = ("^[A-Z_]+$");

	@Override
	public Permission mapToObject(Permission existentObject, String input) throws RequestValidationException {
		JsonObject requestObject = Json.parse(input).asObject();
		String newName = requestObject.getString("permissionName", null);
		if (newName != null && newName.toUpperCase().matches(REGEX_PERMISSION)) {
			existentObject.rename(newName);
		} else {
			throw new RequestValidationException("permissionName");
		}
		return existentObject;
	}

}
