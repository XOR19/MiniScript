package miniscript;

import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

final class MiniScriptCompiledScript extends CompiledScript {
	
	private static final String EXECUTING_SCRIPT = "isExecuting";//$NON-NLS-1$
	
	private static final int[] NULL_EXT = new int[0];
	
	private MiniScriptScriptEngine engine;
	private byte[] data;
	private int programPointer;
	private int[] register;
	private int[] ram;
	private int[] ext;
	private int[] activePtr;
	private int ptr;
	
	MiniScriptCompiledScript(MiniScriptScriptEngine engine, byte[] data){
		this.engine = engine;
		this.data = data;
	}
	
	@Override
	public Object eval(ScriptContext context) throws ScriptException {
		checkContext(context);
		try{
			context.setAttribute(EXECUTING_SCRIPT, true, ScriptContext.ENGINE_SCOPE);
			execute(context);
		}finally{
			register = null;
			ram = null;
			ext = null;
			activePtr = null;
			context.setAttribute(EXECUTING_SCRIPT, false, ScriptContext.ENGINE_SCOPE);
		}
		return null;
	}
	
	private void execute(ScriptContext context) throws ScriptException{
		programPointer = 0;
		while(programPointer<data.length){
			int inst = data[programPointer++]&0xFF;
			int v, v2;
			switch(inst){
			case MiniScriptLang.INST_NOT:
				loadPtr();
				activePtr[ptr] = ~activePtr[ptr];
				break;
			case MiniScriptLang.INST_NEG:
				loadPtr();
				activePtr[ptr] = -activePtr[ptr];
				break;
			case MiniScriptLang.INST_INC:
				loadPtr();
				activePtr[ptr]++;
				break;
			case MiniScriptLang.INST_DEC:
				loadPtr();
				activePtr[ptr]--;
				break;
			case MiniScriptLang.INST_ADD:
				loadPtr();
				activePtr[ptr]+=loadValue();
				break;
			case MiniScriptLang.INST_SUB:
				loadPtr();
				activePtr[ptr]-=loadValue();
				break;
			case MiniScriptLang.INST_MUL:
				loadPtr();
				activePtr[ptr]*=loadValue();
				break;
			case MiniScriptLang.INST_DIV:
				loadPtr();
				activePtr[ptr]/=loadValue();
				break;
			case MiniScriptLang.INST_MOD:
				loadPtr();
				activePtr[ptr]%=loadValue();
				break;
			case MiniScriptLang.INST_SHL:
				loadPtr();
				activePtr[ptr]<<=loadValue();
				break;
			case MiniScriptLang.INST_SHR:
				loadPtr();
				activePtr[ptr]>>=loadValue();
				break;
			case MiniScriptLang.INST_USHR:
				loadPtr();
				activePtr[ptr]>>>=loadValue();
				break;
			case MiniScriptLang.INST_AND:
				loadPtr();
				activePtr[ptr]&=loadValue();
				break;
			case MiniScriptLang.INST_OR:
				loadPtr();
				activePtr[ptr]|=loadValue();
				break;
			case MiniScriptLang.INST_XOR:
				loadPtr();
				activePtr[ptr]^=loadValue();
				break;
			case MiniScriptLang.INST_MOV:
				loadPtr();
				activePtr[ptr]=loadValue();
				break;
			case MiniScriptLang.INST_CMP:
				v = loadValue();
				v2 = loadValue();
				register[0] &= ~MiniScriptLang.CMP_MASK;
				if(v==v2){
					register[0] |= MiniScriptLang.CMP_EQ;
				}else if(v>v2){
					register[0] |= MiniScriptLang.CMP_BIG;
				}
				break;
			case MiniScriptLang.INST_JMP:
				programPointer += data[programPointer] + 1;
				break;
			case MiniScriptLang.INST_JMPL:
				v = ((data[programPointer++]&0xFF)<<8)|(data[programPointer++]&0xFF);
				programPointer += v;
				break;
			case MiniScriptLang.INST_JEQ:
				if((register[0]&MiniScriptLang.CMP_EQ)!=0){
					programPointer += data[programPointer] + 1;
				}else{
					programPointer++;
				}
				break;
			case MiniScriptLang.INST_JNE:
				if((register[0]&MiniScriptLang.CMP_EQ)==0){
					programPointer += data[programPointer] + 1;
				}else{
					programPointer++;
				}
				break;
			case MiniScriptLang.INST_JL:
				if((register[0]&MiniScriptLang.CMP_MASK)==0){
					programPointer += data[programPointer] + 1;
				}else{
					programPointer++;
				}
				break;
			case MiniScriptLang.INST_JLE:
				if((register[0]&MiniScriptLang.CMP_BIG)==0){
					programPointer += data[programPointer] + 1;
				}else{
					programPointer++;
				}
				break;
			case MiniScriptLang.INST_JB:
				if((register[0]&MiniScriptLang.CMP_BIG)!=0){
					programPointer += data[programPointer] + 1;
				}else{
					programPointer++;
				}
				break;
			case MiniScriptLang.INST_JBE:
				if((register[0]&MiniScriptLang.CMP_MASK)!=0){
					programPointer += data[programPointer] + 1;
				}else{
					programPointer++;
				}
				break;
			case MiniScriptLang.INST_NATIVE:
				{
				int paramCount = data[programPointer++]&0xFF;
				int func = loadValue();
				int[] params = new int[paramCount];
				int[] ptr = new int[paramCount];
				int[][] pointer = new int[paramCount][];
				for(int i=0; i<paramCount; i++){
					if(loadPtrOrValue()){
						params[i] = this.ptr;
					}else{
						ptr[i] = this.ptr;
						pointer[i] = this.activePtr;
						params[i] = this.activePtr[this.ptr];
					}
				}
				Object obj = context.getAttribute("func:"+func);//$NON-NLS-1$
				if(!(obj instanceof MiniScriptNativeFunction)){
					throw new ScriptException(MiniScriptMessages.getLocaleMessage("function.not.exists", func));//$NON-NLS-1$
				}
				((MiniScriptNativeFunction)obj).call(context, func, params);
				for(int i=0; i<paramCount; i++){
					if(pointer[i]!=null){
						pointer[i][ptr[i]] = params[i];
					}
				}
				}
				break;
			case MiniScriptLang.INST_SWITCH:
				{
				int paramCount = (data[programPointer++]<<8)|(data[programPointer++]&0xFF);
				int s = loadValue();
				if(s>=0 && s<paramCount){
					programPointer += s*2;
					int jump = (data[programPointer++]<<8)|(data[programPointer++]&0xFF);
					programPointer += (paramCount-s-1)*2;
					programPointer += jump;
				}else{
					programPointer += paramCount*2;
				}
				}
				break;
			case MiniScriptLang.INST_RND:
				{
				loadPtr();
				int min = loadValue();
				int max = loadValue();
				activePtr[ptr]=(int)(Math.random()*(max-min))+min;
				}
				break;
			default:
				throw new ScriptException(MiniScriptMessages.getLocaleMessage("unknow.instruction", inst));//$NON-NLS-1$
			}
		}
	}
	
