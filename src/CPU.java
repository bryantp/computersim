import java.util.Stack;






public class CPU implements Errors{
	
	private Memory mem;
	private IO io; 
	private String currentInstruction; //Program Counter
	private int currentLocation; //Current memory location  
	private boolean running; 
	private Stack<Integer> stack;
	
	private String reg1, reg2; //Instruction register
	private int oreg1, oreg2;  //Operand register
	
	 
	
	public CPU(Memory mem, IO io){
		this.mem = mem; //Give the CPU access to the memory. 
		this.io = io; 
		running = true; 
		stack = new Stack<Integer>(); 
		currentLocation = 0; 
		
	}
	
	public void initCPU(){
		running = true; 
		currentLocation = 0; 
	}
	
	/*
	 * Reads the op codes and determines what to do. 
	 */
	public void start(){
		//Read the instructions in and start performing operations on them. 
		while(running){
			 
			currentInstruction = mem.readInstruction(currentLocation); 
			//System.out.println(currentInstruction + "  " + currentLocation); 
			if(currentLocation%2 == 0){//Means that it contains an opcode and type. 
				
				if(currentInstruction.charAt(0) == '0' && currentInstruction.charAt(1) == '0' && currentInstruction.charAt(2) == '0' && currentInstruction.charAt(3) == '0' )
				{
					if(currentInstruction.charAt(4) == '0' && currentInstruction.charAt(5) == '0' && currentInstruction.charAt(6) == '0' && currentInstruction.charAt(7) == '1'){
						//The instruction is 00000001, load with the next part coming up
						currentLocation++; 
						currentInstruction += mem.readInstruction(currentLocation); 
						load(false); 
						currentLocation++; 
					}
					
				}
				
				else if(currentInstruction.charAt(0) == '0' && currentInstruction.charAt(1) == '1' && currentInstruction.charAt(2) == '1' && currentInstruction.charAt(3) == '1'){
					//HALT op code
					running = false; 
					System.out.println("HALT"); 
				}
				
				else if(currentInstruction.charAt(0) == '0' && currentInstruction.charAt(1) == '0' && currentInstruction.charAt(2) == '1' && currentInstruction.charAt(3) == '1'){
					//Add opcode
					currentLocation++; 
					currentInstruction += mem.readInstruction(currentLocation);
					add();
					currentLocation++; 
				}
				
				else if(currentInstruction.charAt(0) == '1' && currentInstruction.charAt(1) == '1' && currentInstruction.charAt(2) == '0' && currentInstruction.charAt(3) == '0'){
					//Display opcode. 
					currentLocation++; 
					currentInstruction += mem.readInstruction(currentLocation); 
					display(); 
					currentLocation++; 
				}
					
				
				else if(currentInstruction.charAt(0) == '1' && currentInstruction.charAt(1) == '1' && currentInstruction.charAt(2) == '1' && currentInstruction.charAt(3) == '1'){ 
					//Move opcode
					currentLocation++;
					currentInstruction += mem.readInstruction(currentLocation);
					move(); 
					currentLocation++; 
				}
				
				else if(currentInstruction.charAt(0) == '1' && currentInstruction.charAt(1) == '1' && currentInstruction.charAt(2) == '1' && currentInstruction.charAt(3) == '0'){
					//Delete opcode
					currentLocation++;
					currentInstruction += mem.readInstruction(currentLocation);
					delete();
					currentLocation++; 
					
				}
				
				else if(currentInstruction.charAt(0) == '0' && currentInstruction.charAt(1) == '0' && currentInstruction.charAt(2) == '0' && currentInstruction.charAt(3) == '1'){
					//Pops from the stack and stores it in a mem location. 
					currentLocation++;
					currentInstruction += mem.readInstruction(currentLocation);
					load(true); 
					currentLocation++; 
					
				}
				
				else if(currentInstruction.charAt(0) == '1' && currentInstruction.charAt(1) == '0' && currentInstruction.charAt(2) == '0' && currentInstruction.charAt(3) == '1'){
					//Subtract op code
					currentLocation++;
					currentInstruction += mem.readInstruction(currentLocation);
					subtract();
					currentLocation++; 
				}
				
				
				else if(currentInstruction.charAt(0) == '1' && currentInstruction.charAt(1) == '0' && currentInstruction.charAt(2) == '1' && currentInstruction.charAt(3) == '1'){
					//Divide op code
					currentLocation++; 
					currentInstruction += mem.readInstruction(currentLocation); 
					divide();
					currentLocation++; 
				}
				
				else if(currentInstruction.charAt(0) == '1' && currentInstruction.charAt(1) == '1' && currentInstruction.charAt(2) == '0' && currentInstruction.charAt(3) == '1'){
					//multiply op code
					currentLocation++;
					currentInstruction += mem.readInstruction(currentLocation);
					multiply();
					currentLocation++; 
				}
				
				else if(currentInstruction.charAt(0) == '0' && currentInstruction.charAt(1) == '1' && currentInstruction.charAt(2) == '0' && currentInstruction.charAt(3) == '1'){
					//register read op code 
					currentLocation++;
					currentInstruction += mem.readInstruction(currentLocation);
					registerOperation(true);
					currentLocation++; 
				}
				
				else if(currentInstruction.charAt(0) == '1' && currentInstruction.charAt(1) == '0' && currentInstruction.charAt(2) == '1' && currentInstruction.charAt(3) == '0'){
					//register write op code
					currentLocation++; 
					currentInstruction += mem.readInstruction(currentLocation);
					registerOperation(false);
					currentLocation++; 
				}
				
				else if(currentInstruction.charAt(0) == '0' && currentInstruction.charAt(1) == '1' && currentInstruction.charAt(2) == '0' && currentInstruction.charAt(3) == '0'){
					//Continue op code
					running = false;
					currentLocation = 0; 
					io.reset(); 
					
					 
				}
				
				else{
					//Invalid op code. 
					currentLocation++;
					currentInstruction += mem.readInstruction(currentLocation);
					error(currentInstruction, errors.INVALID_OPCODE); 
					currentLocation++; 
				}
			}
		}
			
	}
	
	
	
	
	
