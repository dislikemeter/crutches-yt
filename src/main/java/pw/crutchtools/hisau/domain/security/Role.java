package pw.crutchtools.hisau.domain.security;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import javax.persistence.JoinColumn;

import pw.crutchtools.hisau.domain.DomainEntity;
import pw.crutchtools.hisau.domain.SerializableEntity;

@Entity
public class Role extends DomainEntity implements SerializableEntity {
	private static final String ROLE_PREFIX = "ROLE_";

	@Column(unique = true)
	private String name;
	
	@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="role2permission", joinColumns=@JoinColumn(name="role_id"), inverseJoinColumns=@JoinColumn(name="permission_id"))
	private Set<Permission> permissions;

	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}
	
	public void rename(String name) {
		String newName = name.trim().toUpperCase();
		if (!newName.startsWith(ROLE_PREFIX)) newName = ROLE_PREFIX + newName;
		this.setName(newName);
	}

	public Set<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
	}
	
	public String getHumanizedName(){
		return this.name.substring(ROLE_PREFIX.length());
	}
	
	public Role() {}
	
	public Role(String emptyRoleName) {
		this.rename(emptyRoleName);
	}

	@Override
	public JsonObject toJson() {
		JsonArray linkedPermissions = Json.array().asArray();
		this.getPermissions().stream().forEach((p) -> {
			linkedPermissions.add(p.getId());
		});
		JsonObject result = Json.object()
				.add("id", this.getId())
				.add("name", this.getHumanizedName())
				.add("linkedPermissions", linkedPermissions);
		return result;
	}
	
}
