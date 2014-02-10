package miniscript;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.tools.DiagnosticListener;
import javax.tools.Diagnostic.Kind;

import miniscript.MiniScriptDummyInst.DummyInstLabel;

final class MiniScriptCodeGen {

	private List<MiniScriptDummyInst> instructions = new ArrayList<MiniScriptDummyInst>();
	private DiagnosticListener<Void> diagnosticListener;
	private boolean errored;
	private boolean backjumpDisabled;
	
	MiniScriptCodeGen(DiagnosticListener<Void> diagnosticListener, boolean backjumpDisabled){
		this.diagnosticListener = diagnosticListener;
		this.backjumpDisabled = backjumpDisabled;
	}
	
	byte[] getData() {
		for(MiniScriptDummyInst inst:instructions){
			inst.resolve(this, instructions);
		}
		delete();
		int size = 0;
		for(MiniScriptDummyInst inst:instructions){
			size += inst.getSize(this, instructions);
		}
		byte[] data = new byte[size];
		int pos = 0;
		for(MiniScriptDummyInst inst:instructions){
			pos = inst.compile(this, instructions, data, pos);
		}
		if(errored)
			return null;
		return data;
	}

	private void delete(){
		boolean deleted;
		do{
			deleted = false;
			ListIterator<MiniScriptDummyInst> i = instructions.listIterator();
			while(i.hasNext()){
				MiniScriptDummyInst inst = i.next();
				if(inst instanceof DummyInstLabel){
					for(MiniScriptDummyInst inst2:instructions){
						inst2.delete(this, instructions, inst);
					}
					deleted = true;
					i.remove();
				}else if(inst.asm==MiniScriptASM.JMP){
					if(i.hasNext()){
						MiniScriptDummyInst inst3 = i.next();
						if(inst.isPointingTo(inst3)){
							i.previous();
							i.previous();
							i.next();
							for(MiniScriptDummyInst inst2:instructions){
								inst2.delete(this, instructions, inst);
							}
							deleted = true;
							i.remove();
						}else{
							boolean visible = false;
							for(MiniScriptDummyInst inst2:instructions){
								if(inst2.isPointingTo(inst3)){
									visible = true;
									break;
								}
							}
							if(!visible){
								for(MiniScriptDummyInst inst2:instructions){
									inst2.delete(this, instructions, inst3);
								}
								deleted = true;
								i.remove();
							}else{
								i.previous();
							}
						}
					}else{
						if(inst.isPointingTo(null)){
							for(MiniScriptDummyInst inst2:instructions){
								inst2.delete(this, instructions, inst);
							}
							deleted = true;
							i.remove();
						}
					}
				}
			}
		}while(deleted);
	}
	
	void addInst(MiniScriptDummyInst inst) {
		instructions.add(inst);
	}

	MiniScriptDummyInst getTarget(MiniScriptDummyInst inst, String target) {
		boolean notYet = backjumpDisabled;
		for(MiniScriptDummyInst inst1:instructions){
			if(inst1 == inst)
				notYet = false;
			if(inst1 instanceof DummyInstLabel){
				if(((DummyInstLabel) inst1).label.equals(target)){
					if(notYet){
						errored = true;
						diagnosticListener.report(new MiniScriptDiagnostic(Kind.ERROR, inst.line, "backjump.not.allowed", target));//$NON-NLS-1$
					}
					return inst;
				}
			}
		}
		errored = true;
		diagnosticListener.report(new MiniScriptDiagnostic(Kind.ERROR, inst.line, "target.not.found", target));//$NON-NLS-1$
		return null;
	}

	int getDist(MiniScriptDummyInst inst1, MiniScriptDummyInst inst2) {
		int index1 = instructions.indexOf(inst1);
		int index2;
		if(inst2==null){
			index2 = instructions.size();
		}else{
			index2 = instructions.indexOf(inst2);
		}
		int dist = 0;
		if(index1<index2){
			for(int i=index1+1; i<index2; i++){
				dist += instructions.get(i).getSize(this, instructions);
			}
		}else{
			for(int i=index2; i<=index1; i++){
				dist -= instructions.get(i).getSize(this, instructions);
			}
		}
		return dist;
	}

}
