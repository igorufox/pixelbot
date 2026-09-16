package pixelbot.script.general;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.*;

import javax.swing.event.EventListenerList;

import pixelbot.components.editor.hint.*;
import pixelbot.log.ScriptLogLevel;
import pixelbot.misc.Tools;
import pixelbot.script.debuger.general.*;
import pixelbot.script.general.ScriptManager.ScriptEvent.ScriptState;
import pixelbot.script.helper.IOHelper;
import pixelbot.script.instantiator.general.*;
import pixelbot.script.misc.JSONScriptValue;
import pixelbot.script.parser.general.*;
import pixelbot.script.scope.general.*;

public class ScriptManager {

	private Thread work = null;
	private IOHelper helper;
	private Logger logger = Logger.getLogger("job");

	private Map<String, Collection<IScriptInstantiator>> script_compiled = new HashMap<String, Collection<IScriptInstantiator>>();
	private GlobalScope scope = new GlobalScope();

	private ScriptParserManager script_parser_manager = new ScriptParserManager(this);
	protected ScriptDebugerManager script_debug_manager = new ScriptDebugerManager(this);
	private ScriptInstantiatorManager script_instantiator_manager = new ScriptInstantiatorManager();

	public IOHelper getHelper() {
		return this.helper;
	}

	public GlobalScope getScope() {
		return this.scope;
	}

	public ScriptParserManager getScriptParsers() {
		return this.script_parser_manager;
	}

	public ScriptDebugerManager getScriptDebugger() {
		return this.script_debug_manager;
	}

	public ScriptInstantiatorManager getScriptInstantiator() {
		return this.script_instantiator_manager;
	}

	public ScriptManager(IOSettings settings) {
		this.helper = new IOHelper(this, settings);
		ServiceLoader<IScriptParserRegistrator> script_parser_regisrators = ServiceLoader
				.load(IScriptParserRegistrator.class);
		for (IScriptParserRegistrator r : script_parser_regisrators) {
			r.register(this);
		}
		ServiceLoader<IScriptDebugerRegistrator> script_debuger_regisrators = ServiceLoader
				.load(IScriptDebugerRegistrator.class);
		for (IScriptDebugerRegistrator r : script_debuger_regisrators) {
			r.register(this);
		}
		ServiceLoader<IScriptInstantiatorRegistrator> script_instantiator_regisrators = ServiceLoader
				.load(IScriptInstantiatorRegistrator.class);
		for (IScriptInstantiatorRegistrator r : script_instantiator_regisrators) {
			r.register(this);
		}
		this.scopeInit();
	}

	// public static class Message {
	// public enum MessageType {
	// None, Fatal, Error, Warning, Info, Trace, Success
	// }
	//
	// private String time;
	// private String text;
	// private MessageType type;
	//
	// public Message(String time, String text, MessageType type) {
	// this.time = time;
	// this.text = text;
	// this.type = type;
	// }
	//
	// public Message(String text, MessageType type) {
	// this(new SimpleDateFormat("hh:mm:ss.SSS").format(new Date()), text, type);
	// }
	//
	// public String getTime() {
	// return this.time;
	// }
	//
	// public String getText() {
	// return this.text;
	// }
	//
	// public MessageType getType() {
	// return this.type;
	// }
	//
	// }

	public boolean fillHintModel(Singleton sgt, HintListModel model) {
		model.clear();
		IScopeObject entries = this.scope;

		boolean found = true;
		for (String node : sgt) {
			if (entries instanceof IScopeMap) {
				if (((IScopeMap) entries).has(node)) {
					entries = ((IScopeMap) entries).get(node);
				} else {
					found = false;
					break;
				}
			} else {
				found = false;
				break;
			}
		}
		if (found && entries instanceof IScopeMap) {

			IScopeMap imap = (IScopeMap) entries;
			for (Map.Entry<String, IScopeObject> entry : imap) {
				if (entry.getKey().toLowerCase().startsWith(sgt.getMember())) {
					if (entry.getValue() instanceof IScopeFunction) {
						StringBuffer sb = new StringBuffer();
						sb.append(entry.getKey()).append("(");
						boolean b = false;
						for (String arg : ((IScopeFunction) entry.getValue()).getArgsNames()) {
							sb.append(arg).append(", ");
							b = true;
						}
						if (b)
							sb.setLength(sb.length() - 2);
						sb.append(")");

						model.add(sb.toString());
					} else {
						model.add(entry.getKey());
					}
				}
			}

		}

		model.sort();

		return model.getSize() > 0;
	}

