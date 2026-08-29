package org.techhub.dto;

import org.techhub.entity.ResourceType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResourceResponse {

	private Long id;

	private String name;

	private ResourceType type;

	private String description;

	private Boolean available;
}