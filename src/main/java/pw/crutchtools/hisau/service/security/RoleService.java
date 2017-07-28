package pw.crutchtools.hisau.service.security;

import java.util.List;

import pw.crutchtools.hisau.domain.security.Role;

public interface RoleService {
	public List<Role> getAllRoles();
	public Role createRole(String request);
	public void deleteRole(Long id);
	public Role changeRole(Long id, String request);
	public Role getById(Long id);
}
