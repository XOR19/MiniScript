package miniscript;

import java.util.HashMap;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

public final class MiniScriptScriptEngineFactory implements ScriptEngineFactory {
	
	private static final HashMap<String, Object> parameters = new HashMap<String, Object>();
	
	static{
		parameters.put(ScriptEngine.ENGINE, MiniScriptLang.ENGINE_NAME);
		parameters.put(ScriptEngine.ENGINE_VERSION, MiniScriptLang.ENGINE_VERSION);
		parameters.put(ScriptEngine.LANGUAGE, MiniScriptLang.LANG_NAME);
		parameters.put(ScriptEngine.LANGUAGE_VERSION, MiniScriptLang.LANG_VERSION);
		parameters.put(ScriptEngine.NAME, MiniScriptLang.NAME);
	}
	
	public MiniScriptScriptEngineFactory(){
		
	}
	
	@Override
	public String getEngineName() {
		return MiniScriptLang.ENGINE_NAME;
	}

	@Override
	public String getEngineVersion() {
		return MiniScriptLang.ENGINE_VERSION;
	}

	@Override
	public List<String> getExtensions() {
		return MiniScriptLang.EXTENSIONS;
	}

	@Override
	public String getLanguageName() {
		return MiniScriptLang.LANG_NAME;
	}

	@Override
	public String getLanguageVersion() {
		return MiniScriptLang.LANG_VERSION;
	}

	@Override
	public String getMethodCallSyntax(String obj, String m, String... args) {
		String ret = m;
		if(args.length>0){
			ret += " "+args[0];//$NON-NLS-1$
			for(int i=1; i<args.length; i++){
				ret += ", "+args[i];//$NON-NLS-1$
			}
		}
		return ret;
	}

	@Override
	public List<String> getMimeTypes() {
		return MiniScriptLang.MIME;
	}

	@Override
	public List<String> getNames() {
		return MiniScriptLang.NAMES;
	}

	@Override
	public String getOutputStatement(String toDisplay) {
		return null;
	}

	@Override
	public Object getParameter(String key) {
		return parameters.get(key);
	}

	@Override
	public String getProgram(String... statements) {
		String ret = "";//$NON-NLS-1$
		for(String s:statements){
			ret += s+"\n";//$NON-NLS-1$
		}
		return ret;
	}

	@Override
	public ScriptEngine getScriptEngine() {
		return new MiniScriptScriptEngine(this);
	}

}
