package pixelbot.script.scope.js.graal;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.graalvm.polyglot.Value;

import pixelbot.script.scope.general.*;

/** A GraalJS function seen as a callable scope object. */
public class ScopeFunction extends WrappersBase implements IScopeFunction {

	/** Graal exposes no parameter names, so they are read back out of the function's source. */
	private static final Pattern PARAMETERS = Pattern.compile("^[^(]*\\(([^)]*)\\)");

	private final Value value;

	ScopeFunction(GraalConverter converter, Value value) {
		super(converter);
		this.value = value;
	}

	private GraalRuntime runtime() {
		return ((GraalConverter) this.converter).getRuntime();
	}

	@Override
	public Object getObject() {
		return this.value;
	}

	@Override
	public IScopeObject call(IScopeObject thisObj, IScopeObject... params) {
		return this.runtime().call(() -> {
			Object[] args = this.converter.ScopeObjectArrayToScriptObjectArray(params);
			Value result = thisObj == null ? this.value.execute(args) : this.value
					.invokeMember("call", prepend(
							this.converter.ScopeObjectToScriptObject(thisObj), args));
			return this.converter.ScriptObjectToScopeObject(result);
		});
	}

	private static Object[] prepend(Object head, Object[] tail) {
		Object[] result = new Object[tail.length + 1];
		result[0] = head;
		System.arraycopy(tail, 0, result, 1, tail.length);
		return result;
	}

	@Override
	public Collection<String> getArgsNames() {
		String source = this.runtime().call(() -> this.value.toString());
		Matcher matcher = PARAMETERS.matcher(source == null ? "" : source);
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
		return "graal";
	}

	@Override
	public String toString() {
		return this.runtime().call(() -> this.value.toString());
	}
}
