package sample.entities;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public abstract class AbstractEntity {
	@GraphId
	private Long id;
	
	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

        if (id == null || obj == null || !getClass().equals(obj.getClass())) {
            return false;
        }
        return id.equals(((AbstractEntity) obj).id);

    }

	@Override
	public int hashCode() {
		return id == null ? 0 : id.hashCode();
	}
}
