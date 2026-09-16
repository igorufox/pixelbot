package pixelbot.script.scope.general;

import java.util.*;

public interface IScopeMap extends Iterable<Map.Entry<String, IScopeObject>>, IScopeObject {

	public IScopeObject get(String name);

	public boolean has(String name);

	public void put(String name, IScopeObject value);

	public void delete(String name);

	public String[] getIds();

	public void setOrigin(String origin);

}
