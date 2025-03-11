package com.burgershopproject.imageserver.controllers;

import java.util.Locale;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.burgershopproject.imageserver.models.Image;
import com.burgershopproject.imageserver.models.ImageDto;
import com.burgershopproject.imageserver.services.ImageService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(tags = "BurgerShop Image Service")
@RestController
@RequestMapping("/image")
public class ImageController {
	
    private final Logger logger = LoggerFactory.getLogger(ImageController.class);
    
	@Autowired
	MessageSource messageSource;
	
    @Autowired
	ImageService service;
    
    @ApiOperation(value = "Retrives a list of all Images in the repository", response = Image.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved list"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
    })
	@ResponseStatus(HttpStatus.OK)
	@GetMapping()
	public ImageDto getImages(
            @RequestParam(defaultValue = "0") Integer offset, 
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
		logger.debug("Get Images");
		return service.getImages(offset, limit, sortBy, direction);
	}
	
	@ApiOperation("Retrieves a spacific Image by Name")
	@ApiResponses(value = {@ApiResponse(code=200, message="Service completed successfully"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code=404, message="Image was not found")})
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/{name}")
	public Image getImageByName(@PathVariable String name) {
		logger.debug(getImageByName(name).getDescription());
		Image image = service.getImageByName(name);
		if (image == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, messageSource.getMessage("messages.imageNotFound", null, null));
		}
		return image;
	}
	
	@ApiOperation("Adds an Image to the repository")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public Image postImage(@Valid @RequestBody Image image, BindingResult bindingResult) {
    	logger.debug("Post Image");
    	
        if (bindingResult.hasErrors()) {
        	StringBuilder errorMessages = new StringBuilder();
        	bindingResult.getAllErrors().forEach(error -> {
        		errorMessages.append(error.getDefaultMessage()  + "; ");
        	});
			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, errorMessages.toString());
        }
    	

		return service.createImage(image);


    }
    
	@ApiOperation("Replaces an Image in the repository")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping()
    public Image putImage(@Valid @RequestBody Image image, BindingResult bindingResult) {
    	logger.debug("Put Image");
    	
        if (bindingResult.hasErrors()) {
        	StringBuilder errorMessages = new StringBuilder();
        	bindingResult.getAllErrors().forEach(error -> {
        		errorMessages.append(error.getDefaultMessage()  + "; ");
        	});
			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, errorMessages.toString());

        }
    	
    	return service.replaceImage(image);

    }
    

}
