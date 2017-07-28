package pw.crutchtools.hisau.domain.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import pw.crutchtools.hisau.domain.DomainEntity;
import pw.crutchtools.hisau.domain.SerializableEntity;

@Entity
@Table
public class ConfigurationNode extends DomainEntity implements SerializableEntity {

	@SuppressWarnings("unused")
	private ConfigurationNode() {}
	
	public ConfigurationNode(String name) {
		this.name = name;
	}
	
	public ConfigurationNode(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public JsonObject toJson() {
		JsonObject result = Json.object().asObject()
				.add("id", getId())
				.add("name", name)
				.add("value", value);
		return result;
	}
	
}
