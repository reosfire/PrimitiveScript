fun main() {
    this.println("Enter number of elements: ")
    var numElements = this.int(this.readln())

    var numbers = new.list()

    var i = 0
    while (i.less(numElements)) {
        this.println("Enter number: ")
        var num = this.int(this.readln())
        numbers.add(num)
        i.increment()
    }

    this.println("Sorted numbers using quick sort: ")
    this.quickSort(numbers, 0, numbers.size().minus(1))
    var j = 0
    while (j.less(numbers.size())) {
        this.println(numbers.get(j))
        j.increment()
    }

    this.println("Sorted numbers using bubble sort:")
    this.bubbleSort(numbers)
    var k = 0
    while (k.less(numbers.size())) {
        this.println(numbers.get(k))
        k.increment()
    }

    this.println("Enter number to binary search: ")
    var target = this.int(this.readln())
    var result = this.binarySearch(numbers, 0, numbers.size().minus(1), target)
    if (result.equal(-1)) {
        this.println("Number not found.")
    }
    if (result.equal(-1).not()) {
        this.print("Number found at index: ")
        this.println(result)
    }
}

fun bubbleSort(numbers) {
    var n = numbers.size()
    var i = 0
    while (i.less(n)) {
        var j = 0
        while (j.less(n.minus(i).minus(1))) {
            if (numbers.get(j).greater(numbers.get(j.plus(1)))) {
                var temp = numbers.get(j)
                numbers.set(j, numbers.get(j.plus(1)))
                numbers.set(j.plus(1), temp)
            }
            j.increment()
        }
        i.increment()
    }
}

fun quickSort(numbers, left, right) {
    if (left.greaterOrEqual(right)) {
        return void
    }

    var pivotIndex = this.partition(numbers, left, right)
    this.quickSort(numbers, left, pivotIndex.minus(1))
    this.quickSort(numbers, pivotIndex.plus(1), right)
}

fun partition(numbers, left, right) {
    var pivot = numbers.get(right)
    var i = left.minus(1)
    var j = new.int(left)

    while (j.less(right)) {
        if (numbers.get(j).lessOrEqual(pivot)) {
            i.increment()
            var temp = numbers.get(i)
            numbers.set(i, numbers.get(j))
            numbers.set(j, temp)
        }
        j.increment()
    }

    var temp = numbers.get(i.plus(1))
    numbers.set(i.plus(1), numbers.get(right))
    numbers.set(right, temp)

    return i.plus(1)
}

fun binarySearch(numbers, left, right, target) {
    if (left.greater(right)) {
        return -1
    }

    var mid = left.plus(right).divide(2)
    if (numbers.get(mid).equal(target)) {
        return mid
    }
    if (numbers.get(mid).greater(target)) {
        return this.binarySearch(numbers, left, mid.minus(1), target)
    }
    return this.binarySearch(numbers, mid.plus(1), right, target)
}
