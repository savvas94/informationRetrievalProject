/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moviereviewclassification;

import java.util.ArrayList;
import java.util.List;

/**
 * Stoores all the terms that appear in a document with their frequencies.
 * @author Savvas
 */
public class DocumentTermsFrequencies {
    
    private List<String> terms;
    private List<Integer> frequencies;

    public DocumentTermsFrequencies(List<String> terms,
                                    List<Integer> frequencies) {
        this.terms = terms;
        this.frequencies = frequencies;
    }

    public List<String> getTerms() {
        return terms;
    }

    public List<Integer> getFrequencies() {
        return frequencies;
    }
    
    
}
