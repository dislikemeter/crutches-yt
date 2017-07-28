package pw.crutchtools.hisau.component.mapping;

import pw.crutchtools.hisau.controller.exceptions.RequestValidationException;

public interface ParametrizedObjectMapper<T, P> {

	@SuppressWarnings("unchecked")
	public T mapToObject(String input, P... params) throws RequestValidationException;
	
}
