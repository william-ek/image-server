package com.burgershopproject.imageserver.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.burgershopproject.imageserver.models.Image;

public interface ImageRepository extends JpaRepository<Image, String> {
	
	/**
	 * Retrieve an Image by name
	 * @param name
	 * @return
	 */
	public Image findByName(String name);

}
