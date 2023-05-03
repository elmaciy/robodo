package com.robodo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.robodo.model.JwtRequest;
import com.robodo.model.JwtResponse;
import com.robodo.model.User;
import com.robodo.security.JwtTokenUtil;
import com.robodo.services.ProcessService;

@RestController
@CrossOrigin
public class JwtAuthenticationController {
	
	@Autowired
	ProcessService processService;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
		Optional<User> optUser = processService.getUsersAll().stream().filter(p-> p.isValid()
				&& p.getUsername().equals(authenticationRequest.getUsername()) 
				&& p.getPassword().equals(authenticationRequest.getPassword()))
			.findAny();
		
		if (optUser.isEmpty()) return ResponseEntity.status(503).body(null);
		
		User user = optUser.get();
		
		UserDetails userDetails = user.asUserDetails(processService);
		
		final String token = jwtTokenUtil.generateToken(userDetails);

		return ResponseEntity.ok(new JwtResponse(token));
	}



}
