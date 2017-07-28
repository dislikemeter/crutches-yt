package pw.crutchtools.hisau.domain.security;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.springframework.security.core.GrantedAuthority;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import pw.crutchtools.hisau.domain.DomainEntity;
import pw.crutchtools.hisau.domain.SerializableEntity;

@Entity
public class Permission extends DomainEntity implements GrantedAuthority, SerializableEntity {
	private static final String AUTHORITY_PREFIX = "PERM_";

	@Column(unique = true)
	private String authority;

	public Permission() {
		
	}

	public Permission(String name) {
		this.rename(name);
	}

	@Override
	public String getAuthority() {
		return authority;
	}
	
	private void setAuthority(String name) {
		this.authority = name;
	}
	
	public void rename(String name) {
		String newName = name.toUpperCase();
		if (!newName.startsWith(AUTHORITY_PREFIX)) {
			newName = AUTHORITY_PREFIX + newName;
		}
		this.setAuthority(newName);
	}
	
	public String getHumanizedName() {
		return this.authority.substring(AUTHORITY_PREFIX.length());
	}

	@Override
	public JsonObject toJson() {
		return Json.object()
			.add("id", this.getId())
			.add("name", this.getHumanizedName());
	}
}
