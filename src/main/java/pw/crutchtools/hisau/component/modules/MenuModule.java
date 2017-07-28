package pw.crutchtools.hisau.component.modules;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

public class MenuModule {
	
	private String path;
	private String catId;
	private String displayName;
	private String[] requiredPermissions = {};
	
	public MenuModule(String displayName, String path, String catId) {
		this.displayName = displayName;
		this.path = path;
		this.catId = catId;
	}
	
	public MenuModule(String displayName, String path, String catId, String... requiredPermissions) {
		this(displayName, path, catId);
		this.requiredPermissions = requiredPermissions;
	}

	public String getPath() {
		return path;
	}

	public String getCatId() {
		return catId;
	}
	
	public String[] getRequiredPermissions() {
		return requiredPermissions;
	}
	
	public boolean hasRequirements() {
		return requiredPermissions.length > 0;
	}

	public JsonObject toJson() {
		JsonObject result = Json.object().asObject()
				.add("id", hashCode())
				.add("name", displayName)
				.add("path", path)
				.add("catId", catId);
		return result;
	}
	
	@Override
	public int hashCode() {
		return this.path.hashCode();
	}
	
}
