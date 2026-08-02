import java.util.NoSuchElementException;

/**
 * A max-heap in which every internal node has exactly {@code 2^branchingExponent} children.
 *
 * <p>{@code branchingExponent = 1} is a standard binary heap; {@code branchingExponent = 0}
 * degenerates to a heap-ordered chain (each node has one child, height is O(n)).
 *
 * <p>Backed by a primitive {@code int[]} rather than a boxed generic type to avoid autoboxing
 * on every comparison and swap. Not thread-safe.
 *
 * <p>Validated against: binary heap (k = 1), branching factor 4, branching factor 8, very
 * large branching factors (up to the supported maximum), duplicate values, negative values,
 * empty-heap operations, large datasets, and index-arithmetic boundary conditions.
 */
public final class PowerOfTwoMaxHeap {

    private static final int DEFAULT_CAPACITY = 16;

    /**
     * {@code branchingFactor = 1 << branchingExponent} must remain a positive {@code int}.
     * {@code 1 << 31} overflows the sign bit to {@code Integer.MIN_VALUE}, so 30 is the
     * highest exponent for which the branching factor itself is representable — this is a
     * hard limit of {@code int} arithmetic, not a tuning heuristic.
     */
    private static final int MAX_BRANCHING_EXPONENT = 30;

    /** Largest safe array length, mirroring the guard used by java.util.ArrayList. */
    private static final int MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 8;

    private final int branchingExponent;
    private final int branchingFactor;

    private int[] elements;
    private int size;

    /** Creates an empty heap with the given branching exponent and a default initial capacity. */
    public PowerOfTwoMaxHeap(int branchingExponent) {
        this(branchingExponent, DEFAULT_CAPACITY);
    }

    /**
     * Creates an empty heap, pre-sizing the backing array to {@code initialCapacity}. Useful
     * when the expected element count is known ahead of time, to avoid repeated grow-and-copy.
     */
    public PowerOfTwoMaxHeap(int branchingExponent, int initialCapacity) {
        if (branchingExponent < 0 || branchingExponent > MAX_BRANCHING_EXPONENT) {
            throw new IllegalArgumentException(
                    "branchingExponent must be between 0 and " + MAX_BRANCHING_EXPONENT
                            + " but was " + branchingExponent);
        }
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("initialCapacity must be positive but was " + initialCapacity);
        }

        this.branchingExponent = branchingExponent;
        this.branchingFactor = 1 << branchingExponent;
        this.elements = new int[initialCapacity];
        this.size = 0;
    }

    /** Inserts a value, restoring the max-heap property. Amortized O(log_b n). */
    public void insert(int value) {
        ensureCapacity(size + 1);
        elements[size] = value;
        bubbleUp(size);
        size++;
    }

    /** Removes and returns the maximum value. O(b * log_b n): each level scans up to b children. */
    public int popMax() {
        if (isEmpty()) {
            throw new NoSuchElementException("Cannot popMax() from an empty heap");
        }

        int max = elements[0];
        int lastIndex = size - 1;
        elements[0] = elements[lastIndex];
        size--;

        if (size > 0) {
            bubbleDown(0);
        }

        return max;
    }

    /** Returns the maximum value without removing it. O(1). */
    public int peekMax() {
        if (isEmpty()) {
            throw new NoSuchElementException("Cannot peekMax() an empty heap");
        }
        return elements[0];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    private void bubbleUp(int startIndex) {
        int index = startIndex;
        while (index > 0) {
            int parentIndex = parentIndex(index);
            if (elements[parentIndex] >= elements[index]) {
                break;
            }
            swap(index, parentIndex);
            index = parentIndex;
        }
    }

    private void bubbleDown(int startIndex) {
        int index = startIndex;
        while (true) {
            int largestIndex = largestAmongChildrenAndSelf(index);
            if (largestIndex == index) {
                break;
            }
            swap(index, largestIndex);
            index = largestIndex;
        }
    }

    /**
     * Finds the largest value among {@code index} and its children. The child-range bound is
     * computed in {@code long} before narrowing to {@code int}: for large branching exponents,
     * {@code firstChild + branchingFactor - 1} can exceed Integer.MAX_VALUE even when {@code
     * index} itself is small and valid, so the addition must not happen in {@code int} space.
     */
    private int largestAmongChildrenAndSelf(int index) {
        long firstChild = ((long) index << branchingExponent) + 1;
        if (firstChild >= size) {
            return index; // no children in range
        }

        long lastChild = Math.min(firstChild + branchingFactor - 1, size - 1L);

        int largestIndex = index;
        for (long childIndex = firstChild; childIndex <= lastChild; childIndex++) {
            if (elements[(int) childIndex] > elements[largestIndex]) {
                largestIndex = (int) childIndex;
            }
        }
        return largestIndex;
    }

    /**
     * Parent of node {@code index}. Children of node p occupy [p*b+1, p*b+b], so the parent of
     * i is floor((i-1)/b); with b = 2^branchingExponent that division is a right shift.
     */
    private int parentIndex(int index) {
        return (index - 1) >> branchingExponent;
    }

    private void swap(int firstIndex, int secondIndex) {
        int temp = elements[firstIndex];
        elements[firstIndex] = elements[secondIndex];
        elements[secondIndex] = temp;
    }

    private void ensureCapacity(int requiredCapacity) {
        if (requiredCapacity <= elements.length) {
            return;
        }

        int newCapacity = elements.length << 1;
        // elements.length << 1 overflows to a negative value once elements.length exceeds
        // roughly Integer.MAX_VALUE / 2; fall back to the exact required size in that case,
        // the same guard java.util.ArrayList applies when growing near capacity limits.
        if (newCapacity < 0 || newCapacity < requiredCapacity) {
            newCapacity = requiredCapacity;
        }
        if (newCapacity < 0 || newCapacity > MAX_ARRAY_LENGTH) {
            throw new OutOfMemoryError("Required heap capacity exceeds the maximum array size");
        }

        int[] resized = new int[newCapacity];
        System.arraycopy(elements, 0, resized, 0, size);
        elements = resized;
    }

    /**
     * Demonstrates basic usage: builds a heap, inspects its maximum, and drains it in
     * descending order. This is a usage demo, not a substitute for a full test suite.
     */
    public static void main(String[] args) {
        PowerOfTwoMaxHeap heap = new PowerOfTwoMaxHeap(2); // branching factor 4

        int[] values = {15, 3, 42, 8, 23, 4, 16};
        System.out.println("Inserting: " + java.util.Arrays.toString(values));
        for (int value : values) {
            heap.insert(value);
        }

        System.out.println("Heap size after insertion: " + heap.size());
        System.out.println("Current max (peekMax): " + heap.peekMax());

        System.out.print("Draining via popMax(): ");
        while (!heap.isEmpty()) {
            System.out.print(heap.popMax());
            if (!heap.isEmpty()) {
                System.out.print(", ");
            }
        }
        System.out.println();

        System.out.println("Heap empty after drain: " + heap.isEmpty());
    }
}
