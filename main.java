import java.util.concurrent.ThreadLocalRandom;

class Main {
    static final double DOUBLE_MIN = 1.0;
    static final double DOUBLE_MAX = 1000.0;

    static class ArrayStruct {
        double[] array;
        int startIndex;
        int endIndex;
        int arraySize;

        ArrayStruct(double[] array, int startIndex, int endIndex, int arraySize) {
            this.array = array;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.arraySize = arraySize;
        }
    }

    static double getRandomDouble() {
        return ThreadLocalRandom.current().nextDouble(DOUBLE_MIN, DOUBLE_MAX);
    }

    static void populateArray(double[] array, int arraySize) {
        for (int i = 0; i < arraySize; i++) {
            double random = getRandomDouble();
            array[i] = random;
        }
    }

    static void printArray(double[] array, int arraySize) {
        for (int i = 0; i < arraySize; i++) {
            System.out.printf("Array[%d] = %.2f\n", i, array[i]);
        }
    }

    static double[] merge(double[] array, int arrayTwoStart, int arraySize) {
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

    static void insertionSort(double[] array, int left, int right) {
        for (int i = left + 1; i < right; i++) {
            double key = array[i];
            int j = i - 1;
            while (j >= left && array[j] > key) {
                array[j + 1] = array[j];
                j = j - 1;
            }
            array[j + 1] = key;
        }
    }

    static void insertSort(ArrayStruct arg) {
        double[] array = arg.array;
        int left = arg.startIndex;
        int right = arg.endIndex;
        for (int i = left + 1; i < right; i++) {
            double key = array[i];
            int j = i - 1;
            while (j >= left && array[j] > key) {
                array[j + 1] = array[j];
                j = j - 1;
            }
            array[j + 1] = key;
        }
    }

    public static void main(String[] args) {
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
        populateArray(array, arraySize);

        double[] arrayTwo = array.clone();

        long ts_begin = System.nanoTime();

        insertSort(new ArrayStruct(array, 0, arraySize, arraySize));

        long ts_end = System.nanoTime();

        double elapsed1 = (ts_end - ts_begin) / 1e9;

        int subArrayOneLeft = 0;
        int subArrayOneRight = arraySize / 2;

        int subArrayTwoLeft = subArrayOneRight;
        int subArrayTwoRight = arraySize;

        ts_begin = System.nanoTime();

        Thread firstHalfThread = new Thread(() -> insertSort(new ArrayStruct(arrayTwo, subArrayOneLeft, subArrayOneRight, arraySize)));
        Thread secondHalfThread = new Thread(() -> insertSort(new ArrayStruct(arrayTwo, subArrayTwoLeft, subArrayTwoRight, arraySize)));

        firstHalfThread.start();
        secondHalfThread.start();

        try {
            firstHalfThread.join();
            secondHalfThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        double[] mergedArray = merge(arrayTwo, subArrayTwoLeft, arraySize);

        ts_end = System.nanoTime();

        double elapsed2 = (ts_end - ts_begin) / 1e9;

        System.out.printf("Sorting is done in %.2f seconds when using one thread!\n", elapsed1);
        System.out.printf("Sorting is done in %.2f seconds when using two threads!\n", elapsed2);
    }
}
