import java.nio.BufferUnderflowException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class BinaryHeap<E extends Comparable<? super E>> {
    Comparator<E> comparator;

    public BinaryHeap(int capacity, Comparator<E> comparator) {
        this.comparator = comparator;
        heap = (E[]) new Comparable[(capacity + 2) * 11 / 10];
        currentSize = 0;
    }

    // Constructor for building a heap from an ArrayList with a comparator
    public BinaryHeap(ArrayList<E> items, Comparator<E> comparator) {
        this.comparator = comparator;
        currentSize = items.size();
        heap = (E[]) new Comparable[(currentSize + 2) * 11 / 10];

        int i = 1;
        for (E item : items) {
            heap[i++] = item;
        }
        buildHeap(); // Rearranges elements to satisfy heap order
    }

    public boolean isEmpty() {
        return currentSize == 0;
    }

    // Inserts a new element into the heap
    public void insert(E element) {
        if (isFull()) {
            enlargeArray(heap.length * 2 + 1); // Double the array size if full
        }
        percolateUp(++currentSize, element); // Restore heap order after insertion
    }

    // Moves the element up to its correct position in the heap
    private void percolateUp(int hole, E element) {
        for (heap[0] = element; comparator.compare(element, heap[hole / 2]) < 0; hole /= 2) {
            heap[hole] = heap[hole / 2]; // Move parent down
        }
        heap[hole] = element;
    }

    // Returns the maximum element (at the root)
    public E findMax() {
        if (isEmpty()) throw new BufferUnderflowException();
        return heap[1];
    }

    // Removes and returns the maximum element
    public E deleteMax() {
        if (isEmpty()) throw new BufferUnderflowException();

        E max = findMax(); // Get the maximum element
        heap[1] = heap[currentSize--]; // Move the last element to the root
        percolateDown(1); // Restore heap order
        return max;
    }

    public boolean isFull() {
        return currentSize == heap.length - 1;
    }

    public int getCurrentSize() {
        return currentSize;
    }

    public void printHeap() {
        System.out.println(Arrays.toString(Arrays.copyOf(heap, currentSize + 1)));
    }

    private static final int DEFAULT_CAPACITY = 200001; // Default capacity
    private int currentSize; // Number of elements in the heap
    private E[] heap; // Array to store heap elements

    // Moves the element at 'hole' down to its correct position
    private void percolateDown(int hole) {
        int child;
        E temp = heap[hole];

        for (; hole * 2 <= currentSize; hole = child) {
            child = hole * 2;
            // Select the larger child
            if (child != currentSize && comparator.compare(heap[child + 1], heap[child]) < 0) {
                child++;
            }
            // If the larger child is greater than the current element, move it up
            if (comparator.compare(heap[child], temp) < 0) {
                heap[hole] = heap[child];
            } else {
                break; // Correct position found
            }
        }
        heap[hole] = temp;
    }

    // Rearranges elements to satisfy heap order
    private void buildHeap() {
        for (int i = currentSize / 2; i > 0; i--) {
            percolateDown(i);
        }
    }

    // Resizes the heap array to the specified new size
    private void enlargeArray(int newSize) {
        E[] newHeap = (E[]) new Comparable[(newSize + 2) * 11 / 10];
        for (int i = 1; i <= currentSize; i++) {
            newHeap[i] = heap[i];
        }
        heap = newHeap;
    }
}
