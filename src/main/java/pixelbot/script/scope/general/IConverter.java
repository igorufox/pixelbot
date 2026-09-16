package pixelbot.script.scope.general;

public interface IConverter {
	public IScopeObject ScriptObjectToScopeObject(Object obj);

	public Object ScopeObjectToScriptObject(IScopeObject obj);

	public IScopeObject[] ScriptObjectArrayToScopeObjectArray(Object[] obj);

	public Object[] ScopeObjectArrayToScriptObjectArray(IScopeObject[] obj);
}