
/*
 * Automatic Subroutines
 */

Delay: // Delay(Amount)
PUSH { R0-R1 }
MOVI R0,0
PEEK R1,2
DelaySubroutineWait:
ADDI R0,R0,1
CMP R0,R1
BNE DelaySubroutineWait
POP { R0-R1 }
RET

Write: // Write(MemoryLoc, Value)
PUSH { R0-R1 }
PEEK R1,2 // value - second param
PEEK R0,3 // memory location - first param
STRI R1,R0,0
POP { R0-R1 }
RET

Read: // Read(MemoryLoc)
PUSH R1
PEEK R1,1 // memory location - first param
LDRI R0,R1,0
POP R1
RET
