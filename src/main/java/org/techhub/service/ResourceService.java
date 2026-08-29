package org.techhub.service;

import java.util.List;

import org.techhub.dto.ResourceRequest;
import org.techhub.dto.ResourceResponse;
import org.techhub.entity.ResourceType;

public interface ResourceService {

	ResourceResponse createResource(ResourceRequest request);

	List<ResourceResponse> getAllResources();

	ResourceResponse getResourceById(Long id);

	List<ResourceResponse> getResourcesByType(ResourceType type);

	ResourceResponse updateResource(Long id, ResourceRequest request);

	void deleteResource(Long id);
}