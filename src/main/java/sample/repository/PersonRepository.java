package sample.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import sample.entities.ForceSensitiveOrder;
import sample.entities.Person;

@Repository
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