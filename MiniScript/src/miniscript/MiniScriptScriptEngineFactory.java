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
	
	/**
	 * Should only be invoked by <code>sun.misc.Service</code>
	 */
	public MiniScriptScriptEngineFactory(){
		
	}
	
	/**
	 * The name of the engine
	 * @see MiniScriptLang
	 * @return {@link MiniScriptLang}.ENGINE_NAME
	 */
	@Override
	public String getEngineName() {
		return MiniScriptLang.ENGINE_NAME;
	}

	/**
	 * The engine version
	 * @see MiniScriptLang
	 * @return {@link MiniScriptLang}.ENGINE_VERSION
	 */
	@Override
	public String getEngineVersion() {
		return MiniScriptLang.ENGINE_VERSION;
	}

	/**
	 * The File extension for this language
	 * @see MiniScriptLang
	 * @return {@link MiniScriptLang}.EXTENSIONS
	 */
	@Override
	public List<String> getExtensions() {
		return MiniScriptLang.EXTENSIONS;
	}

	/**
	 * The language version
	 * @see MiniScriptLang
	 * @return {@link MiniScriptLang}.LANG_NAME
	 */
	@Override
	public String getLanguageName() {
		return MiniScriptLang.LANG_NAME;
	}

	/**
	 * The mime types for this language
	 * @see MiniScriptLang
	 * @return {@link MiniScriptLang}.LANG_VERSION
	 */
	@Override
	public String getLanguageVersion() {
		return MiniScriptLang.LANG_VERSION;
	}

	/**
	 * returns <code>m args0, args1, args2, ...</code>
	 * @param obj unused
	 * @param m assembler instruction name
	 * @param args instruction arguments
	 * @return a string with <code>m args0, args1, args2, ...</code>
	 */
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

	/**
	 * The mime types for this language
	 * @see MiniScriptLang
	 * @return {@link MiniScriptLang}.MIME
	 */
	@Override
	public List<String> getMimeTypes() {
		return MiniScriptLang.MIME;
	}

	/**
	 * The short names
	 * @see MiniScriptLang
	 * @return {@link MiniScriptLang}.NAMES
	 */
	@Override
	public List<String> getNames() {
		return MiniScriptLang.NAMES;
	}

	/**
	 * not used
	 * @param toDisplay unused
	 * @return allways <code>null</code>
	 */
	@Override
	public String getOutputStatement(String toDisplay) {
		return null;
	}

	/**
	 * gets the parameter for a specific option
	 * @param key the key, can be one of {{@link ScriptEngine}.ENGINE, 
	 * {@link ScriptEngine}.ENGINE_VERSION, {@link ScriptEngine}.LANGUAGE, 
	 * {@link ScriptEngine}.LANGUAGE_VERSION, {@link ScriptEngine}.NAME}
	 * @return the specific parameter
	 * @see ScriptEngine
	 */
	@Override
	public Object getParameter(String key) {
		return parameters.get(key);
	}

	/**
	 * returns a program:<br>
	 * <code>statement0<br>
	 * statement1<br>
	 * statement2<br>
	 * ...</code>
	 * @param statements some statements like <code>mov r1, 100</code>
	 * @return a program
	 */
	@Override
	public String getProgram(String... statements) {
		String ret = "";//$NON-NLS-1$
		for(String s:statements){
			ret += s+"\n";//$NON-NLS-1$
		}
		return ret;
	}

	/**
	 * creates a new scriptengine
	 * @return a new scriptengine
	 */
	@Override
	public ScriptEngine getScriptEngine() {
		return new MiniScriptScriptEngine(this);
	}

}
