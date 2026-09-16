package pixelbot.script.scope.general;

import java.util.Enumeration;

public interface IScopeList extends Iterable<IScopeObject>, IScopeObject {

	public IScopeObject get(int pos);

	public boolean has(int pos);

	public void put(int pos, IScopeObject value);

	public void add(IScopeObject value);

	public void delete(int pos);

	public int size();

	public int[] getIds();

	public Enumeration<IScopeObject> enumeration();


}
