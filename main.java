import java.util.Arrays;

class ArraySorter extends Thread {
    private double[] array;
    private int startIndex;
    private int endIndex;

    public ArraySorter(double[] array, int startIndex, int endIndex) {
        this.array = array;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public void run() {
        // Sort the array
        insertSort(array, startIndex, endIndex);
    }

    private void insertSort(double[] array, int left, int right) {
        int i, j;
        for (i = left + 1; i < right; i++) {
            double key = array[i];
            j = i - 1;
            while (j >= left && array[j] > key) {
                array[j + 1] = array[j];
                j = j - 1;
            }
            array[j + 1] = key;
        }
    }
}

public class Main {
    private static final double DOUBLE_MIN = 1.0;
    private static final double DOUBLE_MAX = 1000.0;

    public static double getRandomDouble() {
        return DOUBLE_MIN + (Math.random() * (DOUBLE_MAX - DOUBLE_MIN));
    }

    public static void populateArray(double[] array) {
        for (int i = 0; i < array.length; i++) {
            double random = getRandomDouble();
            array[i] = random;
        }
    }

    public static void printArray(double[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.printf("Array[%d] = %.2f\n", i, array[i]);
        }
    }

    public static double[] merge(double[] array, int arrayTwoStart, int arraySize) {
        int arrayOneIndex = 0;
        int arrayTwoIndex = arrayTwoStart;

        double[] mergedArray = new double[arraySize];

        for (int i = 0; i < arraySize; i++) {
            int index;
            if ((arrayOneIndex < arrayTwoStart) && (arrayTwoIndex == arraySize || array[arrayOneIndex] < array[arrayTwoIndex])) {
                index = arrayOneIndex;
                arrayOneIndex++;
            } else {
                index = arrayTwoIndex;
                arrayTwoIndex++;
            }
            mergedArray[i] = array[index];
        }
        return mergedArray;
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 1) {
            System.out.println("Incorrect use of command line parameters! Enter size of array.");
            return;
        }

        int arraySize = Integer.parseInt(args[0]);
        if (arraySize % 2 != 0 || arraySize < 2) {
            System.out.println("Must enter an even value!");
            return;
        }

        double[] array = new double[arraySize];
        double[] arrayTwo = new double[arraySize];

        populateArray(array);
        System.arraycopy(array, 0, arrayTwo, 0, arraySize);

        long startTime = System.nanoTime();
        ArraySorter sorterThread1 = new ArraySorter(array, 0, arraySize);
        sorterThread1.start();
        sorterThread1.join();
        long endTime = System.nanoTime();
        double elapsedTime1 = (endTime - startTime) / 1e9;

        int subArrayOneRight = arraySize / 2;
        int subArrayTwoLeft = subArrayOneRight;

        startTime = System.nanoTime();
        ArraySorter sorterThread3 = new ArraySorter(arrayTwo, 0, subArrayOneRight);
        ArraySorter sorterThread4 = new ArraySorter(arrayTwo, subArrayTwoLeft, arraySize);
        sorterThread3.start();
        sorterThread4.start();
        sorterThread3.join();
        sorterThread4.join();
        double[] mergedArray = merge(arrayTwo, subArrayTwoLeft, arraySize);
        endTime = System.nanoTime();
        double elapsedTime2 = (endTime - startTime) / 1e9;

        System.out.println("Sorting is done in " + elapsedTime1 + " seconds when using one thread!");
        //printArray(array);
        System.out.println("Sorting is done in " + elapsedTime2 + " seconds when using two threads!");
        //printArray(mergedArray);
    }
}
