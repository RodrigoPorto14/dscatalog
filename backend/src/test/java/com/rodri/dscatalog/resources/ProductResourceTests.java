package com.rodri.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rodri.dscatalog.dto.ProductDTO;
import com.rodri.dscatalog.services.ProductService;
import com.rodri.dscatalog.services.exceptions.DataBaseException;
import com.rodri.dscatalog.services.exceptions.ResourceNotFoundException;
import com.rodri.dscatalog.tests.Factory;
import com.rodri.dscatalog.tests.TokenUtil;

//@WebMvcTest(ProductResource.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductResourceTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private ProductService service;
	
	@Autowired
	private TokenUtil tokenUtil;
	
	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;
	private ProductDTO productDTO;
	private PageImpl<ProductDTO> page;
	
	private String username;
	private String password;
	
	@BeforeEach
	void setUp() throws Exception
	{
		username = "maria@gmail.com";
		password = "123456";
		
		existingId = 1L;
		nonExistingId = 2L;
		dependentId = 3L;
		productDTO = Factory.createProductDTO();
		page = new PageImpl<>(List.of(productDTO));
		
		when(service.findAllPaged(any(),any(),any())).thenReturn(page);
		
		when(service.findById(existingId)).thenReturn(productDTO);
		when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
		
		when(service.update(eq(existingId), any())).thenReturn(productDTO);
		when(service.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);
		
		when(service.insert(any())).thenReturn(productDTO);
		
		doNothing().when(service).delete(existingId);
		doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
		doThrow(DataBaseException.class).when(service).delete(dependentId);
	}
	
	@Test
	public void findAllShouldReturnPage() throws Exception
	{
		ResultActions result = mvcGet("/products");
		
		result.andExpect(status().isOk());
	}
	
	@Test
	public void findByIdShouldReturnProductWhenIdExists() throws Exception
	{
		ResultActions result = mvcGet("/products/{id}",existingId);
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception
	{
		ResultActions result = mvcGet("/products/{id}",nonExistingId);
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception
	{
		ResultActions result = mvcPut("/products/{id}",existingId);
				
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception
	{
		ResultActions result = mvcPut("/products/{id}",nonExistingId);
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void insertShouldReturnProductDTOCreated() throws Exception
	{
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		MediaType jsonType = MediaType.APPLICATION_JSON;
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		ResultActions result = mockMvc.perform(post("/products")
									  .header("Authorization", "Bearer " + accessToken)
		  			  				  .content(jsonBody)
		  			  				  .contentType(jsonType)
		  			  				  .accept(jsonType));
		
		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").exists());
	}
	
	@Test
	public void deleteShouldReturnNoContentWhenIdExists() throws Exception
	{
		ResultActions result = mvcDelete("/products/{id}",existingId);
		
		result.andExpect(status().isNoContent());
	}
	
	@Test
	public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception
	{
		ResultActions result = mvcDelete("/products/{id}",nonExistingId);
		
		result.andExpect(status().isNotFound());
	}
	
	
	private ResultActions mvcGet(String urlTemplate,Object... uriVars) throws Exception
	{
		return mockMvc.perform(get(urlTemplate,uriVars).accept(MediaType.APPLICATION_JSON));
	}
	
	private ResultActions mvcDelete(String urlTemplate,Object... uriVars) throws Exception
	{
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		return mockMvc.perform(delete(urlTemplate,uriVars)
					  .header("Authorization", "Bearer " + accessToken)
					  .accept(MediaType.APPLICATION_JSON));
	}
	
	private ResultActions mvcPut(String urlTemplate,Object... uriVars) throws Exception
	{
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		MediaType jsonType = MediaType.APPLICATION_JSON;
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		return mockMvc.perform(put(urlTemplate,uriVars)
					  .header("Authorization", "Bearer " + accessToken)
		  			  .content(jsonBody)
		              .contentType(jsonType)
		              .accept(jsonType));
	}
}
