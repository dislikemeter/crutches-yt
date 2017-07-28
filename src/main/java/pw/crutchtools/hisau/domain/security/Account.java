package pw.crutchtools.hisau.domain.security;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import pw.crutchtools.hisau.app.SecurityConfig;
import pw.crutchtools.hisau.component.util.PassGen;
import pw.crutchtools.hisau.component.util.avatars.AvatarProvider;
import pw.crutchtools.hisau.domain.DomainEntity;
import pw.crutchtools.hisau.domain.SerializableEntity;
import pw.crutchtools.hisau.domain.ytstats.Video;

@Entity
@Table(name="account")
public class Account extends DomainEntity implements UserDetails, SerializableEntity {
	private static final long serialVersionUID = 1L;

	@Version
	public long getVersion() {
	    return serialVersionUID;
	}
	
	@Column(unique=true, nullable = false)
	private String username;
	
	@Column(nullable = false)
	private String password;
	
	@Column(nullable = false)
	private boolean enabled;
	
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date accountExpireDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	@Column(nullable = false)
	private Date creationDate;
	
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date credentialsExpireDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date passChangedDate;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="role_id")
	@NotNull
	private Role role;
	
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@PrimaryKeyJoinColumn
	@NotNull
	private UserProfile userProfile;
	
	//Getters And Setters
	
	@Override
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	private void setPassword(String password) {
		this.password = password;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Date getAccountExpireDate() {
		return accountExpireDate;
	}

	public void setAccountExpireDate(Date accountExpireDate) {
		this.accountExpireDate = accountExpireDate;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	private void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getCredentialsExpireDate() {
		return credentialsExpireDate;
	}

	private void setCredentialsExpireDate(Date credentialsExpireDate) {
		this.credentialsExpireDate = credentialsExpireDate;
	}

	public Date getPassChangedDate() {
		return passChangedDate;
	}

	private void setPassChangedDate(Date passChangedDate) {
		this.passChangedDate = passChangedDate;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public UserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}
	
	// Non GS
	
	@Override
	public boolean isAccountNonExpired() {
		return (accountExpireDate == null) || (System.currentTimeMillis() < this.accountExpireDate.getTime());
	}
	
	@Override
	public boolean isCredentialsNonExpired() {
		return (credentialsExpireDate == null) || (System.currentTimeMillis() < this.credentialsExpireDate.getTime());
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.getRole().getPermissions(); 
	}
	
	public String getFullName() {
		String result = ((userProfile.getLastName() != null) && (userProfile.getLastName().length()>0)) ?
				userProfile.getFirstName() + ' ' + userProfile.getLastName() :
					userProfile.getFirstName();
		return result;
	}
	
	public void changePassword(String password) {
		this.setPassword(SecurityConfig.passwordEncoder().encode(password));
		this.setPassChangedDate(new Date());
		this.setCredentialsExpireDate(null);			
	}
	
	public String resetPassword() {
		String newPassword = PassGen.generatePassword();
		this.setPassword(SecurityConfig.passwordEncoder().encode(newPassword));
		this.setPassChangedDate(new Date());
		this.setCredentialsExpireDate(new Date(System.currentTimeMillis() + 86400000));
		return newPassword;
	}
	
	public boolean hasAnyAuthority(String... perms) {
		for (String perm : perms) {
			if (this.getRole().getPermissions().stream()
					.map(Permission::getAuthority)
					.anyMatch(accountPermName->accountPermName.equals(perm)))
				return true;
		}
		return false;
	}
	
	@PrePersist
	private void persist(){
		if (this.creationDate == null)
			this.setCreationDate(new Date());
	}
	
	public String getAvatar() {
		return AvatarProvider.getInstance().getAvatar(this);
	}

	@Override
	public JsonObject toJson() {
		JsonObject result = Json.object();
		
		result.add("id", this.getId())
			.add("email", this.getUsername())
			.add("avatar", this.getAvatar())
			.add("roleId", this.getRole().getId())
			.add("roleName", this.getRole().getHumanizedName())
			.add("enabled", this.isEnabled())
			
		//profile
			.add("firstName", this.getUserProfile().getFirstName())
			.add("lastName", this.getUserProfile().getLastName())
			.add("middleName", this.getUserProfile().getMiddleName())
			.add("phone", this.getUserProfile().getPhone())
		
		//dates
			.add("registeredDate", this.getCreationDate().getTime())
			.add("passChangeDate", this.getPassChangedDate().getTime());
		if (this.getAccountExpireDate() != null) {
			result.add("expireDate", this.getAccountExpireDate().getTime());
		} else {
			result.add("expireDate", Json.NULL);
		}
		
		if (this.getCredentialsExpireDate() != null) {
			result.add("passExpireDate", this.getCredentialsExpireDate().getTime());
		} else {
			result.add("passExpireDate", Json.NULL);
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj!=null) {
			if (obj instanceof Account) {
				return this.username.equals(((Account) obj).getUsername());
			} else {
				return false;
			}
		} else
			return this == null;
	}

	@Override
	public int hashCode() {
		return this.username.hashCode();
	}
	
	/*linked entities
	 * i need to delete if account-owner has been deleted
	 * */

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "owner", orphanRemoval=true)
	private Set<Video> myVideos = new HashSet<>();

}
