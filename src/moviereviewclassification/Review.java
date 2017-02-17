/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moviereviewclassification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Savvas
 */
public class Review {

    private String fullText; //the full text of the review
    private String docid;
    private int rating;
    private SparseList<Integer> wordCount2; //the number of times each word appears. Each index addresses to one particular word.
    private HashMap<String, Integer> wordCount;

    /**
     * Creates a review object from a review text. The process followed is: 1.
     * Remove single quotes from the text. 2. Stem the text. 3. Split the text
     * into words. 4. Count the frequency of each word. 5. Create a sparse
     * vector with the word frequencies.
     *
     * @param text the full text representation of the review
     */
    public Review(String text, String docid, int rating) {
        this.fullText = text;
        this.docid = docid;
        this.rating = rating;

        /*
            1. Delete single quotes.
            2. Stem sentence.
            3. split words.
         */
        text = text.replaceAll("'", "");
        text = PorterStemmer.getStemmedSentence(text);
        String[] words = text.split("[\\W]"); // \W means any non-word character. (Word characters: [a-zA-Z_0-9])

        HashMap<String, Integer> stemmedWordCount = new HashMap<>();

        for (String word : words) {
            if (word.length() > 0) {
                Integer count = stemmedWordCount.get(word);
                if (count == null) {
                    stemmedWordCount.put(word, 1);
                }
                else {
                    stemmedWordCount.put(word, count + 1);
                }
            }
        }

        wordCount2 = new SparseList<>(stemmedWordCount.size());
        wordCount = new HashMap<>(stemmedWordCount.size());
        for (String stemmed : stemmedWordCount.keySet()) {
            int uindex = UniqueWordIndexing.getIndex(stemmed); //get the unique index created for this word
            wordCount2.set(uindex, stemmedWordCount.get(stemmed)); //add the word to the sparse vector of this review.
            wordCount.put(stemmed, stemmedWordCount.get(stemmed));
        }
    }

    /**
     * For this document, return the frequency of each term of the document in term-frequency pairs.
     * @return Pairs of term-frequency with the frequency of each term of the document
     */
    public DocumentTermsFrequencies getTermsFrequencies() {
        List<String> terms = new ArrayList<>(wordCount.size());
        List<Integer> frequencies = new ArrayList<>(wordCount.size());
        for (String word : wordCount.keySet()) {
            terms.add(word);
            frequencies.add(wordCount.get(word));
        }
        
        if(true) {
            return new DocumentTermsFrequencies(terms, frequencies);
        }
        List<Integer> arrayPositions = wordCount2.getArrayPositions();

        
        for (int i = 0; i < arrayPositions.size(); i++) {
            int position = arrayPositions.get(i);
            int frequency = wordCount2.get(position);
            String term = UniqueWordIndexing.indexToWord.get(position);
            terms.add(term);
            frequencies.add(frequency);
        }
        return new DocumentTermsFrequencies(terms, frequencies);
    }

    public String getFullText() {
        return this.fullText;
    }

    public String getDocid() {
        return this.docid;
    }

    public int getRating() {
        return this.rating;
    }
}
