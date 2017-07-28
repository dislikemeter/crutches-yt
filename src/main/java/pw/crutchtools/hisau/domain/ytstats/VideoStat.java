package pw.crutchtools.hisau.domain.ytstats;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import pw.crutchtools.hisau.domain.DomainEntity;
import pw.crutchtools.hisau.domain.SerializableEntity;

@Entity
@Table
@SuppressWarnings("unused")
public class VideoStat extends DomainEntity {
	
	private VideoStat() {}
	
	public VideoStat(Video video, String etag) {
		this.timestamp = new Date();
		this.video = video;
		this.etag = etag;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date timestamp;
	
	@ManyToOne
	@JoinColumn(name = "video", nullable=false)
	private Video video;
	
	@Column(nullable = false)
	private Long likeCount;
	
	@Column(nullable = false)
	private Long dislikeCount;
	
	@Column(nullable = false)
	private Long viewCount;
	
	@Column(nullable = false)
	private Long commentCount;
	
	@Transient
	String etag;

	public Date getTimestamp() {
		return timestamp;
	}

	private void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Video getVideo() {
		return video;
	}

	private void setVideo(Video video) {
		this.video = video;
	}

	public Long getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(Long likesCount) {
		this.likeCount = likesCount;
	}

	public Long getDislikeCount() {
		return dislikeCount;
	}

	public void setDislikeCount(Long dislikesCount) {
		this.dislikeCount = dislikesCount;
	}

	public Long getViewCount() {
		return viewCount;
	}

	public void setViewCount(Long viewsCount) {
		this.viewCount = viewsCount;
	}

	public Long getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Long commentsCount) {
		this.commentCount = commentsCount;
	}

	public String getEtag() {
		return etag;
	}

	@Override
	public boolean equals(Object arg0) {
		if (this.etag == null) return false;
		if (arg0 instanceof VideoStat) {
			VideoStat argument = (VideoStat) arg0;
			return (argument.getEtag() != null) &&
					(this.getEtag().equals(argument.getEtag()));
		} else {
			return false;
		}
	}

}
