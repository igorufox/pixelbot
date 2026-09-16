package pixelbot.script.scope.js.graal;

import java.util.function.Supplier;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

/**
 * The one GraalJS context the scripts share, and the lock that serialises access to it.
 * <p>
 * A polyglot context may only be entered by one thread at a time, and every {@link Value} handed
 * out stays tied to it. Scripts run on a worker thread while the debug tree reads the same scope
 * from the event dispatch thread, so every touch of a value goes through {@link #call}. Holding one
 * lock is enough because the engine itself is what needs protecting, not the data.
 * <p>
 * The context is deliberately permissive — host access and host class lookup are open — because
 * scripts here exist to drive Java: that is the same trust model the Rhino backend has always had,
 * and it is why the program should only ever run scripts you wrote.
 */
public final class GraalRuntime implements AutoCloseable {

	private static final Engine ENGINE = Engine.newBuilder("js")
			.option("engine.WarnInterpreterOnly", "false")
			.build();

	private final Context context;

	public GraalRuntime() {
		this.context = Context.newBuilder("js")
				.engine(ENGINE)
				.allowHostAccess(HostAccess.ALL)
				.allowHostClassLookup(name -> true)
				.allowExperimentalOptions(true)
				.option("js.nashorn-compat", "true")
				.build();
	}

	/** Runs {@code action} with exclusive use of the context. */
	public synchronized <T> T call(Supplier<T> action) {
		return action.get();
	}

	public synchronized void run(Runnable action) {
		action.run();
	}

	/** The JavaScript global object: reading it lists every global the scripts have defined. */
	public synchronized Value bindings() {
		return this.context.getBindings("js");
	}

	public synchronized Value eval(Source source) {
		return this.context.eval(source);
	}

	@Override
	public synchronized void close() {
		this.context.close(true);
	}
}
