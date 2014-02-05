package miniscript;

import java.io.Reader;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.script.SimpleScriptContext;

final class MiniScriptScriptEngine implements ScriptEngine, Compilable{
	
	private MiniScriptScriptEngineFactory factory;
	private ScriptContext defaultContext;
	
	MiniScriptScriptEngine(MiniScriptScriptEngineFactory factory){
		this.factory = factory;
		defaultContext = new SimpleScriptContext();
		defaultContext.setAttribute(MiniScriptLang.BINDING_RAM, new int[1024], ScriptContext.ENGINE_SCOPE);
		defaultContext.setAttribute(MiniScriptLang.BINDING_REGISTER, new int[32], ScriptContext.ENGINE_SCOPE);
	}
	
	@Override
	public Bindings createBindings() {
		return new SimpleBindings();
	}

	@Override
	public Object eval(String script) throws ScriptException {
		return compile(script).eval();
	}

	@Override
	public Object eval(Reader reader) throws ScriptException {
		return compile(reader).eval();
	}

	@Override
	public Object eval(String script, ScriptContext context) throws ScriptException {
		return compile(script).eval(context);
	}

	@Override
	public Object eval(Reader reader, ScriptContext context) throws ScriptException {
		return compile(reader).eval(context);
	}

	@Override
	public Object eval(String script, Bindings n) throws ScriptException {
		return compile(script).eval(n);
	}

	@Override
	public Object eval(Reader reader, Bindings n) throws ScriptException {
		return compile(reader).eval(n);
	}

	@Override
	public Object get(String key) {
		return getBindings(ScriptContext.ENGINE_SCOPE).get(key);
	}

	@Override
	public Bindings getBindings(int scope) {
		return defaultContext.getBindings(scope);
	}

	@Override
	public ScriptContext getContext() {
		return defaultContext;
	}

	@Override
	public ScriptEngineFactory getFactory() {
		return factory;
	}

	@Override
	public void put(String key, Object value) {
		getBindings(ScriptContext.ENGINE_SCOPE).put(key, value);
	}

	@Override
	public void setBindings(Bindings bindings, int scope) {
		defaultContext.setBindings(bindings, scope);
	}

	@Override
	public void setContext(ScriptContext context) {
		defaultContext = context;
	}

	@Override
	public CompiledScript compile(String script) throws ScriptException {
		return new MiniScriptCompiler(this).compile(script);
	}

	@Override
	public CompiledScript compile(Reader script) throws ScriptException {
		return new MiniScriptCompiler(this).compile(script);
	}
	
}
