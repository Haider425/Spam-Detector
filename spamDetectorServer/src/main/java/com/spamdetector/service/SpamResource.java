package com.spamdetector.service;

import com.spamdetector.domain.TestFile;
import com.spamdetector.util.SpamDetector;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.io.File;
import java.util.List;

import jakarta.ws.rs.core.Response;

@Path("/spam")
public class SpamResource {

    //    your SpamDetector Class responsible for all the SpamDetecting logic
    private final SpamDetector detector;

    /**
     * constructor for the SpamResource, calls trainAndTest to train and test the SpamDetector
     */
    public SpamResource() {
        // Initialize the SpamDetector
        this.detector = new SpamDetector();
        // Load resources, train, and test to improve performance on the endpoint calls
        System.out.println("Training and testing the model, please wait...");
        this.trainAndTest();
    }

    /**
     * @return a response containing the spam results
     */
    @GET
    @Produces("application/json")
    public Response getSpamResults() {
        try {
            // Get the spam results from the SpamDetector
            List<TestFile> results = detector.getSpamResults();
            // Return the spam results as a response
            return Response.status(200)
                    .entity(results)
                    .header("Content-Type", "application/json")
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            // Return a 500 Internal Server Error response
            return Response.status(500)
                    .entity("Error processing spam detection results")
                    .header("Content-Type", "text/plain")
                    .build();
        }
    }

    /**
     * @return a response containing the accuracy
     */
    @GET
    @Path("/accuracy")
    @Produces("application/json")
    public Response getAccuracy() {
        try {
            // Get the accuracy from the SpamDetector
            double accuracy = detector.getAccuracy();
            // Return the accuracy as a response
            return Response.status(200)
                    .entity(accuracy)
                    .header("Content-Type","application/json")
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            // Return a 500 Internal Server Error response
            return Response.status(500)
                    .entity("Error processing spam detection results")
                    .header("Content-Type", "text/plain")
                    .build();
        }
    }

    /**
     * @return a response containing the precision
     */
    @GET
    @Path("/precision")
    @Produces("application/json")
    public Response getPrecision() {
        try {
            // Get the precision from the SpamDetector
            double precision = detector.getPrecision();
            // Return the precision as a response
            return Response.status(200)
                    .entity(precision)
                    .header("Content-Type","application/json")
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            // Return a 500 Internal Server Error response
            return Response.status(500)
                    .entity("Error processing spam detection results")
                    .header("Content-Type", "text/plain")
                    .build();
        }
    }

    /**
     * Train and test the SpamDetector
     */
    private void trainAndTest() {
        // Load the main directory "data" here from the Resources folder
        File mainDirectory = new File(getClass().getClassLoader().getResource("data").getFile());
        detector.trainAndTest(mainDirectory);
    }
}
