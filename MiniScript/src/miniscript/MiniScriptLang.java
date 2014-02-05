package miniscript;

import java.util.List;

/**
 * 
 * All the static important things of MiniScript are here
 * 
 * @author XOR
 *
 */
public final class MiniScriptLang {

	/**
	 * The name of the script
	 */
	public static final String NAME = "MiniScript";//$NON-NLS-1$
	/**
	 * The name of the engine
	 */
	public static final String ENGINE_NAME = NAME;
	/**
	 * The engine version. To get allways the right, use <code>javax.script.ScriptEngineFactory.getEngineVersion()</code>
	 */
	public static final String ENGINE_VERSION = "1.0.0";//$NON-NLS-1$
	/**
	 * The language name, allways "MiniScript"
	 */
	public static final String LANG_NAME = NAME;
	/**
	 * The language version. To get allways the right, use <code>javax.script.ScriptEngineFactory.getLanguageVersion()</code>
	 */
	public static final String LANG_VERSION = "1.0.0";//$NON-NLS-1$
	/**
	 * The File extension for this language
	 */
	public static final List<String> EXTENSIONS = new MiniScriptImmutableList<String>();
	/**
	 * The mime types for this language
	 */
	public static final List<String> MIME = new MiniScriptImmutableList<String>();
	/**
	 * The short names
	 */
	public static final List<String> NAMES = new MiniScriptImmutableList<String>(NAME);
	
