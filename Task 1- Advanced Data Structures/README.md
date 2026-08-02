# Power-of-Two Max Heap

A max-heap in which every node has exactly `2^branchingExponent` children, generalizing
the classic binary heap to an arbitrary power-of-two branching factor.

## Project Overview

This project implements `PowerOfTwoMaxHeap`, an array-backed max-heap whose branching
factor is configurable at construction time as `2^k`. Setting `k = 1` reproduces a
standard binary heap; other values trade tree height against per-level work, which is
useful when the cost of a comparison is cheap relative to the cost of traversing levels
(e.g. cache-friendly wide heaps for large in-memory datasets).

The heap supports `insert` and `popMax` as core operations, plus `peekMax`, `size`, and
`isEmpty` for standard collection-like usability. It is backed by a primitive `int[]` to
avoid autoboxing overhead, and is not thread-safe.

## Problem Statement

Implement a max-heap data structure where:
- The max-heap property holds (every parent ≥ all of its descendants).
- Every internal node has `2^k` children, where `k` is supplied by the caller.
- `insert` and `popMax` are the primary operations, implemented for high performance.
- The solution generalizes the binary heap without hardcoding a branching factor of 2.

## Design Approach

The heap is stored as a flat, 0-indexed array in level order, exactly like a binary
heap — the only difference is how parent/child relationships are computed. Each node
`i` reserves a contiguous block of `b = 2^k` slots for its children immediately
following the slots used by nodes `0..i`. `insert` appends to the end of the array and
bubbles the new value up; `popMax` swaps the root with the last element, shrinks the
array logically, and bubbles the new root down. Both operations are iterative (no
recursion) to avoid stack overhead on large heaps.

Because `b` is always a power of two, index arithmetic (`× b`, `÷ b`) is implemented
with bit shifts rather than multiplication or division.

## Heap Index Formulas

For a node at index `i` with branching factor `b = 2^k`:

| Relationship | Formula | Bit-shift form |
|---|---|---|
| First child  | `i·b + 1` | `(i << k) + 1` |
| Last child   | `firstChild(i) + b - 1`, capped at `size - 1` | — |
| Parent       | `⌊(i - 1) / b⌋` | `(i - 1) >> k` |

**Derivation:** node `i` reserves a contiguous block of `b` child slots starting right
after all slots claimed by nodes `0..i`, so its children occupy
`[i·b + 1, i·b + b]`. Solving `p·b + 1 ≤ i ≤ p·b + b` for `p` gives the parent formula.
Since `b` is a power of two, `× b` / `÷ b` become `<<` / `>>`.

## Time Complexity

| Operation | Complexity | Notes |
|---|---|---|
| `insert`    | O(log_b n) | One comparison per level while bubbling up. |
| `popMax`    | O(b · log_b n) | Each of the O(log_b n) levels scans up to `b` children to find the new maximum. |
| `peekMax`   | O(1) | |
| `size` / `isEmpty` | O(1) | |

Larger `k` shortens the tree but widens the per-level scan in `popMax` — the two
operations trade off against each other as the branching factor grows.

## Space Complexity

O(n) for `n` stored elements, using a single `int[]` with amortized-O(1) doubling
growth (no per-element object overhead, no boxing).

## Design Decisions

- **`int[]` over a boxed generic type** — avoids autoboxing on every comparison and
  swap, in line with the requirement for a high-performance implementation. Trade-off:
  the heap only stores primitive `int` values.
- **Branching exponent capped at 30** — `1 << 31` overflows the sign bit of a Java
  `int`, so 30 is the highest exponent for which the branching factor itself is a valid
  positive `int`. This is a hard consequence of `int` representation, not a tuning
  heuristic.
- **Child-index range computed in `long`** — `firstChild + branchingFactor - 1` can
  exceed `Integer.MAX_VALUE` for large exponents even when the index itself is small
  and valid; the bound is computed in `long` and narrowed to `int` only once proven
  within `size`.
- **Iterative, not recursive, bubble-up/down** — avoids call-stack depth concerns on
  very large heaps.
- **Capacity-hint constructor** — an overload accepting an initial capacity avoids
  repeated grow-and-copy when the expected element count is known, mirroring
  `ArrayList(int)`.
- **Minimal public API** — only `insert`, `popMax`, `peekMax`, `size`, and `isEmpty`
  are exposed; internal helpers (`bubbleUp`, `bubbleDown`, index math, `swap`,
  `ensureCapacity`) are private.

## Edge Cases Considered

- Popping or peeking an empty heap (`NoSuchElementException`).
- Single-element heap.
- Zero exponent (`k = 0`): each node has exactly one child, degenerating to a
  heap-ordered chain with O(n) height.
- Maximum supported exponent (`k = 30`) with only a handful of elements — the overflow
  scenario described above.
- Exact child-block boundaries (`branchingFactor` and `branchingFactor + 1` elements),
  exercising the transition to a second tree level.
- Duplicate and negative values.
- Ascending, descending, and random insertion order.
- Backing-array growth across the resize boundary.
- Reuse of a heap after it has been fully drained.
- Invalid constructor arguments (negative or out-of-range exponent, non-positive
  initial capacity).

## How to Compile

```bash
javac PowerOfTwoMaxHeap.java Main.java
```

## How to Run

```bash
java Main
```

This runs the full test suite (90 assertions) and prints a pass/fail summary line for
each check, ending with an overall `SUMMARY` line.

## Example Usage

```java
PowerOfTwoMaxHeap heap = new PowerOfTwoMaxHeap(2); // branching factor 4

heap.insert(15);
heap.insert(3);
heap.insert(42);
heap.insert(8);

System.out.println(heap.peekMax()); // 42
System.out.println(heap.popMax());  // 42
System.out.println(heap.popMax());  // 15
System.out.println(heap.size());    // 2
```
