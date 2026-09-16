package pixelbot.script.scope.general;

import java.util.*;

public class GlobalScope implements IScopeMap {
	private Map<String, IScopeObject> map = new HashMap<String, IScopeObject>();
	private String current_origin;

	public ScopeJob getJob(String id) {
		IScopeObject obj = this.get(id);
		if (obj instanceof IScopeMap) {
			if (((IScopeMap) obj).has("main")) {
				return (new ScopeJob(id, (IScopeMap) obj));
			}
		}
		return null;
	}

	public Collection<ScopeJob> getJobs() {
		Collection<ScopeJob> result = new ArrayList<ScopeJob>();
		for (Map.Entry<String, IScopeObject> obj : this) {
			if (obj.getValue() instanceof IScopeMap) {

				if (((IScopeMap) obj.getValue()).has("main")
						&& ((IScopeMap) obj.getValue()).has("params")) {
					boolean active = false;
					Object params = ((IScopeMap) obj.getValue()).get("params");
					if (params instanceof IScopeMap) {
						IScopeMap paramsmap = (IScopeMap) params;
						if (paramsmap.has("active"))
							active = ((IScopeBoolean) paramsmap.get("active")).value();
					} else if (params instanceof IScopeFunction) {
						IScopeFunction paramsFunction = (IScopeFunction) params;
						try {
							IScopeMap m = (IScopeMap) paramsFunction.call(obj.getValue());
							active = ((IScopeBoolean) ((IScopeFunction) m.get("active")).call(m))
									.value();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (active)
						result.add(new ScopeJob(obj.getKey(), (IScopeMap) obj.getValue()));
				}
			}
		}
		return result;
	}

	@Override
	public Object getObject() {
		return this;
	}

	@Override
	public Iterator<Map.Entry<String, IScopeObject>> iterator() {
		return this.map.entrySet().iterator();
	}

	@Override
	public IScopeObject get(String name) {
		return this.map.get(name);
	}

	@Override
	public boolean has(String name) {
		return this.map.containsKey(name);
	}

	@Override
	public void put(String name, IScopeObject value) {
		if (value instanceof IScopeMap) {
			((IScopeMap) value).setOrigin(this.current_origin);
		}
		this.map.put(name, value);
	}

	@Override
	public void delete(String name) {
		this.map.remove(name);
	}

	@Override
	public String[] getIds() {
		return this.map.keySet().toArray(new String[] {});
	}

	public void clear() {
		this.map.clear();
	}

	@Override
	public String origin() {
		return "general";
	}

	public void setCurrentOrigin(String origin) {
		this.current_origin = origin;
	}

	@Override
	public void setOrigin(String origin) {}

}
