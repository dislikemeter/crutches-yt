package pw.crutchtools.hisau.component.mapping.ytstats;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import pw.crutchtools.hisau.component.mapping.ParametrizedObjectMapper;
import pw.crutchtools.hisau.controller.exceptions.RequestValidationException;
import pw.crutchtools.hisau.domain.ytstats.Video;
import pw.crutchtools.hisau.domain.ytstats.VideoStat;

@Component
public class VideoStatMapper implements ParametrizedObjectMapper<Set<VideoStat>, Video> {

	@Override
	public Set<VideoStat> mapToObject(String input, Video... params) throws RequestValidationException {
		JsonObject root = Json.parse(input).asObject();
		JsonArray items = root.get("items").asArray();

		Set<VideoStat> result = new HashSet<>();

		for (JsonValue item : items) {
			JsonObject itemObject = item.asObject();

			// parse item id and etag
			String videoId = itemObject.getString("id", "");
			String etag = itemObject.getString("etag", "");

			// find video entity by videoId
			Video foundVideo = null;
			for (Video video : params) {
				if (video.getVideoId().equals(videoId)) {
					foundVideo = video;
					break;
				}
			}

			// create and fill VideoStat
			if (foundVideo == null)
				throw new RuntimeException(String.format("Cannot find video %s in active watchers", videoId));
			VideoStat videoStat = new VideoStat(foundVideo, etag);
			JsonObject statistics = itemObject.get("statistics").asObject();
			String tmp = statistics.getString("viewCount", null);
			videoStat.setViewCount(tmp != null ? Long.parseLong(tmp) : 0L);
			tmp = statistics.getString("likeCount", null);
			videoStat.setLikeCount(tmp != null ? Long.parseLong(tmp) : 0L);
			tmp = statistics.getString("dislikeCount", null);
			videoStat.setDislikeCount(tmp != null ? Long.parseLong(tmp) : 0L);
			tmp = statistics.getString("commentCount", null);
			videoStat.setCommentCount(tmp != null ? Long.parseLong(tmp) : 0L);

			result.add(videoStat);
		}

		return result;
	}

}
