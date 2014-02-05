package miniscript;

import javax.script.ScriptContext;

/**
 * 
 * For native functions called by <code>ext funcID, ...</code><br>
 * implement and register to binging as <code>"func:"+funcID</code>
 * 
 * @author XOR
 *
 */
public interface MiniScriptNativeFunction {

	/**
	 * 
	 * Called when <code>ext</code> is called
	 * 
	 * @param context the scriptcontext currently using
	 * @param func the funcID
	 * @param params a list of the parameters, can be changed an will change the values in the script
	 */
	public void call(ScriptContext context, int func, int[] params);

}
