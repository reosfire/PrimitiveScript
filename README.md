# Yet another dynamically typed scripting language written for fun.

Can be useful when you need to transfer some script over the web, but don't want to take some huge scripting languages like js or lua, and also prepared to write interpreter for it by yourself in language you need.

## Currently supports next statements:
- creating script file with list of functions that can have:
  - **if** statements without else
  - **while** loops with **break** and **continue**
  - **var**-iable declarations
  - invoking function call chains
  - **return**-ing from functions
- making your own _CallableTypes_ in kotlin and provide them into the script via function parameters or global variables.

## More detailed description

### Variable declaration
`var i = <Evaluable>`

### Evaluable
Evaluable is either a compilation constant, a variable name, or a function call chain

### Compilation constants(literals)
- Boolean `true`, `false`
- Int `1`, `0`, `-1`
- Double `1.0`, `0.0`, `-1.0`
- String `"Hello World!"` String literals support escape sequences like `\n`, `\t`, `\\`, `\"`

### Variable name
Variables can be accessed by name. Value of variable will be searched at interpretation first in **local** then in **global memory**

### Function call
Function call is a language construct started with dot followed by function name and ended with **evaluable** parameters in parentheses.
Example:
`.foo()` `.bar("hi")`
Because function call accepts **evaluable** and **function call chain** is itsels evaluable, it's possible to do something like the following:
`this.foo(bar(123, this.foo()))`

### Function call chain
It starts with a **compilation constant** or a **variable name**.
Then there is one of more **function call** chained.
Each  _k_-th call except first will be called on _k - 1_-th call result. </br>
`this.foo().bar()` </br>

**this** variable name is provided by interpretation, so it's like any other global variable.
currently there is also **new** variable name that is used to create new instances of CallableTypes. </br>
However, **this** variable name can be inferred and omitted. For example this code is equivalent to the one above: </br>
`foo().bar()`

### Code block
Code block is a sequence of **statements** enclosed in curly braces. </br>

### If statement
It is started with if keyword followed by **evaluable** condition in round brackets and ended with code block in curly brackets.

### While loop
It is started with while keyword followed by **evaluable** condition in round brackets and ended with code block in curly brackets.

While loop can have **break** and **continue** statements inside it.


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
