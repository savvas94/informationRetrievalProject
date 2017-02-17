/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moviereviewclassification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.DynAnyPackage.Invalid;

/**
 *
 * @author Savvas
 */
public class MovieReviewClassification {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {        
        VectorModel vectorModel = new VectorModel();
        
        vectorModel.runVectorModel("data/train/neg", "data/train/pos", 10);
        
        if(true) {
            return;
        }

//        try {
//            File directory = new File("data/train/neg");
//            File[] filesPaths = directory.listFiles();
//            for (int i = 0; i < filesPaths.length; i++) {
//                File file = filesPaths[i];
//                String[] split = file.getName().split("_");
//                String docid = "n" + split[0];
//                String ratingStr = split[1].split("[.]")[0];
//                int rating = Integer.parseInt(ratingStr);
//                BufferedReader br = new BufferedReader(new FileReader(file));
//                StringBuilder fullText = new StringBuilder();
//                String line;
//                while((line = br.readLine())!= null) {
//                    fullText.append(line);
//                }
//                Review r = new Review(fullText.toString(), docid, rating);
//                iv.insertDocument(r);
//                if(i%100 == 0) {
//                    System.out.println((int)(i+1));
//                }
//            }
//            
//            directory = new File("data/train/pos");;
//            filesPaths = directory.listFiles();
//            for (int i = 0; i < filesPaths.length; i++) {
//                File file = filesPaths[i];
//                String[] split = file.getName().split("_");
//                String docid = "p" + split[0];
//                String ratingStr = split[1].split("[.]")[0];
//                int rating = Integer.parseInt(ratingStr);
//                BufferedReader br = new BufferedReader(new FileReader(file));
//                StringBuilder fullText = new StringBuilder();
//                String line;
//                while((line = br.readLine())!= null) {
//                    fullText.append(line);
//                }
//                Review r = new Review(fullText.toString(), docid, rating);
//                iv.insertDocument(r);
//                if(i%100 == 0) {
//                    System.out.println((int)(i+1+12500));
//                }
//            }
//        }
//        catch (FileNotFoundException ex) {
//            Logger.getLogger(MovieReviewClassification.class.getName())
//                    .log(Level.SEVERE, null, ex);
//        }
//        catch (IOException ex) {
//            Logger.getLogger(MovieReviewClassification.class.getName())
//                    .log(Level.SEVERE, null, ex);
//        }
//        
//        System.out.println("test");
//        try {
//            File file = new File("data/test/00000.txt");
//            BufferedReader br = new BufferedReader(new FileReader(file));
//            StringBuilder fullText = new StringBuilder();
//            String line;
//            while((line = br.readLine())!= null) {
//                fullText.append(line);
//            }
//            Review r = new Review(fullText.toString(), "00001", 0);
//            
//            DocumentTermsFrequencies termsFrequencies = r.getTermsFrequencies();
//            List<String> terms = termsFrequencies.getTerms();
//            List<Integer> frequencies = termsFrequencies.getFrequencies();
//            int maxFrequency = Collections.max(frequencies);
//            HashMap<String, Double> distances = new HashMap<>();
//            System.out.println(terms.size());
//            System.out.println(frequencies.size());
//            for (int i = 0; i < terms.size(); i++) {
//                String term = terms.get(i);
//                int frequency = frequencies.get(i);
//                List<String> termDocuments = iv.getTermDocuments(term);
//                for (int j = 0; j < termDocuments.size(); j++) {
//                    String docid = termDocuments.get(j);
//                    //calculate the tf of the term in the document, and add it in the documents distance
//                    double tfDoc = iv.getTF(term, termDocuments.get(j));
//                    double tfQuest = 1.0 * frequency / maxFrequency;
//                    double factor = Math.pow( tfDoc * tfQuest * iv.getIDF(term), 2);
//                    double value = distances.getOrDefault(docid, 0.0);
//                    if(docid.equals("p12235")) {
//                        System.out.println(". term: " + term + " " + tfDoc + " " + tfQuest + " " + factor + " " + value);
//                    }
//                    distances.put(docid, value + factor);
//                }
//            }
//            
//            ArrayList<DocumentDistance> docDistancesPairs = new ArrayList<>(distances.size());
//            
//            for (String docid : distances.keySet()) {
//                double docLength = iv.getDocLength(docid);
//                double distance = distances.get(docid)/docLength;
//                docDistancesPairs.add(new DocumentDistance(docid, distance));
//            }
//            
//            Collections.sort(docDistancesPairs);
//            System.out.println("distances sorted:");
//            
//            for (int i = 0; i < 100; i++) {
//                DocumentDistance doc = docDistancesPairs.get(i);
//                System.out.println(doc.getDocid() + " " + doc.getDistance() + " " + iv.getDocRating(doc.getDocid()));
//            }
//            for (int i = docDistancesPairs.size() - 100; i < docDistancesPairs.size(); i++) {
//                DocumentDistance doc = docDistancesPairs.get(i);
//                System.out.println(doc.getDocid() + " " + doc.getDistance() + " " + iv.getDocRating(doc.getDocid()));
//            }
//        }
//        catch (FileNotFoundException ex) {
//            Logger.getLogger(MovieReviewClassification.class.getName())
//                    .log(Level.SEVERE, null, ex);
//        }
//        catch (IOException ex) {
//            Logger.getLogger(MovieReviewClassification.class.getName())
//                    .log(Level.SEVERE, null, ex);
//        }
        

//        long totalVmHeap = Runtime.getRuntime().totalMemory();
//        long freeVmHeap = Runtime.getRuntime().freeMemory();
//        long usedVmHeap = totalVmHeap - freeVmHeap;
//        long maxVmHeap = Runtime.getRuntime().maxMemory();
//        long availableVmHeap = maxVmHeap - usedVmHeap + freeVmHeap;

//        System.out.println(totalVmHeap / 1024 / 1024);
//        System.out.println(freeVmHeap / 1024 / 1024);
//        System.out.println(usedVmHeap / 1024 / 1024);
//        System.out.println(maxVmHeap / 1024 / 1024);
//        System.out.println(availableVmHeap / 1024 / 1024);

    }

}
