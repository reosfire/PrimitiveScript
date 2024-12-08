fun main() {
    var inputText = new.File("input.txt").readText()
    var inputLines = inputText.split("\r\n")
    var n = inputLines.size()
    var m = inputLines.get(0).size()

    var result = 0

    var obstacleRow = 0
    while (obstacleRow.less(n)) {
        var obstacleCol = 0
        while (obstacleCol.less(m)) {
            if (inputLines.get(obstacleRow).get(obstacleCol).equal("#")) {
                 obstacleCol.increment()
                 continue
            }
            if (inputLines.get(obstacleRow).get(obstacleCol).equal("^")) {
                 obstacleCol.increment()
                 continue
            }
            if (this.isStuck(inputText, obstacleRow, obstacleCol)) {
                this.print(obstacleRow)
                this.print(" ")
                this.print(obstacleCol)
                this.println()

                result.increment()
            }
            obstacleCol.increment()
        }
        obstacleRow.increment()
    }

    this.println(result)
}

fun isStuck(inputText, obstacleRow, obstacleCol) {
    var inputLines = inputText.split("\n")
    inputLines.get(obstacleRow).replaceAt(obstacleCol, "#")

    var n = inputLines.size()
    var m = inputLines.get(0).size()

    var currentRow = 0
    var currentCol = 0

    var i = 0
    while (i.less(inputLines.size())) {
        var line = inputLines.get(i)
        var j = 0
        while (j.less(line.size())) {
            var char = line.get(j)
            if (char.equal("^")) {
                currentRow.set(i)
                currentCol.set(j)
            }
            j.increment()
        }
        i.increment()
    }

    var directionRow = -1
    var directionCol = 0

    var count = 0
    while (count.less(20000)) {
        var nextRow = currentRow.plus(directionRow)
        var nextCol = currentCol.plus(directionCol)

        if (nextRow.less(0).or(nextRow.greaterOrEqual(n)).or(nextCol.less(0)).or(nextCol.greaterOrEqual(m))) {
            return false
        }
        while (inputLines.get(nextRow).get(nextCol).equal("#")) {
            var newDirectionRow = this.rotateDirectionRow(directionRow, directionCol)
            var newDirectionCol = this.rotateDirectionCol(directionCol, directionRow)
            directionRow.set(newDirectionRow)
            directionCol.set(newDirectionCol)
            nextRow.set(currentRow.plus(directionRow))
            nextCol.set(currentCol.plus(directionCol))
        }

        currentRow.set(nextRow)
        currentCol.set(nextCol)

        count.increment()
    }

    return true
}

fun rotateDirectionRow(row, col) {
    if (row.equal(0).not()) {
        return 0
    }
    if (col.equal(1)) {
        return 1
    }
    return -1
}

fun rotateDirectionCol(col, row) {
    if (col.equal(0).not()) {
        return 0
    }
    if (row.equal(1)) {
        return -1
    }
    return 1
}
