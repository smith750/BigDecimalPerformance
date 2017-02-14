'use strict';

const Decimal = require('decimal.js');

const NUMBERS_COUNT = 10000;
const ITERATIONS = 10000;


function generateRandomNumbers(count) {
    let nums = [];
    for (let i = 0; i < count; i++) {
        nums.push(Math.random()*count);
    }
    return nums;
}

function calculateSum(nums) {
    let sum = 0.0;
    for (let i = 0; i < nums.length; i++) {
        sum += nums[i];
    }
    return sum;
}

function calculateMean(nums) {
    return calculateSum(nums)/nums.length;
}

function calculateDeviation(timePoint, mean) {
    let delta = timePoint - mean;
    return delta * delta;
}

function calculateDeviations(timePoints, mean) {
    let deviations = [];
    for (let i = 0; i < timePoints.length; i++) {
        deviations.push(calculateDeviation(timePoints[i], mean));
    }
    return deviations;
}

function calculateStandardDeviation(timePoints, mean) {
    return Math.sqrt(calculateMean(calculateDeviations(timePoints, mean)));
}

function timer(consumer, nums) {
    let start = new Date().getTime();
    consumer(nums);
    let end = new Date().getTime();
    return end - start;
}

function timeSamples(consumer, nums, testName) {
    let timePoints = [];
    for (let i = 0; i < ITERATIONS; i++) {
        timePoints.push(timer(consumer, nums));
    }
    let mean = calculateMean(timePoints);
    let standardDeviation = calculateStandardDeviation(timePoints, mean);
    console.log("For test: "+testName+" Mean duration: "+mean+"ms with standard deviation of "+standardDeviation+"ms");
}

function additionTest(nums) {
    let sum = new Decimal(0.0);
    for (let i = 0; i < nums.length; i++) {
        sum = sum.plus(new Decimal(nums[i]));
    }
}

function subtractionTest(nums) {
    let sum = new Decimal(0.0);
    const compare = new Decimal(15.0);
    for (let i = 0; i < nums.length; i++) {
        let num = new Decimal(nums[i]);
        if (num.comparedTo(compare) >= 0) {
            sum = sum.plus(num.minus(compare));
        } else {
            sum = sum.plus(compare.minus(num));
        }
    }
}

function multiplicationTest(nums) {
    for (let i = 1; i < nums.length; i++) {
        (new Decimal(nums[i-1])).times(new Decimal(nums[i]));
    }
}

function divisionTest(nums) {
    const fivePointFive = new Decimal(5.5);
    for (let i = 0; i < nums.length; i++) {
        (new Decimal(nums[i])).dividedBy(fivePointFive).toPrecision(2, 0);
    }
}

const nums = generateRandomNumbers(NUMBERS_COUNT);
timeSamples(n => additionTest(n), nums, "Addition");
timeSamples(n => subtractionTest(n), nums, "Subtraction");
timeSamples(n => multiplicationTest(n), nums, "Multiplication");
timeSamples(n => divisionTest(n), nums, "Division");
