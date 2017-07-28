package pw.crutchtools.hisau.component.repo.ytstats;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pw.crutchtools.hisau.domain.ytstats.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

	public List<Video> findByVideoId(String videoId);
}
