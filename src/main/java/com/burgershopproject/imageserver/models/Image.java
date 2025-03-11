package com.burgershopproject.imageserver.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Image Entity")
@Entity
@Table(name="images")
public class Image implements Serializable{
	
	@Transient
	private static final long serialVersionUID = 1L;

	@Transient
	private final Logger logger = LoggerFactory.getLogger(Image.class);

	@ApiModelProperty(notes="Unique name of the image")
	@Id
	@Column(name="image_name", nullable=false)
	@NotNull(message="{validation.image.name}")
	private String name;

	@ApiModelProperty(notes="Version used for optomistic locking")
	@Version
    @Column(name="version", nullable=true)
    private Long version;
	
	@ApiModelProperty(notes="Image media type e.g. image/png")
	@Column(name="image_type", nullable=false)
	@NotNull(message="{validation.image.type}")
	private String type;
	
	@ApiModelProperty(notes="Description of the image including tags for searches")
	@Column(name="image_description", nullable=false)
	@NotNull(message="{validation.image.description}")
	private String description;
	
	@ApiModelProperty(notes="The image itself as a base64 String")
	@Column(name="image_blob", nullable=false)
	@NotNull(message="{validation.image.image}")
	private String image;
	
	@Override
	public String toString() {
		return "Image [" + (name != null ? "name=" + name + ", " : "")
				+ (version != null ? "version=" + version + ", " : "") + (type != null ? "type=" + type + ", " : "")
				+ (description != null ? "description=" + description + ", " : "")
				+ (image != null ? "image=" + image : "") + "]";
	}
	
	/**
	 * This override is included only for JUNIT testing.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Image other = (Image) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}


	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

}
