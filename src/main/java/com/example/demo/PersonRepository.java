package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;


public interface PersonRepository extends JpaRepository<Person, Long> {

	//List<Person> findByLastName(@Param("name") String lastName);+
	
	
	//List<Person> findAllPerson(Pageable pageable);
}
