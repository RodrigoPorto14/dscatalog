package com.rodri.dscatalog.tests;

import java.time.Instant;

import com.rodri.dscatalog.dto.ProductDTO;
import com.rodri.dscatalog.entities.Category;
import com.rodri.dscatalog.entities.Product;

public class Factory {
	
	public static Product createProduct()
	{
		Product p = new Product(1L,"Phone","Good Phone",800.0,"https://img.com/img.png",Instant.parse("2020-10-20T03:00:00Z"));
		p.getCategories().add(new Category(2L,"Eletronics"));
		return p;
	}
	
	public static ProductDTO createProductDTO()
	{
		Product p = createProduct();
		return new ProductDTO(p,p.getCategories());
	}
	
	public static Category createCategory()
	{
		return new Category(1L,"Eletronics");
	}
}
