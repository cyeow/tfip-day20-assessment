package ibf2022.batch2.ssf.frontcontroller.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/protected")
public class ProtectedController {

	private static final String LOGGED_IN_KEY = "logged_in_user";

	// TODO Task 5
	// Write a controller to protect resources rooted under /protected

	@GetMapping("/{resource}")
	public String protectResources(HttpSession session, @PathVariable String resource) {
		if (isUserLoggedIn(session)) {
			// will redirect to 404 as long as the mapping does not exist. if there is
			// already a mapping it will not come in here.
			return "redirect:/" + resource;
		}
		return "redirect:/";
	}

	@GetMapping("/view1.html")
	public String showTopSecret(HttpSession session) {
		if (isUserLoggedIn(session)) {
			return "view1";
		}
		return "redirect:/";
	}

	private boolean isUserLoggedIn(HttpSession session) {
		if (session.getAttribute(LOGGED_IN_KEY) != null) {
			return true;
		}
		return false;
	}
}
