package ibf2022.batch2.ssf.frontcontroller.services;

import java.io.StringReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import ibf2022.batch2.ssf.frontcontroller.model.Captcha;
import ibf2022.batch2.ssf.frontcontroller.model.Login;
import ibf2022.batch2.ssf.frontcontroller.respositories.AuthenticationRepository;
import jakarta.json.Json;

@Service
public class AuthenticationService {

	@Value("${login.auth.endpoint}")
	private String authUrl;

	@Autowired
	private AuthenticationRepository authRepo;

	// TODO: Task 2
	// DO NOT CHANGE THE METHOD'S SIGNATURE
	// Write the authentication method in here
	public void authenticate(String username, String password) throws Exception {
		
		// send login attempt to endpoint
		String fullAuthPath = UriComponentsBuilder.fromUriString(authUrl)
				.path("/api/authenticate")
				.toUriString();

		System.out.println(fullAuthPath);
		RequestEntity<String> req = RequestEntity.post(fullAuthPath)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(new Login(username, password).toJSONString());

		RestTemplate template = new RestTemplate();

		ResponseEntity<String> resp = template.exchange(req, String.class);

		// try {
		// 	resp = template.exchange(fullAuthPath, HttpMethod.POST, req, String.class);
		// } catch (Exception e) {
		// 	System.out.println("exception thrown on exchange");
		// 	e.printStackTrace();
		// }

		// check response
		if(resp.getStatusCode().isSameCodeAs(HttpStatus.CREATED)) {
			// exit method if login is valid
			return;
		}

		if(resp.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST) || resp.getStatusCode().isSameCodeAs(HttpStatus.UNAUTHORIZED)) {
			// if invalid
			System.out.println("prog reaches here");
			throw new Exception(resp.getBody());
		}

		// code should not reach this point assuming the above 3 status codes are the only ones being sent from the endpoint
		throw new Exception("Unhandled status code" + resp.getStatusCode().toString() + " with message: " + getErrorMsg(resp.getBody()));
	}

	// TODO: Task 3
	// DO NOT CHANGE THE METHOD'S SIGNATURE
	// Write an implementation to disable a user account for 30 mins
	public void disableUser(String username) {
		authRepo.addUser(username);
	}

	// TODO: Task 5
	// DO NOT CHANGE THE METHOD'S SIGNATURE
	// Write an implementation to check if a given user's login has been disabled
	public boolean isLocked(String username) {
		// if can find user in redis --> isLocked = true
		// if cannot find user in redis --> isLocked = false
		return authRepo.canFindUser(username);
	}

	
	private String getErrorMsg(String json) {
		return Json.createReader(new StringReader(json)).readObject().getString("message");
	}

	public boolean checkCaptcha(Captcha catpcha, Double answer) {
		
		return true;
	}
}
