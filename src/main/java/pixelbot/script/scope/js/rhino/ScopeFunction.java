package pixelbot.script.scope.js.rhino;

import java.util.*;

import org.mozilla.javascript.*;
import org.mozilla.javascript.debug.DebuggableScript;

import pixelbot.script.scope.general.*;

public class ScopeFunction extends WrappersBase implements IScopeFunction {
	Function scriptable = null;

	public ScopeFunction(IConverter converter, Function scriptable) {
		super(converter);
		this.scriptable = scriptable;
	}

	@Override
	public Object getObject() {
		return this.scriptable;
	}

	@Override
	public IScopeObject call(IScopeObject thisObj, IScopeObject... params) {
		// Context cx, Scriptable scope, Scriptable thisObj, Object[] args
		try {
			Context cx = Context.enter();
			return this.converter.ScriptObjectToScopeObject(this.scriptable.call(cx,
					this.scriptable.getParentScope(),
					(Scriptable) this.converter.ScopeObjectToScriptObject(thisObj),
					this.converter.ScopeObjectArrayToScriptObjectArray(params)));
		} finally {
			Context.exit();
		}
	}

	@Override
	public Collection<String> getArgsNames() {
		try {
			NativeFunction t = (NativeFunction) this.scriptable;

			DebuggableScript ds = t.getDebuggableView();

			String[] r = new String[ds.getParamCount()];

			for (int i = 0; i < r.length; ++i) {
				r[i] = ds.getParamOrVarName(i);
			}
			return Arrays.asList(r);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Arrays.asList(new String[] {});
	}

	@Override
	public String origin() {
		return "rhino";
	}

}
