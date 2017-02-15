import java.util.Random;
import java.util.function.Consumer;

public class Double {
    public static final int NUMBERS_COUNT = 20_000_000;
    public static final int ITERATIONS = 20_000_000;

    public static void main(String[] args) throws Exception {
        final double[] randomNumbers = generateRandomNumbers(NUMBERS_COUNT);
        timeSamples(numbers -> additionTest(numbers), randomNumbers, "Addition");
        timeSamples(numbers -> subtractionTest(numbers), randomNumbers, "Subtraction");
        timeSamples(numbers -> multiplicationTest(numbers), randomNumbers, "Multiplication");
        timeSamples(numbers -> divisionTest(numbers), randomNumbers, "Division");
    }

    private static double[] generateRandomNumbers(int count) {
        double[] nums = new double[count];
        Random r = new Random();
        for (int i = 0; i < count; i++) {
            nums[i] = r.nextDouble()*(double)count;
        }
        return nums;
    }

    private static void timeSamples(Consumer<double[]> consumer, double[] nums, String testName) {
        double[] timePoints = new double[ITERATIONS];
        for (int i = 0; i < ITERATIONS; i++) {
            timePoints[i] = timer(consumer, nums);
        }
        final double mean = calculateMean(timePoints);
        final double standardDeviation = calculateStandardDeviation(timePoints, mean);
        System.out.println("For test: "+testName+" Mean duration: "+mean+"ms with standard deviation of "+standardDeviation+"ms");
    }

    private static double calculateStandardDeviation(double[] timePoints, double mean) {
        return Math.sqrt(calculateMean(calculationDeviations(timePoints, mean)));
    }

    private static double sum(double[] timePoints) {
        double sum = 0.0;
        for (int i = 0; i < timePoints.length; i++) {
            sum += timePoints[i];
        }
        return sum;
    }

    private static double calculateMean(double[] timePoints) {
        return sum(timePoints)/(double)timePoints.length;
    }

    private static double[] calculationDeviations(double[] timePoints, double mean) {
        double[] deviations = new double[timePoints.length];
        for (int i = 0; i < timePoints.length; i++) {
            deviations[i] = calculateDeviation(timePoints[i], mean);
        }
        return deviations;
    }

    private static double calculateDeviation(double timePoint, double mean) {
        final double delta = timePoint - mean;
        return delta * delta;
    }

    private static double timer(Consumer<double[]> consumer, double[] nums) {
        final long start = System.currentTimeMillis();
        consumer.accept(nums);
        final long end = System.currentTimeMillis();
        return (double)(end - start);
    }

    private static void additionTest(double[] nums ) {
        double sum = 0.0;
        for (int i = 0; i < nums.length; i++) {
            sum = sum + nums[i];
        }
    }

    private static void subtractionTest(double[] nums) {
        double sum = 0.0;
        double compare = 15.0;
        for (int i = 0; i < nums.length; i++) {
            double num = nums[i];
            if (num >= compare) {
                sum = sum + (num - compare);
            } else {
                sum = sum + (compare - num);
            }
        }
    }

    private static void multiplicationTest(double[] nums) {
        for (int i = 1; i < nums.length; i++) {
            double a = nums[i-1] * nums[i];
        }
    }

    private static void divisionTest(double[] nums) {
        double fivePointFive = 5.5;
        for (int i = 0; i < nums.length; i++) {
            double b = nums[i] / fivePointFive;
        }
    }
}
