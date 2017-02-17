/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moviereviewclassification;

/**
 * Stores data for a document so that it is not necessary to calculate the values everytime.
 * @author Savvas
 */
public class DocumentInfo {
    
    private int maxDocumentFrequency;
    private double documentLength;
    private int rating;

    public DocumentInfo(int maxDocumentFrequency, double documentLength, int rating) {
        this.maxDocumentFrequency = maxDocumentFrequency;
        this.documentLength = documentLength;
        this.rating = rating;
    }

    public void setMaxDocumentFrequency(int maxDocumentFrequency) {
        this.maxDocumentFrequency = maxDocumentFrequency;
    }

    public void setDocumentLength(double documentLength) {
        this.documentLength = documentLength;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getMaxDocumentFrequency() {
        return maxDocumentFrequency;
    }

    public double getDocumentLength() {
        return documentLength;
    }

    public int getRating() {
        return rating;
    }   

}
