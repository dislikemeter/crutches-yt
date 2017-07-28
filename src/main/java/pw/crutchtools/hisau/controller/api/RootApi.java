package pw.crutchtools.hisau.controller.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pw.crutchtools.hisau.controller.exceptions.ResourceNotFoundException;

@RestController
public class RootApi extends AbstractAjaxAction {

	@RequestMapping(value = AbstractAjaxAction.API_PATH + "**", produces = MediaType.APPLICATION_JSON_VALUE)
	public void anyUnhandledRequest() {
		throw new ResourceNotFoundException("This API doesn't exists");
	}
	
}
