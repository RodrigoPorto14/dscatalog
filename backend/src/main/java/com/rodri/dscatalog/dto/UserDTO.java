package com.rodri.dscatalog.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.rodri.dscatalog.entities.User;

public class UserDTO implements Serializable{
	private static final long serialVersionUID = 1L;

	private Long id;
	
	@NotBlank(message = "Campo obrigatório")
	private String firstName;
	private String lastName;
	
	@Email(message = "Favor entrar um email válido")
	private String email;
	
	Set<RoleDTO> roles = new HashSet<>();
	
	public UserDTO() {}
	
	public UserDTO(User entity)
	{
		id = entity.getId();
		firstName = entity.getFirstName();
		lastName = entity.getLastName();
		email = entity.getEmail();
		entity.getRoles().forEach((role) -> roles.add(new RoleDTO(role)));
	}

	public Long getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}
	
	public Set<RoleDTO> getRoles(){
		return roles;
	}
	
	
	
}
