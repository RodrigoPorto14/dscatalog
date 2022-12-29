package com.rodri.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.rodri.dscatalog.entities.Product;
import com.rodri.dscatalog.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {
	
	@Autowired
	private ProductRepository repository;
	
	private Long existingId;
	private Long nonExistingId;
	private Long countTotalProducts;
	
	@BeforeEach
	void setUp() throws Exception
	{
		existingId = 1L;
		nonExistingId = -1L;
		countTotalProducts = 25L;
	}
	
	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull()
	{
		Product product = Factory.createProduct();
		product.setId(null);
		
		product = repository.save(product);
		Long productId = product.getId();
		
		Assertions.assertNotNull(productId);
		Assertions.assertEquals(countTotalProducts+1, productId);
	}
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists()
	{
		repository.deleteById(existingId);		
		Optional<Product> p = repository.findById(existingId);
		
		Assertions.assertFalse(p.isPresent());
	}
	
	@Test
	public void deleteShouldDThrowEmptyResultDataAccessExceptionWhenIdDoesNotExists()
	{
		Assertions.assertThrows(EmptyResultDataAccessException.class, () ->
		{
			repository.deleteById(nonExistingId);
		});
	}
	
	@Test 
	public void findByIdShouldReturnNotNullWhenIdExists()
	{
		Optional<Product> p = repository.findById(existingId);
		Assertions.assertTrue(p.isPresent());
	}
	
	@Test 
	public void findByIdShouldReturnNullWhenIdDoesNotExists()
	{
		Optional<Product> p = repository.findById(nonExistingId);
		Assertions.assertTrue(p.isEmpty());
	}
}
