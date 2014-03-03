package miniscript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.tools.Diagnostic.Kind;
import javax.tools.DiagnosticListener;

import miniscript.MiniScriptDummyInst.DummyInstJump;
import miniscript.MiniScriptDummyInst.DummyInstNative;
import miniscript.MiniScriptDummyInst.DummyInstNormal;
import miniscript.MiniScriptDummyInst.DummyInstSwitch;
import miniscript.MiniScriptDummyInst.DummyInstUnary;
import miniscript.MiniScriptValue.ValueNum;
import miniscript.MiniScriptValue.ValuePtr;
import miniscript.MiniScriptValue.ValueReg;

enum MiniScriptASM {
	NOT(Type.UNARY, MiniScriptLang.INST_NOT),
	NEG(Type.UNARY, MiniScriptLang.INST_NEG),
	INC(Type.UNARY, MiniScriptLang.INST_INC),
	DEC(Type.UNARY, MiniScriptLang.INST_DEC),

	ADD(Type.NORMAL, MiniScriptLang.INST_ADD),
	SUB(Type.NORMAL, MiniScriptLang.INST_SUB),
	MUL(Type.NORMAL, MiniScriptLang.INST_MUL),
	DIV(Type.NORMAL, MiniScriptLang.INST_DIV),
	MOD(Type.NORMAL, MiniScriptLang.INST_MOD),
	SHL(Type.NORMAL, MiniScriptLang.INST_SHL),
	SHR(Type.NORMAL, MiniScriptLang.INST_SHR),
	USHR(Type.NORMAL, MiniScriptLang.INST_USHR),
	AND(Type.NORMAL, MiniScriptLang.INST_AND),
	OR(Type.NORMAL, MiniScriptLang.INST_OR),
	XOR(Type.NORMAL, MiniScriptLang.INST_XOR),
	MOV(Type.NORMAL, MiniScriptLang.INST_MOV),
	
	CMP(Type.COMP, MiniScriptLang.INST_CMP),
	
	JMP(Type.JUMP, MiniScriptLang.INST_JMP),
	JMPL(Type.JUMP, MiniScriptLang.INST_JMPL),
	JEQ(Type.JUMP, MiniScriptLang.INST_JEQ),
	JNE(Type.JUMP, MiniScriptLang.INST_JNE),
	JL(Type.JUMP, MiniScriptLang.INST_JL),
	JLE(Type.JUMP, MiniScriptLang.INST_JLE),
	JB(Type.JUMP, MiniScriptLang.INST_JB),
	JBE(Type.JUMP, MiniScriptLang.INST_JBE),
	
	EXT(Type.NATIVE, MiniScriptLang.INST_NATIVE),
	
	SWITCH(Type.SWITCH, MiniScriptLang.INST_SWITCH),
	
	RND(Type.NORMAL3, MiniScriptLang.INST_RND),
	
	ELM(Type.GROUP, MiniScriptLang.INST_ELM)
	;

	final Type type;
	final int id;
	
	MiniScriptASM(Type type, int id){
		this.type = type;
		this.id = id;
	}
	
	MiniScriptDummyInst makeInst(DiagnosticListener<Void> diagnosticListener, int line, String[] p, HashMap<String, Integer> replacements) {
		CompileInfo ci = new CompileInfo();
		ci.diagnosticListener = diagnosticListener;
		ci.asm = this;
		ci.line = line;
		ci.replacements = replacements;
		MiniScriptDummyInst inst = type.makeInst(ci, p);
		if(ci.errored)
			return null;
		return inst;
	}
	
	static MiniScriptASM getInst(String name) {
		try{
			return valueOf(name.toUpperCase());
		}catch(IllegalArgumentException e){
			return null;
		}
	}

	private static enum Type{
		
