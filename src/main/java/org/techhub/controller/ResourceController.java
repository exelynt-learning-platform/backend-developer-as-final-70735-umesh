package org.techhub.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.techhub.dto.ResourceRequest;
import org.techhub.dto.ResourceResponse;
import org.techhub.entity.ResourceType;
import org.techhub.service.ResourceService;

@RestController
@RequestMapping("/resources")
public class ResourceController {

	private final ResourceService resourceService;

	public ResourceController(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	// Create Resource
	@PostMapping
	public ResponseEntity<ResourceResponse> createResource(@RequestBody ResourceRequest request) {

		ResourceResponse response = resourceService.createResource(request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// Get All Resources
	@GetMapping
	public ResponseEntity<List<ResourceResponse>> getAllResources() {

		return ResponseEntity.ok(resourceService.getAllResources());
	}

	// Get Resource By ID
	@GetMapping("/{id}")
	public ResponseEntity<ResourceResponse> getResourceById(@PathVariable Long id) {

		return ResponseEntity.ok(resourceService.getResourceById(id));
	}

	// Get Resources By Type
	@GetMapping("/type/{type}")
	public ResponseEntity<List<ResourceResponse>> getResourcesByType(@PathVariable ResourceType type) {

		return ResponseEntity.ok(resourceService.getResourcesByType(type));
	}

	// Update Resource
	@PutMapping("/{id}")
	public ResponseEntity<ResourceResponse> updateResource(@PathVariable Long id,
			@RequestBody ResourceRequest request) {

		return ResponseEntity.ok(resourceService.updateResource(id, request));
	}

	// Delete Resource
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteResource(@PathVariable Long id) {

		resourceService.deleteResource(id);

		return ResponseEntity.ok("Resource deleted successfully");
	}
}