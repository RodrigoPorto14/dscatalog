package com.rodri.dscatalog.services;

import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.rodri.dscatalog.dto.ProductDTO;
import com.rodri.dscatalog.entities.Category;
import com.rodri.dscatalog.entities.Product;
import com.rodri.dscatalog.repositories.CategoryRepository;
import com.rodri.dscatalog.repositories.ProductRepository;
import com.rodri.dscatalog.services.exceptions.DataBaseException;
import com.rodri.dscatalog.services.exceptions.ResourceNotFoundException;
import com.rodri.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	
	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository productRep;
	
	@Mock
	private CategoryRepository categoryRep;
	
	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private Product product;
	private Category category;
	private PageImpl<Product> page;
	
	@BeforeEach
	void setUp() throws Exception 
	{ 
		existingId=1L; 
		nonExistingId=2L;
		dependentId=3L;
		product = Factory.createProduct();
		category = Factory.createCategory();
		page = new PageImpl<>(List.of(product));
		
		Mockito.when(productRep.findAll((Pageable)any())).thenReturn(page);
		Mockito.when(productRep.save(any())).thenReturn(product);
		Mockito.when(productRep.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(productRep.findById(nonExistingId)).thenReturn(Optional.empty());
		Mockito.when(productRep.find(any(),any(),any())).thenReturn(page);
		Mockito.when(productRep.getOne(existingId)).thenReturn(product);
		Mockito.when(productRep.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);
		Mockito.when(categoryRep.getOne(existingId)).thenReturn(category);
		Mockito.when(categoryRep.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);
		Mockito.doNothing().when(productRep).deleteById(existingId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(productRep).deleteById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(productRep).deleteById(dependentId);
	}
	
	@Test
	public void findAllPagedShouldReturnPage()
	{
		Pageable pageable = PageRequest.of(0, 10);
		Page<ProductDTO> result = service.findAllPaged(0L,"",pageable);
		Assertions.assertNotNull(result);
	}
	
	@Test
	public void findByIdShouldReturnProductWhenIdExists()
	{
		ProductDTO result = service.findById(existingId);
		Assertions.assertNotNull(result);
		Mockito.verify(productRep).findById(existingId);
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists()
	{
		Assertions.assertThrows(ResourceNotFoundException.class,() -> 
		{
			service.findById(nonExistingId);
		});
		Mockito.verify(productRep).findById(nonExistingId);
	}
	
	@Test
	public void updateShouldReturnProductWhenIdExists()
	{
		ProductDTO result = service.update(existingId,new ProductDTO(product));
		Assertions.assertNotNull(result);
		//Mockito.verify(productRep).getOne(existingId);
		//Mockito.verify(categoryRep).getOne(existingId);
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists()
	{
		Assertions.assertThrows(ResourceNotFoundException.class,() -> 
		{
			service.update(nonExistingId,new ProductDTO(product));
		});
		//Mockito.verify(productRep).getOne(nonExistingId);
		//Mockito.verify(categoryRep).getOne(nonExistingId);
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists()
	{
		Assertions.assertDoesNotThrow(() -> 
		{
			service.delete(existingId);
		});
		
		Mockito.verify(productRep, Mockito.times(1)).deleteById(existingId);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists()
	{
		Assertions.assertThrows(ResourceNotFoundException.class,() -> 
		{
			service.delete(nonExistingId);
		});
		
		Mockito.verify(productRep, Mockito.times(1)).deleteById(nonExistingId);
	}
	
	@Test
	public void deleteShouldThrowDataBaseExceptionWhenDependentId()
	{
		Assertions.assertThrows(DataBaseException.class,() -> 
		{
			service.delete(dependentId);
		});
		
		Mockito.verify(productRep, Mockito.times(1)).deleteById(dependentId);
	}
}
