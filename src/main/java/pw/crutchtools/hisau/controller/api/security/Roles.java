package pw.crutchtools.hisau.controller.api.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;

import pw.crutchtools.hisau.controller.api.AbstractAjaxAction;
import pw.crutchtools.hisau.controller.exceptions.BadRequestException;
import pw.crutchtools.hisau.controller.exceptions.RequestValidationException;
import pw.crutchtools.hisau.controller.exceptions.ServerErrorException;
import pw.crutchtools.hisau.domain.security.Role;
import pw.crutchtools.hisau.service.security.PermissionService;
import pw.crutchtools.hisau.service.security.RoleService;

@RestController
@RequestMapping(value = AbstractAjaxAction.API_PATH + "roles", produces = MediaType.APPLICATION_JSON_VALUE)
public class Roles extends AbstractAjaxAction {

	@Autowired
	RoleService roleService;
	PermissionService permissionService;

	@PreAuthorize("hasAnyAuthority('PERM_MANAGE_ROLES', 'PERM_MANAGE_USERS')")
	@RequestMapping(method = RequestMethod.GET)
	public String get() {
		List<Role> roles = roleService.getAllRoles();
		return rolesToJson(roles.toArray(new Role[roles.size()])).toString();
	}

	@PreAuthorize("hasAuthority('PERM_MANAGE_ROLES')")
	@RequestMapping(method = RequestMethod.POST)
	public String add(@RequestBody String request) {
		try {
			Role role = roleService.createRole(request);
			return rolesToJson(role).toString();
		} catch (DataIntegrityViolationException exception) {
			throw new BadRequestException(RequestValidationException.VALIDATION_ERROR_MESSAGE);
		}		
	}

	@PreAuthorize("hasAuthority('PERM_MANAGE_ROLES')")
	@RequestMapping(path = "/{roleId}", method = RequestMethod.POST)
	public String change(@RequestBody String request, @PathVariable("roleId") Long id) {
		try {
			Role role = roleService.changeRole(id, request);
			return rolesToJson(role).toString();
		} catch (DataIntegrityViolationException exception) {
			throw new BadRequestException(RequestValidationException.VALIDATION_ERROR_MESSAGE);
		}		
	}

	@PreAuthorize("hasAuthority('PERM_MANAGE_ROLES')")
	@RequestMapping(path = "/{roleId}", method = RequestMethod.DELETE)
	public String delete(@PathVariable("roleId") Long id) {
		try {
			roleService.deleteRole(id);
			return EMPTY_JSON;
		} catch (DataIntegrityViolationException exception) {
			throw new ServerErrorException("${cannot-delete-linked-role}");
		}		
	}

	private JsonArray rolesToJson(Role... roles) {
		JsonArray result = Json.array().asArray();
		for (Role role : roles) {
			result.add(role.toJson());
		}
		return result;
	}

}
