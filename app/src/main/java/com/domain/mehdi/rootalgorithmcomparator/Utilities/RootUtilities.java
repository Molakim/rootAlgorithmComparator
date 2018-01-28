package com.domain.mehdi.rootalgorithmcomparator.Utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.InvalidParameterException;

/**
 * Created by mehdi on 27/01/2018.
 */

public class RootUtilities {



    private static double inverseQuadratic(double a, double b, double c, double fa, double fb, double fc) {
        double s1 = (a*fb*fc)/((fa-fb)*(fa-fc));
        double s2 = (b*fa*fc)/((fb-fa)*(fb-fc));
        double s3 = (c*fa*fb)/((fc-fa)*(fc-fb));
        return  s1 + s2 + s3;
    }

    private static double secant(double a, double b, double fa, double fb) {
        return b - fb*((b-a)/(fb-fa));
    }

    private static double bisection(double a, double b) {
        return (a+b)/2;
    }

    private static double func(double bcurrent, int n, double a) {
        return (elevateTo(bcurrent,n) - a );
    }

    public static double elevateTo(double base, int exponent) {
        double result = base;
        for (int i=1;i<exponent;i++) result*=base;
        return result;
    }

    public static double absolute(double num){
        if (num < 0) return num*(-1);
        else return num;
    }

    private static double calculError(int exponent, double number, double rootApproximation) {
        double approximation = elevateTo(rootApproximation,exponent);
        if (number > approximation){
            return number-approximation;
        }
        else {
            return approximation-number;
        }
    }

    public static double truncateToTen(double num){
        num = BigDecimal.valueOf(num)
                .setScale(10, RoundingMode.HALF_UP)
                .doubleValue();
        return num;
    }

    public static double rootBisection(int n, double a, double precision) {

        double start = 0;
        double end = a>=1 ? a : 1;

        while (true){

            double mid = (start+end)/2;
            double error = calculError(n,a,mid);

            if (precision>=error){
                return mid;
            }

            if (elevateTo(mid,n) > a){
                end = mid;
            }
            else {
                start = mid;
            }
        }

    }

    public static double rootNewton(int n, double a, double precision) {
        double result = a>=1 ? a : 1;
        double error = calculError(n,a,result);
        while (precision<error){
            result = (1/(double) n)*(((double)n-1)*result + a /(elevateTo(result,n-1)) );
            error = calculError(n,a,result);
        }
        return result;
    }

    public static double rootDekker(int n, double a, double precision) {
        double acurrent = 0;
        double bcurrent = a>=1 ? a : 1;
        double blastround = acurrent;
        double bnext;
        double anext;
        double temp;


        do {
            double s = bcurrent - ((bcurrent - blastround) * func(bcurrent, n, a)) / (func(bcurrent, n, a) - func(blastround, n, a));
            double m = (acurrent + bcurrent) / 2;

            if ((s >= m) && (s <= bcurrent)) bnext = s;
            else bnext = m;

            if (func(acurrent, n, a) * func(bnext, n, a) > 0) anext = bcurrent;
            else anext = acurrent;

            if (func(anext, n, a) < func(bnext, n, a)) {
                temp = anext;
                anext = bnext;
                bnext = temp;
            }

            blastround = bcurrent;
            bcurrent = bnext;
            acurrent = anext;

        }while (absolute(func(bcurrent,n,a)) > precision);
        return bcurrent;

    }

    public static double rootBrent(int n, double num, double precision) {
        double a = num>=1 ? num : 1;
        double b = 0;
        double c = a;
        boolean mflag = true;
        Double d = null;
        double tempSwap;
        if (func(a,n,num)*func(b,n,num) > 0 ) throw new InvalidParameterException("Can't find a value");


        double s;



        while (absolute(func(b,n,num)) > precision) {
            double fa = func(a,n,num);
            double fb = func(b,n,num);
            double fc = func(b,n,num);

            if ((fa != fc) && (fb != fc)) {
                // we find s by using the inverse quadratic interpolation
                s = inverseQuadratic(a,b,c,fa,fb,fc);
            }
            else{
                // we find s by using the secant method)
                s = secant(a,b,fa,fb);
            }

            if (((s > (3*a+b)/4) && (s < b)) ||
                    (mflag && absolute(s-b) >= absolute((b-c)/2)) ||
                    (!mflag && absolute(s-b)>= absolute((c-d)/2)) ||
                    (mflag && absolute(b-c) < precision) ||
                    (!mflag && absolute(c-d) < precision )){
                s = bisection(a,b);
                mflag = true;
            }
            else {
                mflag = false;
            }

            double fs = func(s,n,num);
            d = c;
            c = b;

            if (fa*fs < 0) b = s;
            else a = s;
            if (absolute(fa) < absolute(fb)) {
                tempSwap = a;
                a = b;
                b = tempSwap;
            }

        }
        return b;

    }

    public static long estimateBisection(int n, double a, double precision){
        long timeBegin = System.currentTimeMillis();
        for (int i =0;i<10000;i++ ) {
            rootBisection(n, a, precision);
        }
        long timeEnd = System.currentTimeMillis();
        return timeEnd - timeBegin;
    }

    public static long estimateNewton(int n, double a, double precision){
        long timeBegin = System.currentTimeMillis();
        for (int i =0;i<10000;i++ ) {
            rootNewton(n, a, precision);
        }
        long timeEnd = System.currentTimeMillis();
        return timeEnd - timeBegin;
    }

    public static long estimateDekker(int n, double a, double precision){
        long timeBegin = System.currentTimeMillis();
        for (int i =0;i<10000;i++ ) {
            rootDekker(n, a, precision);
        }
        long timeEnd = System.currentTimeMillis();
        return timeEnd - timeBegin;
    }

    public static long estimateBrent(int n, double a, double precision){
        long timeBegin = System.currentTimeMillis();
        for (int i =0;i<10000;i++ ) {
            rootBrent(n, a, precision);
        }
        long timeEnd = System.currentTimeMillis();
        return timeEnd - timeBegin;
    }

}
