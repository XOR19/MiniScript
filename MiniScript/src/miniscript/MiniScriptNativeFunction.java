package miniscript;

import javax.script.ScriptContext;

public interface MiniScriptNativeFunction {

	public void call(ScriptContext context, int funk, int[] params);

}
