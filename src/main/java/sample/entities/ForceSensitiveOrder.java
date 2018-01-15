package sample.entities;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;


public class ForceSensitiveOrder extends AbstractEntity{
	private String name;
	
	@Relationship(type="IS MADE OF", direction = Relationship.UNDIRECTED)
    private Set<Person> members = new HashSet<>();
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Person> getMembers() {
		return members;
	}

	public void setMembers(Set<Person> members) {
		this.members = members;
	}
	
	public void addTo(Person person) {
		members.add(person);
		person.getPartOf().add(this);
	}
}
