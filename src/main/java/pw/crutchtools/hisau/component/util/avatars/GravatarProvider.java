package pw.crutchtools.hisau.component.util.avatars;

import org.apache.commons.codec.digest.DigestUtils;

import pw.crutchtools.hisau.domain.security.Account;

public final class GravatarProvider extends AvatarProvider {
	private static final String URL_PREFIX = "https://www.gravatar.com/avatar/";
	private static final String[] STYLE = { "mm", "identicon", "monsterid", "wavatar", "retro" };	
	private static String URL_POSTFIX = "?d=" + STYLE[3];
	
	@Override
	public String getAvatar(Account account) {
		String builtIn = getBuiltInAvatarUrl(account);
		if (builtIn != null)
			return builtIn;
		else {
			String hashedEmail = DigestUtils.md5Hex(account.getUsername().toLowerCase());
			return URL_PREFIX + hashedEmail + URL_POSTFIX;
		}
	}
}