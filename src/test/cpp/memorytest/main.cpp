/* 
 * File:   main.cpp
 * Author: peter
 *
 * Created on 04 August 2011, 14:26
 */

#include <cstdlib>
#include <iostream>
#include <time.h>
#include <sys/time.h> 

using namespace std;

#define length (64LL * 1024 * 1024 * 1024)

double diff(timeval a, timeval b) {
    return a.tv_sec - b.tv_sec + (a.tv_usec - b.tv_usec) / 1e6;
}

/*
 * 
 */
void timeBytesTest() {

    struct timeval init, mid1, mid2, final;
    gettimeofday(&init, 0);

    //
    char* bytes = new char[length];

    gettimeofday(&mid1, 0);
    for (long long i = 0; i < length; i++)
        bytes[i] = (char) i;

    gettimeofday(&mid2, 0);

    for (long long i = 0; i < length; i++) {
        char b = bytes[i];
        if (b != (char) i) {
            cerr << "Expected " << (char) i << " but was " << b << endl;
            return;
        }
    }
    gettimeofday(&final, 0);
    delete bytes;

    //
    cout << "char[] took "
            << diff(mid1, init)*1e6 << " usec to allocate and "
            << diff(mid2, mid1) << " sec to write and "
            << diff(final, mid2) << " sec to read" << endl;
    return;
}

void timeBytesUnrolledTest() {

    struct timeval init, mid1, mid2, final;
    gettimeofday(&init, 0);

    //
    char* bytes = new char[length];

    gettimeofday(&mid1, 0);
    for (long long i = 0; i < length; i += 4) {
        bytes[i] = (char) i;
        bytes[i + 1] = (char) (i + 1);
        bytes[i + 2] = (char) (i + 2);
        bytes[i + 3] = (char) (i + 3);
    }

    gettimeofday(&mid2, 0);

    for (long long i = 0; i < length; i += 4) {
        char b0 = bytes[i];
        char b1 = bytes[i + 1];
        char b2 = bytes[i + 2];
        char b3 = bytes[i + 3];
        if (b0 != (char) i) {
            cerr << "Expected " << (char) i << " but was " << b0 << endl;
            return;
        }
        if (b1 != (char) (i + 1)) {
            cerr << "Expected " << (char) (i + 1) << " but was " << b1 << endl;
            return;
        }
        if (b2 != (char) (i + 2)) {
            cerr << "Expected " << (char) (i + 2) << " but was " << b2 << endl;
            return;
        }
        if (b3 != (char) (i + 3)) {
            cerr << "Expected " << (char) (i + 3) << " but was " << b3 << endl;
            return;
        }
    }
    gettimeofday(&final, 0);
    delete bytes;

    //
    cout << "char[] unrolled took "
            << diff(mid1, init)*1e6 << " usec to allocate and "
            << diff(mid2, mid1) << " sec to write and "
            << diff(final, mid2) << " sec to read" << endl;
    return;
}

/*
 * 
 */
void timeLongTest() {

    struct timeval init, mid1, mid2, final;
    gettimeofday(&init, 0);

    //
    long long* longs = new long long[length/8];

    gettimeofday(&mid1, 0);
    for (long long i = 0; i < length/8; i++)
        longs[i] =  i;

    gettimeofday(&mid2, 0);

    for (long long i = 0; i < length/8; i++) {
        long long l = longs[i];
        if (l != i) {
            cerr << "Expected " <<  i << " but was " << l << endl;
            return;
        }
    }
    gettimeofday(&final, 0);
    delete longs;

    //
    cout << "long[] took " 
            << diff(mid1, init)*1e6 << " usec to allocate and "
            << diff(mid2, mid1) << " sec to write and "
            << diff(final, mid2) << " sec to read" << endl;
    return;
}


void timeLongUnrolledTest() {

    struct timeval init, mid1, mid2, final;
    gettimeofday(&init, 0);

    //
    long long* longs = new long long[length/8];

    gettimeofday(&mid1, 0);
    for (long long i = 0; i < length/8; i += 4) {
        longs[i] = i;
        longs[i + 1] = i + 1;
        longs[i + 2] = i + 2;
        longs[i + 3] = i + 3;
    }

    gettimeofday(&mid2, 0);

    for (long long i = 0; i < length/8; i += 4) {
        long long b0 = longs[i];
        long long b1 = longs[i + 1];
        long long b2 = longs[i + 2];
        long long b3 = longs[i + 3];
        if (b0 !=  i) {
            cerr << "Expected " << i << " but was " << b0 << endl;
            return;
        }
        if (b1 !=  (i + 1)) {
            cerr << "Expected " << (i + 1) << " but was " << b1 << endl;
            return;
        }
        if (b2 !=  (i + 2)) {
            cerr << "Expected " << (i + 2) << " but was " << b2 << endl;
            return;
        }
        if (b3 !=  (i + 3)) {
            cerr << "Expected " <<  (i + 3) << " but was " << b3 << endl;
            return;
        }
    }
    gettimeofday(&final, 0);
    delete longs;

    //
    cout << "long[] unrolled took " 
            << diff(mid1, init)*1e6 << " usec to allocate and "
            << diff(mid2, mid1) << " sec to write and " 
            << diff(final, mid2) << " sec to read" << endl;
    return;
}

/*
 * 
 */
int main(int argc, char** argv) {
//    timeBytesTest();
//    timeBytesUnrolledTest();
//    timeLongTest();
    timeLongUnrolledTest();
    return 0;
}
