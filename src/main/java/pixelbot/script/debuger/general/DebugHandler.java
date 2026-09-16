package pixelbot.script.debuger.general;

import java.util.*;

import pixelbot.script.general.ScriptManager.BreakPointEvent;
import pixelbot.script.scope.general.*;

public abstract class DebugHandler extends WrappersBase {
	private ScriptDebugerManager manager;

	public class CallFrame {
		private String function;
		private int line;
		private IScopeMap thisObj;
		private IScopeMap locals;

		public CallFrame(String function, int line, IScopeMap thisObj, IScopeMap locals) {
			this.function = function;
			this.line = line;
			this.thisObj = thisObj;
			this.locals = locals;
		}

		public void setLine(int line) {
			this.line = line;
		}

		@Override
		public String toString() {
			return this.function + ":" + this.line;
		}

		public IScopeMap getThisObj() {
			return this.thisObj;
		}

		public IScopeMap getLocals() {
			return this.locals;
		}
	}

	public DebugHandler(IConverter converter, ScriptDebugerManager manager) {
		super(converter);
		this.manager = manager;
	}

	@Override
	public String toString() {
		return this.getName();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof String) {
			return this.getId().equals(obj);
		} else {
			return super.equals(obj);
		}
	}

	@Override
	public int hashCode() {
		return this.getId().hashCode();
	}

	public abstract String getName();

	public abstract String getId();

	public abstract void addBreakPoint(String name, int line);

	public abstract void removeBreakPoint(String name, int line);

	public abstract Map<String, Integer[]> getBreakPoints();

	public CallFrame[] getCallStack() {
		return null;
	}

	public void fireBreakPoint(BreakPointEvent event) {
		this.manager.fireBreakPoint(event);
	}

	public abstract void resume();

	public abstract void step_into();

	public abstract void step_over();

	public abstract void step_return();

	public abstract void suspend();

	public abstract void startDebug();

	public abstract void endDebug();

}