package pw.crutchtools.hisau.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class AccessDeniedException extends RuntimeException {

	public AccessDeniedException(String message) {
		super(message);
	}
	
	public AccessDeniedException(){
		super("You haven't permissions to perform this action");
	}
}
