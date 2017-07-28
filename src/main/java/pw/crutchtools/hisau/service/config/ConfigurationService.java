package pw.crutchtools.hisau.service.config;

import java.util.List;

import pw.crutchtools.hisau.domain.config.ConfigurationNode;

public interface ConfigurationService {
	public String getParameter(String name);
	
	public ConfigurationNode saveParameter(String name, String value);
	
	public void deleteParameter(Long id);
	
	public List<ConfigurationNode> getAll();
}
