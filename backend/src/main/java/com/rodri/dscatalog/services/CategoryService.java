package com.rodri.dscatalog.services;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rodri.dscatalog.dto.CategoryDTO;
import com.rodri.dscatalog.entities.Category;
import com.rodri.dscatalog.repositories.CategoryRepository;
import com.rodri.dscatalog.services.exceptions.DataBaseException;
import com.rodri.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository repository;
	
	@Transactional(readOnly=true)
	public Page<CategoryDTO> findAllPaged(PageRequest pageRequest)
	{
		return repository.findAll(pageRequest).map(x -> new CategoryDTO(x));
		//return repository.findAll(pageRequest).stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
	}
	
	@Transactional(readOnly=true)
	public CategoryDTO findById(Long id) 
	{
		Category obj = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new CategoryDTO(obj);
	}
	
	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		Category entity = new Category();
		entity.setName(dto.getName());
		entity = repository.save(entity);
		return new CategoryDTO(entity);		
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {
		try
		{
			Category entity = repository.getOne(id); // ou getReferenceById
			entity.setName(dto.getName());
			entity = repository.save(entity);
			return new CategoryDTO(entity);
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
