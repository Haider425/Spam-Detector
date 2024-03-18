package com.spamdetector.util;

import com.spamdetector.domain.TestFile;

import java.io.*;
import java.util.*;

/**
 * TODO: This class will be implemented by you
 * You may create more methods to help you organize you strategy and make you code more readable
 */
public class SpamDetector {

    //Variable for storing the results of the spam detector
    List<TestFile> results = new ArrayList<>();

    /**
     * Merge two directories into one arraylist.
     * @param mainDir the main directory
     * @param dirPath1 the first child directory
     * @param dirPath2 the second child directory
     * @return ArrayList<File> that contains all the files from both directories
     */
    public ArrayList<File> mergeDir(File mainDir, String dirPath1, String dirPath2){
        ArrayList<File> res = new ArrayList<File>();
        //Create new files and arrays for ham and ham2
        File dir1 = new File(mainDir, dirPath1);
        File dir2 = new File(mainDir, dirPath2);

        //Get the files from the directories
        File[] dirFiles = dir1.listFiles();
        File[] dir2Files = dir2.listFiles();

        //Append to res
        for(File f : dirFiles){
            res.add(f);
        }
        for(File f: dir2Files){
            res.add(f);
        }

        return res;
    }

    /**
     * The training method for the spam detector. It parses the files in the training directory and calculates the
     * probability of each word in the files.
     * @param mainDirectory the main directory
     * @return TreeMap<String, Double> that contains the probability of each word in the files
     */
    public TreeMap<String, Double> trainingModel(File mainDirectory){
        //Initialize Maps
        HashMap<String, Integer> trainHamFreq = new HashMap<String, Integer>();
        HashMap<String, Integer> trainSpamFreq = new HashMap<String, Integer>();
        TreeMap<String, Double> probability = new TreeMap<String, Double>();

        //Ham Files has 2 folders, so merge 2 into 1 arraylist
        ArrayList<File> hamFiles = mergeDir(mainDirectory, "train/ham", "train/ham2");

        //Spam files, only one directory no need to merge
        File spam = new File(mainDirectory, "train/spam");
        File[] spamFiles = spam.listFiles();

        Scanner s; //Need scanner to read files

        //Parse each word, update frequency (HAM)
        System.out.println("Training the model with "+hamFiles.size()+" ham files");

        for(File f : hamFiles){
            try{
                s = new Scanner(f);
                List<String> wordsInFile = new ArrayList<String>(); //useful for checking if in file, easier computation

                while(s.hasNext()){
                    String word = s.next().toLowerCase(); //ignore case

                    //conditional, check if word is not in wordsInFile, then update frequency
                    if(!wordsInFile.contains(word)){
                        if(trainHamFreq.containsKey(word)){
                            trainHamFreq.put(word, trainHamFreq.get(word)+1); //+1 for every file
                        }else{
                            trainHamFreq.put(word, 1);
                        }
                        wordsInFile.add(word); //add to list, already in file no need to check again
                    }
                }

            }catch(FileNotFoundException e){ //catch the exception from Scanner
                System.out.println("Scanner has failed! File not found: "+f);
            }
        }
        System.out.println("Done!");

        //Parse each word, update frequency (SPAM)
        System.out.println("Training the model with "+spamFiles.length+" spam files");

        for(File f : spamFiles){
            try{
                s = new Scanner(f);
                List<String> wordsInFile = new ArrayList<String>(); //useful for checking if in file, easier computation

                while(s.hasNext()){
                    String word = s.next().toLowerCase(); //ignore case

                    //conditional, check if word is already in the map and has existed in file, skip if True
                    if(!wordsInFile.contains(word)){
                        if(trainSpamFreq.containsKey(word)){
                            trainSpamFreq.put(word, trainSpamFreq.get(word)+1); //+1 for every file
                        }else{
                            trainSpamFreq.put(word, 1);
                        }
                        wordsInFile.add(word); //add to list, already in file no need to check again
                    }
                }

            }catch(FileNotFoundException e){ //catch the exception from Scanner
                System.out.println("Scanner has failed! File not found: "+f);
            }
        }
        System.out.println("Done!");

        //Probability of Each Word in both Spam and Ham
        System.out.println("Calculating the probability of each word in both spam and ham");

        double alpha = 1.0; //Laplace Smoothing

        //Create List with every unique word, easier computation for the loop
        Set<String> allWords = new HashSet<String>();
        allWords.addAll(trainHamFreq.keySet());
        allWords.addAll(trainSpamFreq.keySet());

        //Calculating Probability
        System.out.println("Calculating probability...");

        for(String word: allWords){
            //Init freq
            double spamFreq = 0.0;
            double hamFreq = 0.0;

            if(trainSpamFreq.containsKey(word)){
                spamFreq = trainSpamFreq.get(word);
            }
            if(trainHamFreq.containsKey(word)){
                hamFreq = trainHamFreq.get(word);
            }

            //Calculate prob with Laplace Smoothing, K=2
            double spamProb = (spamFreq + alpha) / (spamFiles.length + 2*alpha);
            double hamProb = (hamFreq + alpha) / (hamFiles.size() + 2*alpha);

            //Add to probability map
            probability.put(word, spamProb / (spamProb + hamProb));
        }

        return probability;
    }