	/**
	 * The instruction id for the <code>not</code> instruction<br>
	 * usage:<br>
	 * <code>
	 * not register<br>
	 * not [pointer]
	 * </code><br>
	 * in java: <code>value = ~value<code>
	 */
	public static final int INST_NOT = 1;
	/**
	 * The instruction id for the <code>neg</code> instruction<br>
	 * usage:<br>
	 * <code>
	 * neg register<br>
	 * neg [pointer]
	 * </code><br>
	 * in java: <code>value = -value<code>
	 */
	public static final int INST_NEG = 2;
	/**
	 * The instruction id for the <code>inc</code> instruction<br>
	 * usage:<br>
	 * <code>
	 * inc register<br>
	 * inc [pointer]
	 * </code><br>
	 * in java: <code>value++<code>
	 */
	public static final int INST_INC = 3;
	/**
	 * The instruction id for the <code>dec</code> instruction<br>
	 * usage:<br>
	 * <code>
	 * dec register<br>
	 * dec [pointer]
	 * </code><br>
	 * in java: <code>value--<code>
	 */
	public static final int INST_DEC = 4;
	/**
	 * The instruction id for the <code>add</code> instruction<br>
	 * usage:<br>
	 * <code>
	 * add register, register<br>
	 * add [pointer], register<br>
	 * add register, [pointer]<br>
	 * add [pointer], [pointer]<br>
	 * add register, const<br>
	 * add [pointer], const
	 * </code><br>
	 * in java: <code>value1 += value2<code>
	 */
	public static final int INST_ADD = 5;
	/**
	 * The instruction id for the <code>sub</code> instruction<br>
	 * usage:<br>
	 * <code>
	 * sub register, register<br>
	 * sub [pointer], register<br>
	 * sub register, [pointer]<br>
	 * sub [pointer], [pointer]<br>
	 * sub register, const<br>
	 * sub [pointer], const
	 * </code><br>
	 * in java: <code>value1 -= value2<code>
	 */
	public static final int INST_SUB = 6;
	/**
	 * The instruction id for the <code>mul</code> instruction<br>
	 * usage:<br>
	 * <code>
	 * mul register, register<br>
	 * mul [pointer], register<br>
	 * mul register, [pointer]<br>
	 * mul [pointer], [pointer]<br>
	 * mul register, const<br>
	 * mul [pointer], const
	 * </code><br>
	 * in java: <code>value1 *= value2<code>
	 */
	public static final int INST_MUL = 7;
	/**
	 * The instruction id for the <code>div</code> instruction<br>
	 * usage:<br>
	 * <code>
	 * div register, register<br>
	 * div [pointer], register<br>
	 * div register, [pointer]<br>
	 * div [pointer], [pointer]<br>
	 * div register, const<br>
	 * div [pointer], const
	 * </code><br>
	 * in java: <code>value1 /= value2<code>
	 */
	public static final int INST_DIV = 8;
	/**
	 * The instruction id for the <code>mod</code> instruction<br>
	 * usage:<br>
	 * <code>
	 * mod register, register<br>
	 * mod [pointer], register<br>
	 * mod register, [pointer]<br>
	 * mod [pointer], [pointer]<br>
	 * mod register, const<br>
	 * mod [pointer], const
	 * </code><br>
	 * in java: <code>value1 %= value2<code>
	 */
	public static final int INST_MOD = 9;
	/**
	 * The instruction id for the <code>shl</code> instruction<br>
	 * usage:<br>
	 * <code>
	 * shl register, register<br>
	 * shl [pointer], register<br>
	 * shl register, [pointer]<br>
	 * shl [pointer], [pointer]<br>
	 * shl register, const<br>
	 * shl [pointer], const
	 * </code><br>
	 * in java: <code>value1 <<= value2<code>
	 */
	public static final int INST_SHL = 10;
	/**
	 * The instruction id for the <code>shr</code> instruction<br>
	 * usage:<br>
	 * <code>
	 * shr register, register<br>
	 * shr [pointer], register<br>
	 * shr register, [pointer]<br>
	 * shr [pointer], [pointer]<br>
	 * shr register, const<br>
	 * shr [pointer], const
	 * </code><br>
	 * in java: <code>value1 >>= value2<code>
	 */
	public static final int INST_SHR = 11;
	/**
	 * The instruction id for the <code>ushr</code> instruction<br>
	 * usage:<br>
	 * <code>
	 * ushr register, register<br>
	 * ushr [pointer], register<br>
	 * ushr register, [pointer]<br>
	 * ushr [pointer], [pointer]<br>
	 * ushr register, const<br>
	 * ushr [pointer], const
	 * </code><br>
	 * in java: <code>value1 >>>= value2<code>
	 */
	public static final int INST_USHR = 12;
	/**
	 * The instruction id for the <code>and</code> instruction<br>
	 * usage:<br>
	 * <code>
	 * and register, register<br>
	 * and [pointer], register<br>
	 * and register, [pointer]<br>
	 * and [pointer], [pointer]<br>
	 * and register, const<br>
	 * and [pointer], const
	 * </code><br>
	 * in java: <code>value1 &= value2<code>
	 */
	public static final int INST_AND = 13;
	/**
	 * The instruction id for the <code>or</code> instruction<br>
	 * usage:<br>
	 * <code>
	 * or register, register<br>
	 * or [pointer], register<br>
	 * or register, [pointer]<br>
	 * or [pointer], [pointer]<br>
	 * or register, const<br>
	 * or [pointer], const
	 * </code><br>
	 * in java: <code>value1 |= value2<code>
	 */
	public static final int INST_OR = 14;
	/**
	 * The instruction id for the <code>xor</code> instruction<br>
	 * usage:<br>
	 * <code>
	 * xor register, register<br>
	 * xor [pointer], register<br>
	 * xor register, [pointer]<br>
	 * xor [pointer], [pointer]<br>
	 * xor register, const<br>
	 * xor [pointer], const
	 * </code><br>
	 * in java: <code>value1 ^= value2<code>
	 */
	public static final int INST_XOR = 15;
	/**
	 * The instruction id for the <code>mov</code> instruction<br>
	 * usage:<br>
	 * <code>
	 * mov register, register<br>
	 * mov [pointer], register<br>
	 * mov register, [pointer]<br>
	 * mov [pointer], [pointer]<br>
	 * mov register, const<br>
	 * mov [pointer], const
	 * </code><br>
	 * in java: <code>value1 = value2<code>
	 */
	public static final int INST_MOV = 16;
	/**
	 * The instruction id for the <code>cmp</code> instruction<br>
	 * usage:<br>
	 * <code>
	 * cmp register, register<br>
	 * cmp [pointer], register<br>
	 * cmp const, register<br>
	 * cmp register, [pointer]<br>
	 * cmp [pointer], [pointer]<br>
	 * cmp const, [pointer]<br>
	 * cmp register, const<br>
	 * cmp [pointer], const<br>
	 * cmp const, const
	 * </code><br>
	 * in java: <code>value1.compareTo(value2)<code><br>
	 * puts the result in <code>r0</code>
	 */
	public static final int INST_CMP = 17;
	/**
	 * The instruction id for the <code>jmp</code> instruction<br>
	 * usage:<br>
	 * <code>
	 * jmp label
	 * </code>
	 */
	public static final int INST_JMP = 18;
	/**
	 * The instruction id for the <code>jmpl</code> instruction<br>
	 * usage:<br>
	 * <code>
	 * jmpl label
	 * </code>
	 */
	public static final int INST_JMPL = 19;
	/**
	 * The instruction id for the <code>jeq</code> instruction<br>
	 * usage:<br>
	 * <code>
	 * jeq label
	 * </code>
	 */
	public static final int INST_JEQ = 20;
	/**
	 * The instruction id for the <code>jne</code> instruction<br>
	 * usage:<br>
	 * <code>
	 * jne label
	 * </code>
	 */
	public static final int INST_JNE = 21;
	/**
	 * The instruction id for the <code>jl</code> instruction<br>
	 * usage:<br>
	 * <code>
	 * jl label
	 * </code>
	 */
	public static final int INST_JL = 22;
	/**
	 * The instruction id for the <code>jle</code> instruction<br>
	 * usage:<br>
	 * <code>
	 * jle label
	 * </code>
	 */
	public static final int INST_JLE = 23;
	/**
	 * The instruction id for the <code>jb</code> instruction<br>
	 * usage:<br>
	 * <code>
	 * jb label
	 * </code>
	 */
	public static final int INST_JB = 24;
	/**
	 * The instruction id for the <code>jbe</code> instruction<br>
	 * usage:<br>
	 * <code>
	 * jbe label
	 * </code>
	 */
	public static final int INST_JBE = 25;
	/**
	 * The instruction id for the <code>ext</code> instruction<br>
	 * usage:<br>
	 * <code>
	 * ext funkID, ...
	 * </code>
	 */
	public static final int INST_NATIVE = 26;
	
	/**
	 * Mask for the compare bits in r0
	 */
	public static final int CMP_MASK = 3;
	/**
	 * Set in r0 if last cmp is equal
	 */
	public static final int CMP_EQ = 1;
	/**
	 * Set in r0 if last cmp is bigger
	 */
	public static final int CMP_BIG = 2;
	
	/**
	 * ram position in the binding, need to be a int array
	 */
	public static final String BINDING_RAM = "ram";//$NON-NLS-1$
	/**
	 * ext position in the binding, need to be a int array, can be not specified
	 */
	public static final String BINDING_EXT = "ext";//$NON-NLS-1$
	/**
	 * register position in the binding, need to be a int array
	 */
	public static final String BINDING_REGISTER = "register";//$NON-NLS-1$
	
	/**
	 * position in the binding for the diagnostic listener for compilation
	 */
	public static final String COMPILER_DIAGNOSTICLISTENER = "diagnosticlistener";//$NON-NLS-1$
	/**
	 * position in the binding for the replacements, need to be a HashMap<String, Integer>
	 */
	public static final String COMPILER_REPLACEMENTS = "replacements";//$NON-NLS-1$
	
	private MiniScriptLang(){
		throw new InstantiationError();
	}
	
}
