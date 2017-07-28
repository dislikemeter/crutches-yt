package pw.crutchtools.hisau.service.ytstats;

import java.util.List;
import java.util.Set;

import pw.crutchtools.hisau.domain.commons.Tag;
import pw.crutchtools.hisau.domain.security.Account;
import pw.crutchtools.hisau.domain.ytstats.Video;
import pw.crutchtools.hisau.domain.ytstats.VideoStat;

public interface YTStatsService {

	public List<Video> getAllVideos();
	
	//public Video getVideo(Long id);
	
	public Set<Video> getWatchedVideos();
	
	public Video addVideo(String request, Account owner);
	
	public void startCapturing(Long video, Account account);
	
	public void stopCapturing(Long video, Account account);
	
	public List<VideoStat> getStatFor(Long video, Account account);
	
	public List<VideoStat> updateStatFor(Long video, Long from, Account account);
	
	public void deleteVideo(Long video, Account account);
	
	public Tag tagVideo(Long video, String tag, Account account);
	
	public void untagVideo(Long video, Long tag, Account account);
	
	public void setShared(Long video, boolean sharedState, Account account);
	
}
