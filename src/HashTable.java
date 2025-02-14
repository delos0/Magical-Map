public class HashTable<E> {
    // Inner static class to represent an entry in the hash table
    private static class HashEntry<E> {
        public E element; // The actual element stored in the hash table
        public boolean isActive; // Indicates whether the entry is active (not deleted)

        public HashEntry(E element) {
            this(element, true);
        }

        public HashEntry(E element, boolean i) {
            this.element = element;
            isActive = i;
        }
    }

    private HashEntry<E>[] list; // Array to hold hash table entries
    private int currentSize; // Number of active elements in the hash table
    private static final int TABLE_SIZE = 1000003; // Default size of the hash table

    public HashTable() {
        this(TABLE_SIZE);
    }

    public HashTable(int size) {
        allocateArray(size); //
        makeEmpty();
    }

    // Inserts an element into the hash table
    public boolean insert(E key) {
        int currentPos = findPos(key); // Find the position for the key
        if (isActive(currentPos)) return false; // If already present, do not insert

        // Insert the new entry at the determined position
        list[currentPos] = new HashEntry<>(key, true);

        // If the table is more than half full, rehash to expand capacity
        if (++currentSize > list.length / 2) rehash();
        return true;
    }

    // Retrieves an element from the hash table
    public E get(E key) {
        int position = findPos(key);
        if (isActive(position)) {
            return list[position].element;
        }
        return null; // Return null if the key is not found
    }

    // Removes an element from the hash table
    public boolean remove(E key) {
        int currentPos = findPos(key); // Find the position of the key
        if (!isActive(currentPos)) return false; // If not active, cannot remove
        list[currentPos].isActive = false; // Mark the entry as inactive (deleted)
        return true;
    }

    // Checks if the hash table contains a specific key
    public boolean contains(E key) {
        int currentPos = findPos(key); // Find the position of the key
        return isActive(currentPos); // Return true if the position is active (not deleted)
    }

    // Clears the hash table, removing all elements
    public void makeEmpty() {
        currentSize = 0; // Reset the size
        for (int i = 0; i < list.length; i++) {
            list[i] = null; // Set each entry to null
        }
    }

    // Allocates an array for the hash table with a specified size
    private void allocateArray(int arraySize) {
        list = new HashEntry[nextPrime(arraySize)]; // Ensure array size is a prime number
    }

    // Checks if a position in the hash table is active (not deleted)
    private boolean isActive(int currentPos) {
        return list[currentPos] != null && list[currentPos].isActive;
    }

    // Finds the position of a key in the hash table using linear probing
    private int findPos(E key) {
        int currentPos = myHash(key); // Compute the hash value
        while (list[currentPos] != null && !list[currentPos].element.equals(key)) {
            currentPos += 1; // Move to the next position
            if (currentPos >= list.length) currentPos -= list.length; // Wrap around
        }
        return currentPos;
    }

    // Rehashes the hash table by expanding its size and re-inserting all active elements
    private void rehash() {
        HashEntry<E>[] oldList = list; // Save the current list
        allocateArray(nextPrime(oldList.length)); // Allocate a larger array
        currentSize = 0; // Reset the size

        // Reinsert all active elements into the new array
        for (HashEntry<E> eHashEntry : oldList) {
            if (eHashEntry != null && eHashEntry.isActive) insert(eHashEntry.element);
        }
    }

    // Hash function to compute the hash value for a given key
    private int myHash(E key) {
        int hashKey = key.hashCode(); // Get the hash code of the key
        hashKey %= list.length; // Ensure it fits within the array bounds
        if (hashKey < 0) hashKey += list.length; // Handle negative hash codes
        return hashKey;
    }

    public static int nextPrime(int n) {
        if (n <= 1) return 2;
        int prime = n;
        boolean found = false;

        while (!found) {
            prime++;
            if (isPrime(prime)) found = true;
        }
        return prime;
    }

    public static boolean isPrime(int n) {
        if (n <= 1) return false;
        for (int i = 2; i <= (int) Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    // Prints all active elements in the hash table
    public void print() {
        for (HashEntry<E> eHashEntry : list) {
            if (eHashEntry != null && eHashEntry.isActive) {
                System.out.println(eHashEntry.element);
            }
        }
    }
}
