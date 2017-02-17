package moviereviewclassification;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This acts something like a Map from int to float
 *
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */
public class SparseList <T extends Comparable> {

    ArrayList<Integer> indexes;
    ArrayList<T> values;
    Comparator comparator = new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {
            T t1 = (T)o1;
            T t2 = (T)o2;
            return t1.compareTo(t2);
        }
    };

    public SparseList(int length) {
        this.indexes = new ArrayList<>(length);
        this.values = new ArrayList<>(length);
    }
    
    public SparseList(List<Integer> indexes, List<T> values) {
        
    }
    
    public void set(int index, T value) {
        //search to see if the index 
        int j = Collections.binarySearch(this.indexes, index, this.comparator);
        
        if (j >= 0) { //if there is a value for this index, then update it.
            this.values.set(j, value);
        }
        else {
            j = -j-1; //this is the position where the value should be inserted into.
            indexes.add(j, index);
            values.add(j, value);
        }
    }

    public int maxIndex() {
        return indexes.get(indexes.size() - 1);
    }
    
    /**
     * Returns the number of elements in this list.
     * @return the number of elements in this list
     */
    public int size() {
        return  indexes.size();
    }

    /**
     * Returns the element at the specified position in this list.
     * @param index index of the element to return
     * @return the element at the specified position in this list.
     */
    public T get(int index) {
        int j = Collections.binarySearch(this.indexes, index, this.comparator);
        if (j < 0) {
            return null;
        } else {
            return this.values.get(j);
        }
    }
    
    public List<Integer> getArrayPositions() {
        return this.indexes;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return "{length: " + this.indexes.size() + ", max index: " + this.maxIndex() + "}";
    }
    
    public void printSparseList() {
        for (int i = 0; i < this.indexes.size(); i++) {
            System.out.println(this.indexes.get(i));
        }
    }
}
