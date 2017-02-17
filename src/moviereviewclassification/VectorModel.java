/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moviereviewclassification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Savvas
 */
public class VectorModel {

    InvertedIndex iv;

    public VectorModel() {
        iv = new InvertedIndex();
    }

    public void runVectorModel(String negative, String positive, int kNN) {
        File directory2 = new File(negative);
        List<File> allFiles = new ArrayList(Arrays
                .asList(directory2.listFiles()));

        directory2 = new File(positive);
        allFiles.addAll(new ArrayList(Arrays.asList(directory2.listFiles())));

        Collections.shuffle(allFiles);

        int k = 10;
        int idfThreshold = 3;
        int partitionSize = allFiles.size() / k;
        int trainSize = partitionSize * (k - 1);
        
        double avgPercentage = 0;
        double avgInvertedIndexTime = 0;
        double avgTestingtime = 0;

        //do the k-fold cross validation. Split the list in k parts, and keep 1/k for testing.
        System.out.println("Running " + k + "-fold cross validation. kNN=" + kNN + " idf>" + idfThreshold);
        for (int i = 0; i < k; i++) {
            System.out.println("Round " + (int)(i+1) + "...");
            List<File> trainSet = new ArrayList<>(trainSize);
            List<File> testSet = new ArrayList<>(partitionSize);
            
            long time = 0;

            trainSet.addAll(allFiles.subList(0, i * partitionSize));
            testSet.addAll(allFiles.subList(i * partitionSize,
                    (i+1) * partitionSize ));
            trainSet.addAll(allFiles.subList((i + 1) * partitionSize,
                    k * partitionSize));
            
            System.out.println("Creating inverted index...");
            
            time = System.currentTimeMillis();
            
            createInvertedIndex(trainSet);
            iv.clearSmallIdfs(idfThreshold);
            
            avgInvertedIndexTime += System.currentTimeMillis() - time;
            
            System.out.println("Running test sample...");
            
            time = System.currentTimeMillis();
            
            avgPercentage += runTestSample(testSet, kNN);
            
            avgTestingtime += System.currentTimeMillis() - time;
        }
        System.out.println("Average: " + avgPercentage*100.0/k + "/100");
        System.out.println("Average time to build inverted index: " + avgInvertedIndexTime/1000/k + " secs.");
        System.out.println("Average time to run test set: " + avgTestingtime/1000/k + " secs.");
    }

