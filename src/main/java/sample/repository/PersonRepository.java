package sample.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import sample.entities.Person;

@RepositoryRestResource(collectionResourceRel = "person", path = "person")
public interface PersonRepository extends Neo4jRepository<Person, Long>{

	@Query(	
			"MATCH  (j:ForceSensitiveOrder) --> (p:Person), "
			+ " (s:ForceSensitiveOrder) --> (p)"
			+ "WHERE "
			+ "	j.name='jedi' AND"
			+ "	s.name='sith' "
			+ "RETURN p "
			
			)
	public List<Person> findX();
}