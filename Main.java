package com.company;

import java.util.Arrays;

import static java.lang.Math.*;

public class Main {

    public static void main(String[] args) {

        Distribution ds = new Distribution();
        ds.setParameters();
        ds.printWithdrawalFlow();
    }
}

class Distribution {

    int wallets[];
    public final int N;
    public final int S;
    public final int L;

    public final double F;
    int T[];
    double s[];

    public Distribution() { this( new int[] { 0, 1, 10, 25, 35, 50, 71, 156 }, 9 ); }

    public Distribution(int d_wallets[], int nr_epochs) {
        wallets = Arrays.copyOf( d_wallets, d_wallets.length );
        N = nr_epochs;

        int sm = 0;
        for(int i = 0; i < d_wallets.length; ++i ) sm += d_wallets[ i ];

        S = sm;
        L = wallets.length - 1;

        F = S / (double)N;
        T = new int[ N + 1 ]; // using indexes starting from 1, not 0 
        s = new double[ N + 1 ]; // using indexes starting from 1, not 0
    }

    public void setParameters() {
        double paid2acc = 0;

        for(int epoch = 1; epoch <= N; ++epoch ) {
            int t = T[ epoch - 1 ];
            s[ epoch ] = F / ( L - t );

            int tc = t + 1;
            double sm = 0;

            double sa = ( F - (wallets[ tc ] - paid2acc) ) / ( L - tc );

            while ( wallets[ tc ] - paid2acc < sa  ){
                s[ epoch ] = sa;
                sm += wallets[ tc ] - paid2acc;
                t = tc++;
                sa = ( F - sm - (wallets[ tc ] - paid2acc) ) / ( L - tc );
            }

            T[ epoch ] = t;
            paid2acc += s[ epoch ];
        }

    }

    public double withdraw( int wallet_index, int epoch, double r ) {

        if ( wallet_index < T[ epoch ] ) {
            return wallets[ wallet_index ];
        }

        double withdrawP = 0;
        for(int i = 1; i <= epoch - 1; ++i )
            withdrawP += s[ i ];

        return withdrawP + min( r * s[ epoch ], (double) wallets[ wallet_index ] - withdrawP );
    }


    public void printWalletFlow(int wallet_index) {
        System.out.printf( "%3d | %4d | ", wallet_index, wallets[ wallet_index ] );

        for(int i = 1; i <= N; ++i)
            System.out.printf("%6.2f |", withdraw(wallet_index, i, 1));

        System.out.println();
    }

    public void printWithdrawalFlow() {
        System.out.println( "Total per epoch: " + F );

        System.out.printf("%7s|", "per wallet:");
        for(int i = 1; i <= N; ++i )
            System.out.printf("%6.2f |", s[ i ] );
        System.out.println();

        for(int i = 1; i < wallets.length; ++i )
            printWalletFlow( i );
    }
}
