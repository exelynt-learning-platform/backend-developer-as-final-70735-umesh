package org.techhub.serviceimpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.techhub.dto.ResourceRequest;
import org.techhub.dto.ResourceResponse;
import org.techhub.entity.Reservation;
import org.techhub.entity.ReservationStatus;
import org.techhub.entity.Resource;
import org.techhub.entity.ResourceType;
import org.techhub.exception.ResourceNotFoundException;
import org.techhub.repository.ReservationRepository;
import org.techhub.repository.ResourceRepository;

@ExtendWith(MockitoExtension.class)
class ResourceServiceImplTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    private Resource resource;

    @BeforeEach
    void setUp() {
        resource = new Resource();
        resource.setId(1L);
        resource.setName("Conference Room A");
        resource.setType(ResourceType.ROOM);
        resource.setDescription("Spacious conference room");
        resource.setLocation("Floor 2");
        resource.setPrice(150.0);
        resource.setAvailable(true);
    }

    @Test
    void createResource_shouldSaveAndReturn() {
        ResourceRequest request = new ResourceRequest();
        request.setName("Conference Room A");
        request.setType(ResourceType.ROOM);
        request.setDescription("Spacious conference room");
        request.setLocation("Floor 2");
        request.setPrice(150.0);
        request.setAvailable(true);

        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);

        ResourceResponse response = resourceService.createResource(request);

        assertNotNull(response);
        assertEquals("Conference Room A", response.getName());
        assertEquals(150.0, response.getPrice());
    }

    @Test
    void getResourceById_shouldReturnResource() {
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));

        ResourceResponse response = resourceService.getResourceById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Conference Room A", response.getName());
    }

    @Test
    void getResourceById_notFound_shouldThrow() {
        when(resourceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> resourceService.getResourceById(99L));
    }

    @Test
    void deleteResource_withActiveReservations_shouldThrowIllegalStateException() {
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        when(reservationRepository.existsByResourceIdAndStatusIn(
                eq(1L),
                eq(List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED))
        )).thenReturn(true);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> resourceService.deleteResource(1L)
        );

        assertEquals("Cannot delete resource with active or pending reservations", exception.getMessage());
    }

    @Test
    void deleteResource_withNoActiveReservations_shouldDeleteSuccessfully() {
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        when(reservationRepository.existsByResourceIdAndStatusIn(
                eq(1L),
                eq(List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED))
        )).thenReturn(false);

        Reservation cancelledReservation = new Reservation();
        cancelledReservation.setId(10L);
        cancelledReservation.setStatus(ReservationStatus.CANCELLED);

        when(reservationRepository.findByResourceId(1L)).thenReturn(List.of(cancelledReservation));

        resourceService.deleteResource(1L);

        verify(reservationRepository).deleteAll(List.of(cancelledReservation));
        verify(resourceRepository).delete(resource);
    }
}
