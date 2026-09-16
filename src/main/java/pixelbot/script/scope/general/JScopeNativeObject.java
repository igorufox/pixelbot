package pixelbot.script.scope.general;

import java.lang.reflect.*;
import java.util.*;
import java.util.Map.Entry;

import pixelbot.misc.MapEntry;

public class JScopeNativeObject extends WrappersBase implements IScopeNative {
	protected Object obj;
	private Map<String, IMember> members;
	private String origin;

	public interface IMember {
		IScopeObject getValue();
	}

	private class JField implements IMember {
		Field f;

		public JField(Field f) {
			this.f = f;
		}

		@Override
		public IScopeObject getValue() {
			try {
				return JScopeNativeObject.this.converter.ScriptObjectToScopeObject(this.f
						.get(JScopeNativeObject.this.obj));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		public void setValue(IScopeObject value) {
			try {
				this.f.set(JScopeNativeObject.this.obj,
						JScopeNativeObject.this.converter.ScopeObjectToScriptObject(value));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public JScopeNativeObject(IConverter converter, Object obj) {
		super(converter);
		this.obj = obj;
		this.members = new HashMap<String, IMember>();

		Class<?> c = this.obj.getClass();
		for (Field f : c.getFields()) {
			this.members.put(f.getName(), new JField(f));
		}

		for (Method m : c.getMethods()) {
			IMember member = this.members.get(m.getName());
			if (member == null || !(member instanceof JScopeNativeFunction)) {
				member = new JScopeNativeFunction(converter);
				this.members.put(m.getName(), member);
			}
			((JScopeNativeFunction) member).addMethod(m);
		}
	}

	@Override
	public Object getObject() {
		return this.obj;
	}

	@Override
	public IScopeObject get(String name) {
		IMember member = this.members.get(name);
		if (member == null) {
			return null;
		}
		return member.getValue();
	}

	@Override
	public boolean has(String name) {
		return this.members.containsKey(name);
	}

	@Override
	public void put(String name, IScopeObject value) {
		IMember member = this.members.get(name);
		if (member instanceof JField) {
			((JField) member).setValue(value);
		}
	}

	@Override
	public void delete(String name) {}

	@Override
	public String[] getIds() {
		return this.members.keySet().toArray(new String[] {});
	}

	@Override
	public Iterator<Entry<String, IScopeObject>> iterator() {
		final Iterator<Map.Entry<String, IMember>> iter = this.members.entrySet().iterator();
		return new Iterator<Map.Entry<String, IScopeObject>>() {
			Iterator<Map.Entry<String, IMember>> iterator = iter;

			@Override
			public void remove() {
				this.iterator.remove();
			}

			@Override
			public Entry<String, IScopeObject> next() {
				Map.Entry<String, IMember> entry = this.iterator.next();
				return new MapEntry<IScopeObject>(entry.getKey(),
						JScopeNativeObject.this.converter.ScriptObjectToScopeObject(entry
								.getValue().getValue()));
			}

			@Override
			public boolean hasNext() {
				return this.iterator.hasNext();
			}
		};
	}

	@Override
	public String toString() {
		return this.obj.toString();
	}

	@Override
	public String origin() {
		if (this.origin == null || this.origin.length() == 0) {
			return "general";
		} else {
			return this.origin + "/general";
		}
	}

	@Override
	public void setOrigin(String origin) {
		this.origin = origin;

	}
}
