package pw.crutchtools.hisau.component.modules;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModulesConfig {
	private static final String CAT_ADMIN = "admin";
	private static final String CAT_ACCOUNT = "account";
	private static final String CAT_MEDIA = "media";
	
	@Bean
	MenuModule manageRoles() {
		return new MenuModule("roles", "/manage/roles", CAT_ACCOUNT, "MANAGE_ROLES");
	}
	
	@Bean
	MenuModule managePermissions() {
		return new MenuModule("permissions", "/manage/permissions", CAT_ACCOUNT, "MANAGE_ROLES");
	}

	@Bean
	MenuModule manageUsers() {
		return new MenuModule("edit-users", "/manage/users", CAT_ACCOUNT, "MANAGE_USERS");
	}
	
	@Bean
	MenuModule ytStats() {
		return new MenuModule("ytstats", "/ytstats", CAT_MEDIA, "YTSTATS");
	}
	
	@Bean
	MenuModule config() {
		return new MenuModule("config", "/config", CAT_ADMIN, "CONFIG");
	}
	
}
