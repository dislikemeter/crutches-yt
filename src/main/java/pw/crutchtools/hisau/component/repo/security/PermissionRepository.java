package pw.crutchtools.hisau.component.repo.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pw.crutchtools.hisau.domain.security.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
