package pw.crutchtools.hisau.component.cron.ytstats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pw.crutchtools.hisau.component.mapping.ParametrizedObjectMapper;
import pw.crutchtools.hisau.component.repo.ytstats.VideoStatRepository;
import pw.crutchtools.hisau.component.util.SimpleHttpClient;
import pw.crutchtools.hisau.domain.ytstats.Video;
import pw.crutchtools.hisau.domain.ytstats.VideoStat;
import pw.crutchtools.hisau.service.config.ConfigurationService;

@Component
public class VideoStatsFetcher {
	private static final String BASE_URL = "https://www.googleapis.com/youtube/v3/videos?part=statistics";
	private static final int CACHE_SIZE = 600;

	Map<String, VideoStat> lastValues = new HashMap<>();
	List<VideoStat> cache = new ArrayList<>();
	Set<Video> watchedVideos = new HashSet<>();
	String joinedVideoIds = "";
	long lastCacheFlushTime = System.currentTimeMillis();

	@Autowired
	private ParametrizedObjectMapper<Set<VideoStat>, Video> videoStatMapper;

	@Resource
	private VideoStatRepository videoStatRepository;
	
	@Autowired
	private ConfigurationService configService;
	
	private Logger logger = Logger.getLogger(getClass());

	private void compileVideoIds() {
		StringJoiner sj = new StringJoiner(",");
		this.watchedVideos.stream().forEach(video -> {
			sj.add(video.getVideoId());
		});
		this.joinedVideoIds = sj.toString();
	}
	
	public void addVideoWatcher(Video video) {
		this.watchedVideos.add(video);
		compileVideoIds();
	}
	
	public void removeVideoWatcher(Video video) {
		this.watchedVideos.remove(video);
		compileVideoIds();
	}
	
	public Set<Video> getWatchedVideos(){
		return this.watchedVideos;
	}
	
	@Scheduled(fixedRate = 10000)
	private void tick() {
		if (watchedVideos.size() > 0) {
			try {
				//request
				String apiKey = configService.getParameter("service.google.api.key");
				String requestUrl = BASE_URL + "&key=" + apiKey + "&id=" + joinedVideoIds;
				String response = SimpleHttpClient.getRequest(requestUrl);
				
				//map response
				Video[] watchedVideosArr = watchedVideos.toArray(new Video[watchedVideos.size()]);
				Set<VideoStat> fetched = videoStatMapper.mapToObject(response, watchedVideosArr);

				// put OLD and NEW values only if there is difference
				fetched.stream().forEach(fetchedVs -> {
					if (lastValues.containsKey(fetchedVs.getVideo().getVideoId())) {	
						VideoStat lastVs = lastValues.get(fetchedVs.getVideo().getVideoId());
						if (!fetchedVs.equals(lastVs)) {
							cache.add(lastVs);
							cache.add(fetchedVs);
						}
					} else {
						cache.add(fetchedVs);
					}
					// update lastValues
					lastValues.put(fetchedVs.getVideo().getVideoId(), fetchedVs);
				});
			} catch (Exception e) {
				logger.warn(e);
				return;
			}
		}
		if ((!cache.isEmpty()) &&
				(cache.size() >= CACHE_SIZE ||
				System.currentTimeMillis() > (lastCacheFlushTime + 60000))) {
			flushCache();
		}
	}
	
	private void flushCache() {
		for (VideoStat vs : cache) {
			try {
				videoStatRepository.save(vs);
			} catch (Exception e) {
				logger.warn("Cannot persist entity", e);
			}
		}
		cache.clear();
		lastCacheFlushTime = System.currentTimeMillis();
	}
}
