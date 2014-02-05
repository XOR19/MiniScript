import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import miniscript.MiniScriptLang;
import miniscript.MiniScriptScriptEngineFactory;


public class Test {
	
	public static void main(String[] args) {
		MiniScriptScriptEngineFactory factory = new MiniScriptScriptEngineFactory();
		ScriptEngine scriptEngine = factory.getScriptEngine();
		int[] ext = new int[16];
		scriptEngine.getContext().setAttribute(MiniScriptLang.BINDING_EXT, ext, ScriptContext.ENGINE_SCOPE);
		try {
			scriptEngine.eval(new FileReader(new File("Test.miniscript")));
		} catch (ScriptException e) {
			e.printStackTrace();
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}
		System.out.println(Arrays.toString((int[])scriptEngine.getContext().getAttribute(MiniScriptLang.BINDING_REGISTER)));
		System.out.println(Arrays.toString(ext));
		
	}

}
