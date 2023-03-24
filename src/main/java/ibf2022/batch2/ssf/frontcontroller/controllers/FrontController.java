package ibf2022.batch2.ssf.frontcontroller.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import ibf2022.batch2.ssf.frontcontroller.model.Captcha;
import ibf2022.batch2.ssf.frontcontroller.model.Login;
import ibf2022.batch2.ssf.frontcontroller.services.AuthenticationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class FrontController {

	@Autowired
	private AuthenticationService authSvc;

	private static final String LOGIN_ATTEMPT_KEY = "login_attempt_";
	private static final String ERROR_KEY = "error";

	// TODO: Task 2, Task 3, Task 4, Task 6

	@GetMapping("/")
	public String goToLandingPage(Model model, HttpSession session) {
		// check if user already logged in via session

		model.addAttribute("login", new Login());

		return "view0";
	}

	@PostMapping("/login")
	public String attemptLogin(Model model, HttpSession session, @Valid Login login, BindingResult binding) {
		// first check if login is syntatically valid as this does not constitute a
		// login attempt
		if (binding.hasErrors()) {
			return "view0";
		}

		// redirect if disabled
		if(authSvc.isLocked(login.getUsername())) {
			model.addAttribute("username", login.getUsername());
			return "view2";
		}

		// check captcha if there is
		if(!validateCaptcha(login.getCaptchaAnswer(), (Captcha) session.getAttribute("captcha"))) {
			// captcha is incorrect
			
			// update login attempts and redirect if failed
			if (getAndSetLoginAttempts(session, login.getUsername()) > 3) {
				authSvc.disableUser(login.getUsername());
				resetLoginAttempts(session, login.getUsername());
				return "view2";
			}

			// add the error to the model
			binding.addError(new FieldError("login", "captchaAnswer", "Incorrect captcha response."));
			return "view0";
		}


		// authenticate login
		try {
			authSvc.authenticate(login.getUsername(), login.getPassword());
		} catch (Exception e) {

			// update login attempts and redirect if failed
			if (getAndSetLoginAttempts(session, login.getUsername()) > 3) {
				authSvc.disableUser(login.getUsername());
				resetLoginAttempts(session, login.getUsername());
				return "view2";
			}

			// add the error to the model
			model.addAttribute(ERROR_KEY, e.getMessage());
			System.out.println(e.getMessage());

			// trigger captcha
			Captcha captcha = Captcha.generateCaptcha();
			session.setAttribute("captcha", captcha);
			model.addAttribute("captcha", captcha);

			return "view0";
		}

		// mark as logged in
		// reset login attempt
		return "redirect:/protected/view1.html";
	}

	private boolean validateCaptcha(Double captchaAnswer, Object attribute) {
		return false;
	}

	private void resetLoginAttempts(HttpSession session, String username) {
		String loginAttribute = LOGIN_ATTEMPT_KEY + username;
		session.removeAttribute(loginAttribute);
	}

	private Integer getAndSetLoginAttempts(HttpSession session, String username) {
		String loginAttribute = LOGIN_ATTEMPT_KEY + username;

		Integer attempts = (Integer) session.getAttribute(loginAttribute);
		if (attempts == null) {
			// user has not attempted to login before & failed for the first time
			session.setAttribute(loginAttribute, 1);
		} else {
			// increment number of attempts
			session.setAttribute(loginAttribute, attempts + 1);
		}

		return (Integer) session.getAttribute(loginAttribute);
	}
}
