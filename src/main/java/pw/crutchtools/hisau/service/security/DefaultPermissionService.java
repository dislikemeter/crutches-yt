package pw.crutchtools.hisau.service.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.crutchtools.hisau.component.mapping.security.PermissionMapper;
import pw.crutchtools.hisau.component.repo.security.PermissionRepository;
import pw.crutchtools.hisau.domain.security.Permission;

@Service
public class DefaultPermissionService implements PermissionService {
	
	@Autowired
	PermissionRepository repository;
	
	@Autowired
	PermissionMapper permissionMapper;
	
	@Override
	public List<Permission> getAllPermissions() {
		return repository.findAll();
	}
	
	@Override
	public void rename(Permission permission, String name) {
		permission.rename(name);
		repository.save(permission);		
	}

	@Override
	public Permission create(String request) {
		Permission permission = permissionMapper.mapToObject(new Permission(), request);
		repository.save(permission);
		return permission;
	}

	@Override
	public Permission changePermission(Long id, String request) {
		Permission permission = repository.getOne(id);
		permissionMapper.mapToObject(permission, request);
		repository.save(permission);
		return permission;
	}

	@Override
	public void delete(Long id) {
		repository.delete(id);
	}
	
}
