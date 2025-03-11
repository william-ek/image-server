package com.burgershopproject.imageserver.models;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * This test is included to test the equals implementation 
 * @author Bill
 *
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Image.class)
@SpringBootTest
public class TestImage {
	
	@Mock
	Image image;
	
	@Mock
	Image compareImage;
	
	
	@Before
	public void setup() {
		
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test
	public void equalCompareObjectIdentical() {
		
       
		image = new Image();
		
		assertTrue(image.equals(image));
		
	}
	
	@Test
	public void NotEqualCompareObjectNotImage() {
		
       
		image = new Image();
		
		Object compareObject = new Object();
		
		assertFalse(image.equals(compareObject));
		
	}
	
	@Test
	public void motEqualCompareObjectNull() {
		
       
		image = new Image();
		image.setName("image1");
		
		compareImage = null;
		
		assertFalse(image.equals(compareImage));
		
	}
	@Test
	public void motEqualCompareThisNameNull() {
		
       
		image = new Image();
		
		compareImage = new Image();
		compareImage.setName("image1");
		
		assertFalse(image.equals(compareImage));
		
	}
	
	@Test
	public void motEqualCompareNameNull() {
		
       
		image = new Image();
		image.setName("image1");
		
		compareImage = new Image();
		
		assertFalse(image.equals(compareImage));
		
	}
	
	@Test
	public void motEqualCompareNameNotEqual() {
		
       
		image = new Image();
		image.setName("image1");
		
		compareImage = new Image();
		image.setName("image2");
		
		assertFalse(image.equals(compareImage));
		
	}
	
	@Test
	public void motEqualCompareNameEqual() {
		
       
		image = new Image();
		image.setName("image1");
		
		compareImage = new Image();
		image.setName("image1");
		
		assertFalse(image.equals(compareImage));
		
	}
	

}
