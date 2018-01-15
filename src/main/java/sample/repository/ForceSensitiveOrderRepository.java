package sample.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import sample.entities.ForceSensitiveOrder;

@Repository
public interface ForceSensitiveOrderRepository extends Neo4jRepository<ForceSensitiveOrder, Long>{
	
	public ForceSensitiveOrder findByName(String name);
	
	
	
}