	public Collection<ScopeJob> getJobs() {
		return this.scope.getJobs();
	}

	public ScopeJob getJob(String id) {
		return this.scope.getJob(id);
	}

	public void startWork(final ScopeJob job, final IScopeMap params, final boolean debug) {
		if (job == null) {
			ResourceBundle messages = ResourceBundle.getBundle(Tools.MESSAGES, Locale.getDefault());
			this.log(ScriptLogLevel.Fatal, messages.getString("control.job.not_selected"));
		} else {
			if (this.work != null) {
				this.stopWork();
			}

			this.work = new Thread("ScriptJob") {
				@Override
				public void run() {
					ScriptManager.this.fireStateChange(debug ? ScriptState.job_debug
							: ScriptState.job_run);

					try {
						if (debug)
							ScriptManager.this.script_debug_manager.startDebug();
						job.main(params);
						// } catch (ScriptException e) {
						// ScriptManager.this.helper.log(e.toString(), MessageType.Error,
						// Destination.job);
						// } catch (NoSuchMethodException e) {
						// ScriptManager.this.helper.log(e.toString(), MessageType.Error,
						// Destination.job);
					} catch (ThreadDeath e) {
						ResourceBundle messages = ResourceBundle.getBundle(Tools.MESSAGES,
								Locale.getDefault());
						ScriptManager.this.log(ScriptLogLevel.Error,
								messages.getString("control.job.stop"));
					} catch (UserInterruptionException e) {
						ResourceBundle messages = ResourceBundle.getBundle(Tools.MESSAGES,
								Locale.getDefault());
						ScriptManager.this.log(ScriptLogLevel.Error,
								messages.getString("control.job.user_activity"));
					} catch (RuntimeException e) {
						Throwable inner = e;
						while (inner instanceof RuntimeException && inner.getCause() != null) {
							inner = e.getCause();
						}

						if (inner instanceof InvocationTargetException && inner.getCause() != null) {
							if (inner.getCause() instanceof ThreadDeath) {
								ResourceBundle messages = ResourceBundle.getBundle(Tools.MESSAGES,
										Locale.getDefault());
								ScriptManager.this.log(ScriptLogLevel.Error,
										messages.getString("control.job.stop"));
							} else {
								ScriptManager.this.log(ScriptLogLevel.Error, inner.getCause()
										.getMessage());
							}
						} else if ("JavaScriptException".equals(inner.getClass().getSimpleName())) {
							ScriptManager.this.log(ScriptLogLevel.Error, inner.getMessage());
						} else {
							inner.printStackTrace();
							ScriptManager.this.log(ScriptLogLevel.Error, inner.toString());
						}
					} catch (Throwable e) {
						e.printStackTrace();
						ScriptManager.this.log(ScriptLogLevel.Fatal, e.toString());
					} finally {
						if (debug)
							ScriptManager.this.script_debug_manager.endDebug();

					}
					ScriptManager.this.fireStateChange(ScriptState.job_done);
				}
			};
			this.work.setContextClassLoader(Thread.currentThread().getContextClassLoader());
			this.work.start();
		}

	}

	@SuppressWarnings("deprecation")
	public void stopWork() {
		if (this.work != null) {
			this.work.stop();
		}
		this.work = null;

	}

