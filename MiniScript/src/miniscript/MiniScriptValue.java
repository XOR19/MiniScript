package miniscript;

import java.util.List;

abstract class MiniScriptValue{
	
	static final class ValueReg extends MiniScriptValue{
		
		int reg;
		
		ValueReg(int reg){
			this.reg = reg;
		}

		@Override
		int getSize() {
			return 1;
		}

		@Override
		int writeTo(byte[] data, int pos) {
			data[pos++] = (byte)(reg&0x1F);
			return pos;
		}
		
	}
	
	static final class ValueNum extends MiniScriptValue{
		
		int num;
		
		ValueNum(int num){
			this.num = num;
		}

		@Override
		int getSize() {
			return num<128 && num>-128?2:num<32000 && num>-32000?3:5;
		}

		@Override
		int writeTo(byte[] data, int pos) {
			if(num<128 && num>-128){
				data[pos++] = (byte) (0x80 | (3<<4));
				data[pos++] = (byte) num;
			}else if(num<32000 && num>-32000){
				data[pos++] = (byte) (0x80 | (4<<4));
				data[pos++] = (byte) (num>>8);
				data[pos++] = (byte) num;
			}else{
				data[pos++] = (byte) (0x80 | (5<<4));
				data[pos++] = (byte) (num>>24);
				data[pos++] = (byte) (num>>16);
				data[pos++] = (byte) (num>>8);
				data[pos++] = (byte) num;
			}
			return pos;
		}
		
	}
	
	static final class ValuePtr extends MiniScriptValue{
		
		List<MiniScriptValue> values;
		List<Boolean> mul;

		ValuePtr(List<MiniScriptValue> values, List<Boolean> mul) {
			this.values = values;
			this.mul = mul;
		}

		@Override
		int getSize() {
			int size = 0;
			for(MiniScriptValue value:values){
				size += value.getSize();
			}
			return size;
		}

		@Override
		int writeTo(byte[] data, int pos) {
			for(int i=0; i<values.size(); i++){
				boolean last = i==values.size()-1;
				MiniScriptValue value = values.get(i);
				boolean ismul = mul.get(i);
				if(value instanceof ValueReg){
					if(last){
						data[pos++]=(byte) (1<<5 | ((ValueReg)value).reg);
					}else{
						if(ismul){
							data[pos++]=(byte) (3<<5 | ((ValueReg)value).reg);
						}else{
							data[pos++]=(byte) (2<<5 | ((ValueReg)value).reg);
						}
					}
				}else{
					int num = ((ValueNum)value).num;
					if(last){
						if(num<128 && num>-128){
							data[pos++] = (byte) (0x80 | (0<<4));
							data[pos++] = (byte) num;
						}else if(num<32000 && num>-32000){
							data[pos++] = (byte) (0x80 | (1<<4));
							data[pos++] = (byte) (num>>8);
							data[pos++] = (byte) num;
						}else{
							data[pos++] = (byte) (0x80 | (2<<4));
							data[pos++] = (byte) (num>>24);
							data[pos++] = (byte) (num>>16);
							data[pos++] = (byte) (num>>8);
							data[pos++] = (byte) num;
						}
					}else{
						if(num<128 && num>-128){
							data[pos++] = (byte) (0x80 | (0<<4));
							data[pos++] = (byte) num;
						}else if(num<32000 && num>-32000){
							data[pos++] = (byte) (0x80 | (1<<4));
							data[pos++] = (byte) (num>>8);
							data[pos++] = (byte) num;
						}else{
							data[pos++] = (byte) (0x80 | (2<<4));
							data[pos++] = (byte) (num>>24);
							data[pos++] = (byte) (num>>16);
							data[pos++] = (byte) (num>>8);
							data[pos++] = (byte) num;
						}
					}
				}
			}
			return pos;
		}
		
	}

	abstract int getSize();

	abstract int writeTo(byte[] data, int pos);
	
}