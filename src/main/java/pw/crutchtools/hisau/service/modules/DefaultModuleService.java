package pw.crutchtools.hisau.service.modules;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.crutchtools.hisau.component.modules.MenuModule;
import pw.crutchtools.hisau.domain.security.Account;
import pw.crutchtools.hisau.domain.security.Permission;

@Service
public class DefaultModuleService implements ModuleService {

	@Autowired
	Set<MenuModule> allModules;

	@Override
	public Set<MenuModule> getModulesForUser(Account user) {
		Set<String> userPrivileges = user.getRole().getPermissions().stream().map(Permission::getHumanizedName)
				.collect(Collectors.toSet());
		Set<String> flatSet = new HashSet<>();
		Set<MenuModule> result = allModules.stream().filter(module -> {
			if (module.hasRequirements()) {
				flatSet.clear();
				flatSet.addAll(userPrivileges);
				flatSet.addAll(Arrays.asList(module.getRequiredPermissions()));
				return flatSet.size() < module.getRequiredPermissions().length + userPrivileges.size();
			} else { // if module requirements is empty, add module
				return true;
			}
		}).collect(Collectors.toSet());

		return result;
	}

}