	/*
	 * Performs the load operation. Takes an operand and puts it into a memory location. 
	 */
	public void load(boolean readStack){
		if(readStack){
			
			try{
			int operand = stack.pop();
			int location = Integer.parseInt(currentInstruction.substring(12),2);
			mem.writeOperand(operand, location); 
			}
			
			catch(Exception e){
				
				if(e instanceof java.util.EmptyStackException){
					error(errors.STACK_EMPTY); 
				}
				
				else if(e instanceof java.lang.NumberFormatException){
					error(errors.INVALID_OPERANDS_NaN); 
					
				}
				
				else{
					error(errors.SYS_KILL); 
				}
			}
		}
		
		else{
			int operand = Integer.parseInt(currentInstruction.substring(8,12),2); //The location to place the operand into. 
			int location = Integer.parseInt(currentInstruction.substring(12),2); //The operand to place into location
			mem.writeOperand(operand, location); //Write the operand to the memory location. 
		} 
	}
	
	/*
	 * Performs addition. 
	 */
	public void add(){
		try{
			int operand1 = mem.readOperand(Integer.parseInt(currentInstruction.substring(8,12),2));
			int operand2 = mem.readOperand(Integer.parseInt(currentInstruction.substring(12),2)); 
			stack.push(operand1 + operand2);
		} 
		
		catch (Exception e){
			
			if(e instanceof java.lang.NullPointerException ) error(errors.INVALID_ADDRESS);
			else error(errors.INVALID_OPERANDS_NaN); 
			
		}
		 
	} 
	
	//Displays information from location. 
	public void display(){
		try{
			
			int location = Integer.parseInt(currentInstruction.substring(12),2); 
			if(location < 0 || location > 16) error(errors.INVALID_ADDRESS); 
			Integer operand = mem.readOperand(location); 
			io.display(operand.toString(),null); 
		}
		
		catch (Exception e){
			 
			if(e instanceof java.lang.NumberFormatException){
				error(errors.INVALID_OPERANDS_NaN); 
			}
			
			else error(errors.SYS_KILL); //Unknown error 
		}
	}
	
	//Moves operands
	public void move(){
		int source = Integer.parseInt(currentInstruction.substring(8,12),2);
		int destination = Integer.parseInt(currentInstruction.substring(12),2); 
		mem.writeOperand(mem.readOperand(source), destination); 
	}
	
	//Deletes operand
	public void delete(){
		int location = Integer.parseInt(currentInstruction.substring(12),2); 
		mem.deleteLocation(location); 
	}
	
	//Subtract operand
	public void subtract(){
		try{
			int firstOperand = mem.readOperand(Integer.parseInt(currentInstruction.substring(8,12),2)); 
			int secondOperand = mem.readOperand(Integer.parseInt(currentInstruction.substring(12),2));
			stack.push(firstOperand - secondOperand);
		}
		
		catch (Exception e){
			error(errors.INVALID_OPERANDS_NaN); 
		}
	}
	
