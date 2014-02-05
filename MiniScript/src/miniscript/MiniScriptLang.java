package miniscript;

import java.util.List;

public final class MiniScriptLang {

	public static final String NAME = "MiniScript";//$NON-NLS-1$
	public static final String ENGINE_NAME = NAME;
	public static final String ENGINE_VERSION = "1.0.0";//$NON-NLS-1$
	public static final String LANG_NAME = NAME;
	public static final String LANG_VERSION = "1.0.0";//$NON-NLS-1$
	public static final List<String> EXTENSIONS = new MiniScriptImmutableList<String>();
	public static final List<String> MIME = new MiniScriptImmutableList<String>();
	public static final List<String> NAMES = new MiniScriptImmutableList<String>(NAME);
	
	public static final int INST_NOT = 1;
	public static final int INST_NEG = 2;
	public static final int INST_INC = 3;
	public static final int INST_DEC = 4;
	public static final int INST_ADD = 5;
	public static final int INST_SUB = 6;
	public static final int INST_MUL = 7;
	public static final int INST_DIV = 8;
	public static final int INST_MOD = 9;
	public static final int INST_SHL = 10;
	public static final int INST_SHR = 11;
	public static final int INST_USHR = 12;
	public static final int INST_AND = 13;
	public static final int INST_OR = 14;
	public static final int INST_XOR = 15;
	public static final int INST_MOV = 16;
	public static final int INST_CMP = 17;
	public static final int INST_JMP = 18;
	public static final int INST_JMPL = 19;
	public static final int INST_JEQ = 20;
	public static final int INST_JNE = 21;
	public static final int INST_JL = 22;
	public static final int INST_JLE = 23;
	public static final int INST_JB = 24;
	public static final int INST_JBE = 25;
	public static final int INST_NATIVE = 26;
	
	public static final int CMP_MASK = 3;
	public static final int CMP_EQ = 1;
	public static final int CMP_BIG = 2;
	
	public static final String BINDING_STACK = "stack";//$NON-NLS-1$
	public static final String BINDING_EXT = "ext";//$NON-NLS-1$
	public static final String BINDING_REGISTER = "register";//$NON-NLS-1$
	
	public static final String COMPILER_DIAGNOSTICLISTENER = "diagnosticlistener";//$NON-NLS-1$
	public static final String COMPILER_REPLACEMENTS = "replacements";//$NON-NLS-1$
	
	private MiniScriptLang(){
		throw new InstantiationError();
	}
	
}