    /**
     * The testing method for the spam detector. It looks at the testfiles and with the probability map from the
     * training method, it checks which files are spam or ham.
     * @param mainDirectory the main directory
     * @param trainSpamProbability the probability of each word in the files
     * @return TreeMap<String, Double> that contains the probability of each word in the files
     */
    public List<TestFile> testModel(File mainDirectory, TreeMap<String, Double> trainSpamProbability){
        //Load test
        File testDir = new File(mainDirectory, "test");

        //Create list for results
        ArrayList<File> testFiles = mergeDir(testDir, "ham", "spam");
        boolean isSpam = false; //flag for spam/ham

        for (File testFile : testFiles) {
            TestFile result = new TestFile(testFile.getName());
            Scanner scanner;

            //Flag condition
            if(result.getFilename().equals("00001.317e78fa8ee2f54cd4890fdc09ba8176")){
                isSpam = true;
            }

            //Classify the file based on flag
            if(!isSpam){
                result.setActualClass("ham");
            }else{
                result.setActualClass("spam");
            }

            try {
                scanner = new Scanner(testFile);
                double b = 0.0; //exponent that affects spam probability

                while (scanner.hasNext()) {
                    String word = scanner.next().toLowerCase();
                    if (trainSpamProbability.containsKey(word)) {
                        //Logarithm of the probability
                        b += Math.log(1 - trainSpamProbability.get(word)) - Math.log(trainSpamProbability.get(word));
                    }
                }

                //Calculate spam probability
                double spamProbability = 1.0 / (1.0 + Math.exp(b));
                result.setSpamProbability(spamProbability);

                results.add(result);

            } catch (FileNotFoundException e) {
                System.out.println("Scanner has failed! File not found: " + testFile);
            }
        }

        return results;
    }

    /**
     * The main method for the spam detector. It calls the training and testing methods and stores the results.
     * @param mainDirectory the main directory
     * @return List<TestFile> that contains the probability of each file
     */
    public List<TestFile> trainAndTest(File mainDirectory) {
        List<TestFile> results = new ArrayList<>();
        TreeMap<String, Double> trainSpamProbability = trainingModel(mainDirectory); //Train
        results = testModel(mainDirectory, trainSpamProbability); //Test

        //Store the results to the class variable
        this.results = results;

        return results;
    }

    /**
     * @return the spam results
     */
    public List<TestFile> getSpamResults(){
        return Collections.unmodifiableList(results);
    }

    /**
     * @return the accuracy of the spam detector
     */
    public double getAccuracy(){
    int correctPredictions = 0;

    for (TestFile file : results) {
        String actualClass = file.getActualClass();
        double spamProbability = file.getSpamProbability();

        if ((actualClass.equals("spam") && spamProbability >= 0.5) || (actualClass.equals("ham") && spamProbability < 0.5)) {
            correctPredictions++;
        }
    }

    return (double)correctPredictions/(double)results.size();

    }

    /**
     * @return the precision of the spam detector
     */
    public double getPrecision(){
        int truePositive = 0;

        for (TestFile file : results) {
            String actualClass = file.getActualClass();
            double spamProbability = file.getSpamProbability();

            if (actualClass.equals("spam") && spamProbability >= 0.5){
                truePositive++;
            }
        }

        return (double)truePositive/(double)1401;
    }
}    



