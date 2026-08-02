import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Manual test harness for {@link PowerOfTwoMaxHeap}. Each scenario drains the heap via
 * repeated {@code popMax()} and checks the output is non-increasing and matches a reference
 * sort of the same input.
 */
public final class Main {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testBinaryHeapCase();
        testBranchingFactorFour();
        testBranchingFactorEight();
        testLargeHeap();
        testDuplicateValues();
        testNegativeValues();
        testSingleElement();
        testEmptyHeapPop();
        testEmptyHeapPeek();
        testRandomInsertionOrder();
        testAscendingInsertion();
        testDescendingInsertion();
        testRepeatedPopMax();
        testZeroExponentChain();
        testWideBranchingFactor();
        testMaximumExponentWithFewElements();
        testExactChildBlockBoundary();
        testReuseAfterDraining();
        testInvalidConstructorArguments();
        testSizeAndIsEmptyTracking();

        System.out.println();
        System.out.println("=== SUMMARY: " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) {
            System.exit(1);
        }
    }

    // ------------------------------------------------------------------
    // Core scenarios required by spec
    // ------------------------------------------------------------------

    private static void testBinaryHeapCase() {
        verifyHeapAgainstReference("Branch factor = 2 (binary heap)", 1,
                new int[]{5, 3, 8, 1, 9, 2, 7, 4, 6, 0});
    }

    private static void testBranchingFactorFour() {
        verifyHeapAgainstReference("Branch factor = 4 (exponent 2)", 2,
                new int[]{10, 4, 15, 20, 3, 7, 8, 30, 1, 12, 25, 6});
    }

    private static void testBranchingFactorEight() {
        verifyHeapAgainstReference("Branch factor = 8 (exponent 3)", 3,
                new int[]{50, 20, 90, 10, 5, 70, 60, 30, 40, 80, 100, 15, 25, 35, 45, 55, 65, 75});
    }

    private static void testLargeHeap() {
        int elementCount = 100_000;
        Random random = new Random(42);
        int[] values = new int[elementCount];
        for (int i = 0; i < elementCount; i++) {
            values[i] = random.nextInt();
        }
        verifyHeapAgainstReference("Large heap (100,000 elements, exponent 3)", 3, values);
    }

    private static void testDuplicateValues() {
        verifyHeapAgainstReference("Duplicate values", 2,
                new int[]{7, 7, 7, 3, 3, 9, 9, 9, 9, 1, 1, 1, 1, 1});
    }

    private static void testNegativeValues() {
        verifyHeapAgainstReference("Negative values", 1,
                new int[]{-5, -1, -100, 0, -42, 17, -3, 8, -8});
    }

    private static void testSingleElement() {
        PowerOfTwoMaxHeap heap = new PowerOfTwoMaxHeap(2);
        heap.insert(99);
        check("Single element: size is 1", heap.size() == 1);
        check("Single element: isEmpty is false", !heap.isEmpty());
        check("Single element: peekMax is 99", heap.peekMax() == 99);
        check("Single element: popMax is 99", heap.popMax() == 99);
        check("Single element: isEmpty after pop", heap.isEmpty());
    }

    private static void testEmptyHeapPop() {
        PowerOfTwoMaxHeap heap = new PowerOfTwoMaxHeap(1);
        try {
            heap.popMax();
            check("Empty heap popMax throws NoSuchElementException", false);
        } catch (NoSuchElementException expected) {
            check("Empty heap popMax throws NoSuchElementException", true);
        }
    }

    private static void testEmptyHeapPeek() {
        PowerOfTwoMaxHeap heap = new PowerOfTwoMaxHeap(1);
        try {
            heap.peekMax();
            check("Empty heap peekMax throws NoSuchElementException", false);
        } catch (NoSuchElementException expected) {
            check("Empty heap peekMax throws NoSuchElementException", true);
        }
    }

    private static void testRandomInsertionOrder() {
        int elementCount = 5_000;
        Random random = new Random(7);
        int[] values = new int[elementCount];
        for (int i = 0; i < elementCount; i++) {
            values[i] = random.nextInt(1_000_000) - 500_000;
        }
        verifyHeapAgainstReference("Random insertion order (exponent 2)", 2, values);
    }

    private static void testAscendingInsertion() {
        int elementCount = 2_000;
        int[] values = new int[elementCount];
        for (int i = 0; i < elementCount; i++) {
            values[i] = i;
        }
        verifyHeapAgainstReference("Ascending insertion order (exponent 1)", 1, values);
    }

    private static void testDescendingInsertion() {
        int elementCount = 2_000;
        int[] values = new int[elementCount];
        for (int i = 0; i < elementCount; i++) {
            values[i] = elementCount - i;
        }
        verifyHeapAgainstReference("Descending insertion order (exponent 3)", 3, values);
    }

    private static void testRepeatedPopMax() {
        PowerOfTwoMaxHeap heap = new PowerOfTwoMaxHeap(2);
        int[] values = {4, 8, 15, 16, 23, 42};
        for (int value : values) {
            heap.insert(value);
        }
        int[] expected = {42, 23, 16, 15, 8, 4};
        boolean ok = true;
        for (int expectedValue : expected) {
            if (heap.popMax() != expectedValue) {
                ok = false;
                break;
            }
        }
        check("Repeated popMax drains in strictly correct max order", ok && heap.isEmpty());
    }

    // ------------------------------------------------------------------
    // Edge cases, including the overflow class of bug found in review
    // ------------------------------------------------------------------

    private static void testZeroExponentChain() {
        // branchingExponent = 0 => branching factor 1 => each node has exactly one child
        // (a heap-ordered chain). Still must satisfy the max-heap property end-to-end.
        verifyHeapAgainstReference("Zero exponent (branching factor 1, chain heap)", 0,
                new int[]{3, 1, 4, 1, 5, 9, 2, 6});
    }

    private static void testWideBranchingFactor() {
        int elementCount = 5_000;
        Random random = new Random(123);
        int[] values = new int[elementCount];
        for (int i = 0; i < elementCount; i++) {
            values[i] = random.nextInt();
        }
        verifyHeapAgainstReference("Wide branching factor (exponent 10, factor 1024)", 10, values);
    }

    private static void testMaximumExponentWithFewElements() {
        // Regression test for the overflow bug found during review: at the maximum supported
        // exponent, firstChildIndex(1) + branchingFactor - 1 overflows a 32-bit int even
        // though only a handful of elements are stored. This must not corrupt the heap.
        verifyHeapAgainstReference("Maximum exponent (30) with few elements", 30,
                new int[]{5, 2, 9, 1, 7});
    }

    private static void testExactChildBlockBoundary() {
        // Exercises the transition from "all children fit in one block" to "a second level
        // is needed", at exactly branchingFactor and branchingFactor + 1 elements.
        int branchingExponent = 2; // branching factor 4
        int branchingFactor = 1 << branchingExponent;

        int[] exactlyOneLevel = new int[branchingFactor + 1]; // root + one full block of children
        for (int i = 0; i < exactlyOneLevel.length; i++) {
            exactlyOneLevel[i] = i;
        }
        verifyHeapAgainstReference("Exact child-block boundary (root + full block)", branchingExponent,
                exactlyOneLevel);

        int[] oneLevelPlusOne = new int[branchingFactor + 2]; // spills into a second level
        for (int i = 0; i < oneLevelPlusOne.length; i++) {
            oneLevelPlusOne[i] = i;
        }
        verifyHeapAgainstReference("Child-block boundary + 1 (spills to second level)", branchingExponent,
                oneLevelPlusOne);
    }

    private static void testReuseAfterDraining() {
        // Confirms the heap remains usable after being fully drained, not just after a single
        // fill-then-drain cycle.
        PowerOfTwoMaxHeap heap = new PowerOfTwoMaxHeap(2);
        for (int value : new int[]{3, 1, 4}) {
            heap.insert(value);
        }
        while (!heap.isEmpty()) {
            heap.popMax();
        }

        for (int value : new int[]{10, 40, 20, 30}) {
            heap.insert(value);
        }
        int[] expected = {40, 30, 20, 10};
        boolean ok = true;
        for (int expectedValue : expected) {
            if (heap.popMax() != expectedValue) {
                ok = false;
                break;
            }
        }
        check("Heap is reusable after being fully drained", ok && heap.isEmpty());
    }

    private static void testInvalidConstructorArguments() {
        boolean rejectedNegative = false;
        try {
            new PowerOfTwoMaxHeap(-1);
        } catch (IllegalArgumentException expected) {
            rejectedNegative = true;
        }
        check("Constructor rejects negative branchingExponent", rejectedNegative);

        boolean rejectedTooLarge = false;
        try {
            new PowerOfTwoMaxHeap(31); // 1 << 31 overflows int's sign bit
        } catch (IllegalArgumentException expected) {
            rejectedTooLarge = true;
        }
        check("Constructor rejects branchingExponent above 30", rejectedTooLarge);

        boolean rejectedBadCapacity = false;
        try {
            new PowerOfTwoMaxHeap(2, 0);
        } catch (IllegalArgumentException expected) {
            rejectedBadCapacity = true;
        }
        check("Constructor rejects non-positive initial capacity", rejectedBadCapacity);
    }

    private static void testSizeAndIsEmptyTracking() {
        PowerOfTwoMaxHeap heap = new PowerOfTwoMaxHeap(2, 2); // tiny initial capacity forces resizing
        check("New heap isEmpty", heap.isEmpty());
        for (int i = 0; i < 10; i++) {
            heap.insert(i);
            check("size grows correctly after insert #" + (i + 1), heap.size() == i + 1);
        }
        for (int i = 10; i > 0; i--) {
            heap.popMax();
            check("size shrinks correctly, remaining=" + (i - 1), heap.size() == i - 1);
        }
        check("Heap isEmpty after draining", heap.isEmpty());
    }

    // ------------------------------------------------------------------
    // Shared verification helper
    // ------------------------------------------------------------------

    private static void verifyHeapAgainstReference(String label, int branchingExponent, int[] values) {
        PowerOfTwoMaxHeap heap = new PowerOfTwoMaxHeap(branchingExponent);
        for (int value : values) {
            heap.insert(value);
        }
        check(label + ": size after inserts matches input length", heap.size() == values.length);

        int[] expectedSorted = values.clone();
        Arrays.sort(expectedSorted);

        int[] actualDescending = new int[values.length];
        int previous = Integer.MAX_VALUE;
        boolean nonIncreasing = true;
        for (int i = 0; i < values.length; i++) {
            int max = heap.popMax();
            actualDescending[i] = max;
            if (max > previous) {
                nonIncreasing = false;
            }
            previous = max;
        }

        int[] actualAscendingForComparison = actualDescending.clone();
        reverse(actualAscendingForComparison);

        boolean matchesReference = Arrays.equals(expectedSorted, actualAscendingForComparison);
        check(label + ": popMax sequence is non-increasing", nonIncreasing);
        check(label + ": popMax sequence matches sorted reference", matchesReference);
        check(label + ": heap is empty after full drain", heap.isEmpty());
    }

    private static void reverse(int[] array) {
        for (int i = 0, j = array.length - 1; i < j; i++, j--) {
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    private static void check(String description, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("[PASS] " + description);
        } else {
            failed++;
            System.out.println("[FAIL] " + description);
        }
    }
}
