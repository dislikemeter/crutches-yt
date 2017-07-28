package pw.crutchtools.hisau.domain;

import com.eclipsesource.json.JsonObject;

public interface SerializableEntity {

	public JsonObject toJson();
	
}
