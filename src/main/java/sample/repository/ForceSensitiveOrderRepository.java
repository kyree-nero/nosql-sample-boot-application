package sample.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import sample.entities.ForceSensitiveOrder;

@RepositoryRestResource(collectionResourceRel = "orders", path = "orders")

public interface ForceSensitiveOrderRepository extends Neo4jRepository<ForceSensitiveOrder, Long>{
	
	public ForceSensitiveOrder findByName(String name);
	
	
	
}
