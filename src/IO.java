/**
 * 16 bit computer emulator 
 * 
 * Bryan Perino
 * 
 * 12/7/2012 
 * 
 * 
 */


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class IO implements Errors{
	
	public static ArrayList<String> instructions = new ArrayList<String>(); 
	public static Memory main;
	public static CPU cpu; 
	private boolean stackTrace = false; //Change this if you want to see the stack trace on errors.
	private static int oldLocation = 0; 
	
	public static void main(String[] args){
		main = new Memory();
		cpu = new CPU(main, new IO()); 
		init(); 
		main.setBus(cpu); 
	}
	
	//Initializes the parsing. 
	public static void init(){
		System.out.println("Please enter the file name of the program: ");
		Scanner in = new Scanner(System.in);
		String fileString = in.nextLine(); 
		try {
			BufferedReader file = new BufferedReader(new FileReader(fileString));
			parse(file); 
		} catch (FileNotFoundException e) {
			System.err.println("File was not found"); 
			e.printStackTrace();
		}
		
		//When parsing through the file is complete, start to execute the instructions.
		 
		cpu.start();  
		 
	}
	
	
	//Adds the instructions to the arraylist
	public static void parse(BufferedReader file){
		String input; 
		try {
			while((input = file.readLine()) != null){
				instructions.add(input);
			}
			memPlace(instructions, -1); 
		} catch (IOException e) {
			
			e.printStackTrace();
		}
			
	}
	
	//Checks the type code to see if it needs to decide memory placement. 
	public static void memPlace(ArrayList<String> instructions, int start){
		main.erase(); 
	
		//System.out.println("Value of count: " + count + " Value of start " + start); 
		if(start == -1) start = 0; 
		
		
		//System.out.println("Value of start: " + start); 
		for(int i = start; i < instructions.size(); i++){
			 
			String line = instructions.get(i);
			//System.out.println("Line being processed " + line);
			if(line.charAt(0) != '#'){
				if(line.length() > 16){
					line = line.substring(0,16); 
				}
			
			if(line.charAt(4) == '0' && line.charAt(5) == '0' && line.charAt(6) == '0' && line.charAt(7) == '0'){
				//Take the entire instruction and place it in one memory location without 
				// worrying about the next memory location. 
				main.writeInstruction(line.substring(0,8)); 
				
			}
			
			else if (line.charAt(4) == '0' && line.charAt(5) == '0' && line.charAt(6) == '0' && line.charAt(7) == '1'){
				//Place the first 8 characters of the instruction into the first memory location
				// and the second half in the next memory location. 
				main.writeInstruction(line.substring(0,8)); //Sends the first half of the instructions
				main.writeInstruction(line.substring(8)); //Sends the second half of the instructions. 
					
			}
			
			else if(line.charAt(4) == '0' && line.charAt(5) == '0' && line.charAt(6) == '1' && line.charAt(7) == '1'){
				main.writeInstruction(line.substring(0,8)); //Sends the first half of the instructions
				main.writeInstruction(line.substring(8)); //Sends the second half of the instructions.
			}
		
			else if(line.charAt(4) == '0' && line.charAt(5) == '1' && line.charAt(6) == '1' && line.charAt(7) == '1'){
				main.writeInstruction(line.substring(0,8)); //Sends the first half of the instructions
				main.writeInstruction(line.substring(8)); //Sends the second half of the instructions.
			}
				
			else cpu.error(errors.INVALID_TYPE_CODE); //If the type code is not correct, then it will throw an error.  
				
				
			} 
			
		
		}
	}
	
	//Handles the displaying. 
	public void display(String line, errors Errors){
		
		if(Errors != null){
			StackTraceElement[] elm = Thread.currentThread().getStackTrace(); 
			
			if(stackTrace){
				for(StackTraceElement e : elm){
					System.out.println(e.toString()); 
				}
			} 
			
			System.out.println("There was an error " + Errors + " with the instruction: " + line); 	
		}
		
		else{
			
		
			for(int i = 0; i < 14; i++){
				if(i == 4) System.out.print('O');
				else if(i == 5) System.out.print('U');
				else if(i == 6) System.out.print('T');
				else if(i == 7) System.out.print('P');
				else if(i == 8) System.out.print('U');
				else if(i == 9) System.out.print('T');
				else{
					System.out.print('_'); 
				}
			
			
			}
			System.out.println('\n' + line);
		}
	} 

	public void reset(){
		int location = oldLocation; 
		for(int i = oldLocation + 1; i < instructions.size(); i++){
			String line = instructions.get(i);
			
			if(line.substring(0,8).equals("01000000")){
			   location = i; 
			   //System.out.println("OldLocation: " + oldLocation + " i: " + i); 
			   break; 
			} 
		}
		oldLocation = location; 
		 
		memPlace(instructions, location + 1); 
		cpu.initCPU(); 
		cpu.start(); 
	}
	

}
