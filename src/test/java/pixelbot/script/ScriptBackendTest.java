package pixelbot.script;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.Test;

import pixelbot.script.scope.general.IScopeFunction;
import pixelbot.script.scope.general.IScopeList;
import pixelbot.script.scope.general.IScopeMap;
import pixelbot.script.scope.general.IScopeNumber;
import pixelbot.script.scope.general.IScopeObject;
import pixelbot.script.scope.general.IScopeString;
import pixelbot.script.scope.general.JConverter;
import pixelbot.script.scope.js.graal.GraalConverter;
import pixelbot.script.scope.js.graal.GraalRuntime;
import pixelbot.script.scope.js.nashorn.NashornConverter;

/**
 * The two engines added alongside Rhino, exercised through the same scope model the rest of the
 * program talks to.
 */
class ScriptBackendTest {

	private static final String SCRIPT = """
			var Job = {
				name: 'demo',
				count: 3,
				items: [10, 20, 30],
				total: function(bonus) { return this.count + bonus; }
			};
			Job;
			""";

	@Test
	void graalExposesObjectsListsAndFunctions() {
		try (GraalRuntime runtime = new GraalRuntime()) {
			GraalConverter converter = new GraalConverter(runtime);
			Value value = runtime.eval(Source.create("js", SCRIPT));

			IScopeObject scoped = converter.ScriptObjectToScopeObject(value);
			assertJobShape(scoped, "graal");
		}
	}

	@Test
	void nashornExposesObjectsListsAndFunctions() throws Exception {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		assertNotNull(engine, "the standalone Nashorn engine should be on the test classpath");
		assertTrue(engine instanceof Compilable);

		CompiledScript compiled = ((Compilable) engine).compile(SCRIPT);
		Object value = compiled.eval();

		IScopeObject scoped = new NashornConverter().ScriptObjectToScopeObject(value);
		assertJobShape(scoped, "nashorn");
	}

	@Test
	void graalSeesHostObjectsHandedToIt() {
		try (GraalRuntime runtime = new GraalRuntime()) {
			GraalConverter converter = new GraalConverter(runtime);

			Map<String, Object> host = new HashMap<String, Object>();
			host.put("answer", Integer.valueOf(42));
			IScopeMap hostScope = (IScopeMap) new JConverter().ScriptObjectToScopeObject(host);

			Object proxy = converter.ScopeObjectToScriptObject(hostScope);
			runtime.run(() -> runtime.bindings().putMember("host", proxy));

			Value result = runtime.eval(Source.create("js", "host.answer + 1"));
			assertEquals(43, runtime.call(() -> Integer.valueOf(result.asInt())).intValue(),
					"a host map must be readable from JavaScript as an ordinary object");
		}
	}

	private static void assertJobShape(IScopeObject scoped, String expectedOrigin) {
		assertTrue(scoped instanceof IScopeMap, "the script's result should be an object");
		IScopeMap job = (IScopeMap) scoped;

		assertEquals(expectedOrigin, job.origin());
		assertTrue(job.has("name"));
		assertEquals("demo", ((IScopeString) job.get("name")).value());
		assertEquals(3, ((IScopeNumber) job.get("count")).intValue());

		assertTrue(Arrays.asList(job.getIds()).containsAll(
				Arrays.asList("name", "count", "items", "total")), "all members should be listed");

		IScopeList items = (IScopeList) job.get("items");
		assertEquals(3, items.size());
		assertEquals(20, ((IScopeNumber) items.get(1)).intValue());

		IScopeFunction total = (IScopeFunction) job.get("total");
		List<String> args = List.copyOf(total.getArgsNames());
		assertEquals(List.of("bonus"), args, "parameter names are recovered from the source");

		try {
			IScopeObject sum = total.call(job, new JConverter()
					.ScriptObjectToScopeObject(Integer.valueOf(4)));
			assertEquals(7, ((IScopeNumber) sum).intValue(), "3 + 4");
		} catch (InterruptedException e) {
			throw new AssertionError(e);
		}
	}
}
