package com.burgershopproject.imageserver.controllers;


import org.hibernate.StaleObjectStateException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.burgershopproject.imageserver.models.Image;
import com.burgershopproject.imageserver.models.ImageDto;
import com.burgershopproject.imageserver.services.ImageService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@WebMvcTest(ImageController.class)
@ActiveProfiles("test")
public class TestImageController {
	
	@Autowired
	MessageSource messageSource;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@MockBean
	private Image image;
	
	private List<Image> images;

	@MockBean
	private ImageService service;

	@Autowired
	private MockMvc mvc;
	
	@Before
	public void setup() {
		
		images = new ArrayList<>();
		
		image = new Image();
		image.setName("image1");
		image.setVersion(0L);
		image.setType("image/png");
		image.setDescription("image1");
		image.setImage("long image blob1");
		images.add(image);
		
		image = new Image();
		image.setName("image2");
		image.setVersion(1L);
		image.setType("image/jpg");
		image.setDescription("image2");
		image.setImage("long image blob2");
		images.add(image);
		
		image = new Image();
		image.setName("image3");
		image.setVersion(2L);
		image.setType("image/gif");
		image.setDescription("image3");
		image.setImage("long image blob3");
		images.add(image);
		
		
	}
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	@Test
	public void getImagesDefaultSucceeds() throws Exception {
		
		ImageDto imageDto = new ImageDto(0, 20, 3, "name", "ASC", images);

		when(service.getImages(0, 20, "name", "ASC")).thenReturn(imageDto);

		mvc.perform(get("/image"))
			.andExpect(jsonPath("$.offset", equalTo(0)))
			.andExpect(jsonPath("$.limit", equalTo(20)))
			.andExpect(jsonPath("$.totalCount", equalTo(3)))
			.andExpect(jsonPath("$.sortBy", equalTo("name")))
			.andExpect(jsonPath("$.direction", equalTo("ASC")))
			.andExpect(jsonPath("$.images.[*].name", hasItems("image1", "image2", "image3")))
			.andExpect(jsonPath("$.images.[*].version", hasItems(0, 1, 2)))
			.andExpect(jsonPath("$.images.[*].type", hasItems("image/png", "image/jpg", "image/gif")))
			.andExpect(jsonPath("$.images.[*].description", hasItems("image1", "image2", "image3")))
			.andExpect(jsonPath("$.images.[*].image", hasItems("long image blob1", "long image blob2", "long image blob3")))
			.andExpect(status().isOk());
		
	}
	
	@Test
	public void getImagesPassedParamsSucceeds() throws Exception {
		
		ImageDto imageDto = new ImageDto(1, 5, 3, "type", "DESC", images);

		when(service.getImages(1, 5, "type", "DESC")).thenReturn(imageDto);

		mvc.perform(get("/image?offset=1&limit=5&sortBy=type&direction=DESC"))
			.andExpect(jsonPath("$.offset", equalTo(1)))
			.andExpect(jsonPath("$.limit", equalTo(5)))
			.andExpect(jsonPath("$.totalCount", equalTo(3)))
			.andExpect(jsonPath("$.sortBy", equalTo("type")))
			.andExpect(jsonPath("$.direction", equalTo("DESC")))
			.andExpect(jsonPath("$.images.[*].name", hasItems("image1", "image2", "image3")))
			.andExpect(jsonPath("$.images.[*].version", hasItems(0, 1, 2)))
			.andExpect(jsonPath("$.images.[*].type", hasItems("image/png", "image/jpg", "image/gif")))
			.andExpect(jsonPath("$.images.[*].description", hasItems("image1", "image2", "image3")))
			.andExpect(jsonPath("$.images.[*].image", hasItems("long image blob1", "long image blob2", "long image blob3")))
			.andExpect(status().isOk());
		
	}
	
	@Test
	public void getImageByNameSucceeds() throws Exception {

		when(service.getImageByName("image1")).thenReturn(images.get(0));

		mvc.perform(get("/image/image1"))
			.andExpect(jsonPath("$.name", equalTo("image1")))
			.andExpect(jsonPath("$.version", equalTo(0)))
			.andExpect(jsonPath("$.type", equalTo("image/png")))
			.andExpect(jsonPath("$.description", equalTo("image1")))
			.andExpect(jsonPath("$.image", equalTo("long image blob1")))
			.andExpect(status().isOk());
	}
	
