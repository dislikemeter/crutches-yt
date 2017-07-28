package pw.crutchtools.hisau.service.security;

import java.util.List;

import pw.crutchtools.hisau.domain.security.Permission;

public interface PermissionService {
	public List<Permission> getAllPermissions();
	public void rename(Permission permission, String name);
	public Permission create(String request);
	public Permission changePermission(Long id, String request);
	public void delete(Long id);
}