		UNARY{
			@Override
			protected MiniScriptDummyInst makeInst(CompileInfo ci, String[] p) {
				if(p.length!=1){
					makeDiagnostic(ci, Kind.ERROR, "expect.one.param", p.length);//$NON-NLS-1$
					return null;
				}else{
					return new DummyInstUnary(ci.asm, ci.line, readRegOrPtr(ci, p[0]));
				}
			}
		},
		NORMAL{
			@Override
			protected MiniScriptDummyInst makeInst(CompileInfo ci, String[] p) {
				if(p.length!=2){
					makeDiagnostic(ci, Kind.ERROR, "expect.two.param", p.length);//$NON-NLS-1$
					return null;
				}else{
					return new DummyInstNormal(ci.asm, ci.line, readRegOrPtr(ci, p[0]), readValue(ci, p[1]));
				}
			}
		},
		NORMAL3{
			@Override
			protected MiniScriptDummyInst makeInst(CompileInfo ci, String[] p) {
				if(p.length!=3){
					makeDiagnostic(ci, Kind.ERROR, "expect.three.param", p.length);//$NON-NLS-1$
					return null;
				}else{
					return new DummyInstNormal(ci.asm, ci.line, readRegOrPtr(ci, p[0]), readValue(ci, p[1]), readValue(ci, p[2]));
				}
			}
		},
		COMP{
			@Override
			protected MiniScriptDummyInst makeInst(CompileInfo ci, String[] p) {
				if(p.length!=2){
					makeDiagnostic(ci, Kind.ERROR, "expect.two.param", p.length);//$NON-NLS-1$
					return null;
				}else{
					return new DummyInstNormal(ci.asm, ci.line, readValue(ci, p[0]), readValue(ci, p[1]));
				}
			}
		},
		JUMP{
			@Override
			protected MiniScriptDummyInst makeInst(CompileInfo ci, String[] p) {
				if(p.length!=1){
					makeDiagnostic(ci, Kind.ERROR, "expect.one.param", p.length);//$NON-NLS-1$
					return null;
				}else{
					return new DummyInstJump(ci.asm, ci.line, checkLabelName(ci, p[0]));
				}
			}
		},
		NATIVE{
			@Override
			protected MiniScriptDummyInst makeInst(CompileInfo ci, String[] p) {
				if(p.length<1){
					makeDiagnostic(ci, Kind.ERROR, "expect.at.least.one.param", p.length);//$NON-NLS-1$
					return null;
				}else{
					MiniScriptValue v[] = new MiniScriptValue[p.length];
					for(int i=0; i<p.length; i++){
						v[i] = readValue(ci, p[i]);
					}
					return new DummyInstNative(ci.asm, ci.line, v);
				}
			}
		},
		SWITCH{
			@Override
			protected MiniScriptDummyInst makeInst(CompileInfo ci, String[] p) {
				if(p.length<1){
					makeDiagnostic(ci, Kind.ERROR, "expect.at.least.one.param", p.length);//$NON-NLS-1$
					return null;
				}else{
					MiniScriptValue v = readValue(ci, p[0]);
					ValueNum[] targetIDs = new ValueNum[p.length-1];
					String[] targets = new String[p.length-1];
					for(int i=0; i<targets.length; i++){
						String[] target = p[i+1].split(":", 2);//$NON-NLS-1$
						targetIDs[i] = readNum(ci, target[0]);
						if(target.length==1){
							targets[i] = checkLabelName(ci, "");//$NON-NLS-1$
						}else{
							targets[i] = checkLabelName(ci, target[1]);
						}
					}
					checkDuplicated(ci, targetIDs);
					return new DummyInstSwitch(ci.asm, ci.line, v, targetIDs, targets);
				}
			}
		}, GROUP{
			@Override
			protected MiniScriptDummyInst makeInst(CompileInfo ci, String[] p) {
				if(p.length<3){
					makeDiagnostic(ci, Kind.ERROR, "use.compare.instead", p.length);//$NON-NLS-1$
					return null;
				}else{
					MiniScriptValue v[] = new MiniScriptValue[p.length];
					for(int i=0; i<p.length; i++){
						v[i] = readValue(ci, p[i]);
					}
					return new DummyInstNative(ci.asm, ci.line, v);
				}
			}
		};
		
		protected MiniScriptDummyInst makeInst(CompileInfo ci, String[] p) {
			throw new UnsupportedOperationException();
		}
		
	}
	
	private static class CompileInfo{
		
		DiagnosticListener<Void> diagnosticListener;
		
		MiniScriptASM asm;
		
		int line;
		
		boolean errored;
		
		HashMap<String, Integer> replacements;
		
	}
	
	private static MiniScriptValue readValue(CompileInfo ci, String p){
		p = p.trim();
		if(p.isEmpty()){
			makeDiagnostic(ci, Kind.ERROR, "no.value.empty");//$NON-NLS-1$
			return null;
		}else{
			if(p.charAt(0)=='['){
				return readPtr(ci, p);
			}else if(p.charAt(0)=='r' || p.charAt(0)=='R'){
				if(p.length()>1 && isNum(p.charAt(1))){
					return readReg(ci, p);
				}else{
					return readNum(ci, p);
				}
			}else{
				return readNum(ci, p);
			}
		}
	}
	
	private static boolean isNum(char c){
		return c>='0' && c<='9';
	}
	
	private static MiniScriptValue readValueOrReg(CompileInfo ci, String p){
		p = p.trim();
		if(p.isEmpty()){
			makeDiagnostic(ci, Kind.ERROR, "no.value.empty");//$NON-NLS-1$
			return null;
		}else{
			if(p.charAt(0)=='r' || p.charAt(0)=='R'){
				if(p.length()>1 && isNum(p.charAt(1))){
					return readReg(ci, p);
				}else{
					return readNum(ci, p);
				}
			}else{
				return readNum(ci, p);
			}
		}
	}
	
