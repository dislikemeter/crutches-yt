package pw.crutchtools.hisau.service.mail;

import java.util.Map;
import java.util.Properties;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import pw.crutchtools.hisau.service.config.ConfigurationService;

@Service
public class Mailer implements MailService {
	
	@Autowired
	FreeMarkerConfigurer freemarkerConfigurer;
	
	@Autowired
	ConfigurationService configService;

	@Override
	public void sendEmail(String toAddress, String subject, String template, Map<String, ?> model) {
		new Thread(() -> {
			StringBuffer content = new StringBuffer();
			try {
				content.append(FreeMarkerTemplateUtils.processTemplateIntoString(
						freemarkerConfigurer.getConfiguration().getTemplate(template), model));
			} catch (Exception e) {
			}

			MimeMessagePreparator preparator = new MimeMessagePreparator() {
				public void prepare(MimeMessage mimeMessage) throws Exception {
					MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
					helper.setSubject(subject);
					helper.setFrom(configService.getParameter("mailer.username"));
					helper.setTo(toAddress);
					helper.setText(content.toString(), true);
				}
			};
			getMailSender().send(preparator);
		}).start();
	}
	
    private JavaMailSender getMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.checkserveridentity", "false");
        props.put("mail.smtps.ssl.trust", "*");
        
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.connectiontimeout", "10000");
        
        props.put("mail.mime.charset", "UTF-8");
        mailSender.setUsername(configService.getParameter("mailer.username"));
        mailSender.setPassword(configService.getParameter("mailer.password"));
        mailSender.setHost(configService.getParameter("mailer.server.host"));
        mailSender.setPort(Integer.parseInt(configService.getParameter("mailer.server.port")));
        mailSender.setJavaMailProperties(props);
        return mailSender;
    }

}
