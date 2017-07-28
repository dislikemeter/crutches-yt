package pw.crutchtools.hisau.domain;

import java.util.Set;

import pw.crutchtools.hisau.domain.commons.Tag;

public interface TaggableEntity {

	public Set<Tag> getTags();
}
