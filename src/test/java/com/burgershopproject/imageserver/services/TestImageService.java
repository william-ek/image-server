package com.burgershopproject.imageserver.services;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.hibernate.StaleObjectStateException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import com.burgershopproject.imageserver.ImageServerApplication;
import com.burgershopproject.imageserver.models.Image;
import com.burgershopproject.imageserver.models.ImageDto;
import com.burgershopproject.imageserver.repositories.ImageRepository;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ImageServerApplication.class, MessageSource.class})
@SpringBootTest
@ActiveProfiles("test")
public class TestImageService {
	
	private List<Image> images;
	
	@InjectMocks
	private ImageService service;
	
	@MockBean
	private Image image;
	
	@MockBean
	private MessageSource messageSource;
	
	@Mock
	private ImageRepository repository;
	
	
	@Before
	public void setup() {
		
		MockitoAnnotations.initMocks(this);
		
		images = new ArrayList<>();
		
		Image image = new Image();
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
	public void gatAllImagesSucceeds() throws Exception {
		
		Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "name"));
		Page<Image> page = new PageImpl<>(images, pageable, 3L);

		
		when(repository.findAll(pageable)).thenReturn(page);
		
		ImageDto returnedImageDto = service.getImages(0, 20, "name", "DESC");
		
        assertEquals(new Integer(0), returnedImageDto.getOffset());
        assertEquals(new Integer(20), returnedImageDto.getLimit());
        assertEquals(new Integer(3), returnedImageDto.getTotalCount());
        assertEquals("name", returnedImageDto.getSortBy());
        assertEquals("DESC", returnedImageDto.getDirection());
        assertEquals(3, returnedImageDto.getImages().size());
        assertEquals("image1", returnedImageDto.getImages().get(0).getName());
        assertEquals("image2", returnedImageDto.getImages().get(1).getName());
        assertEquals("image3", returnedImageDto.getImages().get(2).getName());
        assertEquals("image/png", returnedImageDto.getImages().get(0).getType());
        assertEquals("image/jpg", returnedImageDto.getImages().get(1).getType());
        assertEquals("image/gif", returnedImageDto.getImages().get(2).getType());
        assertEquals("image1", returnedImageDto.getImages().get(0).getDescription());
        assertEquals("image2", returnedImageDto.getImages().get(1).getDescription());
        assertEquals("image3", returnedImageDto.getImages().get(2).getDescription());
        assertEquals("long image blob1", returnedImageDto.getImages().get(0).getImage());
        assertEquals("long image blob2", returnedImageDto.getImages().get(1).getImage());
        assertEquals("long image blob3", returnedImageDto.getImages().get(2).getImage());
        assertEquals(new Long(0), returnedImageDto.getImages().get(0).getVersion());
        assertEquals(new Long(1), returnedImageDto.getImages().get(1).getVersion());
        assertEquals(new Long(2), returnedImageDto.getImages().get(2).getVersion());
		
	}
	
	@Test
	public void getImageByNameSucceeds() throws Exception {
		
		image = new Image();
		image.setName("image3");
		image.setVersion(2L);
		image.setType("image/gif");
		image.setDescription("image3");
		image.setImage("long image blob3");
		
		when(repository.findByName("image3")).thenReturn(image);
		
        Image returnedImage = service.getImageByName("image3");
         
        assertEquals("image3", returnedImage.getName());
        assertEquals(new Long(2), returnedImage.getVersion());
        assertEquals("image/gif", returnedImage.getType());
        assertEquals("image3", returnedImage.getDescription());
        assertEquals("long image blob3", returnedImage.getImage());
		
	}
	
	@Test
	public void getImageByNameFails() throws Exception {
		
		
		when(repository.findByName("image4")).thenThrow(new EmptyResultDataAccessException("Image not found", 1));
		
		exception.expect(EmptyResultDataAccessException.class);
		
        Image returnedImage = service.getImageByName("image4");
        
        assertEquals(null, returnedImage);
         
		
	}
	
	@Test
	public void addImageSucceeds() throws Exception {
		
		image = new Image();
		image.setName("image3");
		image.setType("image/gif");
		image.setDescription("image3");
		image.setImage("long image blob3");
		
		when(repository.save(image)).thenReturn(image);
		
		Image returnedImage = service.createImage(image);
         
        assertEquals("image3", returnedImage.getName());
        assertEquals(null, returnedImage.getVersion());
        assertEquals("image/gif", returnedImage.getType());
        assertEquals("image3", returnedImage.getDescription());
        assertEquals("long image blob3", returnedImage.getImage());
        
	}
	
	@Test
	public void addImageFailsDuplicate() throws Exception {
		
		image = new Image();
		image.setName("image3");
		image.setType("image/gif");
		image.setDescription("image3");
		image.setImage("long image blob3");
		
		when(repository.save(image)).thenThrow(new ConstraintViolationException("Duplicate entry 'image3' for key 'PRIMARY'", null, null));
		
		exception.expect(ConstraintViolationException.class);
		
		service.createImage(image);
		
        
	}
	
	@Test
	public void replaceImageSucceeds() throws Exception {
		
		image = new Image();
		image.setName("image3");
		image.setType("image/gif");
		image.setDescription("image3");
		image.setImage("long image blob3");
		
		when(repository.findByName("image3")).thenReturn(image);
		when(repository.save(image)).thenReturn(image);
		
		Image returnedImage = service.replaceImage(image);
         
        assertEquals("image3", returnedImage.getName());
        assertEquals(null, returnedImage.getVersion());
        assertEquals("image/gif", returnedImage.getType());
        assertEquals("image3", returnedImage.getDescription());
        assertEquals("long image blob3", returnedImage.getImage());
        
	}
	
	@Test
	public void replaceImageFailsNotFound() throws Exception {
		
		image = new Image();
		image.setName("image1");
		image.setType("image/gif");
		image.setDescription("image1");
		image.setImage("longer image blob1");
		
		when(repository.findByName("image3")).thenReturn(null);
		when(messageSource.getMessage("messages.imageNotFound", null, "Image not found", Locale.ENGLISH)).thenReturn("Image not found");
		
		exception.expect(ResponseStatusException.class);
		
		service.replaceImage(image);
		
        
	}
	
	@Test
	public void replaceImageFailsVersionStale() throws Exception {
		
		image = new Image();
		image.setName("image3");
		image.setType("image/gif");
		image.setDescription("image3");
		image.setImage("long image blob3");
		
		when(repository.findByName("image3")).thenReturn(image);
		when(repository.save(image)).thenThrow(new StaleObjectStateException("Image", "image3"));
		
		exception.expect(StaleObjectStateException.class);
		
		service.replaceImage(image);
		
	}
	
	

}
