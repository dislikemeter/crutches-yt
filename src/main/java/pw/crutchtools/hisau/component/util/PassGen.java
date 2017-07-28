package pw.crutchtools.hisau.component.util;

import java.util.Random;

public final class PassGen {
	private static final char[] PASS_CHARS = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
			'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
			'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
	private static final int PASS_LENGTH = 10;
	
	public static String generatePassword() {
		StringBuffer newPassword = new StringBuffer();
		Random rand = new Random();
		for (int i = 0; i < PASS_LENGTH; i++) {
			newPassword.append(PASS_CHARS[rand.nextInt(PASS_CHARS.length)]);
		}
		return newPassword.toString();
	}
	
}
