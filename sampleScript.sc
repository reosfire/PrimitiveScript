fun runInDirection(direction) {
    var ifwhile = 1
    var a = 0
    var b = true
    var v = this.makeVec(10, 10)
    return a
}

fun noArgsFunction() {
    this.noArgsFunction()
    this.print(1)
}

fun recursiveLoop(i) {
    if (i.less(0)) {
        return void
    }

    this.println(i, "hello world")

    this.recursiveLoop(i.minus(1))
}

fun main1(someNumber) {
    var fifty = 50
    var i = 0
    while (true) {
        if (i.modulo(4).equals(0)) {
            this.println(i)
        }

        if (i.greater(fifty.plus(fifty))) {
            break
        }

        i.increment()
    }
}

fun main(currentPos, brd, result) {
    var i = 0

    while (i.lessThan(10)) {
        this.runInDirection(this.makeVec(-1, -2))
        i.set(i.plus(1))
    }

	if (currentPos.getRow().equals(2)) {
		result.set(currentPos.minus(this.makeVec(1, 0)), true)
		result.set(currentPos.minus(this.makeVec(2, 0)), true)
	}

	if (currentPos.getRow().notEquals(2)) {

	}
}