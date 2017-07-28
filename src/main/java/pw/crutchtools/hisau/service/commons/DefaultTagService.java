package pw.crutchtools.hisau.service.commons;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import pw.crutchtools.hisau.component.repo.commons.TagRepository;
import pw.crutchtools.hisau.domain.commons.Tag;

@Service
public class DefaultTagService implements TagService {
	private List<Tag> allTags;
	
	@Resource
	TagRepository tagRepo;

	@Override
	public List<Tag> getAllTags() {
		if (allTags == null)
			allTags = tagRepo.findAll();
		return allTags;
	}

	@Override
	public Tag getOrAddTag(final String name) {
		for (Tag tag : getAllTags()) {
			if (tag.getName().toLowerCase().equals(name.toLowerCase())) {
				return tag;
			}
		}
		//if nothing found
		Tag newTag = new Tag(name);
		tagRepo.save(newTag);
		allTags.add(newTag);
		return newTag;
	}

	@Override
	public void deleteTag(Long id) {
		tagRepo.delete(id);
	}

}
