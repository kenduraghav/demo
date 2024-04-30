package com.example.demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.github.javafaker.Faker;

import lombok.Data;

@SpringBootApplication
public class DemoApplication {


	Faker faker = new Faker();

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner initializeData(PersonRepository personRepository) {
		return args -> {
			IntStream.range(0, 10).forEach(i -> {
				personRepository.save(createPerson());
			});
		};
	}

	private Person createPerson() {
		return new Person(faker.name().firstName(), faker.name().lastName());
	}
}

record PersonRecord(Long id, String firstName, String lastName) {}

@Data
class RequestParamModel {

	int pageNo = 0, pageSize = 5;
	String sortBy = "id", sortDirection = "asc";
}

@RestController
@RequestMapping("/api/persons")
class PersonController {

	private static final Logger log = LoggerFactory.getLogger(PersonController.class);

	@Autowired
	PersonService personService;

	// Create a GetMapping
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Map<String, Object> getPersons(@ModelAttribute RequestParamModel params) {
		log.info("Request Params: {}", params);
		Map<String, Object> allPerson = personService.getAllPerson(params);
		log.info("getPersons: {} ", allPerson);
		return allPerson;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PersonRecord createPerson(@RequestBody PersonRecord personRecord) {
		log.info("createPerson: request body {} ", personRecord);
		PersonRecord record = personService.createPerson(personRecord);
		log.info("createPerson: response body {} ", personRecord);
		return record;

	}
}

@Service
class PersonService {

//	private static final Logger log = LoggerFactory.getLogger(PersonService.class);

	@Autowired
	PersonRepository personRepository;

	public Map<String, Object> getAllPerson(RequestParamModel params) {
		Pageable pageRequest = PageRequest.of(params.getPageNo(), params.getPageSize(),
				Sort.by(Direction.fromString(params.getSortDirection()), params.getSortBy()));
		Page<Person> pagePerson = personRepository.findAll(pageRequest);
		List<PersonRecord> list = pagePerson.stream().map(this::mapToDto).toList();
		Map<String, Object> response = new HashMap<>();
		response.put("records", list);
		response.put("currentPage", pagePerson.getNumber());
		response.put("total", pagePerson.getTotalElements());
		response.put("totalPages", pagePerson.getTotalPages());
		return response;
	}

	PersonRecord createPerson(PersonRecord personRecord) {
		Person entity = personRepository.save(mapToEntity(personRecord));
		return mapToDto(entity);
	}

	private PersonRecord mapToDto(Person person) {
		return new PersonRecord(person.getId(), person.getFirstName(), person.getLastName());
	}

	private Person mapToEntity(PersonRecord personRecord) {
		return new Person(personRecord.firstName(), personRecord.lastName());
	}
}