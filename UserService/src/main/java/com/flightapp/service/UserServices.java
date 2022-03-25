package com.flightapp.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.flightapp.model.AdminLoginDetails;
import com.flightapp.model.AuthResponse;
import com.flightapp.model.LoginDetails;
import com.flightapp.model.UserData;


@Service
public interface UserServices {
	public ResponseEntity<AuthResponse> userLogin(UserData loginDetails);
	public ResponseEntity<Object> userRegister(UserData user);
	public ResponseEntity<AuthResponse> validate(String authToken);
	
	
	
	//public ResponseEntity<Object> getAllUsers();
	//public ResponseEntity<Object> searchByUsername(String username);
	//public ResponseEntity<Object> forgotPassword(LoginDetails data);
}
