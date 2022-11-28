package com.rodri.dscatalog.services;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rodri.dscatalog.dto.ProductDTO;
import com.rodri.dscatalog.entities.Product;
import com.rodri.dscatalog.repositories.ProductRepository;
import com.rodri.dscatalog.services.exceptions.DataBaseException;
import com.rodri.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository repository;
	
	@Transactional(readOnly=true)
	public Page<ProductDTO> findAllPaged(PageRequest pageRequest)
	{
		return repository.findAll(pageRequest).map(x -> new ProductDTO(x));
		//return repository.findAll(pageRequest).stream().map(x -> new ProductDTO(x)).collect(Collectors.toList());
	}
	
	@Transactional(readOnly=true)
	public ProductDTO findById(Long id) 
	{
		Product entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new ProductDTO(entity,entity.getCategories());
	}
	
	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product entity = new Product();
		//entity.setName(dto.getName());
		entity = repository.save(entity);
		return new ProductDTO(entity);		
	}

	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {
		try
		{
			Product entity = repository.getOne(id); // ou getReferenceById
			//entity.setName(dto.getName());
			entity = repository.save(entity);
			return new ProductDTO(entity);
		}
		catch(EntityNotFoundException e)
		{
			throw new ResourceNotFoundException("Id not found "+id);
		}
	}

	public void delete(Long id) {
		try{repository.deleteById(id);}
		catch(EmptyResultDataAccessException e){throw new ResourceNotFoundException("Id not found " +id);}
		catch(DataIntegrityViolationException e) {throw new DataBaseException("Integrity violation");}
	}
}
