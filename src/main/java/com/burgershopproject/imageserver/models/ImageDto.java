package com.burgershopproject.imageserver.models;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ImageDto implements Serializable {

	@JsonIgnore
	private static final long serialVersionUID = 1L;
	
	private Integer offset;
	private Integer limit;
	private Integer totalCount;
	private String sortBy;
	private String direction;
	private List<Image> images;

	public ImageDto(Integer offset, Integer limit, Integer totalCount, String sortBy, String direction,
			List<Image> images) {
		super();
		this.offset = offset;
		this.limit = limit;
		this.totalCount = totalCount;
		this.sortBy = sortBy;
		this.direction = direction;
		this.images = images;
	}

	public Integer getOffset() {
		return offset;
	}

	public Integer getLimit() {
		return limit;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public String getSortBy() {
		return sortBy;
	}

	public String getDirection() {
		return direction;
	}

	public List<Image> getImages() {
		return images;
	}

	
}

