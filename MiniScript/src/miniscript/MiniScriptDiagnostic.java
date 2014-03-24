package miniscript;

import java.util.Locale;
import java.util.concurrent.Callable;

import javax.tools.Diagnostic;

final class MiniScriptDiagnostic implements Diagnostic<Void>, Callable<String[]> {

	private Kind kind;
	private int line;
	private String message;
	private Object[] args;
	
	MiniScriptDiagnostic(Kind kind, int line, String message, Object... args){
		this.kind = kind;
		this.line = line;
		this.message = message;
		this.args = args;
	}
	
	@Override
	public String getCode() {
		return null;
	}

	@Override
	public long getColumnNumber() {
		return NOPOS;
	}

	@Override
	public long getEndPosition() {
		return NOPOS;
	}

	@Override
	public Kind getKind() {
		return kind;
	}

	@Override
	public long getLineNumber() {
		return line;
	}

	@Override
	public String getMessage(Locale locate) {
		return MiniScriptMessages.getLocaleMessage(message, locate, args);
	}

	@Override
	public long getPosition() {
		return NOPOS;
	}

	@Override
	public Void getSource() {
		return null;
	}

	@Override
	public long getStartPosition() {
		return NOPOS;
	}

	@Override
	public String[] call() throws Exception {
		String[] messageAndArguments = new String[args.length+1];
		messageAndArguments[0] = message;
		for(int i=0; i<args.length; i++){
			messageAndArguments[i+1] = args[i]==null?"null":args[i].toString();
		}
		return messageAndArguments;
	}

}
