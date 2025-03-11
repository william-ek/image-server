package com.burgershopproject.imageserver.services;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.burgershopproject.imageserver.models.Image;
import com.burgershopproject.imageserver.models.ImageDto;
import com.burgershopproject.imageserver.repositories.ImageRepository;

@Service
@Transactional(readOnly=true)
public class ImageService {
	
	private final Logger logger = LoggerFactory.getLogger(ImageService.class);
	
	@Autowired
	MessageSource messageSource;
	
	@Autowired
	ImageRepository repository;
	
	/**
	 * Retrieve all Images
	 * @param direction 
	 * @param sortBy 
	 * @param limit 
	 * @param offset 
	 * @return ImageDto
	 */
	public ImageDto getImages(Integer offset, Integer limit, String sortBy, String direction) {
		logger.debug("Get All Images");
		Pageable pageable = PageRequest.of(offset, limit, Sort.by(direction == "DESC" ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy));
		Page<Image> page = repository.findAll(pageable);
		return new ImageDto(offset, page.getSize(), Long.valueOf(page.getTotalElements()).intValue(), sortBy, direction, page.getContent());
	}
	
	/**
	 * Retrieve an Image by its name
	 * @param name
	 * @return
	 */
	public Image getImageByName(String name) {
		logger.debug("Get Image By Name: " + name);
		
		String[] st = "12345054321".split("0");
		int i = st.length;
		return repository.findByName(name);
	}
	
	/**
	 * Creates a new Image
	 * @param Image
	 * @return Image
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
	public Image createImage(Image image) {
		
		logger.debug("Create Image: " + image.getName());
		/*
		 * A new image will not yet have a version number
		 */
		image.setVersion(null);
		return repository.save(image);
	}
	
	/**
	 * Replaces an Image
	 * @param Image
	 * @return Image
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
	public Image replaceImage(Image image) {
		logger.debug("Replace Image: " + image.getName());
		Image returnedImage = repository.findByName(image.getName());
		if (returnedImage == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, messageSource.getMessage("messages.imageNotFound", null, "Image not found", Locale.ENGLISH));
		}
		return repository.save(image);
	}
	
	/**
	 * Images are never deleted.
	 */

	


}
