package com.rodri.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rodri.dscatalog.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
	
}
