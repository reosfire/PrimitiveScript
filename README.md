# Yet another dynamically typed scripting language written for fun.

Can be usefull when you need to transfer some script over the web, but don't want to take some huge scripting languages like js or lua, and also prepared to write interpreter for it by yourself in language you need.

## Currently supports:
- creating script file with list of functions that can have:
  - **if** statements without else
  - **while** loops with **break** and **continue** inside
  - **var**iable declarations
  - invoking function call chains
  - **return**ing 
- making your own _CallableTypes_ in kotlin and provide them into the script via function parameters or global variables (**this** variable).

## More detaled description

### Variable declaration
`var i = <Evaluable>`

### Evaluable
Evaluable is either a compilation constant, variable name, or a function call chain

### Compilation constants(literals)
Currently language supports three types of "compilation time" constants:
- Boolean `true`, `false`
- Int `1`, `0`, `-1`...
- String `"Hello World!"`

### Variable name
Variables can be accessed just by name. Value of variable will be searched at runtime first in **local** then in **global memory**

### Function call
Function call is a language construct started with dot followed by function name and ended with **evaluable** parameters in parentheses.
`.foo()` `.bar("hi")`
Because function call accepts **evaluable** and **function call chain** is itsels evaluable, it's possible to do something like the following:
`this.foo(bar(123, this.foo()))`

### Function call chain
Function call chain is a sequence of tokens in the following form. </br>
It starts with a **compilation constant** or a **variable name** </br>
Then there is one of more **function call** chained. </br>
Each  _k_-th call except first will be called on _k - 1_-th call result. </br>

`this.foo().bar()`

### Memory
Runtime have two types of memory: local and global. </br>
- Local memory is the memory for functions local variables. Each **variable declaration** in function will be stored in its local memory. Each function call creates new local memory for the function and fills it with provided arguments.
- Global memory is global for all function calls and currently used to make **this** special variable available in every function without passing it down by hand.

### Function definition
Example: `fun <functionName>(parameter1, parameter2) { <body of the function> }` </br>
Each function in this language is attached to some CallableClass and can be called only on variable or constant. </br>
Parsed custom functions from script are attached in **this** special variable. </br>
For example this code calls function from example above:
`this.<functionName>("arg1", "arg2")`
