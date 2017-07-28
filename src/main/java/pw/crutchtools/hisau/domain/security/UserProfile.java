package pw.crutchtools.hisau.domain.security;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

import pw.crutchtools.hisau.domain.DomainEntity;

@Entity
@Table
public class UserProfile extends DomainEntity {
	
	@Column(nullable = false)
	private String firstName;

	@Column
	private String lastName;

	@Column
	private String middleName;

	@Column
	private String phone;

	@Column
	private String photo;

	@Column
	private String favstring;

	@Transient
	private Set<Integer> favorites = new HashSet<>();

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public boolean isFavorite(int moduleHash) {
		return this.favorites.contains(moduleHash);
	}
	
	public void fav(int moduleHash, boolean isFavorite) {
		if (isFavorite)
			this.favorites.add(moduleHash);
		else
			this.favorites.remove(moduleHash);
		this.favstring = this.favorites.stream().map(fav -> fav.toString()).collect(Collectors.joining(","));
	}
	
	// ==================
	public UserProfile() {
	}

	public UserProfile(String name) {
		this.firstName = name;
	}

	@PostLoad
	private void decomposeFavs() {
		if (this.favstring != null && this.favstring.length() > 0) {
			this.favorites = Arrays.asList(this.favstring.split(",")).stream().map(Integer::parseInt)
					.collect(Collectors.toSet());
		}
	}

}
