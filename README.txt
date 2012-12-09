16 bit computer emulator 

Instruction
<Op Code> <Type Code> < Data > 
<4 bits > < 4 bits  > <8 bits> 

Memory Limitation: 16 addressable spaces 
Operand Limitations: 2^4 = 16. 0 - 15 
Instruction Limitation: 16 bits maximum length, 8 bits minimum length. 

Registers: 
2 Instruction. Address 0 and 1
2 Operand. Address 0 and 1

Op Codes: 
0000 - Load <Operand> <Location>. Loads operands into a memory location
0001 - Pop  <NULL> <Location>. Pops off the stack after an math operation has occured. 
0011 - Add  <Location> <Location>. Adds the two operands in the memory locations specified together
1100 - Display <NULL> <Location>. Displays the contents of the memory location
0111 - HALT. Stops the program 
1111 - Move <Origin> <Destination>. Moves operands in memory locations 
1110 - Remove <NULL> <Location>. Removes an operand from a memory location
1001 - Subtract <Location> <Location>. Subtracts the two operands in the memory locations
1011 - Divide <Location> <Location>. Divides the two operands in the memory locations
1101 - Multiply <Location> <Location>. Multiplies the two operands in the memory locations
0101 - Read Register <Register> <Location>. Reads from either the operand or instrutction register. 
1010 - Write Register <Register> <Location>. Writes to either the operand or instruction regsister. 
0100 - CONTINUE. Signifies that there is more of the program. Useful for programs that are more than 16 lines. Wipes main memory, registers still retain their information. 


Type Codes: 
0001 - Broken up instruction. Tells the IO to break the instruction down into two 8 bit segments
0011 - Read/write to/from Instruction register
0111 - Read/Write to/from Operand register
