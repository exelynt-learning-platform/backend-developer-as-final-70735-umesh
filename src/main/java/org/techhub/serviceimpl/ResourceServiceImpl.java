package org.techhub.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.techhub.dto.ResourceRequest;
import org.techhub.dto.ResourceResponse;
import org.techhub.entity.Resource;
import org.techhub.entity.ResourceType;
import org.techhub.repository.ResourceRepository;
import org.techhub.service.ResourceService;

@Service
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceServiceImpl(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Override
    public ResourceResponse createResource(ResourceRequest request) {

        Resource resource = new Resource();

        resource.setName(request.getName());
        resource.setType(request.getType());
        resource.setDescription(request.getDescription());

        if (request.getAvailable() != null) {
            resource.setAvailable(request.getAvailable());
        } else {
            resource.setAvailable(true);
        }


        Resource savedResource = resourceRepository.save(resource);

        return convertToResponse(savedResource);
    }

    @Override
    public List<ResourceResponse> getAllResources() {

        return resourceRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public ResourceResponse getResourceById(Long id) {

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Resource not found with id: " + id));

        return convertToResponse(resource);
    }

    @Override
    public List<ResourceResponse> getResourcesByType(ResourceType type) {

        return resourceRepository.findByType(type)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public ResourceResponse updateResource(
            Long id,
            ResourceRequest request) {

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Resource not found with id: " + id));

        resource.setName(request.getName());
        resource.setType(request.getType());
        resource.setDescription(request.getDescription());

        if (request.getAvailable() != null) {
            resource.setAvailable(request.getAvailable());
        }


        Resource updatedResource =
                resourceRepository.save(resource);

        return convertToResponse(updatedResource);
    }

    @Override
    public void deleteResource(Long id) {

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Resource not found with id: " + id));

        resourceRepository.delete(resource);
    }

    private ResourceResponse convertToResponse(Resource resource) {

        return new ResourceResponse(
                resource.getId(),
                resource.getName(),
                resource.getType(),
                resource.getDescription(),
                resource.getAvailable()
        );
    }
}