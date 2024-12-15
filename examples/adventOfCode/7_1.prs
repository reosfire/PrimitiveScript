fun main() {
    var file = new.File("input.txt")
    var text = file.readText()
    var lines = text.split("\r\n")

    var result = new.long(0)

    var i = 0
    while (i.less(lines.size())) {
        var tokens = lines.get(i).split(": ")
        var target = new.long(tokens.get(0))
        var numbers = tokens.get(1).split(" ")

        var numbersCast = new.list()

        var j = 0
        while (j.less(numbers.size())) {
            numbersCast.add(new.long(numbers.get(j)))
            j.increment()
        }

        var isPossible = solve(numbersCast.get(0), 1, numbersCast, target)
        if (isPossible) {
            result.set(result.plus(target))
        }

        i.increment()
    }

    println(result)
}

fun solve(current, idx, numbers, target) {
    if (idx.equal(numbers.size())) {
        return current.equal(target)
    }

    if (solve(current.plus(numbers.get(idx)), idx.plus(1), numbers, target)) {
        return true
    }
    if (solve(current.multiply(numbers.get(idx)), idx.plus(1), numbers, target)) {
        return true
    }
    return false
}