	private static ValuePtr readPtr(CompileInfo ci, String p) {
		if(p.charAt(0)!='['){
			makeDiagnostic(ci, Kind.ERROR, "expect.ptr.start");//$NON-NLS-1$
		}else if(p.charAt(p.length()-1)!=']'){
			makeDiagnostic(ci, Kind.ERROR, "expect.ptr.end");//$NON-NLS-1$
		}
		List<MiniScriptValue> values = new ArrayList<MiniScriptValue>();
		List<Boolean> mul = new ArrayList<Boolean>();
		int index = 0;
		int oldIndex = 1;
		boolean neg = false;
		while(true){
			oldIndex = index+1;
			index = indexOf(p, "+*-", oldIndex);//$NON-NLS-1$
			if(neg){
				oldIndex--;
				if(index==-1){
					ValueNum val = readNum(ci, p.substring(oldIndex, p.length()-1));
					val.num = -val.num;
					values.add(val);
					mul.add(false);
					break;
				}else{
					makeDiagnostic(ci, Kind.ERROR, "expect.number.at.end");//$NON-NLS-1$
				}
			}else{
				if(index==-1){
					MiniScriptValue val = readValueOrReg(ci, p.substring(oldIndex, p.length()-1));
					values.add(val);
					mul.add(false);
					break;
				}else{
					MiniScriptValue val = readValueOrReg(ci, p.substring(oldIndex, index));
					values.add(val);
					mul.add(p.charAt(index)=='*');
					if(val instanceof ValueNum){
						makeDiagnostic(ci, Kind.ERROR, "expect.number.at.end");//$NON-NLS-1$
					}
				}
			}
			neg = false;
			neg = p.charAt(index)=='-';
			if(neg){
				index++;
			}
		}
		return new ValuePtr(values, mul);
	}
	
	private static int indexOf(String s, String values, int start){
		for(int i=start; i<s.length(); i++){
			char c = s.charAt(i);
			if(values.indexOf(c)!=-1)
				return i;
		}
		return -1;
	}
	
	private static ValueReg readReg(CompileInfo ci, String p){
		if(p.charAt(0)!='r' && p.charAt(0)!='R'){
			makeDiagnostic(ci, Kind.ERROR, "expect.register");//$NON-NLS-1$
		}
		if(p.charAt(1)=='+' || p.charAt(1)=='-'){
			makeDiagnostic(ci, Kind.ERROR, "expect.number.for.register");//$NON-NLS-1$
		}
		int reg = readIntNum(ci, p.substring(1), true);
		if(reg<0 || reg>31){
			makeDiagnostic(ci, Kind.ERROR, "register.out.of.range", reg);//$NON-NLS-1$
		}
		return new ValueReg(reg);
	}
	
	private static ValueNum readNum(CompileInfo ci, String p){
		return new ValueNum(readIntNum(ci, p, false));
	}

	private static int readIntNum(CompileInfo ci, String p, boolean decimal){
		int radix = 10;
		p = p.trim();
		if(!decimal){
			if(ci.replacements!=null){
				Integer num = ci.replacements.get(p.toLowerCase());
				if(num!=null)
					return num;
			}
			if(p.startsWith("0x")){//$NON-NLS-1$
				p = p.substring(2);
				radix = 16;
			}else if(p.startsWith("0b")){//$NON-NLS-1$
				p = p.substring(2);
				radix = 2;
			}
		}
		try{
			return Integer.parseInt(p, radix);
		}catch(NumberFormatException e){
			makeDiagnostic(ci, Kind.ERROR, "wrong.number.format", p);//$NON-NLS-1$
			return 0;
		}
	}
	
	private static MiniScriptValue readRegOrPtr(CompileInfo ci, String p){
		MiniScriptValue v = readValue(ci, p);
		if(v instanceof ValueReg || v instanceof ValuePtr)
			return v;
		makeDiagnostic(ci, Kind.ERROR, "const.not.accepted");//$NON-NLS-1$
		return null;
	}
	
	private static String checkLabelName(CompileInfo ci, String name){
		name = name.trim();
		if(name.isEmpty()){
			makeDiagnostic(ci, Kind.ERROR, "empty.label", name);//$NON-NLS-1$
			return "!error!";
		}
		for(int i=0; i<name.length(); i++){
			char c = name.charAt(i);
			if(!((c>='A' && c<='Z') || (c>='a' && c<='z') || c=='_' || (c>='0' && c<='9'))){
				makeDiagnostic(ci, Kind.WARNING, "label.with.bad.characters", name);//$NON-NLS-1$
				break;
			}
		}
		return name.toLowerCase();
	}
	
	private static void checkDuplicated(CompileInfo ci, ValueNum[] nums){
		for(int i=0; i<nums.length-1; i++){
			for(int j=i+1; j<nums.length; j++){
				if(nums[i].num == nums[j].num){
					makeDiagnostic(ci, Kind.ERROR, "duplicated.case.numbers", nums[i].num);//$NON-NLS-1$
				}
			}
		}
	}
	
	private static void makeDiagnostic(CompileInfo ci, Kind kind, String message, Object...args){
		Object[] objs = new Object[args.length+1];
		objs[0] = ci.asm;
		System.arraycopy(args, 0, objs, 1, args.length);
		ci.diagnosticListener.report(new MiniScriptDiagnostic(kind, ci.line, message, objs));
		ci.errored = true;
	}
	
}
