package com.husc.models;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "place")
public class Place  {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name ="place_id")
	private Long id;

	@Column( nullable = false)
	private String title;

	@Column(nullable = false)
	private String address;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String shortDescription;
	
	@Column(nullable = false, columnDefinition = "TEXT")
	private String description;

//	@OneToMany(mappedBy = "place", cascade = CascadeType.ALL)
	private String image;
	
	@Column
	private String createdBy;
	
	@JsonFormat(pattern = "dd/MM/yyyy", shape = Shape.STRING)
	@Column
	private Date createdDate;
	
	@JsonIgnore
	@OneToMany(mappedBy = "place", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ListImagePlace> listImagePlace;
	
	@JsonIgnore
	@OneToMany(mappedBy = "place", cascade = CascadeType.ALL)
	private List<CommentPlace> comments;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@JsonIgnore
	private User user;
	
}