	private int loadPtrOffset() throws ScriptException{
		int ptr = data[programPointer++]&0xFF;
		if((ptr&0x80)==0){
			switch((ptr>>5)&0x3){
			case 1:
				return register[ptr & 0x1F];
			case 2:
				return register[ptr & 0x1F]+loadPtrOffset();
			case 3:
				return register[ptr & 0x1F]*loadPtrOffset();
			default:
				throwAssertion();
			}
		}else{
			switch((ptr>>4)&0x7){
			case 0:
				return data[programPointer++];
			case 1:
				return (data[programPointer++]<<8)|(data[programPointer++]&0xFF);
			case 2:
				return (data[programPointer++]<<24)|((data[programPointer++]&0xFF)<<16)|((data[programPointer++]&0xFF)<<8)|(data[programPointer++]&0xFF);
			default:
				throwAssertion();
			}
		}
		return 0;
	}
	
	private int loadValue() throws ScriptException{
		int ptr = data[programPointer++]&0xFF;
		if((ptr&0x80)==0){
			switch((ptr>>5)&0x3){
			case 0:
				return register[ptr & 0x1F];
			case 1:
				ptr = register[ptr & 0x1F];
				if(ptr<ext.length){
					return ext[ptr];
				}else{
					return ram[ptr-ext.length];
				}
			case 2:
				ptr = register[ptr & 0x1F]+loadPtrOffset();
				if(ptr<ext.length){
					return ext[ptr];
				}else{
					return ram[ptr-ext.length];
				}
			case 3:
				ptr = register[ptr & 0x1F]*loadPtrOffset();
				if(ptr<ext.length){
					return ext[ptr];
				}else{
					return ram[ptr-ext.length];
				}
			default:
				throwAssertion();
			}
		}else{
			switch((ptr>>4)&0x7){
			case 0:
				ptr = data[programPointer++]&0xFF;
				if(ptr<ext.length){
					return ext[ptr];
				}else{
					return ram[ptr-ext.length];
				}
			case 1:
				ptr = ((data[programPointer++]&0xFF)<<8)|(data[programPointer++]&0xFF);
				if(ptr<ext.length){
					return ext[ptr];
				}else{
					return ram[ptr-ext.length];
				}
			case 2:
				ptr = ((data[programPointer++]&0xFF)<<24)|((data[programPointer++]&0xFF)<<16)|((data[programPointer++]&0xFF)<<8)|(data[programPointer++]&0xFF);
				if(ptr<ext.length){
					return ext[ptr];
				}else{
					return ram[ptr-ext.length];
				}
			case 3:
				return data[programPointer++];
			case 4:
				return (data[programPointer++]<<8)|(data[programPointer++]&0xFF);
			case 5:
				return (data[programPointer++]<<24)|((data[programPointer++]&0xFF)<<16)|((data[programPointer++]&0xFF)<<8)|(data[programPointer++]&0xFF);
			default:
				throwAssertion();
			}
		}
		return 0;
	}
	
