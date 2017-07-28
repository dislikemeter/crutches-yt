package pw.crutchtools.hisau.component.repo.ytstats;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import pw.crutchtools.hisau.domain.ytstats.Video;
import pw.crutchtools.hisau.domain.ytstats.VideoStat;

@Repository
public interface VideoStatRepository extends JpaRepository<VideoStat, Long> {

	//@Query(value = "SELECT * FROM video_stat WHERE video = ?1 ORDER BY timestamp ASC")
	public List<VideoStat> getByVideoOrderByTimestampAsc(Video video);
	
	@Query(value = "SELECT vs FROM VideoStat vs WHERE vs.video = ?1 AND vs.timestamp > ?2 ORDER BY vs.timestamp ASC")
	public List<VideoStat> getVideoStatisticsFrom(Video video, Date from);
	
	public void deleteByVideo(Video video);
	
}
