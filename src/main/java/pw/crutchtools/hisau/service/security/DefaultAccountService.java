package pw.crutchtools.hisau.service.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pw.crutchtools.hisau.component.mapping.ExistentObjectMapper;
import pw.crutchtools.hisau.component.repo.security.AccountRepository;
import pw.crutchtools.hisau.component.repo.security.PermissionRepository;
import pw.crutchtools.hisau.component.repo.security.RoleRepository;
import pw.crutchtools.hisau.domain.security.Account;
import pw.crutchtools.hisau.domain.security.Permission;
import pw.crutchtools.hisau.domain.security.Role;
import pw.crutchtools.hisau.domain.security.UserProfile;
import pw.crutchtools.hisau.service.config.ConfigurationService;
import pw.crutchtools.hisau.service.mail.MailService;

@Service
public class DefaultAccountService implements AccountService {
	private static final String DEFAULT_FIRSTNAME = "Admin";
	private static final String DEFAULT_ROLE = "admin";
	private static final String[] DEFAULT_AUTHORITIES = {"pass_change", "manage_roles", "manage_users", "config"};
	
	@Resource
	AccountRepository accountRepository;
	
	@Resource
	RoleRepository roleRepository;
	
	@Resource
	PermissionRepository permissionRepository;
	
	@Autowired
	private ExistentObjectMapper<Account> passwordMapper;
	
	@Autowired
	private ExistentObjectMapper<Account> ownProfileMapper;
	
	@Autowired
	private ExistentObjectMapper<Account> accountMapper;
	
	@Autowired
	SessionRegistry sessionRegistry;
	
	@Autowired
	MailService mailer;
	
	@Autowired
	ConfigurationService configService;

	@Override
	@Transactional(readOnly=true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account found = accountRepository.findByUsername(username);
		if (found != null && found.isEnabled() && found.isAccountNonLocked() && found.isAccountNonExpired() && found.isCredentialsNonExpired()) {
			return found;
		} else {
			throw new UsernameNotFoundException(String.format("User %s not found!", username));
		}		
	}
	
	@Override
	public void changeMyPassword(Account account, String request) {
		accountRepository.save(passwordMapper.mapToObject(account, request));
	}

	@Override
	public void updateProfile(Account currentAccount, String request) {
		accountRepository.save(ownProfileMapper.mapToObject(currentAccount, request));
	}

	@Override
	public Account createPrimaryAccount(String email, String password) {
		Account account = new Account();
		account.setUsername(email);
		account.changePassword(password);
		account.setUserProfile(new UserProfile(DEFAULT_FIRSTNAME));
		Role role = new Role(DEFAULT_ROLE);
		Set<Permission> linkedPermissions = new HashSet<>();
		for (String privName : DEFAULT_AUTHORITIES) {
			linkedPermissions.add(new Permission(privName));
		}
		role.setPermissions(linkedPermissions);
		account.setRole(role);
		account.setEnabled(true);
		permissionRepository.save(linkedPermissions);
		roleRepository.save(role);
		accountRepository.save(account);
		return account;
	}

	@Override
	public List<Account> getAllAccounts() {
		return accountRepository.findAll();
	}

	@Override
	public Account updateAccount(Long id, String request) {
		Account account = accountRepository.getOne(id);
		accountMapper.mapToObject(account, request);
		accountRepository.save(account);
		return account;
	}

	@Override
	public Account createAccount(String request) {
		Account account = new Account();
		account.setUserProfile(new UserProfile());
		accountMapper.mapToObject(account, request);
		String newPassword = account.resetPassword();
		accountRepository.save(account);
		sendRegistrationEmail(account, newPassword);
		return account;
	}

	@Override
	public void deleteAccount(Long id) {
		accountRepository.delete(id);
	}
	
	@Override
	public void resetPassword(Long id) {
		Account account = accountRepository.getOne(id);
		if (account != null) {
			String newPassword = account.resetPassword();
			accountRepository.save(account);
			sendPassResetEmail(account, newPassword);
		}			
	}

	@Override
	public void expire(Long id) {
		Account accountToLogout = accountRepository.getOne(id);
		if (accountToLogout == null) return;
		List<SessionInformation> accountSessions = sessionRegistry.getAllSessions(accountToLogout, false);
		if (accountSessions == null || accountSessions.isEmpty()) return;
		for (SessionInformation session : accountSessions) {
			session.expireNow();
		}
	}
	
	@Override
	public boolean toggleFav(Account account, int id) {
		account.getUserProfile().fav(id, !account.getUserProfile().isFavorite(id));
		accountRepository.save(account);
		return account.getUserProfile().isFavorite(id);
	}

	@Override
	public long accountCount() {
		return accountRepository.count();
	}
	
	private void sendRegistrationEmail(Account account, String password) {
		Map<String, String> model = new HashMap<>();
		model.put("login", account.getUsername());
		model.put("password", password);
		model.put("userName", account.getUserProfile().getFirstName());
		model.put("domain", configService.getParameter("app.domain"));
		model.put("project", configService.getParameter("app.project"));
		mailer.sendEmail(account.getUsername(), "Register", "mail/register.ftl", model);
	}
	
	private void sendPassResetEmail(Account account, String password) {
		Map<String, String> model = new HashMap<>();
		model.put("login", account.getUsername());
		model.put("password", password);
		model.put("userName", account.getUserProfile().getFirstName());
		model.put("domain", configService.getParameter("app.domain"));
		model.put("project", configService.getParameter("app.project"));
		mailer.sendEmail(account.getUsername(), "Password reset", "mail/pass-reset.ftl", model);
	}
}
