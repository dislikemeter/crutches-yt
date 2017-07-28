package pw.crutchtools.hisau.service.security;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import pw.crutchtools.hisau.domain.security.Account;

public interface AccountService extends UserDetailsService {
	
	public Account createPrimaryAccount(String email, String password);
	
	public void updateProfile(Account currentAccount, String request);
	
	public void changeMyPassword(Account account, String request);
	
	public List<Account> getAllAccounts();
	
	public Account updateAccount(Long id, String request);
	
	public Account createAccount(String request);
	
	public void deleteAccount(Long id);
	
	public void resetPassword(Long id);
	
	public boolean toggleFav(Account account, int id);
	
	public long accountCount();
	
	public void expire(Long id);
}
