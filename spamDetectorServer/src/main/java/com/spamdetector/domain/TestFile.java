package com.spamdetector.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents a file from the testing data
 * Includes the actual or real class and the predicted class according to the classifier
 * @author CSCI2020U
 */
public class TestFile {
    /**
     * the name of the file this class represents
     */
    @JsonProperty("file")
    private String filename;

    /**
     * the probability of this file belonging to the 'spam' category/class
     */
    @JsonProperty("spamProbability")
    private double spamProbability;

    /**
     * the real class/category of the file; related to the folder it was loaded from 'spam' or 'ham'
     */
    @JsonProperty("actualClass")
    private String actualClass;

    /**
     * Constructor for the TestFile class
     */
    public TestFile(String filename, double spamProbability, String actualClass) {
        this.filename = filename;
        this.spamProbability = spamProbability;
        this.actualClass = actualClass;
    }

    /**
     * Another constructor for the TestFile class with the filename only
     */
    public TestFile(String filename) {
        this.filename = filename;
    }

    /**
     * @return the name of the file
     */
    public String getFilename() {
        return this.filename;
    }

    /**
     * @return the probability of this file being 'spam'
     */
    public double getSpamProbability() {
        return this.spamProbability;
    }

    /**
     * @return the actual/real class of the file
     */
    public String getActualClass() {
        return this.actualClass;
    }

    /**
     * set the name of the file
     */
    public void setFilename(String value) {
        this.filename = value;
    }

    /**
     * set the probability of the file
     */
    public void setSpamProbability(double value) {
        this.spamProbability = value;
    }

    /**
     * set the class of the file
     */
    public void setActualClass(String value) {
        this.actualClass = value;
    }
}
