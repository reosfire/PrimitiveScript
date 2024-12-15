fun binaryDoublePower(a, b) {
    if (b.equal(0)) {
        return 1.0
    }
    if (b.equal(1)) {
        return a
    }
    var result = new.double(binaryDoublePower(a, b.divide(2)))
    result.set(result.multiply(result))
    if (b.mod(2).equal(0)) {
        return result
    }
    return result.multiply(a)
}