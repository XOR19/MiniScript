package miniscript;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Locale;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptException;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.DiagnosticListener;

import miniscript.MiniScriptDummyInst.DummyInstLabel;

final class MiniScriptCompiler implements Compilable, DiagnosticListener<Void>{

	private MiniScriptScriptEngine engine;
	private MiniScriptCodeGen codeGen;
	private DiagnosticListener<Void> diagnosticListener;
	private Diagnostic<?> firstDiagnostic;
	private HashMap<String, Integer> replacements;
	
	@SuppressWarnings("unchecked")
	MiniScriptCompiler(MiniScriptScriptEngine engine) {
		this.engine = engine;
		codeGen = new MiniScriptCodeGen();
		Object obj = engine.get(MiniScriptLang.COMPILER_DIAGNOSTICLISTENER);
		if(obj instanceof DiagnosticListener){
			diagnosticListener = (DiagnosticListener<Void>)obj;
		}else{
			diagnosticListener = this;
		}
		obj = engine.get(MiniScriptLang.COMPILER_REPLACEMENTS);
		if(obj instanceof HashMap){
			replacements = (HashMap<String, Integer>) obj;
		}
	}

	@Override
	public CompiledScript compile(String script) throws ScriptException {
		int pos = 0;
		int num = 1;
		while(true){
			int oldPos = pos;
			pos = script.indexOf('\n', pos);
			if(pos==-1){
				compileLine(script.substring(oldPos).trim(), num);
				break;
			}else{
				compileLine(script.substring(oldPos, pos).trim(), num);
			}
			num++;
			pos++;
		}
		if(codeGen==null){
			if(firstDiagnostic==null){
				throw new ScriptException(MiniScriptMessages.getLocaleMessage("errors.occured"));//$NON-NLS-1$
			}
			throw new ScriptException(firstDiagnostic.getMessage(Locale.getDefault()), MiniScriptLang.NAME, (int)firstDiagnostic.getLineNumber());
		}
		return new MiniScriptCompiledScript(engine, codeGen.getData());
	}

	@Override
	public CompiledScript compile(Reader script) throws ScriptException {
		BufferedReader reader = new BufferedReader(script);
		String line;
		int num = 1;
		try {
			while((line=reader.readLine())!=null){
				compileLine(line.trim(), num);
				num++;
			}
		} catch (IOException e) {
			throw new ScriptException(e);
		}
		if(codeGen==null){
			if(firstDiagnostic==null){
				throw new ScriptException(MiniScriptMessages.getLocaleMessage("errors.occured"));//$NON-NLS-1$
			}
			throw new ScriptException(firstDiagnostic.getMessage(Locale.getDefault()), MiniScriptLang.NAME, (int)firstDiagnostic.getLineNumber());
		}
		return new MiniScriptCompiledScript(engine, codeGen.getData());
	}
	
	private void compileLine(String line, int lineNum){
		try{
			int index = line.indexOf(';');
			if(index!=-1){
				line = line.substring(0, index);
			}
			line = line.trim();
			if(line.isEmpty())
				return;
			String[] s = line.split("\\s", 2);//$NON-NLS-1$
			String[] p = null;
			if(s.length>1){
				p = s[1].split(",");//$NON-NLS-1$
			}
			MiniScriptASM asm = MiniScriptASM.getInst(s[0].trim());
			MiniScriptDummyInst inst;
			if(asm==null){
				if(line.charAt(line.length()-1)==':'){
					inst = new DummyInstLabel(lineNum, line.substring(0, line.length()-1).trim());
				}else{
					inst = null;
					diagnosticListener.report(new MiniScriptDiagnostic(Kind.ERROR, lineNum, "expected.label.end"));//$NON-NLS-1$
				}
			}else{
				inst = asm.makeInst(diagnosticListener, lineNum, p, replacements);
			}
			if(inst==null){
				codeGen = null;
			}
			if(codeGen!=null)
				codeGen.addInst(inst);
		}catch(Throwable e){
			codeGen = null;
			diagnosticListener.report(new MiniScriptDiagnostic(Kind.ERROR, lineNum, "fatal.error"));//$NON-NLS-1$
		}
	}

	@Override
	public void report(Diagnostic<? extends Void> diagnostic) {
		if(firstDiagnostic==null && diagnostic.getKind()==Kind.ERROR){
			firstDiagnostic = diagnostic;
		}
	}
	
}
