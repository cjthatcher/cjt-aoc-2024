import java.util.function.BiFunction

fun main() {

    fun formatInput(input: List<String>): List<List<Int>> {
        return input.map {
            it.split(" ").map { inner -> inner.toInt() }
        }
    }

    fun isSafeCase(testValues: List<Int>): Boolean {
        var increasing = true
        testValues.windowed(2, 1).forEach { pair ->
            val difference = pair.last() - pair.first()
            if (difference < 1 || difference > 3) {
                increasing = false
            }
        }

        var decreasing = true
        testValues.windowed(2, 1).forEach { pair ->
            val difference = pair.first() - pair.last()
            if (difference < 1 || difference > 3) {
                decreasing = false
            }
        }

        return decreasing || increasing
    }

    fun canReachAscending(a: Int, b: Int): Boolean {
        val difference = b - a
        return difference in 1..3
    }

    fun canReachDescending(a: Int, b: Int): Boolean {
        val difference = a - b
        return difference in 1..3
    }


    fun isLooseSafeCase(testValues: List<Int>, reachable: (Int, Int) -> Boolean): Boolean {
        // the first and last elements are known to be omitable
        val skippables = mutableSetOf(0, testValues.size - 1)
        var skipCount = 0;
        var failure = false;

        testValues.withIndex().windowed(3, 1).forEach { args ->
            val a = args[0]
            val b = args[1]
            val c = args[2]

            val aToB = reachable(a.value, b.value)
            val aToC = reachable(a.value, c.value)
            val bToC = reachable(b.value, c.value)

            // There are 3 boolean variables here, so there are eight possible combinations
            // || A->B || A-> C || B-> C ||
            // || Yes  || Yes   || Yes   ||   --> Everything is fine, continue as normal. B is skippable if necessary (won't be)
            // || Yes  || Yes   || No    ||   --> B or C will need to be skipped. If we already have one skip count, we must terminate here.
            // || Yes  || No    || Yes   ||   --> Everything is fine, continue as normal.
            // || Yes  || No    || No    ||   --> If we have already used a skip, we must terminate here. We will have to skip B or C.
            // || No   || No    || No    ||   --> Death
            // || No   || No    || Yes   ||   --> Need to skip A. Is A Skippable? If not, death.
            // || No   || Yes   || No    ||   --> Need to skip B. It is skippable. Do we have a skip?
            // || No   || Yes   || Yes   ||   --> Need to skip A or B. Depending on who is skippable.

            if (aToC) {
                skippables.add(b.index)
            }

            if (aToB && aToC && bToC) {
                // no op. We're good.
            } else if (aToB && aToC && !bToC) {
                if (skipCount >= 1) {
                    // we have already skipped one, and we can't afford to skip the problem between B and C. This might be the last frame, so we have to terminate here.
                    failure = true
                }
            } else if (aToB && !aToC && bToC) {
                // no op! We're good! We'll check it out next frame.
            } else if (aToB && !aToC && !bToC) {
                if (skipCount >= 1) {
                    // We already skipped one, and now we know that we'll have to skip B or C. Since this might be the last frame, we have to terminate here.
                    failure = true
                };
            } else if (!aToB && !aToC && !bToC) {
                skipCount++;
                failure = true;
            } else if (!aToB && !aToC && bToC) {
                if (!skippables.contains(a.index)) {
                    failure = true;
                }
                skipCount++
            } else if (!aToB && aToC && !bToC) {
                // I don't need to check if B is in the skippable set, since I JUST put it there because aToC is true.
                if (skipCount >= 1) {
                    // we have already skipped one, and we're definitely going to need to skip B. This might be the last frame, so we have to terminate here.
                    failure = true
                }
            } else if (!aToB && aToC && bToC) {
                if (!skippables.contains(a.index) && !skippables.contains(b.index)) {
                    failure = true
                }
                // We could skip A, or we could skip B. If we've already skipped, we're boned. If we haven't skipped,
                skipCount++
            }

            if (failure || skipCount >= 2) {
                return false
            }
        }

        return true
    }

    fun part1(input: List<String>): Int {
        val testCases = formatInput(input)
        return testCases.count { testCase -> isSafeCase(testCase) }
    }

    fun part2(input: List<String>): Int {
        val testCases = formatInput(input)
        return testCases.count { testCase ->
            val safe = isLooseSafeCase(testCase, ::canReachAscending) || isLooseSafeCase(testCase, ::canReachDescending)
            println("Case " + testCase.joinToString(",", "", " ") + "was " + safe)
            isLooseSafeCase(testCase, ::canReachAscending) || isLooseSafeCase(testCase, ::canReachDescending)
        }
    }

    // Test if implementation meets criteria from the description, like:
    check(part1(listOf("7 6 4 2 1", "1 2 7 8 9")) == 1)

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 2)

    // Read the input from the `src/Day01.txt.txt` file.
    val input = readInput("Day02")
    part1(input).println()

    check(part2(testInput) == 4)
    check(part2(listOf("33 34 34 37 39 40 43 43")) == 0)
    part2(input).println()
}
