package pw.crutchtools.hisau.controller.api.commons;

import java.util.List;

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
import com.eclipsesource.json.JsonObject;

import pw.crutchtools.hisau.controller.api.AbstractAjaxAction;
import pw.crutchtools.hisau.controller.exceptions.BadRequestException;
import pw.crutchtools.hisau.domain.commons.Tag;
import pw.crutchtools.hisau.service.commons.TagService;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping(value = AbstractAjaxAction.API_PATH + "tags", produces = MediaType.APPLICATION_JSON_VALUE)
public class TagApi {
	
	@Autowired
	TagService tagService;
	
	@GetMapping
	public String getAll() {
		List<Tag> allTags = tagService.getAllTags();
		JsonArray result = Json.array().asArray();
		allTags.stream().forEach(t->result.add(t.toJson()));
		return result.toString();
	}
	
	@PostMapping
	public String getOrAdd(@RequestBody String request) {
		JsonObject requestObject = Json.object().asObject();
		String newTagName = requestObject.getString("name", null);
		if (newTagName == null) throw new BadRequestException("You should specify tag name");
		Tag result = tagService.getOrAddTag(newTagName);
		return result.toJson().toString();
	}
	
}
