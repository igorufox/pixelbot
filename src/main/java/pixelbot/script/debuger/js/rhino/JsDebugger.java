package pixelbot.script.debuger.js.rhino;

import java.lang.reflect.Field;
import java.util.*;

import org.mozilla.javascript.*;
import org.mozilla.javascript.debug.*;

import pixelbot.script.debuger.general.*;
import pixelbot.script.general.ScriptManager.BreakPointEvent;
import pixelbot.script.general.ScriptManager.BreakPointEvent.BreakPointType;
import pixelbot.script.scope.general.IScopeMap;
import pixelbot.script.scope.js.rhino.JsConverter;

public class JsDebugger extends DebugHandler implements Debugger {
	protected HashMap<String, JsDebugFrame> frames = new HashMap<String, JsDebugFrame>();
	protected StepType step = StepType.None;
	protected Stack<CallFrame> callstack = new Stack<CallFrame>();
	protected CallFrame step_return_target = null;

	public enum StepType {
		None, Into, Over, Return
	}

	public JsDebugger(ScriptDebugerManager manager) {
		super(new JsConverter(), manager);
	}

	@Override
	public void handleCompilationDone(Context cx, DebuggableScript fnOrScript, String source) {}

	@Override
	public DebugFrame getFrame(Context cx, DebuggableScript fnOrScript) {
		// fnOrScript.getLineNumbers()
		return this.getFrame(fnOrScript.getSourceName());
	}

	private JsDebugFrame getFrame(String source) {
		JsDebugFrame frame = this.frames.get(source);
		if (frame == null) {
			frame = new JsDebugFrame(source);
			this.frames.put(source, frame);
		}
		return frame;

	}

	public static class BreakPoint {
		private int line;
		private boolean enabled;

		public BreakPoint(int line) {
			this.line = line;
			this.enabled = true;
		}

		public int getLine() {
			return this.line;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public boolean isEnabled() {
			return this.enabled;
		}

	}

	private class JsDebugFrame implements DebugFrame {
		private String source;
		private List<BreakPoint> breakpoints = new ArrayList<BreakPoint>();

		JsDebugFrame(String source) {
			this.source = source;
		}

		@Override
		public void onEnter(Context cx, Scriptable activation, Scriptable thisObj, Object[] args) {
			if (activation instanceof NativeCall) {
				ScriptableObject arg = (ScriptableObject) activation.get("arguments", activation);
				NativeFunction f = (NativeFunction) arg.get("callee", arg);
				if ("".equals(f.getFunctionName())) {
					populateFunctionName(f);
				}
				StringBuffer sb = new StringBuffer();
				sb.append(f.getFunctionName());
				sb.append("(");
				for (int i = 0; i < f.getDebuggableView().getParamAndVarCount(); ++i) {
					sb.append(f.getDebuggableView().getParamOrVarName(i));
					sb.append(", ");
				}
				if (f.getDebuggableView().getParamAndVarCount() > 0) {
					sb.setLength(sb.length() - 2);
				}
				sb.append(")");
				JsDebugger.this.callstack.push(new CallFrame(sb.toString(), f.getDebuggableView()
						.getLineNumbers()[0], (IScopeMap) JsDebugger.this.converter
						.ScriptObjectToScopeObject(thisObj), (IScopeMap) JsDebugger.this.converter
						.ScriptObjectToScopeObject(activation)));
			} else {
				JsDebugger.this.callstack
						.push(new CallFrame("init?", 0, (IScopeMap) JsDebugger.this.converter
								.ScriptObjectToScopeObject(thisObj),
								(IScopeMap) JsDebugger.this.converter
										.ScriptObjectToScopeObject(activation)));
			}

			if (JsDebugger.this.step == StepType.Over) {
				JsDebugger.this.step = StepType.Return;
				JsDebugger.this.step_return_target = JsDebugger.this.callstack.peek();
			}
		}

