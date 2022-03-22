package com.flightapp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
@Entity
public class UserData {
	
	   
	    @Id
	    @Pattern(regexp = "[a-zA-Z0-9@.]*$", message = "user name should contain only alphabets and digits")
	    private String emailid;
	    @Column
	    @NotBlank(message="Password should not be empty")
	    @Size(min = 8, message = "minimum 8 Characters required")
	    private String password;
	   
	    
}