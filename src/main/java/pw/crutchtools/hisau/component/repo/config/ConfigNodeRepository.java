package pw.crutchtools.hisau.component.repo.config;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pw.crutchtools.hisau.domain.config.ConfigurationNode;

@Repository
public interface ConfigNodeRepository extends JpaRepository<ConfigurationNode, Long> {

	public void deleteByName(String name);
	
	public ConfigurationNode getByName(String name);
	
}
