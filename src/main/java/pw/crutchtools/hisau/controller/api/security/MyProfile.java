package pw.crutchtools.hisau.controller.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;

import pw.crutchtools.hisau.controller.api.AbstractAjaxAction;
import pw.crutchtools.hisau.domain.security.Account;
import pw.crutchtools.hisau.service.security.AccountService;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping(value = AbstractAjaxAction.API_PATH + "myprofile", produces = MediaType.APPLICATION_JSON_VALUE)
public class MyProfile extends AbstractAjaxAction {

	@Autowired
	AccountService accountService;

	@PostMapping
	public String post(@RequestBody String request) {
		Account currentUser = getCurrentUser();
		accountService.updateProfile(currentUser, request);
		JsonArray modalButtons = Json.array().asArray().add(Json.object().add("name", "${return-to-edit}"))
				.add(Json.object().add("name", "OK").add("icon", "i-ok").add("class", "green"));
		return Json.object().add("modal", Json.object().add("title", "OK")
				.add("content", "${profile-updated}").add("buttons", modalButtons)).toString();
	}

	@GetMapping
	public String get(){
		Account currentUser = getCurrentUser();
		return currentUser.toJson().toString();
	}
}
