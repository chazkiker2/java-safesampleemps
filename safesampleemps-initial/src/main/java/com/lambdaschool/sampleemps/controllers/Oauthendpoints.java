package com.lambdaschool.sampleemps.controllers;


import com.lambdaschool.sampleemps.models.User;
import com.lambdaschool.sampleemps.models.UserMinimum;
import com.lambdaschool.sampleemps.models.UserRoles;
import com.lambdaschool.sampleemps.services.RoleService;
import com.lambdaschool.sampleemps.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * The class allows access to endpoints that are open to all users regardless of authentication status. Its most
 * important function is to allow any user to create their own username.
 */
@RestController
public class Oauthendpoints {

	/**
	 * The default role name (String) for new users ("USER")
	 */
	private static final String      DEFAULT_ROLE = "USER";
	/**
	 * The default content type for endpoints to consume and produce
	 */
	private static final String      APP_JSON     = "application/json";
	/**
	 * A method in this controller adds a new user to the application, so it needs access to UserService to add new user
	 */
	private final        UserService userService;
	/**
	 * A method in this controller adds a new UserRole to the application, so it needs access to RoleService to create
	 * the new Role (and UserRoles relationship)
	 */
	private final        RoleService roleService;
	/**
	 * Connect to the Token store so the application can remove the token on logout
	 */
	private final        TokenStore  tokenStore;

	/**
	 * Autowired constructor to connect all services & stores necessary for OAuth to work
	 * @param userService The UserService to add a new user
	 * @param roleService The RoleService to add a new Role
	 * @param tokenStore The Token store to connect so we can remove tokens on logout
	 */
	@Autowired
	public Oauthendpoints(
			UserService userService,
			RoleService roleService,
			TokenStore tokenStore
	) {
		this.userService = userService;
		this.roleService = roleService;
		this.tokenStore  = tokenStore;
	}

	/**
	 * This endpoint always anyone to create an account with the default role of USER. That role is hardcoded in this method.
	 *
	 * @param httpServletRequest the request that comes in for creating the new user
	 * @param newMinUser         A special minimum set of data that is needed to create a new user
	 *
	 * @return The token access and other relevant data to token access. Status of CREATED. The location header to look
	 * up the new user.
	 *
	 * @throws URISyntaxException we create some URIs during this method. If anything goes wrong with that creation, an exception is thrown.
	 */
	@PostMapping(value = "/createnewuser",
	             consumes = APP_JSON,
	             produces = APP_JSON)
	public ResponseEntity<?> addSelf(
			HttpServletRequest httpServletRequest,
			@Valid
			@RequestBody
					UserMinimum newMinUser
	)
			throws
			URISyntaxException {

		if (newMinUser.getUsername().isBlank() || newMinUser.getPassword().isBlank()) {
			throw new EntityNotFoundException("Username and / or password cannot be blank");
		}
//		if (userService.findByName(newMinUser.getUsername()) != null) {
//			throw new EntityExistsException("Username already exists!")
//		}

		// Create the user
		User newUser = new User();
		newUser.setUsername(newMinUser.getUsername());
		newUser.setPassword(newMinUser.getPassword());
		// add default roles of the user
		Set<UserRoles> newRoles = new HashSet<>();
		newRoles.add(new UserRoles(
				newUser,
				roleService.findByName(DEFAULT_ROLE)
		));
		newUser.setRoles(newRoles);
		newUser = userService.save(newUser);

		// set the location header for the newly created resource
		// the location comes from a different controller!
		HttpHeaders responseHeaders = new HttpHeaders();
		URI newUserURI = ServletUriComponentsBuilder.fromUriString(
				httpServletRequest.getServerName() + ":" + httpServletRequest.getLocalPort() + "/users/user/{userId}")
		                                            .buildAndExpand(newUser.getUserid())
		                                            .toUri();
		responseHeaders.setLocation(newUserURI);

		// return the access token
		// To get the access token, surf to the endpoint /login (always on the server where this is running)
		// just as if a client had done this
		RestTemplate    restTemplate         = new RestTemplate();
		String          requestURI           = "http://localhost" + ":" + httpServletRequest.getLocalPort() + "/login";

		List<MediaType> acceptableMediaTypes = new ArrayList<>();
		acceptableMediaTypes.add(MediaType.APPLICATION_JSON);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setAccept(acceptableMediaTypes);
		headers.setBasicAuth(
				System.getenv("OAUTHCLIENTID"),
				System.getenv("OAUTHCLIENTSECRET")
		);
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add(
				"grant_type",
				"password"
		);
		map.add(
				"scope",
				"read write trust"
		);
		map.add(
				"username",
				newMinUser.getUsername()
		);
		map.add(
				"password",
				newMinUser.getPassword()
		);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(
				map,
				headers
		);
		String theToken = restTemplate.postForObject(
				requestURI,
				request,
				String.class
		);
		return new ResponseEntity<>(
				theToken,
				responseHeaders,
				HttpStatus.CREATED
		);
	}


	/**
	 * Removes the token for the signed on user. The signed user will lose access to the application. They would have to sign on again.
	 *
	 * <br>Example: <a href="http://localhost:2019/logout">http://localhost:2019/logout</a>
	 *
	 * @param request the Http request from which we find the authorization header which includes the token to be removed
	 */
	@GetMapping(value = {"/oauth/revoke-token", "/logout"},
	            produces = APP_JSON)
	public ResponseEntity<?> logoutSelf(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null) {
			String tokenValue = authHeader.replace(
					"Bearer",
					""
			)
			                              .trim();
			OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
			tokenStore.removeAccessToken(accessToken);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
