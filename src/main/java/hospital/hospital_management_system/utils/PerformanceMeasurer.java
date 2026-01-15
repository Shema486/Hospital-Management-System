package hospital.hospital_management_system.utils;

/**
 * Simple utility class to measure execution time of operations
 * Used to demonstrate performance improvements from caching and indexing
 */
public class PerformanceMeasurer {
    
    /**
     * Measure how long a task takes to execute
     * @param taskDescription Description of what we're measuring
     * @param task The code to measure (using a Runnable)
     * @return Execution time in milliseconds
     */
    public static long measureTime(String taskDescription, Runnable task) {
        // Record start time
        long startTime = System.currentTimeMillis();
        
        // Execute the task
        task.run();
        
        // Record end time
        long endTime = System.currentTimeMillis();
        
        // Calculate and return the difference
        long executionTime = endTime - startTime;
        
        System.out.println(taskDescription + " took: " + executionTime + " milliseconds");
        
        return executionTime;
    }
    
    /**
     * Measure average execution time over multiple runs
     * This gives more accurate results by running the task several times
     * @param taskDescription Description of what we're measuring
     * @param task The code to measure
     * @param numberOfRuns How many times to run the task
     * @return Average execution time in milliseconds
     */
    public static long measureAverageTime(String taskDescription, Runnable task, int numberOfRuns) {
        long totalTime = 0;
        
        // Run the task multiple times and add up the times
        for (int i = 0; i < numberOfRuns; i++) {
            long startTime = System.currentTimeMillis();
            task.run();
            long endTime = System.currentTimeMillis();
            totalTime += (endTime - startTime);
        }
        
        // Calculate average
        long averageTime = totalTime / numberOfRuns;
        
        System.out.println(taskDescription + " average time over " + numberOfRuns + " runs: " + averageTime + " milliseconds");
        
        return averageTime;
    }
}
