package pw.crutchtools.hisau.domain.ytstats;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.codec.digest.DigestUtils;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import pw.crutchtools.hisau.domain.DomainEntity;
import pw.crutchtools.hisau.domain.SerializableEntity;
import pw.crutchtools.hisau.domain.SharedEntity;
import pw.crutchtools.hisau.domain.TaggableEntity;
import pw.crutchtools.hisau.domain.commons.Tag;
import pw.crutchtools.hisau.domain.security.Account;

@SuppressWarnings("unused")
@Entity
@Table(name="video")
public class Video extends DomainEntity implements SerializableEntity, TaggableEntity, SharedEntity {

	@Column(unique=true)
	private String videoId;
	
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date addedDate;
	
	@ManyToMany
	@JoinTable(name="video2tag", joinColumns=@JoinColumn(name="video_id"), inverseJoinColumns=@JoinColumn(name="tag_id"))
	private Set<Tag> tags = new HashSet<>();
	
	@ManyToOne
	@JoinColumn(name = "owner", nullable = false)
	private Account owner; 
	
	@Column(nullable = false)
	private Boolean isShared = false;
	
	private Video(){}
	
	public Video(String videoId, Account owner) {
		this.videoId = videoId;
		this.setOwner(owner);
		this.addedDate = new Date();
	}

	public String getVideoId() {
		return videoId;
	}

	private void setVideoId(String videoId) {
		this.videoId = videoId;
	}
	
	public Date getAddedDate() {
		return addedDate;
	}

	private void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}
	
	@Override
	public Set<Tag> getTags() {
		return tags;
	}

	private void setTags(Set<Tag> tags) {
		this.tags = tags;
	}
	
	@Override
	public Account getOwner() {
		return owner;
	}

	private void setOwner(Account owner) {
		this.owner = owner;
	}

	@Override
	public boolean getIsShared() {
		return isShared;
	}

	public void setIsShared(Boolean isShared) {
		this.isShared = isShared;
	}
	
	public boolean isOwner(Account account) {
		return this.owner.getId().equals(account.getId());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Video) {
			return this.videoId.equals(((Video) obj).getVideoId());
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return this.videoId.hashCode();
	}

	@Override
	public JsonObject toJson() {
		JsonObject result = Json.object().asObject()
				.add("id", this.getId())
				.add("videoId", this.videoId)
				.add("date", this.addedDate.getTime())
				.add("shared", this.isShared)
				.add("owner", this.owner.getFullName())
				.add("tags", Json.array(this.tags.stream().mapToLong(Tag::getId).toArray()));
		return result;
	}
	
	/*linked*/
	
	@OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<VideoStat> videoStatistics;
	
}
