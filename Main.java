package com.company;

import java.text.DecimalFormat;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        Distribution ds = new Distribution();
        ds.setParameters();
        ds.printWithdrawalFlow();

	    System.out.println("Finished.");

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

    //public Distribution() { this( new int[] { 0, 1, 10, 25, 35, 50 }, 4, 121 ); }
    public Distribution() { this( new int[] { 0, 1, 10, 25, 35 }, 4, 71 ); }
    public Distribution(int dust_wallets[], int nr_epochs, int total_airdrop) {
        wallets = Arrays.copyOf( dust_wallets, dust_wallets.length );
        N = nr_epochs;
        S = total_airdrop;
        L = wallets.length - 1;

        F = S / (double)N;
        T = new int[ N + 1 ]; // using indexes starting from 1, not 0 - just for convenience to follow spec
        s = new double[ N + 1 ]; // using indexes starting from 1, not 0
    }

    public void setParameters() {
        double paid2acc = 0;

        for(int epoch = 1; epoch <= N; ++epoch ) {
            int t = T[ epoch - 1 ];
            s[ epoch ] = F / ( L - t );

            int tc = t + 1;
            double sm = 0;

            //double sa = ( F - (wallets[ tc ] - paid2acc) ) / (L - (tc - T[ epoch - 1 ]) );
            double sa = ( F - (wallets[ tc ] - paid2acc) ) / ( L - (tc - T[ epoch - 1 ] ) );

            while ( wallets[ tc ] - paid2acc < sa  ){
                s[ epoch ] = sa;
                sm += wallets[ tc ] - paid2acc;
                t = tc++;
                sa = ( F - sm - (wallets[ tc ] - paid2acc) ) / ( L - (tc - T[ epoch - 1 ] ) );
            }

            T[ epoch ] = t;
            paid2acc += s[ epoch ];
        }

    }

    public double withdraw( int wallet_index, int epoch, double r ) {

        if ( wallet_index < T[ epoch ] ) {
            return wallets[ wallet_index ];
        }

        int withdrawP = 0;
        for(int i = 1; i <= epoch - 1; ++i )
            withdrawP += s[ i ];

        return Math.min( (r * s[ epoch ]), (double) wallets[ wallet_index ] - withdrawP ) + withdrawP;
    }


    public void printWalletFlow(int wallet_index) {
        System.out.printf( "%3d | %4d | ", wallet_index, wallets[ wallet_index ] );

        for(int i = 1; i <= N; ++i)
            System.out.printf("%.2f |", withdraw(wallet_index, i, 1));

        System.out.println();
    }

    public void printWithdrawalFlow() {
        System.out.println( "Per epoch: " + F );
        for(int i = 1; i < wallets.length; ++i )
            printWalletFlow( i );
    }
}
