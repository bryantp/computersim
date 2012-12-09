import java.util.ArrayList;




public class Memory implements Errors {
	
	/*
	 * Memory is broken down into two sections. One contains operands, the other contains the 
	 * instructions. They are both separate. I will probably just make two instances of memory instead 
	 * of combining them into one big memory. 
	 * 
	 * Let the memory decide where to place the instructions, the instructions decided where to place
	 * the operands. 
	 */
	
	private ArrayList<String> instructions; 
	private Integer[] operands;  
	private CPU cpu; 
	
	private int usedOperand = 0; //Used to determine how much of the operands array is occupied. 
	
	public void setBus(CPU cpu){
		this.cpu = cpu; 
		instructions = new ArrayList<String>(); //Doesn't matter about keeping track of indices too much. 
		instructions.ensureCapacity(255); 
		operands = new Integer[16]; //Maximum addressable with 4 bits.
	}
	
	public void erase(){
		instructions = new ArrayList<String>(); 
		operands = new Integer[16]; 
	}
	
	public void writeInstruction(String input){
		instructions.add(input); 
	}
	
	public boolean writeInstruction(String input, int location){
		try{
			if(instructions.get(location) != null) return false; 
			instructions.add(location, input); 
			return true; 
		}
		
		catch(Exception e){
			return false; 
		}
		
	}
	
	public String readInstruction(int location){ 
		 return instructions.get(location); 
	}
	
	public void writeOperand(int input, int location){
		if(location > 16 || location < 0) {
			cpu.error(errors.INVALID_ADDRESS);
		}
		operands[location] = input; 
		usedOperand++; 
	}
	
	public void deleteLocation(int location){
		if(location > 16 || location < 0) {
			cpu.error(errors.INVALID_ADDRESS);
		}
		operands[location] = null;
		usedOperand--; 
	}
	
	public int readOperand(int location){
		if(location > 16 || location < 0) {
			cpu.error(errors.INVALID_ADDRESS);
		}
		return operands[location];
	}
	
	public int operandmemSize(){
		return usedOperand; 
	}
	
	public String instructionString(){
		return instructions.toString(); 
	}
	
	

}
