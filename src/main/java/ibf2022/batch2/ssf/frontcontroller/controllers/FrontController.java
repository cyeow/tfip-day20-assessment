package ibf2022.batch2.ssf.frontcontroller.controllers;

import java.text.DecimalFormat;

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
	private static final String CAPTCHA_KEY = "captcha";
	private static final String LOGGED_IN_KEY = "logged_in_user";

	// TODO: Task 2, Task 3, Task 4, Task 6

	@GetMapping(path={"/", "/login"})
	public String goToLandingPage(Model model, HttpSession session) {
		// check if already logged in 
		// tells the user it is already logged in, give option to logout
		String loggedInUser = (String) session.getAttribute(LOGGED_IN_KEY);
		if(loggedInUser != null) {
			model.addAttribute("loggedInUser", loggedInUser);
			return "view0";
		} 

		// if page is refreshed, captcha should still remain if already generated
		Captcha captcha = (Captcha) session.getAttribute(CAPTCHA_KEY);
		if(captcha != null) {
			model.addAttribute(CAPTCHA_KEY, captcha);
		}

		model.addAttribute("login", new Login());

		return "view0";
	}

	@PostMapping("/login")
	public String attemptLogin(Model model, HttpSession session, @Valid Login login, BindingResult binding) {
		// first check if login is syntatically valid as this does not constitute a
		// login attempt
		if (binding.hasErrors()) {
			// retain captcha in the model but reset it
			Captcha captcha = (Captcha) session.getAttribute(CAPTCHA_KEY);
			if(captcha != null) {
				captcha = Captcha.generateCaptcha();
				model.addAttribute(CAPTCHA_KEY, captcha);
				session.setAttribute(CAPTCHA_KEY, captcha);
			}

			return "view0";
		}

		// redirect if disabled
		if(authSvc.isLocked(login.getUsername())) {
			model.addAttribute("username", login.getUsername());
			return "view2";
		}

		// check captcha if there is
		Captcha captcha = (Captcha) session.getAttribute(CAPTCHA_KEY);
		if(captcha != null) {
			// there is a captcha
			if(!isValidCaptcha(login.getCaptchaAnswer(), (Captcha) session.getAttribute(CAPTCHA_KEY))) {
				// captcha answer is incorrect
				
				// update login attempts and redirect if failed
				if (loginAttemptsExceeded(session, login)) {
					disableUserAndResetSession(model, session, login);
					return "view2";
				}
	
				// add the error to the model
				binding.addError(new FieldError("login", "captchaAnswer", "Incorrect captcha response."));
				
				// reset the captcha
				Captcha newCaptcha = Captcha.generateCaptcha();
				model.addAttribute(CAPTCHA_KEY, newCaptcha);
				session.setAttribute(CAPTCHA_KEY, newCaptcha);

				return "view0";
			}
	
		}

		// authenticate login
		try {
			authSvc.authenticate(login.getUsername(), login.getPassword());
		} catch (Exception e) {

			// update login attempts and redirect if failed
			if (loginAttemptsExceeded(session, login)) {
				disableUserAndResetSession(model, session, login);
				return "view2";
			}

			// add the error to the model
			model.addAttribute(ERROR_KEY, e.getMessage());

			// trigger captcha
			captcha = Captcha.generateCaptcha();
			model.addAttribute(CAPTCHA_KEY, captcha);
			session.setAttribute(CAPTCHA_KEY, captcha);

			return "view0";
		}

		// mark as logged in
		session.setAttribute("logged_in_user", login.getUsername());

		// reset login attempts & captcha
		session.removeAttribute(CAPTCHA_KEY);
		resetLoginAttemptsAndCaptcha(session, login.getUsername());

		return "redirect:/protected/view1.html";
	}

	private void disableUserAndResetSession(Model model, HttpSession session, Login login) {
		authSvc.disableUser(login.getUsername());
		resetLoginAttemptsAndCaptcha(session, login.getUsername());
		model.addAttribute("username", login.getUsername());
	}

	@GetMapping("/logout")
	public String logOutUser(HttpSession session) {

		// if user is logged in, log out the user. else just redirect back to landing
		String loggedInUser = (String) session.getAttribute("logged_in_user");
		if(loggedInUser != null) {
			session.removeAttribute("logged_in_user");
		}

		return "redirect:/";
	}

	private boolean isValidCaptcha(Double captchaAnswer, Captcha captcha) {	
		// if no value received it is invalid
		if(captchaAnswer == null) {
			return false;
		}
		// checks by rounding to 2dp -- to account for divide operations	
		return round(captchaAnswer).compareTo(round(captcha.getResult())) == 0 ? true : false;
	}

	private Double round(Double number) {		
		// rounds to 2dp
		DecimalFormat df = new DecimalFormat("0.00");
		return Double.parseDouble(df.format(number));
	}

	private void resetLoginAttemptsAndCaptcha(HttpSession session, String username) {
		String loginAttribute = LOGIN_ATTEMPT_KEY + username;
		session.removeAttribute(loginAttribute);
		session.removeAttribute(CAPTCHA_KEY);
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

	private boolean loginAttemptsExceeded(HttpSession session, Login login) {
		return getAndSetLoginAttempts(session, login.getUsername()) > 3;
	}
}
