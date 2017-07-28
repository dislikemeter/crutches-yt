package pw.crutchtools.hisau.service.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.crutchtools.hisau.component.mapping.security.RoleMapper;
import pw.crutchtools.hisau.component.repo.security.RoleRepository;
import pw.crutchtools.hisau.domain.security.Role;

@Service
public class DefaultRoleService implements RoleService {
	@Autowired
	RoleRepository repository;
	
	@Autowired
	RoleMapper roleMapper;
	
	@Override
	public List<Role> getAllRoles() {
		return repository.findAll();
	}
	
	@Override
	public Role createRole(String request) {
		Role newRole = roleMapper.mapToObject(new Role(), request);
		repository.save(newRole);
		return newRole;
	}
	
	@Override
	public void deleteRole(Long id) {
		repository.delete(id);
	}

	@Override
	public Role changeRole(Long id, String request) {
		Role role = repository.getOne(id);
		roleMapper.mapToObject(role, request);
		repository.save(role);
		return role;
	}

	@Override
	public Role getById(Long id) {
		return repository.getOne(id);
	}
}
