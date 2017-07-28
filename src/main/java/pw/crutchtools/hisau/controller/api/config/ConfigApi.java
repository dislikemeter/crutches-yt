package pw.crutchtools.hisau.controller.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import pw.crutchtools.hisau.controller.api.AbstractAjaxAction;
import pw.crutchtools.hisau.controller.exceptions.RequestValidationException;
import pw.crutchtools.hisau.domain.config.ConfigurationNode;
import pw.crutchtools.hisau.service.config.ConfigurationService;

@RestController
@PreAuthorize("hasAuthority('PERM_CONFIG')")
@RequestMapping(value = AbstractAjaxAction.API_PATH + "config", produces = MediaType.APPLICATION_JSON_VALUE)
public class ConfigApi extends AbstractAjaxAction {
	
	@Autowired
	ConfigurationService configService;

	@GetMapping
	public String get() {
		JsonArray result = Json.array().asArray();
		configService.getAll().stream().forEach(node -> {
			result.add(node.toJson());
		});
		return result.toString();
	}
	
	@PostMapping
	public String addOrChange(@RequestBody String request){
		JsonObject requestObject = Json.parse(request).asObject();
		String paramName = requestObject.getString("name", null);
		String paramValue = requestObject.getString("value", null);
		if (paramName != null && paramValue != null) {
			ConfigurationNode result = configService.saveParameter(paramName, paramValue);
			return result.toJson().toString();
		} else {
			throw new RequestValidationException("Param cannot be null!");
		}
	}
	
	@DeleteMapping(value = "/{nodeId}")
	public String delete(@PathVariable(name = "nodeId") Long id) {
		configService.deleteParameter(id);
		return EMPTY_JSON;
	}
	
}
