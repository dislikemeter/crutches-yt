package pw.crutchtools.hisau.controller.exceptions;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class RequestValidationException extends IllegalArgumentException {
	public static final String VALIDATION_ERROR_MESSAGE = "${validation-error}";
	
	private Set<String> errorSet;
	
	public RequestValidationException(Set<String> errors) {
		super(VALIDATION_ERROR_MESSAGE);
		errorSet = errors;
	};
	
	public RequestValidationException(String error) {
		super(VALIDATION_ERROR_MESSAGE);
		errorSet = new HashSet<>();
		errorSet.add(error);
	}
	
	public Set<String> getErrorSet(){
		return errorSet;
	}

}
