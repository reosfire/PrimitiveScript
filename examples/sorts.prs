fun main() {
    println("Enter number of elements: ")
    var numElements = int(readln())

    var numbers = new.list()

    var i = 0
    while (i.less(numElements)) {
        println("Enter number: ")
        var num = int(readln())
        numbers.add(num)
        i.increment()
    }

    println("Sorted numbers using quick sort: ")
    quickSort(numbers, 0, numbers.size().minus(1))
    var j = 0
    while (j.less(numbers.size())) {
        println(numbers.get(j))
        j.increment()
    }

    println("Sorted numbers using bubble sort:")
    bubbleSort(numbers)
    var k = 0
    while (k.less(numbers.size())) {
        println(numbers.get(k))
        k.increment()
    }

    println("Enter number to binary search: ")
    var target = int(readln())
    var result = binarySearch(numbers, 0, numbers.size().minus(1), target)
    if (result.equal(-1)) {
        println("Number not found.")
    }
    if (result.equal(-1).not()) {
        print("Number found at index: ")
        println(result)
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

    var pivotIndex = partition(numbers, left, right)
    quickSort(numbers, left, pivotIndex.minus(1))
    quickSort(numbers, pivotIndex.plus(1), right)
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
        return binarySearch(numbers, left, mid.minus(1), target)
    }
    return binarySearch(numbers, mid.plus(1), right, target)
}
