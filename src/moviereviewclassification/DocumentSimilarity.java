/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moviereviewclassification;

import java.util.Comparator;

/**
 *
 * @author Savvas
 */
public class DocumentSimilarity implements Comparable<Object> {
    
    private String docid;
    private double similarity;

    public DocumentSimilarity(String docid, double similarity) {
        this.docid = docid;
        this.similarity = similarity;
    }

    public String getDocid() {
        return docid;
    }

    public double getSimilarity() {
        return similarity;
    }
    @Override
    public int compareTo(Object o) {
        DocumentSimilarity d = (DocumentSimilarity)o;
        return Double.compare(this.similarity, d.similarity);
    }
    
    
    
}
