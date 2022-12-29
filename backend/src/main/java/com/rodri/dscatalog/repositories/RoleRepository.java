package com.rodri.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rodri.dscatalog.entities.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
	
}
