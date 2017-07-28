package pw.crutchtools.hisau.component.mapping;

import pw.crutchtools.hisau.controller.exceptions.RequestValidationException;

public interface SimpleObjectMapper<T> {
	
	public T mapToObject(String input) throws RequestValidationException;

}