	private boolean loadPtrOrValue() throws ScriptException{
		ptr = data[programPointer++]&0xFF;
		if((ptr&0x80)==0){
			switch((ptr>>5)&0x3){
			case 0:
				activePtr = register;// inc r1
				ptr = ptr & 0x1F;
				break;
			case 1:
				ptr = register[ptr & 0x1F];// inc [r1]
				if(ptr<ext.length){
					activePtr = ext;
				}else{
					activePtr = ram;
					ptr -= ext.length;
				}
				break;
			case 2:
				ptr = register[ptr & 0x1F]+loadPtrOffset();// inc [r1+...]
				if(ptr<ext.length){
					activePtr = ext;
				}else{
					activePtr = ram;
					ptr -= ext.length;
				}
				break;
			case 3:
				ptr = register[ptr & 0x1F]*loadPtrOffset();// inc [r1*...]
				if(ptr<ext.length){
					activePtr = ext;
				}else{
					activePtr = ram;
					ptr -= ext.length;
				}
				break;
			default:
				throwAssertion();
			}
		}else{
			switch((ptr>>4)&0x7){
			case 0:
				ptr = data[programPointer++];
				break;
			case 1:
				ptr = (data[programPointer++])<<8|(data[programPointer++]&0xFF);
				break;
			case 2:
				ptr = (data[programPointer++])<<24|((data[programPointer++]&0xFF)<<16)|((data[programPointer++]&0xFF)<<8)|(data[programPointer++]&0xFF);
				break;
			case 3:
				ptr = data[programPointer++];
				return true;
			case 4:
				ptr = (data[programPointer++]<<8)|(data[programPointer++]&0xFF);
				return true;
			case 5:
				ptr = (data[programPointer++]<<24)|((data[programPointer++]&0xFF)<<16)|((data[programPointer++]&0xFF)<<8)|(data[programPointer++]&0xFF);
				return true;
			default:
				throwAssertion();
			}
			if(ptr<ext.length){
				activePtr = ext;
			}else{
				activePtr = ram;
				ptr -= ext.length;
			}
		}
		return false;
	}
	
	private void loadPtr() throws ScriptException{
		ptr = data[programPointer++]&0xFF;
		if((ptr&0x80)==0){
			switch((ptr>>5)&0x3){
			case 0:
				activePtr = register;// inc r1
				ptr = ptr & 0x1F;
				break;
			case 1:
				ptr = register[ptr & 0x1F];// inc [r1]
				if(ptr<ext.length){
					activePtr = ext;
				}else{
					activePtr = ram;
					ptr -= ext.length;
				}
				break;
			case 2:
				ptr = register[ptr & 0x1F]+loadPtrOffset();// inc [r1+...]
				if(ptr<ext.length){
					activePtr = ext;
				}else{
					activePtr = ram;
					ptr -= ext.length;
				}
				break;
			case 3:
				ptr = register[ptr & 0x1F]*loadPtrOffset();// inc [r1*...]
				if(ptr<ext.length){
					activePtr = ext;
				}else{
					activePtr = ram;
					ptr -= ext.length;
				}
				break;
			default:
				throwAssertion();
			}
		}else{
			switch((ptr>>4)&0x7){
			case 0:
				ptr = data[programPointer++];
				break;
			case 1:
				ptr = (data[programPointer++])<<8|(data[programPointer++]&0xFF);
				break;
			case 2:
				ptr = (data[programPointer++])<<24|((data[programPointer++]&0xFF)<<16)|((data[programPointer++]&0xFF)<<8)|(data[programPointer++]&0xFF);
				break;
			default:
				throwAssertion();
			}
			if(ptr<ext.length){
				activePtr = ext;
			}else{
				activePtr = ram;
				ptr -= ext.length;
			}
		}
	}
	
	private static void throwAssertion() throws ScriptException{
		throw new ScriptException(MiniScriptMessages.getLocaleMessage("assertion.error"));//$NON-NLS-1$
	}
	
	@Override
	public ScriptEngine getEngine() {
		return engine;
	}
	
	private void checkContext(ScriptContext context) throws ScriptException {
		Object obj = context.getAttribute(MiniScriptLang.BINDING_RAM);
		if(!(obj instanceof int[])){
			throw new ScriptException(MiniScriptMessages.getLocaleMessage("ram.not.intarray.or.null"));//$NON-NLS-1$
		}
		ram = (int[])obj;
		obj = context.getAttribute(MiniScriptLang.BINDING_REGISTER);
		if(!(obj instanceof int[])){
			throw new ScriptException(MiniScriptMessages.getLocaleMessage("registers.not.intarray.or.null"));//$NON-NLS-1$
		}
		register = (int[])obj;
		if(register.length!=32)
			throw new ScriptException(MiniScriptMessages.getLocaleMessage("not.32.registers"));//$NON-NLS-1$
		obj = context.getAttribute(MiniScriptLang.BINDING_EXT);
		if(obj instanceof int[]){
			ext = (int[]) obj;
		}else{
			ext = NULL_EXT;
		}
		obj = context.getAttribute(EXECUTING_SCRIPT);
		if(obj instanceof Boolean && (Boolean)obj)
			throw new ScriptException(MiniScriptMessages.getLocaleMessage("allready.exectuting"));//$NON-NLS-1$
	}
	
}
