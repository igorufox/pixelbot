package pixelbot.script.scope.json;

import java.util.Map;

import net.sf.json.JSONFunction;
import pixelbot.script.general.ScriptManager;
import pixelbot.script.scope.general.*;

public class JsonConverter extends JConverter {
	private ScriptManager manager;

	public JsonConverter(ScriptManager manager) {
		this.manager = manager;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IScopeObject ScriptObjectToScopeObject(Object obj) {
		if (obj instanceof JSONFunction) {
			return new ScopeFunction((JSONFunction) obj, this.manager);
		} else if (obj instanceof Map<?, ?>) {
			return new ScopeMap(this, (Map<String, Object>) obj);
		}

		return super.ScriptObjectToScopeObject(obj);
	}

}
