package kr.faz.app.example;

import sun.misc.DoubleConsts;

public class MathExample {
	public static void main(String[] args) {
		double pi = 3.141592;
		System.out.println(round(pi));
	}
    public static long round(double a) {
        long longBits = Double.doubleToRawLongBits(a);
        System.out.println("longBits : " + longBits);
        long biasedExp = (longBits & DoubleConsts.EXP_BIT_MASK) >> (DoubleConsts.SIGNIFICAND_WIDTH - 1);

        System.out.println("biasedExp : " + biasedExp);
        long shift = (DoubleConsts.SIGNIFICAND_WIDTH - 2 + DoubleConsts.EXP_BIAS) - biasedExp;
        System.out.println("shift : " + shift);

        System.out.println("shift & -64 : " + (shift & -64));
        if ((shift & -64) == 0) { // shift >= 0 && shift < 64
            // a is a finite number such that pow(2,-64) <= ulp(a) < 1
            long r = ((longBits & DoubleConsts.SIGNIF_BIT_MASK) | (DoubleConsts.SIGNIF_BIT_MASK + 1));
            System.out.println("r : " + r);
            if (longBits < 0) {
                r = -r;
            }
            // In the comments below each Java expression evaluates to the value
            // the corresponding mathematical expression:
            // (r) evaluates to a / ulp(a)
            // (r >> shift) evaluates to floor(a * 2)
            // ((r >> shift) + 1) evaluates to floor((a + 1/2) * 2)
            // (((r >> shift) + 1) >> 1) evaluates to floor(a + 1/2)
            return ((r >> shift) + 1) >> 1;
        } else {
            // a is either
            // - a finite number with abs(a) < exp(2,DoubleConsts.SIGNIFICAND_WIDTH-64) < 1/2
            // - a finite number with ulp(a) >= 1 and hence a is a mathematical integer
            // - an infinity or NaN
            return (long) a;
        }
    }
}
