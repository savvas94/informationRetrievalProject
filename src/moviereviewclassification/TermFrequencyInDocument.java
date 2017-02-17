/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moviereviewclassification;

/**
 * Stores a pair of docid-frequency. It is used to store the frequency of a term in a document.
 * @author Savvas
 */
public class TermFrequencyInDocument implements Comparable<Object>{
    
    private String docId;
    private int frequency;

    public TermFrequencyInDocument(String docId, int frequency) {
        this.docId = docId;
        this.frequency = frequency;
    }

    public String getDocId() {
        return docId;
    }

    public int getFrequency() {
        return frequency;
    }

    @Override
    public int compareTo(Object o) {
        TermFrequencyInDocument t = (TermFrequencyInDocument)o;
        return this.docId.compareTo(t.docId);
    }
    
    
    
}
