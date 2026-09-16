package pixelbot.script.scope.general;

import java.lang.reflect.*;
import java.util.*;

import pixelbot.script.scope.general.JScopeNativeObject.IMember;

public class JScopeNativeFunction extends WrappersBase implements IMember, IScopeFunction {
	List<Method> methods = new ArrayList<Method>();

	public JScopeNativeFunction(IConverter converter) {
		super(converter);
	}

	public void addMethod(Method m) {
		m.setAccessible(true);
		this.methods.add(m);
	}

	@Override
	public Object getObject() {
		return null;
	}

	@Override
	public IScopeObject call(IScopeObject thisObj, IScopeObject... params)
			throws InterruptedException {
		Object[] conv_params = this.converter.ScopeObjectArrayToScriptObjectArray(params);
		Method m = this.searchForMethod(conv_params);
		if (m != null) {
			try {
				Class<?>[] types = m.getParameterTypes();
				// Type[] t2 = m.getGenericParameterTypes();
				for (int i = 0; i < conv_params.length; ++i) {
					if (types[i].isPrimitive() && conv_params[i] instanceof Number) {
						if (Byte.TYPE.equals(types[i])) {
							conv_params[i] = Byte.valueOf(((Number) conv_params[i]).byteValue());
						} else if (Short.TYPE.equals(types[i])) {
							conv_params[i] = Short.valueOf(((Number) conv_params[i]).shortValue());
						} else if (Integer.TYPE.equals(types[i])) {
							conv_params[i] = Integer.valueOf(((Number) conv_params[i]).intValue());
						} else if (Long.TYPE.equals(types[i])) {
							conv_params[i] = Long.valueOf(((Number) conv_params[i]).longValue());
						} else if (Float.TYPE.equals(types[i])) {
							conv_params[i] = Float.valueOf(((Number) conv_params[i]).floatValue());
						} else if (Double.TYPE.equals(types[i])) {
							conv_params[i] = Double
									.valueOf(((Number) conv_params[i]).doubleValue());
						}
					}
				}
				IScopeObject o = this.converter.ScriptObjectToScopeObject(m.invoke(
						thisObj.getObject(), conv_params));
				return o;
			} catch (InvocationTargetException e){
				throw new RuntimeException(e);
			} catch (Exception e) {
				Throwable inner = e;

				while (inner != null
						&& !(inner instanceof InterruptedException || inner instanceof ThreadDeath)) {
					inner = inner.getCause();
				}

				if (inner instanceof InterruptedException) {
					throw (InterruptedException) inner;
				} else if (inner instanceof ThreadDeath) {
					throw (ThreadDeath) inner;
				}

				e.printStackTrace();
				if (e instanceof RuntimeException){
					throw (RuntimeException)e;
				}
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	private Method searchForMethod(Object... params) {
		try {
			Class<?>[] sources = new Class[params.length];
			for (int i = 0; i < params.length; ++i) {
				sources[i] = params[i] == null ? null : params[i].getClass();
			}
			for (Method method : this.methods) {
				// Has to be named the same of course.

				Class<?>[] types = method.getParameterTypes();

				// Does it have the same number of arguments that we're looking for.
				if (types.length != params.length)
					continue;

				// Check for type compatibility
				if (areTypesCompatible(types, sources))
					return method;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns true if all classes in the sources list are assignment compatible with the targets
	 * list. In other words, if all targets[n].isAssignableFrom( sources[n] ) then this method
	 * returns true. Any null values in sources are considered wild-cards and will skip the
	 * isAssignableFrom check as if it passed.
	 */
	private static boolean areTypesCompatible(Class<?>[] targets, Class<?>[] sources) {
		if (targets.length != sources.length)
			return false;

		for (int i = 0; i < targets.length; i++) {
			if (sources[i] == null)
				continue;

			if (!translateFromPrimitive(targets[i]).isAssignableFrom(sources[i]))
				return false;
		}
		return true;
	}

	/**
	 * If this specified class represents a primitive type (int, float, etc.) then it is translated
	 * into its wrapper type (Integer, Float, etc.). If the passed class is not a primitive then it
	 * is just returned.
	 */
	private static Class<?> translateFromPrimitive(Class<?> primitive) {
		if (!primitive.isPrimitive())
			return (primitive);

		if (Boolean.TYPE.equals(primitive))
			return Boolean.class;
		if (Character.TYPE.equals(primitive))
			return Character.class;
		if (Byte.TYPE.equals(primitive))
			return Number.class;
		if (Short.TYPE.equals(primitive))
			return Number.class;
		if (Integer.TYPE.equals(primitive))
			return Number.class;
		if (Long.TYPE.equals(primitive))
			return Number.class;
		if (Float.TYPE.equals(primitive))
			return Number.class;
		if (Double.TYPE.equals(primitive))
			return Number.class;

		throw new RuntimeException("Error translating type:" + primitive);
	}

	@Override
	public Collection<String> getArgsNames() {
		try {
			if (this.methods.size() > 0) {
				Class<?>[] t = this.methods.get(0).getParameterTypes();
				String[] r = new String[t.length];
				for (int i = 0; i < t.length; ++i) {
					r[i] = t[i].getSimpleName();
				}
				return Arrays.asList(r);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Arrays.asList(new String[] {});
	}

	@Override
	public IScopeObject getValue() {
		return this;
	}

	@Override
	public String origin() {
		return "general";
	}

}