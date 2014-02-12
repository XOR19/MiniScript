package miniscript;

import javax.script.CompiledScript;

public final class MiniScriptUtils {

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
