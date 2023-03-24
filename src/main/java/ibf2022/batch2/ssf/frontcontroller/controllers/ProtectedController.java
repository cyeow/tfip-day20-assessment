package ibf2022.batch2.ssf.frontcontroller.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/protected")
public class ProtectedController {

	// TODO Task 5
	// Write a controller to protect resources rooted under /protected

	@RequestMapping("/protected/{resource}")
	public String protectResources() {
		return "view0";
	}
}
