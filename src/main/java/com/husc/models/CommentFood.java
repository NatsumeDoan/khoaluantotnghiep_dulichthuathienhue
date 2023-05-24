package com.husc.models;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "comment_food")
@NoArgsConstructor
public class CommentFood {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_food_id")
	private Long id;

	@Column(name = "content", columnDefinition = "TEXT")
	@NotEmpty(message = "*Please write something")
	private String content;

	@Column
	private String createdBy;

	@Column
	private Date createdDate;

	@ManyToOne
	@JoinColumn(name = "food_id")
	private Food food;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

}