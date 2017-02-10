/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.math.BigDecimal;
import java.util.Random;
import java.util.function.Consumer;

public class BigDecimalPerformance {
    public static final int NUMBERS_COUNT = 10_000;
    public static final int ITERATIONS = 10_000;

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
        BigDecimal sum = new BigDecimal(0.0);
        for (int i = 0; i < nums.length; i++) {
            sum = sum.add(new BigDecimal(nums[i]));
        }
    }

    private static void subtractionTest(double[] nums) {
        BigDecimal sum = new BigDecimal(0.0);
        BigDecimal compare = new BigDecimal(15.0);
        for (int i = 0; i < nums.length; i++) {
            BigDecimal num = new BigDecimal(nums[i]);
            if (num.compareTo(compare) >= 0) {
                sum = sum.add(num.subtract(compare));
            } else {
                sum = sum.add(compare.subtract(num));
            }
        }
    }

    private static void multiplicationTest(double[] nums) {
        for (int i = 1; i < nums.length; i++) {
            new BigDecimal(nums[i-1]).multiply(new BigDecimal(nums[i]));
        }
    }

    private static void divisionTest(double[] nums) {
        BigDecimal fivePointFive = new BigDecimal(5.5);
        for (int i = 0; i < nums.length; i++) {
            new BigDecimal(nums[i]).divide(fivePointFive, 2, BigDecimal.ROUND_HALF_UP);
        }
    }
}
