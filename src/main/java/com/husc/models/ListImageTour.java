package com.husc.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "list_image_tour")
public class ListImageTour{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "list_image_tour_id")
	private Long id;
	
	@Column(name = "url_image")
	private String urlImage;
	
	@ManyToOne
    @JoinColumn(name = "tour_id")
    private Tour tour;
   
}
