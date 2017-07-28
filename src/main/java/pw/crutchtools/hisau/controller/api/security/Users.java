package pw.crutchtools.hisau.controller.api.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;

import pw.crutchtools.hisau.controller.api.AbstractAjaxAction;
import pw.crutchtools.hisau.controller.exceptions.BadRequestException;
import pw.crutchtools.hisau.controller.exceptions.RequestValidationException;
import pw.crutchtools.hisau.domain.security.Account;
import pw.crutchtools.hisau.service.security.AccountService;

@RestController
@PreAuthorize("hasAuthority('PERM_MANAGE_USERS')")
@RequestMapping(value = AbstractAjaxAction.API_PATH + "users", produces = MediaType.APPLICATION_JSON_VALUE)
public class Users extends AbstractAjaxAction {

	@Autowired
	private AccountService accountService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String get() {
		List<Account> accounts = accountService.getAllAccounts();
		JsonArray result = accountsToJson(accounts.toArray(new Account[accounts.size()]));
		return result.toString();
	}
	
	@RequestMapping(method = RequestMethod.POST, path = "/{accountId}")
	public String edit(@RequestBody String request, @PathVariable("accountId") Long id) {
		try {
			return accountsToJson(accountService.updateAccount(id, request)).toString();
		} catch (DataIntegrityViolationException exception) {
			throw new BadRequestException(RequestValidationException.VALIDATION_ERROR_MESSAGE);
		}
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String add(@RequestBody String request) {
		try {
			return accountsToJson(accountService.createAccount(request)).toString();
		} catch (DataIntegrityViolationException exception) {
			throw new BadRequestException(RequestValidationException.VALIDATION_ERROR_MESSAGE);
		}
	}
	
	@RequestMapping(method = RequestMethod.DELETE, path = "/{accountId}")
	public String delete(@PathVariable("accountId") Long id) {
		try {
			accountService.deleteAccount(id);
			return EMPTY_JSON;
		} catch (DataIntegrityViolationException exception) {
			throw new BadRequestException(RequestValidationException.VALIDATION_ERROR_MESSAGE);
		}
	}
	
	@RequestMapping(path = "/resetpass/{accountId}", method = RequestMethod.POST)
	public String resetpass(@PathVariable("accountId") Long id) {
		accountService.resetPassword(id);
		return EMPTY_JSON;
	}
	
	@RequestMapping(path = "/expire/{accountId}", method = RequestMethod.POST)
	public String expire(@PathVariable("accountId") Long id) {
		accountService.expire(id);
		return EMPTY_JSON;
	}
	
	private JsonArray accountsToJson(Account...accounts) {
		JsonArray result = Json.array().asArray();
		for (Account account : accounts) {
			result.add(account.toJson());
		}
		return result;
	}
	
}
