package pixelbot.script.scope.js.nashorn;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openjdk.nashorn.api.scripting.JSObject;

import pixelbot.script.scope.general.*;

/** A Nashorn function seen as a callable scope object. */
public class ScopeFunction extends WrappersBase implements IScopeFunction {

	/** Nashorn exposes no parameter names either, so they come from the function's source. */
	private static final Pattern PARAMETERS = Pattern.compile("^[^(]*\\(([^)]*)\\)");

	private final JSObject function;

	ScopeFunction(NashornConverter converter, JSObject function) {
		super(converter);
		this.function = function;
	}

	@Override
	public Object getObject() {
		return this.function;
	}

	@Override
	public IScopeObject call(IScopeObject thisObj, IScopeObject... params) {
		Object result = this.function.call(this.converter.ScopeObjectToScriptObject(thisObj),
				this.converter.ScopeObjectArrayToScriptObjectArray(params));
		return this.converter.ScriptObjectToScopeObject(result);
	}

	@Override
	public Collection<String> getArgsNames() {
		Matcher matcher = PARAMETERS.matcher(String.valueOf(this.function));
		if (!matcher.find() || matcher.group(1).isBlank()) {
			return Collections.emptyList();
		}

		List<String> names = new ArrayList<String>();
		for (String part : matcher.group(1).split(",")) {
			names.add(part.trim());
		}
		return names;
	}

	@Override
	public String origin() {
		return "nashorn";
	}

	@Override
	public String toString() {
		return String.valueOf(this.function);
	}
}
