package pw.crutchtools.hisau.controller.api.modules;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import pw.crutchtools.hisau.component.modules.MenuModule;
import pw.crutchtools.hisau.controller.api.AbstractAjaxAction;
import pw.crutchtools.hisau.service.modules.ModuleService;
import pw.crutchtools.hisau.service.security.AccountService;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping(value = AbstractAjaxAction.API_PATH + "dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
public class Dashboard extends AbstractAjaxAction {
	
	@Autowired
	ModuleService moduleService;
	
	@Autowired
	AccountService accountService;
	
	@GetMapping
	public String get() {
		Set<MenuModule> myModules = moduleService.getModulesForUser(getCurrentUser());
		
		JsonArray modulesArray = Json.array().asArray();
		myModules.stream().forEach(module -> {
			JsonObject moduleObject = module.toJson();
			moduleObject.add("fav", getCurrentUser().getUserProfile().isFavorite(module.hashCode()));
			modulesArray.add(moduleObject);
		});
		return modulesArray.toString();
	}
	
	@PostMapping(path = "/{id}")
	public String fav(@PathVariable(name = "id") int id) {
		boolean isFavorite = accountService.toggleFav(getCurrentUser(), id);
		return Json.object().add("fav", isFavorite).toString();
	}
	
}
