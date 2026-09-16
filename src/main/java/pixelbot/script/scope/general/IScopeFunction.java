package pixelbot.script.scope.general;

import java.util.Collection;

public interface IScopeFunction extends IScopeObject {

	public IScopeObject call(IScopeObject thisObj, IScopeObject... params) throws InterruptedException ;

	public Collection<String> getArgsNames();

}