	public void parse(String name, byte[] data) {
		try {
			if (data != null) {
				String type = name.substring(name.lastIndexOf('.') + 1);
				IScriptParser parser = this.script_parser_manager.get(type);
				if (parser != null) {
					Collection<IScriptInstantiator> units = parser.compile(name, data);
					if (units != null) {
						this.script_compiled.put(name, units);
						for (IScriptInstantiator unit : units) {
							unit.instantiate();
						}
						this.fireStateChange(ScriptState.jobs_changed);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void scopeReparse() {
		this.scopeInit();
		for (Collection<IScriptInstantiator> units : this.script_compiled.values()) {
			try {
				for (IScriptInstantiator unit : units) {
					unit.instantiate();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void scopeReset() {
		this.script_compiled.clear();
		this.scopeInit();
	}

	private void scopeInit() {
		this.scope.clear();
		this.scope.put("Helper", new JScopeNativeObject(new JConverter(), this.helper));

	}

	// public IScopeList getParamConfig(String name, IScopeMap values) {
	// try {
	// this.scope.getJob(name).params().getConfig(values);
	// } catch (Exception e) {}
	// return new JScopeList();
	// }
	//
	// public IScopeMap getParamDefaultValues(String name) {
	// return this.scope.getJob(name).params().getDefault();
	// }

	private EventListenerList listenerList = new EventListenerList();

	public static class ScriptEvent {
		public enum ScriptState {
			job_run, job_debug, job_done, jobs_changed;
		}

		public ScriptState state = ScriptState.job_done;
	}

	public abstract interface ScriptStateListener extends EventListener {
		public abstract void stateChanged(ScriptEvent event);
	}

	public void addStateListener(ScriptStateListener x) {
		this.listenerList.add(ScriptStateListener.class, x);
	}

	public void removeStateListener(ScriptStateListener x) {
		this.listenerList.remove(ScriptStateListener.class, x);
	}

	protected void fireStateChange(ScriptState state) {
		ScriptEvent event = new ScriptEvent();
		event.state = state;
		Object[] listeners = this.listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ScriptStateListener.class) {
				((ScriptStateListener) listeners[i + 1]).stateChanged(event);
			}
		}
	}

	public static class BreakPointEvent {
		public enum BreakPointType {
			Pause, Resume
		}

		private BreakPointType type;
		private String source;
		private int line;

		public BreakPointEvent(BreakPointType type, String source, int line) {
			this.type = type;
			this.source = source;
			this.line = line;
		}

		public BreakPointType getType() {
			return this.type;
		}

		public String getSource() {
			return this.source;
		}

		public int getLine() {
			return this.line;
		}

	}

	public abstract interface BreakPointListener extends EventListener {
		public abstract void breakpoint(BreakPointEvent event);
	}

	public void fireBreakPoint(BreakPointEvent event) {
		Object[] listeners = this.listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == BreakPointListener.class) {
				((BreakPointListener) listeners[i + 1]).breakpoint(event);
			}
		}
	}

	public void addBreakPointListener(BreakPointListener x) {
		this.listenerList.add(BreakPointListener.class, x);
	}

	public void removeBreakPointListener(BreakPointListener x) {
		this.listenerList.remove(BreakPointListener.class, x);
	}

	public void log(Level level, String msg) {
		this.logger.log(level, msg);
	}

	public static class StatisticsEvent {
		public enum StatisticsDest {
			state, statistics;
		}

		private StatisticsDest dest;
		private Object data;
		private int value;

		public StatisticsEvent(String dest, Object data, int value) {
			this.dest = StatisticsDest.valueOf(dest);
			this.data = data;
			this.value = value;
		}

		public StatisticsDest getDest() {
			return this.dest;
		}

		public Object getData() {
			return this.data;
		}

		public int getValue() {
			return this.value;
		}
	}

	public abstract interface StatisticsListener extends EventListener {
		public abstract void log(StatisticsEvent event);
	}

	public void fireStatictics(StatisticsEvent event) {
		Object[] listeners = this.listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == StatisticsListener.class) {
				((StatisticsListener) listeners[i + 1]).log(event);
			}
		}
	}

	public void addStatisticsListener(StatisticsListener x) {
		this.listenerList.add(StatisticsListener.class, x);
	}

	public void removeStatisticsListener(StatisticsListener x) {
		this.listenerList.remove(StatisticsListener.class, x);
	}

	public class ScriptParameters {
		public IScopeList config;
		public IScopeMap values;
	}

	public ScriptParameters decodeParams(ScopeJob job, String encoded) throws InterruptedException {
		ScriptParameters params = new ScriptParameters();
		if (encoded != null) {
			params.values = (IScopeMap) JSONScriptValue.parseForScope(this, encoded);
		}
		if (params.values == null) {
			params.values = job.params().getDefault();
		}

		params.config = job.params().getConfig(params.values);
		return params;
	}

	public File addExtension(File f, String extension) {
		if (f != null) {
			String filename = f.getName();
			if (filename.lastIndexOf('.') == -1) {
				return new File(f.getAbsolutePath() + "." + extension);
			}
		}
		return f;
	}

}
