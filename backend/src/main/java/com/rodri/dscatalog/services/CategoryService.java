package com.rodri.dscatalog.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rodri.dscatalog.dto.CategoryDTO;
import com.rodri.dscatalog.entities.Category;
import com.rodri.dscatalog.repositories.CategoryRepository;
import com.rodri.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository repository;
	
	@Transactional(readOnly=true)
	public List<CategoryDTO> findAll()
	{
		return repository.findAll().stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
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
}
