My name is Caleb Ott and this is a project I created at Neumont University as part of the Programming Languages course, taught by Steve Halladay.

The project was created in Intellij and uses Java 1.8-ea.

The project consists of an Assembler and a Compiler. Both the programming language and the assembly language uses my own syntax, but is fairly easy to understand. The compiler syntax is based completely off a grammar defined in 'Compiler Grammar.docx'.

The Compiler takes in an input file and compiles into an assembly file, which is passed into the Assembler which assembles into machine code in the 'kernel.img' file. This is machine code for the Raspberry PI, which follows the [ARM Instruction Set](http://cseweb.ucsd.edu/~kastner/cse30/arm-instructionset.pdf). The machine code assembled is designed to run raw on the Raspberry PI. It is in fact meant to run as the Operating System, so all compiled code that is written should not return from the 'main()' method.
