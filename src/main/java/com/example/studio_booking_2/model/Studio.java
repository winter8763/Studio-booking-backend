package com.example.studio_booking_2.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "studios")
public class Studio {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

	private String imgUrl;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive;

	@ElementCollection
	@CollectionTable(name = "studio_open_days", joinColumns = @JoinColumn(name = "studio_id"))
	@Column(name = "day_of_week")
	private List<Integer> openDays; // 使用 0（日）到 6（六）表示開放日

	private LocalTime openStart;

	private LocalTime openEnd;

	private BigDecimal price;

	private LocalDateTime createAt;
	
	@Column(columnDefinition = "TEXT")
	private String info;

	@Column(columnDefinition = "TEXT")
	private String notice;

	@Column(columnDefinition = "TEXT")
	private String equipment;

	@PrePersist
	protected void onCreate() {
		this.createAt = LocalDateTime.now(); // 自動設定建立時間
		if (isActive == null)
			this.isActive = true; // 預設 isActive 為 true（如果沒設定）
	}

	@ManyToOne(optional = false)
	@JoinColumn(name = "owner_id", nullable = false)
	private User owner;
	
	
} 
