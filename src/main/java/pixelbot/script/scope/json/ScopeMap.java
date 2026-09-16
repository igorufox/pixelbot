package pixelbot.script.scope.json;

import java.util.Map;

import pixelbot.script.scope.general.*;

public class ScopeMap extends JScopeMap {
	public ScopeMap(IConverter converter, Map<String, Object> map) {
		super(converter, map);
	}

	@Override
	public String toString() {
		if (this.has("toString")) {
			try {
				return this.converter.ScopeObjectToScriptObject(
						((IScopeFunction) this.get("toString")).call(this)).toString();
			} catch (Exception e) {}
		}
		return super.toString();
	}

}
