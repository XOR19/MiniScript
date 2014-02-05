package miniscript;

import java.util.List;

abstract class MiniScriptDummyInst {

	final static class DummyInstUnary extends MiniScriptDummyInst{

		MiniScriptValue v1;
		
		DummyInstUnary(MiniScriptASM asm, int line, MiniScriptValue v1) {
			super(asm, line);
			this.v1 = v1;
		}

		@Override
		int getSize(MiniScriptCodeGen codeGen, List<MiniScriptDummyInst> instructions) {
			return 1+v1.getSize();
		}

		@Override
		int compile(MiniScriptCodeGen codeGen, List<MiniScriptDummyInst> instructions, byte[] data, int pos) {
			data[pos++] = (byte) asm.id;
			pos = v1.writeTo(data, pos);
			return pos;
		}
		
	}
	
	final static class DummyInstNormal extends MiniScriptDummyInst{

		MiniScriptValue v1;
		
		MiniScriptValue v2;
		
		DummyInstNormal(MiniScriptASM asm, int line, MiniScriptValue v1, MiniScriptValue v2) {
			super(asm, line);
			this.v1 = v1;
			this.v2 = v2;
		}

		@Override
		int getSize(MiniScriptCodeGen codeGen, List<MiniScriptDummyInst> instructions) {
			return 1+v1.getSize()+v2.getSize();
		}

		@Override
		int compile(MiniScriptCodeGen codeGen, List<MiniScriptDummyInst> instructions, byte[] data, int pos) {
			data[pos++] = (byte) asm.id;
			pos = v1.writeTo(data, pos);
			pos = v2.writeTo(data, pos);
			return pos;
		}
		
	}
	
	final static class DummyInstJump extends MiniScriptDummyInst{

		String target;
		private MiniScriptDummyInst ttarget;
		
		DummyInstJump(MiniScriptASM asm, int line, String target) {
			super(asm, line);
			this.target = target;
		}
		
		@Override
		void resolve(MiniScriptCodeGen codeGen, List<MiniScriptDummyInst> instructions) {
			ttarget = codeGen.getTarget(target);
		}

		@Override
		boolean isPointingTo(MiniScriptDummyInst inst) {
			return ttarget==inst;
		}

		@Override
		void delete(MiniScriptCodeGen codeGen, List<MiniScriptDummyInst> instructions, MiniScriptDummyInst inst) {
			if(ttarget==inst){
				int index = instructions.indexOf(inst);
				index++;
				if(instructions.size()==index){
					ttarget = null;
				}else{
					ttarget = instructions.get(index);
				}
			}
		}

		@Override
		int getSize(MiniScriptCodeGen codeGen, List<MiniScriptDummyInst> instructions) {
			return asm == MiniScriptASM.JMPL?3:2;
		}

		@Override
		int compile(MiniScriptCodeGen codeGen, List<MiniScriptDummyInst> instructions, byte[] data, int pos) {
			data[pos++] = (byte) asm.id;
			int dist = codeGen.getDist(this, ttarget);
			if(asm == MiniScriptASM.JMPL){
				data[pos++] = (byte) (dist>>8);
				data[pos++] = (byte) dist;
			}else{
				data[pos++] = (byte) dist;
			}
			return pos;
		}
		
	}
	
	final static class DummyInstLabel extends MiniScriptDummyInst{

		String label;
		
		DummyInstLabel(int line, String label) {
			super(null, line);
			this.label = label;
		}

		@Override
		int getSize(MiniScriptCodeGen codeGen, List<MiniScriptDummyInst> instructions) {
			throw new AssertionError();
		}

		@Override
		int compile(MiniScriptCodeGen codeGen, List<MiniScriptDummyInst> instructions, byte[] data, int pos) {
			throw new AssertionError();
		}
		
	}
	
	final static class DummyInstNative extends MiniScriptDummyInst{

		MiniScriptValue values[];
		
		DummyInstNative(MiniScriptASM asm, int line, MiniScriptValue values[]) {
			super(asm, line);
			this.values = values;
		}

		@Override
		int getSize(MiniScriptCodeGen codeGen, List<MiniScriptDummyInst> instructions) {
			int size = 2;
			for(int i=0; i<values.length; i++){
				size += values[i].getSize();
			}
			return size;
		}

		@Override
		int compile(MiniScriptCodeGen codeGen, List<MiniScriptDummyInst> instructions, byte[] data, int pos) {
			data[pos++] = (byte) asm.id;
			data[pos++] = (byte) (values.length-1);
			for(int i=0; i<values.length; i++){
				pos = values[i].writeTo(data, pos);
			}
			return pos;
		}
		
	}
	
	final static class DummyInstSwitch extends MiniScriptDummyInst{

		MiniScriptValue value;
		String[] targets;
		private MiniScriptDummyInst[] ttargets;
		
		DummyInstSwitch(MiniScriptASM asm, int line, MiniScriptValue value, String[] targets) {
			super(asm, line);
			this.value = value;
			this.targets = targets;
		}
		
		@Override
		void resolve(MiniScriptCodeGen codeGen, List<MiniScriptDummyInst> instructions) {
			ttargets = new MiniScriptDummyInst[targets.length];
			for(int i=0; i<targets.length; i++){
				ttargets[i] = codeGen.getTarget(targets[i]);
			}
		}

		@Override
		boolean isPointingTo(MiniScriptDummyInst inst) {
			for(MiniScriptDummyInst ttarget:ttargets){
				if(ttarget==inst){
					return true;
				}
			}
			return false;
		}

		@Override
		void delete(MiniScriptCodeGen codeGen, List<MiniScriptDummyInst> instructions, MiniScriptDummyInst inst) {
			for(MiniScriptDummyInst ttarget:ttargets){
				if(ttarget==inst){
					int index = instructions.indexOf(inst);
					index++;
					if(instructions.size()==index){
						ttarget = null;
					}else{
						ttarget = instructions.get(index);
					}
				}
			}
		}

		@Override
		int getSize(MiniScriptCodeGen codeGen, List<MiniScriptDummyInst> instructions) {
			return 3+value.getSize()+ttargets.length*2;
		}

		@Override
		int compile(MiniScriptCodeGen codeGen, List<MiniScriptDummyInst> instructions, byte[] data, int pos) {
			data[pos++] = (byte) asm.id;
			data[pos++] = (byte) (ttargets.length>>8);
			data[pos++] = (byte) ttargets.length;
			pos = value.writeTo(data, pos);
			for(MiniScriptDummyInst ttarget:ttargets){
				int dist = codeGen.getDist(this, ttarget);
				data[pos++] = (byte) (dist>>8);
				data[pos++] = (byte) dist;
			}
			return pos;
		}
		
	}
	
	MiniScriptASM asm;
	
	int line;
	
	MiniScriptDummyInst(MiniScriptASM asm, int line){
		this.asm = asm;
		this.line = line;
	}

	void resolve(MiniScriptCodeGen codeGen, List<MiniScriptDummyInst> instructions) {}

	boolean isPointingTo(MiniScriptDummyInst inst) {
		return false;
	}

	void delete(MiniScriptCodeGen codeGen, List<MiniScriptDummyInst> instructions, MiniScriptDummyInst inst) {}
	
	abstract int getSize(MiniScriptCodeGen codeGen, List<MiniScriptDummyInst> instructions);

	abstract int compile(MiniScriptCodeGen codeGen, List<MiniScriptDummyInst> instructions, byte[] data, int pos);
	
}
