package com.rodri.dscatalog.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rodri.dscatalog.dto.CategoryDTO;
import com.rodri.dscatalog.entities.Category;
import com.rodri.dscatalog.repositories.CategoryRepository;
import com.rodri.dscatalog.services.exceptions.EntityNotFoundException;

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
		Category obj = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Entity not found"));
		return new CategoryDTO(obj);
	}
	
	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		Category entity = new Category();
		entity.setName(dto.getName());
		entity = repository.save(entity);
		return new CategoryDTO(entity);		
	}
}
