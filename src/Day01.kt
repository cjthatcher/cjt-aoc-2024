import kotlin.math.abs

fun main() {

    /** Generates two lists of integers, and sorts them both from smallest to largest. Returns a List<Pair<Int>> representing the smallest from list A with the smallest from list B, etc.
     * */
    fun formatInput(input: List<String>): List<Pair<Int, Int>> {
        val firstList = input.map { (it.split("   ").first().toInt()) }.sorted().toList()
        val secondList = input.map { it.split("   ").last().toInt() }.sorted().toList()
        return firstList.zip(secondList)
    }

    fun part1(input: List<String>): Int {
        val pairs = formatInput(input)

        return pairs.stream().map { abs(it.first - it.second) }.reduce { t, u -> t + u }.get()
    }

    fun part2(input: List<String>): Int {
        val pairs = formatInput(input)
        // how many times does each number in the left list appear in the right list?
        val frequencyMap = mutableMapOf<Int, Int>()
        pairs.forEach { pair ->
            val key = pair.second
            frequencyMap.merge(key, 1) { a: Int, b: Int? -> a + (b ?: 0) }
        }

        return pairs.map { pair -> pair.first * (frequencyMap[pair.first] ?: 0) }.sum()
    }

    // Test if implementation meets criteria from the description, like:
    check(part1(listOf("1   3")) == 2)

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 11)

    // Read the input from the `src/Day01.txt.txt` file.
    val input = readInput("Day01")
    part1(input).println()

    check(part2(testInput) == 31)
    part2(input).println()
}