		private void populateFunctionName(NativeFunction f) {
			try {
				Scriptable scope = f.getParentScope();
				DebuggableScript d = f.getDebuggableView();
				Field field = d.getClass().getDeclaredField("itsName");
				field.setAccessible(true);
				String name = this.findObjectInScope(scope, f);
				field.set(d, name);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private String findObjectInScope(Scriptable scope, Scriptable obj) {
			for (Object id : scope.getIds()) {
				Object child = null;
				if (id instanceof String) {
					child = scope.get((String) id, scope);
				} else if (id instanceof Integer) {
					child = scope.get(((Integer) id).intValue(), scope);
				}
				if (child == null)
					continue;
				if (child == obj) {
					return id.toString();
				}
				if (child instanceof ScriptableObject) {
					String res = findObjectInScope((Scriptable) child, obj);
					if (res != null) {
						return id.toString() + "." + res;
					}
				}
			}
			return null;
		}

		@Override
		public void onExceptionThrown(Context cx, Throwable ex) {}

		@Override
		public void onExit(Context cx, boolean byThrow, Object resultOrException) {
			CallFrame frame = JsDebugger.this.callstack.pop();
			if (JsDebugger.this.step == StepType.Return
					&& JsDebugger.this.step_return_target == frame) {
				JsDebugger.this.step_return_target = null;
				JsDebugger.this.step = StepType.Into;
			}
		}

		@Override
		public void onLineChange(Context cx, int line) {
			// cx.getThreadLocal(paramObject)
			JsDebugger.this.callstack.peek().setLine(line);
			if (isBreakpoint(line)) {
				JsDebugger.this.fireBreakPoint(new BreakPointEvent(BreakPointType.Pause,
						this.source, line));
				try {
					synchronized (JsDebugger.this) {
						JsDebugger.this.wait();
					}
				} catch (InterruptedException e) {}
				JsDebugger.this.fireBreakPoint(new BreakPointEvent(BreakPointType.Resume,
						this.source, line));
			}

		}

		@Override
		public void onDebuggerStatement(Context cx) {}

		private boolean isBreakpoint(int line) {
			switch (JsDebugger.this.step) {
			case Into:
				JsDebugger.this.step = StepType.None;
				return true;
			case Over:
				JsDebugger.this.step = StepType.None;
				return true;
			default:
			}
			for (BreakPoint b : this.breakpoints) {
				if (b.isEnabled() && b.getLine() == line) {
					return true;
				}
			}
			return false;
		}

		public void addBreakPoint(int line) {
			for (BreakPoint b : this.breakpoints) {
				if (b.getLine() == line) {
					b.setEnabled(true);
					return;
				}
			}
			this.breakpoints.add(new BreakPoint(line));

		}

		public void removeBreakPoint(int line) {
			for (BreakPoint b : this.breakpoints) {
				if (b.getLine() == line) {
					this.breakpoints.remove(b);
					return;
				}
			}
		}

		public Integer[] getBreakpoints() {
			List<Integer> result = new Vector<Integer>();
			for (BreakPoint b : this.breakpoints) {
				if (b.isEnabled()) {
					result.add(Integer.valueOf(b.getLine()));
				}
			}
			return result.toArray(new Integer[] {});
		}

	}

	@Override
	public void resume() {
		synchronized (JsDebugger.this) {
			JsDebugger.this.notify();
		}
	}

	@Override
	public void step_into() {
		this.step = StepType.Into;
		synchronized (JsDebugger.this) {
			JsDebugger.this.notify();
		}
	}

	@Override
	public void step_over() {
		this.step = StepType.Over;
		synchronized (JsDebugger.this) {
			JsDebugger.this.notify();
		}
	}

	@Override
	public void step_return() {
		this.step = StepType.Return;
		JsDebugger.this.step_return_target = JsDebugger.this.callstack.peek();
		synchronized (JsDebugger.this) {
			JsDebugger.this.notify();
		}
	}

	@Override
	public void suspend() {
		this.step = StepType.Into;
	}

	@Override
	public void addBreakPoint(String source, int line) {
		this.getFrame(source).addBreakPoint(line);

	}

	@Override
	public void removeBreakPoint(String source, int line) {
		this.getFrame(source).removeBreakPoint(line);
	}

	@Override
	public CallFrame[] getCallStack() {
		return this.callstack.toArray(new CallFrame[] {});
	}

	@Override
	public Map<String, Integer[]> getBreakPoints() {
		HashMap<String, Integer[]> result = new HashMap<String, Integer[]>();
		for (Map.Entry<String, JsDebugFrame> entry : this.frames.entrySet()) {
			Integer[] breakpoints = entry.getValue().getBreakpoints();
			if (breakpoints.length > 0) {
				result.put(entry.getKey(), breakpoints);
			}
		}
		return result;
	}

	@Override
	public void startDebug() {
		Context cx = Context.enter();
		cx.setDebugger(this, null);

	}

	@Override
	public void endDebug() {
		Context.exit();
	}

	@Override
	public String getName() {
		return "Mozilla Rhino script debugger";
	}

	@Override
	public String getId() {
		return "javascript/rhino";
	}
}
