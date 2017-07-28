package pw.crutchtools.hisau.controller.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import pw.crutchtools.hisau.controller.exceptions.AccessDeniedException;
import pw.crutchtools.hisau.controller.exceptions.AuthenticationRequired;
import pw.crutchtools.hisau.controller.exceptions.RequestValidationException;
import pw.crutchtools.hisau.domain.security.Account;

public abstract class AbstractAjaxAction {
	public static final String EMPTY_JSON = "{}";
	public static final String API_PATH = "/api/";
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public ResponseEntity<String> error(HttpServletRequest request, Exception e) {
		//Security tweaks
		if (e instanceof org.springframework.security.access.AccessDeniedException) {
			e = (getAuth() instanceof AnonymousAuthenticationToken) ? new AuthenticationRequired() : new AccessDeniedException();
		}
		
		//compile response
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpStatus responseStatus;
		try {
			ResponseStatus annotation = AnnotationUtils.getAnnotation(e.getClass(), ResponseStatus.class);
			if (annotation != null) {
				responseStatus = annotation.value();
			} else {
				throw e;
			}
		} catch (Exception ex) {
			responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		String responseBody = convertExceptionToJson(e);
		return new ResponseEntity<String>(responseBody, headers, responseStatus);
	}

	public static String convertExceptionToJson(Exception e) {
		JsonObject result = Json.object()
				.add("exception", e.getClass().getSimpleName())
				.add("message", e.getMessage());
		if (e instanceof RequestValidationException) {
			JsonArray fields = Json.array().asArray();
			for (String field : ((RequestValidationException) e).getErrorSet()) {
				fields.add(field);
			}
			result.add("errorFields", fields);
		}
		return result.toString();
	}

	public Authentication getAuth() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	public Account getCurrentUser() {
		Authentication auth = getAuth();
		return (auth instanceof AnonymousAuthenticationToken) ? null : (Account) auth.getPrincipal();
	}
}
