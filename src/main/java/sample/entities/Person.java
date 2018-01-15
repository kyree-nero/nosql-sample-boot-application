package sample.entities;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.Relationship;

public class Person  extends AbstractEntity {
	private String name;
	private String alias;
	
	@Relationship(type="PARTOF", direction = Relationship.UNDIRECTED)
    private Set<ForceSensitiveOrder> partOf = new HashSet<>();
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<ForceSensitiveOrder> getPartOf() {
		return partOf;
	}

	public void setPartOf(Set<ForceSensitiveOrder> partOf) {
		this.partOf = partOf;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public void addTo(ForceSensitiveOrder group) {
		partOf.add(group);
		group.getMembers().add(this);
	}
}	
