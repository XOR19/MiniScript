package miniscript;

import javax.script.CompiledScript;

/**
 * 
 * Some utils for MiniScript
 * 
 * @author XOR
 *
 */
public final class MiniScriptUtils {

	/**
	 * 
	 * returns a copy of the byte code compiled and executed
	 * 
	 * @param cs the CompiledScript
	 * @return the copy of the byte code
	 */
	public static byte[] getByteCode(CompiledScript cs){
		if(cs instanceof MiniScriptCompiledScript){
			return ((MiniScriptCompiledScript) cs).getByteCode();
		}
		return null;
	}
	
	private MiniScriptUtils(){
		throw new InstantiationError();
	}
	
}
