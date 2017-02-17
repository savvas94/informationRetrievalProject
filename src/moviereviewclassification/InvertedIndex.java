/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moviereviewclassification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

/**
 *
 * @author Savvas
 */
public class InvertedIndex {
    
    //the actual inverted index
    private HashMap<String, ArrayList<TermFrequencyInDocument>> invertedIndex;
    
    //a set with all the documents that appear in the inverted index. For each document, the value is the max frequency of its terms.
    private HashMap<String, DocumentInfo> allDocuments;
    
    //cached values for the last term that was requested, so that the hashmap is not searched every time
    private String cachedTerm = "";
    private ArrayList<TermFrequencyInDocument> cachedFrequencies = null;
    
    //comparator class for sorting the list of each term
    private final Comparator comparator = new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {
            TermFrequencyInDocument t1 = (TermFrequencyInDocument)o1;
            TermFrequencyInDocument t2 = (TermFrequencyInDocument)o2;
            return t1.compareTo(t2);
        }
    };
    
    public InvertedIndex() {
        this.invertedIndex = new HashMap<>();
        this.allDocuments = new HashMap<>();
    }
    
    /**
     * Updates the frequency list for the specified term with the given frequencies. The old list gets discarded.

     * This method does not update the length of the documents, but they should be updated.
     * 
     * @param term the term to update
     * @param termFrequencies the new frequency list of the term
     */
    public void updateTerm(String term, List<TermFrequencyInDocument> termFrequencies) {
        Collections.sort(termFrequencies); //sort the list to support binary search
        this.invertedIndex.put(term, new ArrayList<>(termFrequencies)); //add the frequency list in the inverted index
        for (int i = 0; i < termFrequencies.size(); i++) { //add all document ids to the set of all documents that appear on the inverted index.
            TermFrequencyInDocument termInDoc = termFrequencies.get(i);
            DocumentInfo docInfo = this.allDocuments.getOrDefault(termInDoc.getDocId(), new DocumentInfo(0, 0, 0));
            if(termInDoc.getFrequency() > docInfo.getMaxDocumentFrequency()) {
                docInfo.setMaxDocumentFrequency(termInDoc.getFrequency());
                this.allDocuments.put(termInDoc.getDocId(), docInfo);
            }
        }
        if(this.cachedTerm.equals(term)) { //if the term updated here is the current cached term, then update its cached list.
            this.cachedFrequencies = this.invertedIndex.get(term);
        }
    }
    
    /**
     * Add a new document for this term with the frequency of the term in the document.
     * 
     * This method does not update the length of the document, but it should be updated.
     * 
     * @param term the term for which to add a new document
     * @param docid the document which will be added
     * @param frequency the frequency of the term in the document
     */
    public void addDocumentForTerm(String term, String docid, int frequency) {
        TermFrequencyInDocument docFrequency = new TermFrequencyInDocument(docid, frequency);
        
        //get the list of the term
        ArrayList<TermFrequencyInDocument> freqList = invertedIndex.get(term);
        if(freqList == null) {
            freqList = new ArrayList<>();
        }
        
        //find the position of the document in the term's list
        int position = Collections.binarySearch(freqList, docFrequency, comparator);
        if(position < 0) { //search for the document in the list. If not found, add it. Else, update frequency.
            position = -position-1;
            freqList.add(position, docFrequency);
        }
        else {
            freqList.set(position, docFrequency);
        }
        
        //put the updated list in the hashmap
        this.invertedIndex.put(term, freqList);        
        if(this.cachedTerm.equals(term)) { //if the term updated here is the current cached term, then update its cached list.
            this.cachedFrequencies = freqList;
        }
        
        //update the max frequency for this document if necessary
        DocumentInfo docInfo = this.allDocuments.getOrDefault(docid, new DocumentInfo(0, 0, 0));
        if(frequency > docInfo.getMaxDocumentFrequency()) {
            docInfo.setMaxDocumentFrequency(frequency);
            this.allDocuments.put(docid, docInfo);
        }
    }
    
    /**
     * Inserts the frequencies for each term of a document and calculates the document's length. Each frequency corresponds
     * to the respectiive term of the same position of the other list.
     * @param docid the document whose terms will be updated
     * @param docFreqs the terms-frequencies pairs
     */
    public void insertDocument(Review r) {
        String docid = r.getDocid();
        
        //first add each term of the document in the inverted index. For each term, the term's list is updated to include this document.
        DocumentTermsFrequencies docTermsFrequencies = r.getTermsFrequencies();
        List<String> terms = docTermsFrequencies.getTerms();
        List<Integer> frequencies = docTermsFrequencies.getFrequencies();
        for (int i = 0; i < terms.size(); i++) {
            addDocumentForTerm(terms.get(i), docid, frequencies.get(i));
        }
        
        //calculate the length of the document with the eucledian distance and store the info for the document.
        double length = 0;
        DocumentInfo docInfo = this.allDocuments.get(docid); //get the max frequency for this document
        int maxFrequency = docInfo.getMaxDocumentFrequency();
        for (int i = 0; i < terms.size(); i++) {
            int frequency = frequencies.get(i);
            length += Math.pow(1.0 * frequency /maxFrequency, 2);
        }
        length = Math.sqrt(length);
        docInfo.setDocumentLength(length);
        docInfo.setRating(r.getRating());
        this.allDocuments.put(docid, docInfo);
    }
    
    /**
     * Returns the frequency of the specified term in the specified document.
     * @param term the term to search for
     * @param docid the document to search for
     * @return the frequency of the specified term in the specified document
     */
    public TermFrequencyInDocument getTermFrequencies(String term, String docid) {
        //if the specified term is not currently cached, then cache it.
        if(!term.equals(this.cachedTerm)) {
            this.cachedTerm = new String(term);
            this.cachedFrequencies = invertedIndex.getOrDefault(term, new ArrayList<>());
        }
        //find the position of the specified document in the list, and return the frequency.
        TermFrequencyInDocument docToFind = new TermFrequencyInDocument(docid, 0);
        int position = Collections.binarySearch(cachedFrequencies, docToFind, comparator);
        if(position >= 0) {
            return this.cachedFrequencies.get(position);
        }
        else {
            return null;
        }
    }
    
    /**
     * Returns a list with all the documents that contain the specified term.
     * @param term the term
     * @return a list with all the documents that contain the specified term
     */
    public List<String> getTermDocuments(String term) {
        //if the specified term is not currently cached, then cache it.
        if(!term.equals(this.cachedTerm)) {
            this.cachedTerm = new String(term);
            this.cachedFrequencies = invertedIndex.getOrDefault(term, new ArrayList<>());
        }
        ArrayList<String> docids = new ArrayList<>(this.cachedFrequencies.size());
        for (int i = 0; i < this.cachedFrequencies.size(); i++) {
            TermFrequencyInDocument termInDoc = this.cachedFrequencies.get(i);
            docids.add(termInDoc.getDocId());
        }
        return docids;
    }
    
    /**
     * Calculates the IDF value for this term. Uses the formula IDF=ln(1+N/nt)
     * @param term the term
     * @return the IDF value of this term
     */
    public double getIDF(String term) {
        int totalDocuments = this.allDocuments.size();
        int termDocuments = this.invertedIndex.getOrDefault(term, new ArrayList<>()).size();
        return Math.log(1 + 1.0*totalDocuments/termDocuments);
    }
    
    /**
     * Calculates the TF value of the specified term in the specified document
     * based on the formula TF=freq(t,d)/maxfreq(d).
     * @param term the term
     * @param docid the document
     * @return the TF value of the specified term in the specified document
     */
    public double getTF(String term, String docid) {
        //if the specified term is not currently cached, then cache it.
        if(!term.equals(this.cachedTerm)) {
            this.cachedTerm = new String(term);
            this.cachedFrequencies = invertedIndex.getOrDefault(term, new ArrayList<>());
        }
        int position = Collections.binarySearch(cachedFrequencies, new TermFrequencyInDocument(docid, 0),comparator);
        int frequency = 0;
        if(position >= 0) {
            frequency = cachedFrequencies.get(position).getFrequency();
        }
        int maxFrequency = this.allDocuments.get(docid).getMaxDocumentFrequency();
        
        return 1.0 * frequency / maxFrequency;
    }
    
    public double getDocLength(String docid) {
        return this.allDocuments.get(docid).getDocumentLength();
    }
    
    public int getDocRating(String docid) {
        return this.allDocuments.get(docid).getRating();
    }
    
    public int getDocumentCount() {
        return this.allDocuments.size();
    }
    
    public void idfStats() {
        List<Double> idfs = new ArrayList<>(invertedIndex.size());
        for (String term : invertedIndex.keySet()) {
            double idf = this.getIDF(term);
            idfs.add(idf);
        }
        Collections.sort(idfs);
        for (int i = 0; i < idfs.size(); i++) {
            Double get = idfs.get(i);
            System.out.println(get);
            
        }
    }
    
    /**
     * Deletes from the inverted index all the terms whose IDF value is smaller than the specified threshold.
     * @param threshold the threshold 
     * @return the number of terms that were removed
     */
    public int clearSmallIdfs(double threshold) {
        List<String> toRemove = new ArrayList<>();
        for (String term : invertedIndex.keySet()) {
            if(this.getIDF(term) < threshold) {
                toRemove.add(term);
            }
        }
        
        for (int i = 0; i < toRemove.size(); i++) {
            String term = toRemove.get(i);
            invertedIndex.remove(term);
        }
        
        return toRemove.size();
    }
}
