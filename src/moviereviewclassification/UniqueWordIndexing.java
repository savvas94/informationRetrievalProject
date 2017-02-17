/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moviereviewclassification;

import java.util.HashMap;

/**
 * Keeps a unique representation from words to word indexes and the opposite. For any given word, there is only
 * one index and for any index there is only one word.
 * @author Savvas
 */
public final class UniqueWordIndexing {
    
    public static HashMap<String, Integer> wordToIndex= new HashMap<>();
    public static HashMap<Integer, String> indexToWord= new HashMap<>();
    public static int nextIndex = 0;
    
    private UniqueWordIndexing() {
    }
    
    public static Integer getIndex(String word) {
        Integer index = wordToIndex.get(word); //search for the word in the hashmap
        if(index == null) { //if not found, then put it with the next incremented index and return the assigned index.
            wordToIndex.put(word, nextIndex);
            indexToWord.put(nextIndex, word);
            return nextIndex++;
        }
        else { //if found, return its unique index.
            return index;
        }
    }
    
}
