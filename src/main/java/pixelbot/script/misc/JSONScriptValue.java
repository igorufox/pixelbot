package pixelbot.script.misc;

import java.io.Reader;

import pixelbot.json.JSONValue;
import pixelbot.script.general.ScriptManager;
import pixelbot.script.scope.general.*;
import pixelbot.script.scope.json.JsonConverter;

public class JSONScriptValue extends JSONValue  {
	
	
	public static String toJSONString(Object value_) {
		Object value;
		if (value_ instanceof IScopeObject) {
			value = new JConverter().ScopeObjectToScriptObject((IScopeObject) value_);
		} else {
			value = value_;
		}

		return  JSONValue.toJSONString_(value);
	}

	public static IScopeObject parseForScope(ScriptManager manager, String value) {
		return new JsonConverter(manager).ScriptObjectToScopeObject(parse(value));
	}
	public static IScopeObject parseForScope(ScriptManager manager, Reader reader) {
		return new JsonConverter(manager).ScriptObjectToScopeObject(parse(reader));
	}

}
