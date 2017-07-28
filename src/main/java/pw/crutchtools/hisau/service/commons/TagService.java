package pw.crutchtools.hisau.service.commons;

import java.util.List;

import pw.crutchtools.hisau.domain.commons.Tag;

public interface TagService {

	public List<Tag> getAllTags();
	
	public Tag getOrAddTag(String name);
	
	public void deleteTag(Long id);
}
