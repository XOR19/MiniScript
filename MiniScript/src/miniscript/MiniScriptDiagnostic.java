package miniscript;

import java.util.Locale;

import javax.tools.Diagnostic;

final class MiniScriptDiagnostic implements Diagnostic<Void> {

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

}
