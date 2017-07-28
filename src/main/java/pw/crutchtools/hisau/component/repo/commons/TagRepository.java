package pw.crutchtools.hisau.component.repo.commons;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pw.crutchtools.hisau.domain.commons.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

	public Tag getByName(String name);
	
}
