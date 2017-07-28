package pw.crutchtools.hisau.service.ytstats;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import pw.crutchtools.hisau.component.cron.ytstats.VideoStatsFetcher;
import pw.crutchtools.hisau.component.repo.ytstats.VideoRepository;
import pw.crutchtools.hisau.component.repo.ytstats.VideoStatRepository;
import pw.crutchtools.hisau.controller.exceptions.AccessDeniedException;
import pw.crutchtools.hisau.controller.exceptions.ResourceNotFoundException;
import pw.crutchtools.hisau.domain.commons.Tag;
import pw.crutchtools.hisau.domain.security.Account;
import pw.crutchtools.hisau.domain.ytstats.Video;
import pw.crutchtools.hisau.domain.ytstats.VideoStat;
import pw.crutchtools.hisau.service.commons.TagService;

@Service
public class DefaultYTStatsService implements YTStatsService {
	private static final String YOUTUBE_LINK = "^https?:\\/\\/(www\\.)?youtube\\.com\\/watch\\?(.*&)?v=([a-zA-Z0-9_-]{10,12}).*$";
	private static final String YOUTUBE_SUPERUSER = "PERM_YTSU";
	
	@Resource
	VideoStatRepository videoStatRepo;

	@Resource
	VideoRepository videoRepo;

	@Autowired
	VideoStatsFetcher backgroundTask;
	
	@Autowired
	TagService tagService;

	@Override
	public List<Video> getAllVideos() {
		return videoRepo.findAll();
	}

	@Override
	public Set<Video> getWatchedVideos() {
		return backgroundTask.getWatchedVideos();
	}

	@Override
	public Video addVideo(String request, Account owner) {
		JsonObject requestObject = Json.parse(request).asObject();
		String fullUrl = requestObject.getString("url", null);

		if (fullUrl == null || !fullUrl.matches(YOUTUBE_LINK))
			throw new RuntimeException("Cannot parse link!");
		Matcher matcher = Pattern.compile(YOUTUBE_LINK).matcher(fullUrl);
		matcher.find();
		String videoId = matcher.group(3);
		Video result = new Video(videoId, owner);
		videoRepo.save(result);
		return result;
	}
	
	@Override
	public void startCapturing(Long video, Account account) {
		Video found = videoRepo.findOne(video);
		if (found == null)
			throw new ResourceNotFoundException("This video does not exists");
		
		if (!account.hasAnyAuthority(YOUTUBE_SUPERUSER) && !found.isOwner(account))
			throw new AccessDeniedException();
		
		backgroundTask.addVideoWatcher(found);
	}

	@Override
	public void stopCapturing(Long video, Account account) {
		backgroundTask.removeVideoWatcher(getVideo(video, account, true));
	}

	@Override
	public List<VideoStat> getStatFor(Long video, Account account) {
		return videoStatRepo.getByVideoOrderByTimestampAsc(getVideo(video, account, false));
	}

	@Override
	public List<VideoStat> updateStatFor(Long video, Long from, Account account) {
		return videoStatRepo.getVideoStatisticsFrom(getVideo(video, account, false), new Date(from));
	}

	@Override
	public void deleteVideo(Long video, Account account) {
		Video found = getVideo(video, account, true);
		backgroundTask.removeVideoWatcher(found);
		videoRepo.delete(found);
	}

	@Override
	public Tag tagVideo(Long video, String tag, Account account) {
		Video found = getVideo(video, account, true);
		Tag newTag = tagService.getOrAddTag(tag);
		found.getTags().add(newTag);
		videoRepo.save(found);
		return newTag;
	}

	@Override
	public void untagVideo(Long video, Long tag, Account account) {
		Video found = getVideo(video, account, true);
		found.getTags().removeIf(i->i.getId().equals(tag));
		videoRepo.save(found);
	}

	@Override
	public void setShared(Long video, boolean sharedState, Account account) {
		Video found = getVideo(video, account, true);
		found.setIsShared(sharedState);
		videoRepo.save(found);
	}
	
	private Video getVideo(Long id, Account account, boolean writeAccess) {
		Video found = videoRepo.findOne(id);
		if (found == null)
			throw new ResourceNotFoundException("This video does not exists");
		
		if (found.isOwner(account)
				|| account.hasAnyAuthority(YOUTUBE_SUPERUSER)
				|| (found.getIsShared() && !writeAccess))
			return found;
		else
			throw new AccessDeniedException();
	}

}
