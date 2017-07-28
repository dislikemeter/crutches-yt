package pw.crutchtools.hisau.app;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import pw.crutchtools.hisau.controller.api.AbstractAjaxAction;
import pw.crutchtools.hisau.controller.exceptions.AuthenticationRequired;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private static final int MAX_SESSIONS_PER_USER = -1;
	private static final int INACTIVE_INTERVAL = 60*60;
	
	private static final String[] ANONYMOUS_PAGES = { "/", "/api/**" };

	private static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(11);

	public static PasswordEncoder passwordEncoder() {
		return passwordEncoder;
	}

	@Autowired
	UserDetailsService accountService;

	// AuthManager
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(accountService).passwordEncoder(passwordEncoder());
	}

	// AuthProvider
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(accountService);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	private static SessionRegistry sessionRegistry = new SessionRegistryImpl();

	@Bean
	public SessionRegistry sessionRegistry() {
		return sessionRegistry;
	}

	// Http security
	@Bean
	public HttpSessionListener httpSessionListener() {
		return new HttpSessionListener() {
			@Override
			public void sessionDestroyed(HttpSessionEvent se) {
				
			}
			
			@Override
			public void sessionCreated(HttpSessionEvent se) {
				se.getSession().setMaxInactiveInterval(INACTIVE_INTERVAL);
			}
		};
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		SessionRejecter sessionRejecter = new SessionRejecter();
		http
			.authorizeRequests()
				.antMatchers(ANONYMOUS_PAGES)
				.permitAll()
				.anyRequest()
				.authenticated()
			.and().formLogin()
				.loginPage("/login")
				.permitAll()
				.usernameParameter("username")
				.passwordParameter("password")
				.failureUrl("/login?error")
				.defaultSuccessUrl("/", true)
			.and().csrf()
			.and().logout()
				.permitAll()
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login?logout")
			.and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
				.invalidSessionStrategy(sessionRejecter)
				.maximumSessions(MAX_SESSIONS_PER_USER)
					.maxSessionsPreventsLogin(false)
					.sessionRegistry(sessionRegistry)
					.expiredSessionStrategy(sessionRejecter)
				.and().sessionFixation()
					.migrateSession();
	}

	class SessionRejecter implements SessionInformationExpiredStrategy, InvalidSessionStrategy {

		@Override
		public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {
			request.getSession(true);
			String path = request.getServletPath();			
			// if api request, return an exception
			if (path.startsWith(AbstractAjaxAction.API_PATH)) {
				response.reset();
				response.setStatus(403);
				response.getWriter().append(AbstractAjaxAction.convertExceptionToJson(new AuthenticationRequired()));
				// otherwise redirect
			} else {
				response.sendRedirect("/login");
			}
		}

		@Override
		public void onExpiredSessionDetected(SessionInformationExpiredEvent eventØ)
				throws IOException, ServletException {
			HttpSession session = eventØ.getRequest().getSession(false);
			if (session != null) session.invalidate();
			onInvalidSessionDetected(eventØ.getRequest(), eventØ.getResponse());
		}
	}
}
