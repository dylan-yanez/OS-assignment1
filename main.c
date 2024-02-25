#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <time.h>

#define DOUBLE_MIN 1.0
#define DOUBLE_MAX 1000.0

typedef struct ARRAY_STRUCT {
    double* array;
    int startIndex;
    int endIndex;
    size_t arraySize;
} arrayStruct;

double getRandomDouble()
{
    return DOUBLE_MIN + ((double)rand() / RAND_MAX) * (DOUBLE_MAX - DOUBLE_MIN);
}

void populateArray(double *array, int arraySize)
{
    int i;
    for (i = 0; i < arraySize; i++) {
        double random = getRandomDouble();
        array[i] = random;
    }
}

void printArray(double* array, int arraySize)
{
    int i;
    for (i = 0; i < arraySize; i++) {
        printf("Array[%d] = %.2lf\n", i, array[i]);
    }
}

double* merge(double* array, int arrayTwoStart, int arraySize)
{
    int i;
    int arrayOneIndex = 0;
    int arrayTwoIndex = arrayTwoStart;
    
    double *mergedArray = malloc(arraySize * sizeof(double));
    if (mergedArray == NULL) {
        printf("Failed to allocate memory for array.\n");
        return NULL;
    }
    
    for (i = 0; i < arraySize; i++){
        int index;
        if ((arrayOneIndex < arrayTwoStart) && (arrayTwoIndex == arraySize || array[arrayOneIndex] < array[arrayTwoIndex])){
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
void* insertSort(void* args) {
    arrayStruct *arg = (struct ARRAY_STRUCT *)args;
    double* array = arg->array;
    int left = arg->startIndex;
    int right = arg->endIndex;
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
    return NULL; // should change to proper return type
}
int main(int argc, char *argv[]) {
    if (argc != 2) {
        printf("Incorrect use of command line parameters! Enter size of array.\n");
        return 1;
    }
    int arraySize = atoi(argv[1]);
    if (arraySize % 2 != 0 || arraySize < 2){
        printf("Must enter an even value!\n");
        return 1;
    }
    printf("Creating array of size %d\n", arraySize);
    
    double *array = malloc(arraySize * sizeof(double));
    if (array == NULL) {
        printf("Failed to allocate memory for array.\n");
        return 1;
    }
    
    srand(time(NULL));
    populateArray(array, arraySize);
    
    double *arrayTwo = malloc(arraySize * sizeof(double));
    if (arrayTwo == NULL) {
        printf("Failed to allocate memory for array copy.\n");
        free(array); // Free the memory allocated for the original array
        return 1;
    }
    memcpy(arrayTwo, array, arraySize * sizeof(double));
    
    struct timespec ts_begin, ts_end;
    double elapsed;
    
    arrayStruct arrayStruct1 = {array, 0, arraySize, arraySize}; // {array_pointer, start_index, stop_index, arrayIndex}
    pthread_t tid;
    
    clock_gettime(CLOCK_MONOTONIC, &ts_begin);
    
    pthread_create(&tid, NULL, insertSort, (void*)&arrayStruct1);
    pthread_join(tid, NULL);
    
    clock_gettime(CLOCK_MONOTONIC, &ts_end);
    
    elapsed = ts_end.tv_sec - ts_begin.tv_sec;
    elapsed += (ts_end.tv_nsec - ts_begin.tv_nsec) / 1000000000.0;
    
    double elapsed1 = elapsed * 1000;
    
    
    
    
    int subArrayOneLeft = 0;
    int subArrayOneRight = arraySize / 2;
    
    int subArrayTwoLeft = subArrayOneRight;
    int subArrayTwoRight = arraySize;
    
    pthread_t threads[2];
    
    pthread_t firsthalftid;
    pthread_t secondhalftid;
    
    
    arrayStruct arrayStruct2_firsthalve = {arrayTwo, subArrayOneLeft, subArrayOneRight, arraySize};
    arrayStruct arrayStruct2_secondhalve = {arrayTwo, subArrayTwoLeft, subArrayTwoRight, arraySize};
    
    clock_gettime(CLOCK_MONOTONIC, &ts_begin);
    
    pthread_create(&firsthalftid, NULL, insertSort, (void*)&arrayStruct2_firsthalve); // check if created correctly
    pthread_create(&secondhalftid, NULL, insertSort, (void*)&arrayStruct2_secondhalve); // check if created correctly
    
    threads[0] = firsthalftid;
    threads[1] = secondhalftid;
    
    int i;
    for (i = 0; i < 2; i++) {
        pthread_join(threads[i], NULL); // check if could wait for it
    }
    double* mergedArray = merge(arrayTwo, subArrayTwoLeft, arraySize);
    clock_gettime(CLOCK_MONOTONIC, &ts_end);
    
    elapsed = ts_end.tv_sec - ts_begin.tv_sec;
    elapsed += (ts_end.tv_nsec - ts_begin.tv_nsec) / 1000000000.0;
    
    double elapsed2 = elapsed * 1000;
    
    printArray(array, arraySize);
    printf("Time %.2lf\n", elapsed1);
    printArray(mergedArray, arraySize);
    printf("Time %.2lf\n", elapsed2);
    
    free(array);
    free(mergedArray);
    free(arrayTwo);
    
    return 0;
}
