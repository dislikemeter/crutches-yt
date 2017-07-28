package pw.crutchtools.hisau.component.util.avatars;

import pw.crutchtools.hisau.domain.security.Account;

public abstract class AvatarProvider {
	static private AvatarProvider instance = new GravatarProvider();
	
	static public AvatarProvider getInstance() {
		return instance;
	}
	
	abstract public String getAvatar(Account account);
	
	private static String URL_PREFIX = "/static/img/avatars/";
	
	String getBuiltInAvatarUrl(Account account) {
		String result = account.getUserProfile().getPhoto();
		return (result != null && result.length() > 0) ? URL_PREFIX + result : null;
	}
}
