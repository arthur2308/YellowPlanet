package com.something.yellowplanet;

import java.util.ArrayList;
import java.util.List;

import com.something.yellowplanet.Point;

/**
 * Created by Arthur on 5/17/16.
 */
public class KMeans {

    private int NUM_CLUSTERS = 3;

    private int NUM_POINTS = 15;

    private static final int MIN_COORDINATE = 0;
    private static final int MAX_COORDINATE = 10;

    private List points;
    private List clusters;

    public KMeans() {
        this.points = new ArrayList();
        this.clusters = new ArrayList();
    }

    public static void main(String [] args)
    {
        KMeans kmeans = new KMeans();
        kmeans.init();
        kmeans.calculate();
    }

    public void init() {
        points = Point.createRandomPoints(MIN_COORDINATE,MAX_COORDINATE,NUM_CLUSTERS);


        for(int i = 0; i < NUM_CLUSTERS; i++) {
            Cluster cluster = new Cluster(i);//This needed a integer as an argument for the constructor, so i added one, duunno if its correct
            Point centroid = Point.createRandomPoint(MIN_COORDINATE,MAX_COORDINATE);
            cluster.setCentroid(centroid);
            clusters.add(cluster);
        }

        plotClusters();
    }

    private void plotClusters() {
        for (int i = 0; i < NUM_CLUSTERS; i++) {
            Cluster c = (Cluster)clusters.get(i);
            c.plotCluster();
        }
    }

    public void calculate() {
        boolean finish = false;
        int iteration = 0;

        // Add in new data, one at a time, recalculating centroids with each new one.
        while(!finish) {
            //Clear cluster state
            clearClusters();

            List lastCentroids = getCentroids();

            //Assign points to the closer cluster
            assignCluster();

            //Calculate new centroids.
            calculateCentroids();

            iteration++;

            List currentCentroids = getCentroids();

            //Calculates total distance between new and old Centroids
            double distance = 0;
            for(int i = 0; i < lastCentroids.size(); i++) {
                distance += Point.distance((Point)lastCentroids.get(i),(Point)currentCentroids.get(i));
            }
            System.out.println("#################");
            System.out.println("Iteration: " + iteration);
            System.out.println("Centroid distances: " + distance);
            plotClusters();

            if(distance == 0) {
                finish = true;
            }
        }
    }

    private void clearClusters() {
        for(Object clusterObj : clusters) {
            Cluster cluster = (Cluster)clusterObj;
            cluster.clear();
        }
    }

    private List getCentroids() {
        List centroids = new ArrayList(NUM_CLUSTERS);
        for(Object clusterObj : clusters) {
            Cluster cluster = (Cluster)clusterObj;
            Point aux = cluster.getCentroid();
            Point point = new Point(aux.getX(),aux.getY());
            centroids.add(point);
        }
        return centroids;
    }

    private void assignCluster() {
        double max = Double.MAX_VALUE;
        double min = max;
        int cluster = 0;
        double distance = 0.0;

        for(Object pointObj : points) {
            Point point = (Point)pointObj;
            min = max;
            for(int i = 0; i < NUM_CLUSTERS; i++) {
                Cluster c = (Cluster)clusters.get(i);
                distance = Point.distance(point, c.getCentroid());
                if(distance < min){
                    min = distance;
                    cluster = i;
                }
            }
            point.setCluster(cluster);
            ((Cluster)clusters.get(cluster)).addPoint(point);
        }
    }

    private void calculateCentroids() {
        for(Object clusterObj : clusters) {
            Cluster cluster = (Cluster) clusterObj;
            double sumX = 0;
            double sumY = 0;
            List list = cluster.getPoints();
            int n_points = list.size();

            for(Object pointObj : list) {
                Point point = (Point)pointObj;
                sumX += point.getX();
                sumY += point.getY();
            }

            Point centroid = cluster.getCentroid();
            if(n_points > 0) {
                double newX = sumX / n_points;
                double newY = sumY / n_points;
                centroid.setX(newX);
                centroid.setY(newY);
            }
        }
    }
}
