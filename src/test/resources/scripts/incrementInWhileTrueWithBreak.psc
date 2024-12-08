fun main(iterations) {
    var i = 0
    while (true) {
        if (i.less(iterations).not()) {
            break
        }
        i.increment()
    }
    return i
}