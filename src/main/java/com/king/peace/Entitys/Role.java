package com.king.peace.Entitys;

import jakarta.persistence.*;

@Entity
public class Role {

	public Integer getId(ERole roleAdmin) {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ERole getName() {
		return name;
	}

	public void setName(ERole name) {
		this.name = name;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Enumerated(EnumType.STRING)
	@Column(length = 60)
	private ERole name;

	public Integer getId() {
		return id;
	}

	public Role(Integer id, ERole name) {
		this.id = id;
		this.name = name;
	}

	public Role() {
	}

}