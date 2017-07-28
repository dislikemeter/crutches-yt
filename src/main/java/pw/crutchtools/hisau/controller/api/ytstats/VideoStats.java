package pw.crutchtools.hisau.controller.api.ytstats;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import pw.crutchtools.hisau.controller.api.AbstractAjaxAction;
import pw.crutchtools.hisau.controller.exceptions.BadRequestException;
import pw.crutchtools.hisau.controller.exceptions.RequestValidationException;
import pw.crutchtools.hisau.domain.security.Account;
import pw.crutchtools.hisau.domain.ytstats.Video;
import pw.crutchtools.hisau.domain.ytstats.VideoStat;
import pw.crutchtools.hisau.service.ytstats.YTStatsService;

@RestController
@RequestMapping(value = AbstractAjaxAction.API_PATH + "ytstats", produces = MediaType.APPLICATION_JSON_VALUE)
public class VideoStats extends AbstractAjaxAction {
	private static final String STATUS_ACTIVE = "{\"capturing\": true}";
	private static final String STATUS_INACTIVE = "{\"capturing\": false}";

	@Autowired
	private YTStatsService ytStatsService;

	@GetMapping
	@PreAuthorize("hasAuthority('PERM_YTSTATS')")
	public String getAllVideos() {
		Set<Video> watchedVideo = ytStatsService.getWatchedVideos();
		final boolean isYtSu = getCurrentUser().hasAnyAuthority("PERM_YTSU");
		final Account currentUser = getCurrentUser();
		JsonArray result = Json.array().asArray();
		
		ytStatsService.getAllVideos().stream()
		.filter(video->{
			if (isYtSu || video.getIsShared())
				return true;
			return video.isOwner(currentUser);
		})
		.forEach(video -> {
			JsonObject videoObject = video.toJson();
			videoObject
				.add("watched", watchedVideo.contains(video))
				.add("control", isYtSu || video.isOwner(currentUser));
			result.add(videoObject);
		});

		return result.toString();
	}

	@PostMapping
	@PreAuthorize("hasAuthority('PERM_YTSTATS')")
	public String addVideo(@RequestBody String request) {
		Video saved = ytStatsService.addVideo(request, getCurrentUser());
		JsonObject result = saved.toJson()
				.add("watched", false)
				.add("control", true);
		return result.toString();
	}

	@GetMapping(path = "/{videoId}")
	@PreAuthorize("hasAuthority('PERM_YTSTATS')")
	public String getStats(@PathVariable("videoId") Long id, @RequestParam(name = "from", required = false) Long from) {
		List<VideoStat> videoStats;
		
		if (from != null) {
			videoStats = ytStatsService.updateStatFor(id, from, getCurrentUser());
		} else {
			videoStats = ytStatsService.getStatFor(id, getCurrentUser());
		}

		/* slower than loop */
		/*long[] timestamps = videoStats.stream().mapToLong(vs->vs.getTimestamp().getTime()).toArray();
		long[] likecounts = videoStats.stream().mapToLong(VideoStat::getLikeCount).toArray();
		long[] dislikecounts = videoStats.stream().mapToLong(VideoStat::getDislikeCount).toArray();
		long[] viewcounts = videoStats.stream().mapToLong(VideoStat::getViewCount).toArray();
		long[] commentcounts = videoStats.stream().mapToLong(VideoStat::getCommentCount).toArray();*/
		
		long[] timestamps = new long[videoStats.size()];
		long[] likecounts = new long[videoStats.size()];
		long[] dislikecounts = new long[videoStats.size()];
		long[] viewcounts = new long[videoStats.size()];
		long[] commentcounts = new long[videoStats.size()];
		
		int index = 0;
		for (VideoStat vs: videoStats) {
			timestamps[index] = vs.getTimestamp().getTime();
			likecounts[index] = vs.getLikeCount();
			dislikecounts[index] = vs.getDislikeCount();
			viewcounts[index] = vs.getViewCount();
			commentcounts[index] = vs.getCommentCount();
			index++;
		}
		
		
		JsonObject result = Json.object().asObject();
		result.add("timestamps", Json.array(timestamps))
			.add("likeCount", Json.array(likecounts))
			.add("dislikeCount", Json.array(dislikecounts))
			.add("viewCount", Json.array(viewcounts))
			.add("commentCount", Json.array(commentcounts));

		return result.toString();
	}

	@PostMapping(path = "/{videoId}")
	@PreAuthorize("hasAuthority('PERM_YTSTATS')")
	public String setCapturing(@PathVariable("videoId") Long id, @RequestBody String request) {
		JsonObject requestObject = Json.parse(request).asObject();
		
		if (!requestObject.names().contains("status"))
			throw new BadRequestException("Invalid request");
		boolean newStatus = requestObject.get("status").asBoolean();
		
		if (newStatus) {
			ytStatsService.startCapturing(id, getCurrentUser());
			return STATUS_ACTIVE;
		} else {
			ytStatsService.stopCapturing(id, getCurrentUser());
			return STATUS_INACTIVE;
		}
	}

	@DeleteMapping(path = "/{videoId}")
	@PreAuthorize("hasAuthority('PERM_YTSTATS')")
	public String deleteVideo(@PathVariable("videoId") Long id) {
		ytStatsService.deleteVideo(id, getCurrentUser());
		return EMPTY_JSON;
	}
	
	@PostMapping(path = "/{videoId}/tag")
	@PreAuthorize("hasAuthority('PERM_YTSTATS')")
	public String tag(@RequestBody String request, @PathVariable("videoId") Long id) {
		JsonObject requestObject = Json.parse(request).asObject();
		String action = requestObject.getString("action", "").toLowerCase();
		
		switch (action) {
			case "bind":
				String name = requestObject.getString("name", null);
				if (name == null || name.length() == 0)
					throw new RequestValidationException("Tag name cannot be null");
				
				return ytStatsService.tagVideo(id, name, getCurrentUser()).toJson().toString();
			case "unbind":
				Long tagId = requestObject.getLong("tag", 0);
				if (tagId < 1)
					throw new BadRequestException("Tag id must be greater than 0");
				
				ytStatsService.untagVideo(id, tagId, getCurrentUser());
				return EMPTY_JSON;
			default:
				throw new BadRequestException("Unknown action: " + action);
		}
	}
	
	@PostMapping(path = "/{videoId}/share")
	@PreAuthorize("hasAuthority('PERM_YTSTATS')")
	public String setShared(@RequestBody String request, @PathVariable("videoId") Long id) {
		JsonObject requestObject = Json.parse(request).asObject();
		boolean newSharedState =requestObject.get("state").asBoolean();
		ytStatsService.setShared(id, newSharedState, getCurrentUser());
		return EMPTY_JSON;
	}

}