    private void createInvertedIndex(List<File> trainSet) {
        iv = new InvertedIndex();
        try {
            for (int i = 0; i < trainSet.size(); i++) {
                File file = trainSet.get(i);
                String[] split = file.getName().split("_");
                String reviewId;
                if (file.getPath().contains("neg")) {
                    reviewId = "n";
                }
                else {
                    reviewId = "p";
                }
                reviewId += split[0];
                String ratingStr = split[1].split("[.]")[0];
                int rating = Integer.parseInt(ratingStr);
                BufferedReader br = new BufferedReader(new FileReader(file));
                StringBuilder fullText = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    fullText.append(line);
                }
                br.close();
                Review r = new Review(fullText.toString(), reviewId, rating);
                iv.insertDocument(r);
            }
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(VectorModel.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            Logger.getLogger(VectorModel.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    private double runTestSample(List<File> testSet, int kNN) {
        int successful = 0;
        for (int i = 0; i < testSet.size(); i++) {
            try {
                File file = testSet.get(i);
                BufferedReader br = new BufferedReader(new FileReader(file));
                StringBuilder fullText = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    fullText.append(line);
                }
                br.close();
                String[] split = file.getName().split("_");
                String reviewId;
                if (file.getPath().contains("neg")) {
                    reviewId = "n";
                }
                else {
                    reviewId = "p";
                }
                reviewId += split[0];
                String ratingStr = split[1].split("[.]")[0];
                int rating = Integer.parseInt(ratingStr);
                Review r = new Review(fullText.toString(), reviewId, rating);

                DocumentTermsFrequencies termsFrequencies = r.getTermsFrequencies();
                List<String> terms = termsFrequencies.getTerms();
                List<Integer> frequencies = termsFrequencies.getFrequencies();
                int maxFrequency = Collections.max(frequencies);
                HashMap<String, Double> similarities = new HashMap<>();
                
                for (int j = 0; j < terms.size(); j++) {
                    String term = terms.get(j);
                    int frequency = frequencies.get(j);
                    double idf = iv.getIDF(term);
//                    if(idf > 4) {
                        List<String> termDocuments = iv.getTermDocuments(term); //get the documents in which the term appears.
                        for (int k = 0; k < termDocuments.size(); k++) { //for each document, calculate the w(t,d)*w(t,q). The results are squared and summed. w(t,d)=tfDoc w(t,q)=tfQuest*IDF(t)
                            String docid = termDocuments.get(k);
                            //calculate the tf of the term in the document, and add it in the documents distance
                            double tfDoc = iv.getTF(term, docid);
                            double tfQuest = 1.0 * frequency / maxFrequency;
                            double factor = Math.pow(tfDoc * tfQuest * idf, 2);
                            double value = similarities.getOrDefault(docid, 0.0); //get the sum calculated so far for this document

                            similarities.put(docid, value + factor); //add the value of this term to the already calculated sum.
                        }
//                    }
                }

                //create a new arraylist so that the distances can be paired with their docids in objects.
                ArrayList<DocumentSimilarity> docSimilaritiesPairsArray = new ArrayList<>(similarities.size());

                //For each calculated distance, create a pair of document-distance and put it in the arraylist.
                for (String docid : similarities.keySet()) {
                    double docLength = iv.getDocLength(docid);
                    double similarity = similarities.get(docid) / docLength;
                    docSimilaritiesPairsArray.add(new DocumentSimilarity(docid, similarity));
                }
                
                //create a priority queue so that it is easy to extract top-k with low complexity.
                //Sorting all the distances would be O(nlong), heap is O(n) to create and O(k*logn) to extract top-k
                PriorityQueue<DocumentSimilarity> docSimilaritiesPairs = new PriorityQueue<>(Collections.reverseOrder());
                docSimilaritiesPairs.addAll(docSimilaritiesPairsArray);
                docSimilaritiesPairsArray = null;
                //Collections.sort(docSimilaritiesPairsArray, Collections.reverseOrder());
                
                //from the top-k, count how many are on each class. if equal, then pick at random.
                int positive = 0;
                double ratingCalculated = 0;
                int k = Math.min(kNN, docSimilaritiesPairs.size());
                int weightedAvg = k*(k+1)/2;
                for (int j = 0; j < k; j++) {
                    int docRating = iv.getDocRating(docSimilaritiesPairs.poll().getDocid()); //get the rating stored for this document
                    ratingCalculated += docRating * (k-j);
//                    if(docRating > 5) {
//                        positive++;
//                    }
                }
                ratingCalculated = Math.round(1.0 * ratingCalculated / weightedAvg);
//                int foundRating;
//                if(positive > k/2.0) {
//                    foundRating = 10;
//                }
//                else if(positive < k/2.0) {
//                    foundRating = 2;
//                }
//                else { //when the top k are equally split, choose class at random
//                    foundRating = (int) (Math.random()*10) + 1;
//                }                
//                if( (foundRating < 6 && rating < 6) || (foundRating >=6 && rating >=6)) {
//                    successful++;
//                }
                
                if( (ratingCalculated < 6 && rating < 6) || (ratingCalculated >=6 && rating >=6)) {
                    successful++;
                }
            }
            catch (FileNotFoundException ex) {
                Logger.getLogger(MovieReviewClassification.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
            catch (IOException ex) {
                Logger.getLogger(MovieReviewClassification.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Successful: " + successful + "/" + testSet.size());
        return 1.0 * successful / testSet.size();
    }
}
