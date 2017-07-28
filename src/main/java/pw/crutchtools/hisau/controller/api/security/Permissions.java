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
import pw.crutchtools.hisau.domain.security.Permission;
import pw.crutchtools.hisau.service.security.PermissionService;

@RestController
@PreAuthorize("hasAuthority('PERM_MANAGE_ROLES')")
@RequestMapping(value = AbstractAjaxAction.API_PATH + "permissions", produces = MediaType.APPLICATION_JSON_VALUE)
public class Permissions extends AbstractAjaxAction {

	@Autowired
	PermissionService permissionService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(){
		List<Permission> allPermissions = permissionService.getAllPermissions();
		return permissionsToJson(allPermissions.toArray(new Permission[allPermissions.size()])).toString();
	}

	@RequestMapping(method = RequestMethod.POST)
	public String add(@RequestBody String request) {
		try {
			Permission permission = permissionService.create(request);
			return permissionsToJson(permission).toString();
		} catch (DataIntegrityViolationException exception) {
			throw new BadRequestException(RequestValidationException.VALIDATION_ERROR_MESSAGE);
		}
	}
	
	@RequestMapping(path = "/{permId}", method = RequestMethod.POST)
	public String change(@RequestBody String request, @PathVariable("permId") Long id) {
		try {
			Permission permission = permissionService.changePermission(id, request);
			return permissionsToJson(permission).toString();
		} catch (DataIntegrityViolationException exception) {
			throw new BadRequestException(RequestValidationException.VALIDATION_ERROR_MESSAGE);
		}
	}
	
	@RequestMapping(path = "/{permId}", method = RequestMethod.DELETE)
	public String delete(@PathVariable("permId") Long id) {
		try {
			permissionService.delete(id);
			return EMPTY_JSON;
		} catch (DataIntegrityViolationException exception) {
			throw new ServerErrorException("${cannot-delete-linked-role}");
		}
	}
	
	private JsonArray permissionsToJson(Permission... permissions) {
		JsonArray result = Json.array().asArray();
		for (Permission permission : permissions) {
			result.add(permission.toJson());
		}
		return result;
	}
	
}
