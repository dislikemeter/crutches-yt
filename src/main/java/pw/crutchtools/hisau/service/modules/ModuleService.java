package pw.crutchtools.hisau.service.modules;

import java.util.Set;

import pw.crutchtools.hisau.component.modules.MenuModule;
import pw.crutchtools.hisau.domain.security.Account;

public interface ModuleService {
	public Set<MenuModule> getModulesForUser(Account user);
}
