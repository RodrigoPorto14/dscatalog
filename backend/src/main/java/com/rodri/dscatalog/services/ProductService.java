package com.rodri.dscatalog.services;

import java.util.Set;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rodri.dscatalog.dto.ProductDTO;
import com.rodri.dscatalog.entities.Category;
import com.rodri.dscatalog.entities.Product;
import com.rodri.dscatalog.repositories.CategoryRepository;
import com.rodri.dscatalog.repositories.ProductRepository;
import com.rodri.dscatalog.services.exceptions.DataBaseException;
import com.rodri.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository productRep;
	@Autowired
	private CategoryRepository categoryRep;
	
	
	@Transactional(readOnly=true)
	public Page<ProductDTO> findAllPaged(PageRequest pageRequest)
	{
		return productRep.findAll(pageRequest).map(x -> new ProductDTO(x));
		//return repository.findAll(pageRequest).stream().map(x -> new ProductDTO(x)).collect(Collectors.toList());
	}
	
	@Transactional(readOnly=true)
	public ProductDTO findById(Long id) 
	{
		Product entity = productRep.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new ProductDTO(entity,entity.getCategories());
	}
	
	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		return saveEntity(dto,new Product());
		//entity = repository.save(entity);
		//return new ProductDTO(entity);		
	}

	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {
		try
		{
			Product entity = productRep.getOne(id); // ou getReferenceById
			return saveEntity(dto,entity);
			//entity = repository.save(entity);
			//return new ProductDTO(entity);
		}
		catch(EntityNotFoundException e)
		{
			throw new ResourceNotFoundException("Id not found "+id);
		}
	}

	public void delete(Long id) {
		try{productRep.deleteById(id);}
		catch(EmptyResultDataAccessException e){throw new ResourceNotFoundException("Id not found " +id);}
		catch(DataIntegrityViolationException e) {throw new DataBaseException("Integrity violation");}
	}
	
	private ProductDTO saveEntity(ProductDTO dto, Product entity)
	{
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setDate(dto.getDate());
		entity.setImgUrl(dto.getImgUrl());
		entity.setPrice(dto.getPrice());
		Set<Category> categories = entity.getCategories();
		categories.clear();
		dto.getCategories().forEach(cat -> categories.add(categoryRep.getOne(cat.getId())));
		
		return new ProductDTO(productRep.save(entity));
	}
}
