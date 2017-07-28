package pw.crutchtools.hisau.domain;

import pw.crutchtools.hisau.domain.security.Account;

public interface SharedEntity {

	public Account getOwner();
	
	public boolean getIsShared();
	
}