	@Test
	public void getImageByNameNotFound() throws Exception {

		when(service.getImageByName("image5")).thenReturn(null);

		mvc.perform(get("/image/image5"))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$", containsString("Image not found")));
	}
	
	
	@Test
	public void addImageSucceeds() throws Exception {
		
		image = new Image();
		image.setName("image4");
		image.setVersion(0L);
		image.setType("image/png");
		image.setDescription("image4");
		image.setImage("long image blob4");

		when(service.createImage(image)).thenReturn(image);
		
		mvc.perform(post("/image")
				.content(objectMapper.writeValueAsString(image))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		        .andExpect(status().isCreated())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name", equalTo("image4")))
				.andExpect(jsonPath("$.version", equalTo(0)))
				.andExpect(jsonPath("$.type", equalTo("image/png")))
				.andExpect(jsonPath("$.description", equalTo("image4")))
				.andExpect(jsonPath("$.image", equalTo("long image blob4")));
	}
	
	@Test
	public void addImageFailsValidationName() throws Exception {
		
		image = new Image();
		image.setVersion(0L);
		image.setType("image/png");
		image.setDescription("image5");
		image.setImage("long image blob5");
		
		mvc.perform(post("/image")
				.content(objectMapper.writeValueAsString(image))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", equalTo("name is required;")))
				.andExpect(status().is4xxClientError());
	}
	
	@Test
	public void addImageFailsValidationType() throws Exception {
		
		image = new Image();
		image.setName("image5");
		image.setVersion(0L);
		image.setDescription("image5");
		image.setImage("long image blob5");
		
		mvc.perform(post("/image")
				.content(objectMapper.writeValueAsString(image))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", equalTo("type is required;")))
				.andExpect(status().is4xxClientError());
	}
	
	@Test
	public void addImageFailsValidationDescription() throws Exception {
		
		image = new Image();
		image.setName("image5");
		image.setVersion(0L);
		image.setType("image/png");
		image.setImage("long image blob5");
		
		mvc.perform(post("/image")
				.content(objectMapper.writeValueAsString(image))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", equalTo("description is required;")))
				.andExpect(status().is4xxClientError());
	}
	
	@Test
	public void addImageFailsValidationImage() throws Exception {
		
		image = new Image();
		image.setName("image5");
		image.setVersion(0L);
		image.setType("image/png");
		image.setDescription("image5");
		
		mvc.perform(post("/image")
				.content(objectMapper.writeValueAsString(image))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", equalTo("image is required;")))
				.andExpect(status().is4xxClientError());
	}
	
	@Test
	public void addImageFailsValidationAllFields() throws Exception {
		
		image = new Image();
		
		mvc.perform(post("/image")
				.content(objectMapper.writeValueAsString(image))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", containsString("name is required;")))
				.andExpect(jsonPath("$", containsString("type is required;")))
				.andExpect(jsonPath("$", containsString("description is required;")))
				.andExpect(jsonPath("$", containsString("image is required;")))
				.andExpect(status().is4xxClientError());
	}
	
	@Test
	public void addImageFailsDuplicate() throws Exception {
		
		image = new Image();
		image.setName("image1");
		image.setType("image/png");
		image.setDescription("image1");
		image.setImage("long image blob1");
		
		when(service.createImage(image)).thenThrow(new ConstraintViolationException("Duplicate Image", new SQLException(), ""));
		
		mvc.perform(post("/image")
				.content(objectMapper.writeValueAsString(image))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict());
	}
	
	@Test
	public void replaceImageSucceeds() throws Exception {
		
		image = new Image();
		image.setName("image4");
		image.setVersion(0L);
		image.setType("image/png");
		image.setDescription("image4");
		image.setImage("long image blob4");

		when(service.replaceImage(image)).thenReturn(image);
		
		mvc.perform(put("/image")
				.content(objectMapper.writeValueAsString(image))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		        .andExpect(status().isOk())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name", equalTo("image4")))
				.andExpect(jsonPath("$.version", equalTo(0)))
				.andExpect(jsonPath("$.type", equalTo("image/png")))
				.andExpect(jsonPath("$.description", equalTo("image4")))
				.andExpect(jsonPath("$.image", equalTo("long image blob4")));
	}
	
	@Test
	public void replaceImageFailsValidationName() throws Exception {
		
		image = new Image();
		image.setVersion(0L);
		image.setType("image/png");
		image.setDescription("image5");
		image.setImage("long image blob5");
		
		mvc.perform(put("/image")
				.content(objectMapper.writeValueAsString(image))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", equalTo("name is required;")))
				.andExpect(status().is4xxClientError());
	}
	
	@Test
	public void replaceImageFailsValidationType() throws Exception {
		
		image = new Image();
		image.setName("image5");
		image.setVersion(0L);
		image.setDescription("image5");
		image.setImage("long image blob5");
		
		mvc.perform(put("/image")
				.content(objectMapper.writeValueAsString(image))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", equalTo("type is required;")))
				.andExpect(status().is4xxClientError());
	}
	
	@Test
	public void replaceImageFailsValidationDescription() throws Exception {
		
		image = new Image();
		image.setName("image5");
		image.setVersion(0L);
		image.setType("image/png");
		image.setImage("long image blob5");
		
		mvc.perform(put("/image")
				.content(objectMapper.writeValueAsString(image))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", equalTo("description is required;")))
				.andExpect(status().is4xxClientError());
	}
	
	@Test
	public void replaceImageFailsValidationImage() throws Exception {
		
		image = new Image();
		image.setName("image5");
		image.setVersion(0L);
		image.setType("image/png");
		image.setDescription("image5");
		
		mvc.perform(put("/image")
				.content(objectMapper.writeValueAsString(image))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", equalTo("image is required;")))
				.andExpect(status().is4xxClientError());
	}
	
	@Test
	public void replaceImageFailsValidationAllFields() throws Exception {
		
		image = new Image();
		
		mvc.perform(put("/image")
				.content(objectMapper.writeValueAsString(image))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", containsString("name is required;")))
				.andExpect(jsonPath("$", containsString("type is required;")))
				.andExpect(jsonPath("$", containsString("description is required;")))
				.andExpect(jsonPath("$", containsString("image is required;")))
				.andExpect(status().is4xxClientError());
	}
	
	@Test
	public void replaceImageFailsStaleVersion() throws Exception {
		
		image = new Image();
		image.setName("image1");
		image.setVersion(0L);
		image.setType("image/png");
		image.setDescription("image1");
		image.setImage("long image blob1");
		
		when(service.createImage(image)).thenThrow(new StaleObjectStateException("Image", image));
		
		mvc.perform(post("/image")
				.content(objectMapper.writeValueAsString(image))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isPreconditionFailed());
	}
	

}
