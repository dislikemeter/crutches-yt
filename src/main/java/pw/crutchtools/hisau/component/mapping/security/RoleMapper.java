package pw.crutchtools.hisau.component.mapping.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import pw.crutchtools.hisau.component.mapping.ExistentObjectMapper;
import pw.crutchtools.hisau.component.repo.security.PermissionRepository;
import pw.crutchtools.hisau.controller.exceptions.RequestValidationException;
import pw.crutchtools.hisau.domain.security.Permission;
import pw.crutchtools.hisau.domain.security.Role;

@Component
public class RoleMapper implements ExistentObjectMapper<Role> {
	private static final String REGEX_ROLE = ("^[A-Z_]+$");

	@Autowired
	private PermissionRepository permissionRepository;

	@Override
	public Role mapToObject(Role existentObject, String input) throws RequestValidationException {
		Set<String> errors = new HashSet<>();
		// parsing
		JsonObject inputObject = Json.parse(input).asObject();

		String name = inputObject.getString("roleName", null);
		if (name == null || !name.toUpperCase().matches(REGEX_ROLE))
			errors.add("roleName");

		Set<Permission> linkedPermissions = null;
		JsonValue linkedPermissionsJson = inputObject.get("linkedPermissions");
		if (linkedPermissionsJson == null || !linkedPermissionsJson.isArray()) {
			errors.add("linkedPermissions");
		} else {
			Set<Long> permissionIds = new HashSet<>();
			for (JsonValue permissionId : inputObject.get("linkedPermissions").asArray()) {
				permissionIds.add(permissionId.asLong());
			}
			linkedPermissions = new HashSet<Permission>(permissionRepository.findAll(permissionIds));
		}

		// return
		if (errors.size() > 0) {
			throw new RequestValidationException(errors);
		} else {
			existentObject.rename(name.toUpperCase());
			existentObject.setPermissions(linkedPermissions);
			return existentObject;
		}
	}

}
