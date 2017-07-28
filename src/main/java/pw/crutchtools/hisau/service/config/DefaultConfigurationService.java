package pw.crutchtools.hisau.service.config;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import pw.crutchtools.hisau.component.repo.config.ConfigNodeRepository;
import pw.crutchtools.hisau.controller.exceptions.ServerErrorException;
import pw.crutchtools.hisau.domain.config.ConfigurationNode;

@Service
public class DefaultConfigurationService implements ConfigurationService {

	@Resource
	ConfigNodeRepository configRepo;
	
	private Map<String, String> store;
	
	@Override
	public String getParameter(String name) {
		if (store == null) initStore();
		return store.get(name);
	}

	@Override
	public ConfigurationNode saveParameter(String name, String value) {
		if (store == null) initStore();
		ConfigurationNode result;
		if (store.containsKey(name)) {
			result = configRepo.getByName(name);
			if (result != null) {
				result.setValue(value);
				configRepo.save(result);
			} else {
				throw new ServerErrorException("Cannot find existing parameter by name " + name);
			}
		} else {
			result = new ConfigurationNode(name, value);
			configRepo.save(result);
		}
		
		store.put(result.getName(), result.getValue());
		return result;
	}
	
	@Override
	public void deleteParameter(Long id) {
		ConfigurationNode found = configRepo.findOne(id);
		if (found == null)
			return;
		if (store == null)
			initStore();
		store.remove(found.getName());
		configRepo.delete(found);
	}

	@Override
	public List<ConfigurationNode> getAll() {
		return configRepo.findAll();
	}
	
	private void initStore() {
		List<ConfigurationNode> allNodes = configRepo.findAll();
		store = allNodes.stream().collect(Collectors.toMap(ConfigurationNode::getName, ConfigurationNode::getValue));
	}
	
}
