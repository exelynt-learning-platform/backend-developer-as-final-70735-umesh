package org.techhub.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.techhub.dto.ResourceRequest;
import org.techhub.dto.ResourceResponse;
import org.techhub.entity.Reservation;
import org.techhub.entity.ReservationStatus;
import org.techhub.entity.Resource;
import org.techhub.entity.ResourceType;
import org.techhub.exception.ResourceNotFoundException;
import org.techhub.repository.ReservationRepository;
import org.techhub.repository.ResourceRepository;
import org.techhub.service.ResourceService;

@Service
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final ReservationRepository reservationRepository;

    public ResourceServiceImpl(
            ResourceRepository resourceRepository,
            ReservationRepository reservationRepository) {
        this.resourceRepository = resourceRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    @Transactional
    public ResourceResponse createResource(ResourceRequest request) {

        Resource resource = new Resource();

        resource.setName(request.getName());
        resource.setType(request.getType());
        resource.setDescription(request.getDescription());
        resource.setLocation(request.getLocation());
        resource.setPrice(request.getPrice());

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
                        new ResourceNotFoundException(
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
    @Transactional
    public ResourceResponse updateResource(
            Long id,
            ResourceRequest request) {

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Resource not found with id: " + id));

        resource.setName(request.getName());
        resource.setType(request.getType());
        resource.setDescription(request.getDescription());
        resource.setLocation(request.getLocation());
        resource.setPrice(request.getPrice());

        if (request.getAvailable() != null) {
            resource.setAvailable(request.getAvailable());
        }


        Resource updatedResource =
                resourceRepository.save(resource);

        return convertToResponse(updatedResource);
    }

    @Override
    @Transactional
    public void deleteResource(Long id) {

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Resource not found with id: " + id));

        // Reject deletion if active or pending reservations exist
        boolean hasActiveReservations = reservationRepository.existsByResourceIdAndStatusIn(
                id,
                List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED)
        );

        if (hasActiveReservations) {
            throw new IllegalStateException(
                    "Cannot delete resource with active or pending reservations"
            );
        }

        // Clean up historical cancelled reservations to prevent FK constraint violations
        List<Reservation> historicalReservations = reservationRepository.findByResourceId(id);
        if (!historicalReservations.isEmpty()) {
            reservationRepository.deleteAll(historicalReservations);
        }

        resourceRepository.delete(resource);
    }

    private ResourceResponse convertToResponse(Resource resource) {

        return new ResourceResponse(
                resource.getId(),
                resource.getName(),
                resource.getType(),
                resource.getDescription(),
                resource.getAvailable(),
                resource.getLocation(),
                resource.getPrice()
        );
    }
}