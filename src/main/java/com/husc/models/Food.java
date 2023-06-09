package com.husc.models;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

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
@Table(name = "food")
public class Food {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "food_id")
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String address;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String description;

	private Double price;

//	@OneToMany(mappedBy = "place", cascade = CascadeType.ALL)
	private String image;

	@Column
	private String createdBy;

	@JsonFormat(pattern = "dd/MM/yyyy", shape = Shape.STRING)
	@Column
	private Date createdDate;

	@JsonIgnore
	@OneToMany(mappedBy = "food", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ListImageFood> listImageFood;

	@JsonIgnore
	@OneToMany(mappedBy = "food", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	private List<CommentFood> comments;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@JsonIgnore
	private User user;
}
