package org.techhub.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "resources")
@Getter
@Setter
@NoArgsConstructor
public class Resource {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 150)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private ResourceType type;

	@Column(length = 500)
	private String description;

	@Column(nullable = false)
	private Boolean available = true;

	@Column(length = 200)
	private String location;

	@Column(nullable = true)
	private Double price;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	public void setCreatedDate() {

		LocalDateTime currentTime = LocalDateTime.now();

		createdAt = currentTime;
		updatedAt = currentTime;
	}

	@PreUpdate
	public void setUpdatedDate() {

		updatedAt = LocalDateTime.now();
	}
}