package org.techhub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.techhub.entity.Resource;
import org.techhub.entity.ResourceType;


public interface ResourceRepository extends JpaRepository<Resource, Long> {

	List<Resource> findByType(ResourceType type);

	List<Resource> findByAvailable(Boolean available);
}