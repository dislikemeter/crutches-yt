package pw.crutchtools.hisau.domain.commons;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import pw.crutchtools.hisau.domain.DomainEntity;
import pw.crutchtools.hisau.domain.SerializableEntity;

@Entity
@Table
public class Tag extends DomainEntity implements SerializableEntity {

	@Column(nullable = false, unique = true)
	String name = "";
	
	@SuppressWarnings("unused")
	private Tag() {}
	
	public Tag(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public JsonObject toJson() {
		return Json.object().asObject().add("id", this.getId()).add("name", this.name);
	}
	
}
