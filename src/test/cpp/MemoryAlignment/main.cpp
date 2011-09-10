/* 
 * File:   main.cpp
 * Author: peter
 *
 * Created on 10 September 2011, 08:48
 */

#include <cstdlib>
#include <stddef.h>
#include <iostream>

using namespace std;

class OneInt {
    int i;
};

class TwoInt {
    int i, j;
};

class ThreeInt {
    int i, j, k;
};

class FourInt {
    int i, j, k, l;
};

class FiveInt {
    int i, j, k, l, m;
};

class SixInt {
    int i, j, k, l, m, n;
};

class SevenInt {
    int i, j, k, l, m, n, o;
};

class EightInt {
    int i, j, k, l, m, n, o, p;
};

class OnePtr {
    void * i;
};

class TwoPtr {
    void * i, *j;
};

class ThreePtr {
    void * i, *j, *k;
};

class FourPtr {
    void * i, *j, *k, *l;
};

class FivePtr {
    void * i, *j, *k, *l, *m;
};

class SixPtr {
    void * i, *j, *k, *l, *m, *n;
};

class SevenPtr {
    void * i, *j, *k, *l, *m, *n, *o;
};

class EightPtr {
    void * i, *j, *k, *l, *m, *n, *o, *p;
};

/*
 * 
 */
int main(int argc, char** argv) {
    for (int i = 0; i <= 64; i++) {
        char *p = (char *) malloc(i);
        char *q = (char *) malloc(i);
        size_t reserved = q - p;
        cout << "malloc(" << i << ") reserved " << reserved << " bytes" << endl;
    }
    {
        // int values.
        cout << "heap class with one int reserved " << -((char *) new OneInt() - (char *) new OneInt()) << " bytes" << endl;
        OneInt p1, q1;
        cout << "stack class with one int reserved " << abs((char *) &q1 - (char *) &p1) << " bytes" << endl;
        cout << "heap class with one int reserved " << -((char *) new OneInt() - (char *) new OneInt()) << " bytes" << endl;
        OneInt p1b, q1b;
        cout << "stack class with one int reserved " << abs((char *) &q1b - (char *) &p1b) << " bytes" << endl;
        cout << "heap class with two int reserved " << -((char *) new TwoInt() - (char *) new TwoInt()) << " bytes" << endl;
        TwoInt p2, q2;
        cout << "stack class with two int reserved " << abs((char *) &q2 - (char *) &p2) << " bytes" << endl;
        cout << "heap class with three int reserved " << -((char *) new ThreeInt() - (char *) new ThreeInt()) << " bytes" << endl;
        ThreeInt p3, q3;
        cout << "stack class with three int reserved " << abs((char *) &q3 - (char *) &p3) << " bytes" << endl;
        cout << "heap class with four int reserved " << -((char *) new FourInt() - (char *) new FourInt()) << " bytes" << endl;
        FourInt p4, q4;
        cout << "stack class with four int reserved " << abs((char *) &q4 - (char *) &p4) << " bytes" << endl;
        cout << "heap class with five int reserved " << -((char *) new FiveInt() - (char *) new FiveInt()) << " bytes" << endl;
        FiveInt p5, q5;
        cout << "stack class with five int reserved " << abs((char *) &q5 - (char *) &p5) << " bytes" << endl;
        cout << "heap class with six int reserved " << -((char *) new SixInt() - (char *) new SixInt()) << " bytes" << endl;
        SixInt p6, q6;
        cout << "stack class with six int reserved " << abs((char *) &q6 - (char *) &p6) << " bytes" << endl;
        cout << "heap class with seven int reserved " << -((char *) new SevenInt() - (char *) new SevenInt()) << " bytes" << endl;
        SevenInt p7, q7;
        cout << "stack class with seven int reserved " << abs((char *) &q7 - (char *) &p7) << " bytes" << endl;
        cout << "heap class with eight int reserved " << -((char *) new EightInt() - (char *) new EightInt()) << " bytes" << endl;
        EightInt p8, q8;
        cout << "stack class with eight int reserved " << abs((char *) &q8 - (char *) &p8) << " bytes" << endl;
    }
    {
        // pointers values.
        cout << "heap class with one pointer reserved " << -((char *) new OnePtr() - (char *) new OnePtr()) << " bytes" << endl;
        OnePtr p1, q1;
        cout << "stack class with one pointer reserved " << abs((char *) &q1 - (char *) &p1) << " bytes" << endl;
        cout << "heap class with two pointer reserved " << -((char *) new TwoPtr() - (char *) new TwoPtr()) << " bytes" << endl;
        TwoPtr p2, q2;
        cout << "stack class with two pointer reserved " << abs((char *) &q2 - (char *) &p2) << " bytes" << endl;
        cout << "heap class with three pointer reserved " << -((char *) new ThreePtr() - (char *) new ThreePtr()) << " bytes" << endl;
        ThreePtr p3, q3;
        cout << "stack class with three pointer reserved " << abs((char *) &q3 - (char *) &p3) << " bytes" << endl;
        cout << "heap class with four pointer reserved " << -((char *) new FourPtr() - (char *) new FourPtr()) << " bytes" << endl;
        FourPtr p4, q4;
        cout << "stack class with four pointer reserved " << abs((char *) &q4 - (char *) &p4) << " bytes" << endl;
        cout << "heap class with five pointer reserved " << -((char *) new FivePtr() - (char *) new FivePtr()) << " bytes" << endl;
        FivePtr p5, q5;
        cout << "stack class with five pointer reserved " << abs((char *) &q5 - (char *) &p5) << " bytes" << endl;
        cout << "heap class with six pointer reserved " << -((char *) new SixPtr() - (char *) new SixPtr()) << " bytes" << endl;
        SixPtr p6, q6;
        cout << "stack class with six pointer reserved " << abs((char *) &q6 - (char *) &p6) << " bytes" << endl;
        cout << "heap class with seven pointer reserved " << -((char *) new SevenPtr() - (char *) new SevenPtr()) << " bytes" << endl;
        SevenPtr p7, q7;
        cout << "stack class with seven pointer reserved " << abs((char *) &q7 - (char *) &p7) << " bytes" << endl;
        cout << "heap class with eight pointer reserved " << -((char *) new EightPtr() - (char *) new EightPtr()) << " bytes" << endl;
        EightPtr p8, q8;
        cout << "stack class with eight pointer reserved " << abs((char *) &q8 - (char *) &p8) << " bytes" << endl;
    }
    return 0;
}