	//Divide operand
	public void divide(){
		try{
			int firstOperand = mem.readOperand(Integer.parseInt(currentInstruction.substring(8,12),2)); 
			int secondOperand = mem.readOperand(Integer.parseInt(currentInstruction.substring(12),2));
			stack.push(firstOperand/secondOperand); //Only does integer division. 
		}
		
		catch (Exception e){
			error(errors.INVALID_OPERANDS_NaN); 
		}
	}
	
	//Multiple operand
	public void multiply(){
		try{
			int firstOperand = mem.readOperand(Integer.parseInt(currentInstruction.substring(8,12),2)); 
			int secondOperand = mem.readOperand(Integer.parseInt(currentInstruction.substring(12),2));
			stack.push(firstOperand * secondOperand);
		}
		
		catch (Exception e){
			error(errors.INVALID_OPERANDS_NaN); 
		}
		
	}
	
	//Error handling. 
	public void error(errors Error){
		if(Error.equals(errors.SYS_KILL)) running = false; 
		io.display(currentInstruction, Error);
	}
	
	
	public void error(String inst, errors Error){
		
		io.display(inst, Error); 
	}
	
	public void registerOperation(boolean read){
		
		if(read){
			
			if(currentInstruction.substring(4,9).charAt(0) == '0' &&
			   currentInstruction.substring(4,9).charAt(1) == '0' &&
			   currentInstruction.substring(4,9).charAt(2) == '1' &&
			   currentInstruction.substring(4,9).charAt(3) == '1' 
			){
				//Read from the Instruction Register. 
				try{
					int register = Integer.parseInt(currentInstruction.substring(8,12),2);
					int location = Integer.parseInt(currentInstruction.substring(12),2); 
					if(register == 0){
						if(!mem.writeInstruction(reg1,location)) error(errors.MEM_WRITE_ERROR); 
					}
					else if(register == 1){
						if(!mem.writeInstruction(reg2,location)) error(errors.MEM_WRITE_ERROR); 
					}
					else error(errors.INVALID_REGISTER); 
				}
				catch (Exception e){
					error(errors.INVALID_OPERANDS_NaN); 
				}
			}
			
			else if(currentInstruction.substring(4,9).charAt(0) == '0' &&
					currentInstruction.substring(4,9).charAt(1) == '1' &&
					currentInstruction.substring(4,9).charAt(2) == '1' &&
					currentInstruction.substring(4,9).charAt(3) == '1' 
			){
				//Read from the operand register. 
				try{
					int register = Integer.parseInt(currentInstruction.substring(8,12),2);
					int location = Integer.parseInt(currentInstruction.substring(12),2); 
					if(register == 0) mem.writeOperand(oreg1, location); 
					else if(register == 1) mem.writeOperand(oreg2, location);
					else error(errors.INVALID_REGISTER); 
				}
				
				catch(Exception e){
					error(errors.INVALID_OPERANDS_NaN); 
				}
				
			}
		}
		
		else{
			
			if(currentInstruction.substring(4,9).charAt(0) == '0' &&
			   currentInstruction.substring(4,9).charAt(1) == '0' &&
			   currentInstruction.substring(4,9).charAt(2) == '1' &&
			    currentInstruction.substring(4,9).charAt(3) == '1' 
					){
						//Write to the Instruction Register. 
						try{
							int register = Integer.parseInt(currentInstruction.substring(8,12),2);
							int location = Integer.parseInt(currentInstruction.substring(12),2); 
							if(register == 0) reg1 = mem.readInstruction(location); 
							
							else if(register == 1) reg2 = mem.readInstruction(location); 
							
							else error(errors.INVALID_REGISTER); 
						}
						catch (Exception e){
							error(errors.INVALID_OPERANDS_NaN); 
						}
					}
					
					else if(currentInstruction.substring(4,9).charAt(0) == '0' &&
							currentInstruction.substring(4,9).charAt(1) == '1' &&
							currentInstruction.substring(4,9).charAt(2) == '1' &&
							currentInstruction.substring(4,9).charAt(3) == '1' 
					){
						//Write to the operand register. 
						try{
							int register = Integer.parseInt(currentInstruction.substring(8,12),2);
							int location = Integer.parseInt(currentInstruction.substring(12),2); 
							if(register == 0) oreg1 =  mem.readOperand(location); 
							else if(register == 1) oreg2 = mem.readOperand(location);
							else error(errors.INVALID_REGISTER); 
						}
						
						catch(Exception e){
							error(errors.INVALID_OPERANDS_NaN); 
						}
						
					}
			
		}
		
	}




}
