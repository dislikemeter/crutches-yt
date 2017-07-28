package pw.crutchtools.hisau.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import pw.crutchtools.hisau.domain.security.Account;
import pw.crutchtools.hisau.service.config.ConfigurationService;
import pw.crutchtools.hisau.service.security.AccountService;

@Controller
public final class PageController {

	@Autowired
	ConfigurationService configService;

	@Autowired
	AccountService accountService;

	private Authentication getAuth() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() {
		return (getAuth() instanceof AnonymousAuthenticationToken) ? "page/login" : "redirect:/";
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = getAuth();
		if (auth != null) {
			(new SecurityContextLogoutHandler()).logout(request, response, auth);
			return "redirect:/login?logout";
		}
		return "redirect:/login";
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public String register(@RequestParam(value = "email") String email, @RequestParam(value = "password") String password) {
		if (configService.getParameter("system.configured") == null) {
			Account primaryAccount = accountService.createPrimaryAccount(email, password);
			if (primaryAccount != null) {
				configService.saveParameter("system.configured", "1");
				return "redirect:/login";
			}
		}
		return "redirect:/";
	}

	@RequestMapping(value = "/**", method = RequestMethod.GET)
	public String home() {
		return (getAuth() instanceof AnonymousAuthenticationToken)
				? configService.getParameter("system.configured") != null ? "page/welcome" : "page/register"
				: "page/app";
	}
	
	@ModelAttribute
	private void populateModel(Model model) {
		model.addAttribute("domain", configService.getParameter("app.domain"));
		model.addAttribute("project", configService.getParameter("app.project"));
		model.addAttribute("debug", configService.getParameter("app.debug"));
	}
}
