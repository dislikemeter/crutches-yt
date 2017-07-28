package pw.crutchtools.hisau.service.mail;

import java.util.Map;

public interface MailService {
	public void sendEmail(String toAddress, String subject, String template, Map<String, ?> model);
}
