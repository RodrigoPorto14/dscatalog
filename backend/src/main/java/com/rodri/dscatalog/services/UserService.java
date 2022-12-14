package com.rodri.dscatalog.services;

import java.util.Set;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rodri.dscatalog.dto.UserDTO;
import com.rodri.dscatalog.dto.UserInsertDTO;
import com.rodri.dscatalog.dto.UserUpdateDTO;
import com.rodri.dscatalog.entities.Role;
import com.rodri.dscatalog.entities.User;
import com.rodri.dscatalog.repositories.RoleRepository;
import com.rodri.dscatalog.repositories.UserRepository;
import com.rodri.dscatalog.services.exceptions.DataBaseException;
import com.rodri.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class UserService implements UserDetailsService{
	
	private static Logger logger = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRep;
	
	@Autowired
	private RoleRepository roleRep;
	
	@Transactional(readOnly=true)
	public Page<UserDTO> findAllPaged(Pageable pageable)
	{
		return userRep.findAll(pageable).map(x -> new UserDTO(x));
		//return repository.findAll(pageRequest).stream().map(x -> new UserDTO(x)).collect(Collectors.toList());
	}
	
	@Transactional(readOnly=true)
	public UserDTO findById(Long id) 
	{
		User entity = userRep.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new UserDTO(entity);
	}
	
	@Transactional
	public UserDTO insert(UserInsertDTO dto) {
		User entity = new User();
		entity.setPassword(passwordEncoder.encode(dto.getPassword()));
		return saveEntity(entity,dto);		
	}

	@Transactional
	public UserDTO update(Long id, UserUpdateDTO dto) {
		try
		{ 
			User entity = userRep.getOne(id);
			return saveEntity(entity,dto);  // ou getReferenceById
		} 
		catch(EntityNotFoundException e) { throw new ResourceNotFoundException("Id not found "+id); }
	}

	public void delete(Long id) {
		try
		{
			userRep.deleteById(id);
		}
		catch(EmptyResultDataAccessException e){throw new ResourceNotFoundException("Id not found " +id);}
		catch(DataIntegrityViolationException e) {throw new DataBaseException("Integrity violation");}
	}
	
	private UserDTO saveEntity(User entity,UserDTO dto)
	{
		entity.setFirstName(dto.getFirstName());
		entity.setLastName(dto.getLastName());
		entity.setEmail(dto.getEmail());
		
		Set<Role> roles = entity.getRoles();
		roles.clear();
		dto.getRoles().forEach(role -> roles.add(roleRep.getOne(role.getId())));
		
		return new UserDTO(userRep.save(entity));
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRep.findByEmail(username);
		if(user == null) 
		{
			logger.error("User not found: " + username);
			throw new UsernameNotFoundException("Email not found");
		}
		logger.info("User found: " + username);
		return user;
	}
}
