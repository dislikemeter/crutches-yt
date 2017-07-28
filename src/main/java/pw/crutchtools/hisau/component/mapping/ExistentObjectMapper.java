package pw.crutchtools.hisau.component.mapping;

import pw.crutchtools.hisau.controller.exceptions.RequestValidationException;

public interface ExistentObjectMapper<T> {
	
	public T mapToObject(T existentObject, String input) throws RequestValidationException;
	
}
