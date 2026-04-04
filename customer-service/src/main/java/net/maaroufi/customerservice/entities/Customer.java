package net.maaroufi.customerservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Customer {

	public Customer() {
		super();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String surname;
	private String email;
	private int age;
	// ML features: used for user segmentation and Azure ML training
	private String gender;             // ex: "M", "F", "other"
	private String country;            // ex: "FR", "MA", "US"
	private String preferredCategory;  // ex: "electronics", populated from behavior history

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPreferredCategory() {
		return preferredCategory;
	}

	public void setPreferredCategory(String preferredCategory) {
		this.preferredCategory = preferredCategory;
	}

	public Customer(String name, String surname, String email, int age) {
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.age = age;
	}

}