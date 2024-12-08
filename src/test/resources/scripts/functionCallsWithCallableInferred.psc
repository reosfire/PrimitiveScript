fun main() {
    b()
    b().c()
    b(c())
    b(c().d())
    b(c(), d())
    b(c().d(), e().f())
}
