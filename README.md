My name is Caleb Ott and this is a project I created at Neumont University as part of the Programming Languages course, taught by Steve Halladay.

The project was created in Intellij and uses Java 1.8-ea.

The project consists of an Assembler and a Compiler. Both the programming language and the assembly language uses my own syntax, but is fairly easy to understand. The compiler syntax is based completely off a grammar defined in 'Compiler Grammar.docx'.

The Compiler takes in an input file and compiles into an assembly file, which is passed into the Assembler which assembles into machine code in the 'kernel.img' file. This is machine code for the Raspberry PI, which follows the [ARM Instruction Set](http://cseweb.ucsd.edu/~kastner/cse30/arm-instructionset.pdf). The machine code assembled is designed to run raw on the Raspberry PI. It is in fact meant to run as the Operating System, so all compiled code that is written should not return from the 'main()' method.

This was a project meant to learn about how programming languages work all the way down to the processor level. Though I don't plan on turning this into a real programming language and releasing it (obviously), I would love to hear any feedback on the code and ways I could improve it. Initially I did not care at all about efficiency, and I still don't care a whole lot about efficiency but I am up for hearing about how it could be more efficient.

Here is an interview with me by Jamie King explaining this project and the class - http://www.youtube.com/watch?v=YTDrL3GcKu8.
