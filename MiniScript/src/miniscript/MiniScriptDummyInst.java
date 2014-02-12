package miniscript;

import java.util.Arrays;
import java.util.List;

import miniscript.MiniScriptValue.ValueNum;

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
		
		MiniScriptValue[] vs;
		
		DummyInstNormal(MiniScriptASM asm, int line, MiniScriptValue v1, MiniScriptValue...vs) {
			super(asm, line);
			this.v1 = v1;
			this.vs = vs;
		}

		@Override
		int getSize(MiniScriptCodeGen codeGen, List<MiniScriptDummyInst> instructions) {
			int size = 1+v1.getSize();
			for(MiniScriptValue v:vs){
				size += v.getSize();
			}
			return size;
		}

		@Override
		int compile(MiniScriptCodeGen codeGen, List<MiniScriptDummyInst> instructions, byte[] data, int pos) {
			data[pos++] = (byte) asm.id;
			pos = v1.writeTo(data, pos);
			for(MiniScriptValue v:vs){
				pos = v.writeTo(data, pos);
			}
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
			ttarget = codeGen.getTarget(this, target);
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
		ValueNum[] targetIDs;
		String[] targets;
		private int min;
		private int numSize;
		private int[] sortedNums;
		private MiniScriptDummyInst[] ttargets;
		private static final DummyInstLabel DEFAULT_JUMP = new DummyInstLabel(0, "dummy");
		
		DummyInstSwitch(MiniScriptASM asm, int line, MiniScriptValue value, ValueNum[] targetIDs, String[] targets) {
			super(asm, line);
			this.value = value;
			this.targetIDs = targetIDs;
			this.targets = targets;
		}
		
		@Override
		void resolve(MiniScriptCodeGen codeGen, List<MiniScriptDummyInst> instructions) {
			min = Integer.MAX_VALUE;
			int max = Integer.MIN_VALUE;
			for(int i=0; i<targetIDs.length; i++){
				if(min>targetIDs[i].num){
					min = targetIDs[i].num;
				}
				if(max<targetIDs[i].num){
					max = targetIDs[i].num;
				}
			}
			int diff = max-min+1;
			numSize = new ValueNum(diff/2).getSize()-1;
			if(diff>targets.length/2.0*(2+numSize)){
				ttargets = new MiniScriptDummyInst[targets.length];
				sortedNums = new int[targets.length];
				for(int i=0; i<targets.length; i++){
					sortedNums[i] = targetIDs[i].num;
				}
				Arrays.sort(sortedNums);
				for(int i=0; i<targets.length; i++){
					for(int j=0; j<sortedNums.length; j++){
						if(sortedNums[j]==targetIDs[i].num){
							ttargets[j] = codeGen.getTarget(this, targets[i]);
							break;
						}
					}
				}
				int off = min+diff/2;
				for(int i=0; i<sortedNums.length; i++){
					sortedNums[i] -= off;
				}
				min = -diff/2;
			}else{
				ttargets = new MiniScriptDummyInst[diff];
				for(int i=0; i<diff; i++){
					for(int j=0; j<targets.length; j++){
						if(targetIDs[j].num==i-min){
							ttargets[i] = codeGen.getTarget(this, targets[j]);
							break;
						}
					}
					if(ttargets[i]==null){
						ttargets[i] = DEFAULT_JUMP;
					}
				}
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
			for(int i=0; i<ttargets.length; i++){
				if(ttargets[i]==inst){
					int index = instructions.indexOf(inst);
					index++;
					if(instructions.size()==index){
						ttargets[i] = null;
					}else{
						ttargets[i] = instructions.get(index);
					}
				}
			}
		}

		@Override
		int getSize(MiniScriptCodeGen codeGen, List<MiniScriptDummyInst> instructions) {
			int size = 4+value.getSize()+new ValueNum(min).getSize();
			if(sortedNums==null){
				size += ttargets.length*2;
			}else{
				size += ttargets.length*(2+numSize);
			}
			return size;
		}

		@Override
		int compile(MiniScriptCodeGen codeGen, List<MiniScriptDummyInst> instructions, byte[] data, int pos) {
			data[pos++] = (byte) asm.id;
			data[pos++] = (byte) (ttargets.length>>8);
			data[pos++] = (byte) ttargets.length;
			pos = new ValueNum(min).writeTo(data, pos);
			pos = value.writeTo(data, pos);
			if(sortedNums==null){
				data[pos++] = 0;
				for(MiniScriptDummyInst ttarget:ttargets){
					int dist = ttarget==DEFAULT_JUMP?0:codeGen.getDist(this, ttarget);
					data[pos++] = (byte) (dist>>8);
					data[pos++] = (byte) dist;
				}
			}else{
				data[pos++] = (byte) numSize;
				for(int i=0; i<ttargets.length; i++){
					if(numSize>=4){
						data[pos++] = (byte) (sortedNums[i]>>24);
						data[pos++] = (byte) (sortedNums[i]>>16);
					}
					if(numSize>=2){
						data[pos++] = (byte) (sortedNums[i]>>8);
					}
					data[pos++] = (byte) (sortedNums[i]);
					int dist = codeGen.getDist(this, ttargets[i]);
					data[pos++] = (byte) (dist>>8);
					data[pos++] = (byte) dist;
				}
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
