package Sequencial_Paralelo;

import java.util.Arrays;

public class CaixeiroViajanteParalelo {
    private final int numCidades;
    private final double[][] matrizDistancias;
    private final double[][] dp;
    private final int[][] caminhoReconstruido;
    private static final double INFINITO = Double.POSITIVE_INFINITY;

    public CaixeiroViajanteParalelo(double[][] matrizDistancias) {
        this.numCidades = matrizDistancias.length;
        this.matrizDistancias = matrizDistancias;
        this.dp = new double[1 << numCidades][numCidades];
        this.caminhoReconstruido = new int[1 << numCidades][numCidades];

        for (double[] linha : dp) {
            Arrays.fill(linha, INFINITO);
        }
    }

    public double resolver() {
        dp[1][0] = 0; // Cidade inicial

        // Criação e execução das threads
        Thread[] threads = new Thread[(1 << numCidades)];
        for (int mascara = 1; mascara < (1 << numCidades); mascara++) {
            final int mascaraAtual = mascara; // Variável final para uso na thread
            threads[mascara] = new Thread(() -> preencherDP(mascaraAtual));
            threads[mascara].start(); // Inicia a thread
        }

        // Espera todas as threads terminarem
        for (int mascara = 1; mascara < (1 << numCidades); mascara++) {
            try {
                threads[mascara].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        double custoMinimo = INFINITO;
        int mascaraFinal = (1 << numCidades) - 1;

        for (int i = 1; i < numCidades; i++) {
            double custoAtual = dp[mascaraFinal][i] + matrizDistancias[i][0];
            if (custoAtual < custoMinimo) {
                custoMinimo = custoAtual;
            }
        }

        return custoMinimo;
    }

    private void preencherDP(int mascara) {
        for (int u = 0; u < numCidades; u++) {
            if ((mascara & (1 << u)) != 0) { // Verifica se a cidade 'u' está no conjunto
                for (int v = 0; v < numCidades; v++) {
                    if ((mascara & (1 << v)) == 0) { // Verifica se a cidade 'v' ainda não foi visitada
                        double novaDistancia = dp[mascara][u] + matrizDistancias[u][v];
                        int novaMascara = mascara | (1 << v);

                        synchronized (dp) { // Sincroniza para evitar condições de corrida
                            if (novaDistancia < dp[novaMascara][v]) {
                                dp[novaMascara][v] = novaDistancia;
                                caminhoReconstruido[novaMascara][v] = u;
                            }
                        }
                    }
                }
            }
        }
    }

    public void imprimirCaminho() {
        int mascara = (1 << numCidades) - 1;
        int cidadeAtual = 0;

        int[] caminhoOtimo = new int[numCidades];
        for (int i = numCidades - 1; i >= 0; i--) {
            caminhoOtimo[i] = cidadeAtual;
            int proximaCidade = caminhoReconstruido[mascara][cidadeAtual];
            mascara ^= (1 << cidadeAtual);
            cidadeAtual = proximaCidade;
        }

        System.out.print("Caminho ótimo: ");
        for (int i = 0; i < numCidades; i++) {
            System.out.print(caminhoOtimo[i] + " -> ");
        }
        System.out.println("0");
    }

    public static void main(String[] args) {
        double[][] matrizDistancias = {
            {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 30},
            {20, 25, 30, 0}
        };

        CaixeiroViajanteParalelo tsp = new CaixeiroViajanteParalelo(matrizDistancias);

        long inicio = System.nanoTime();
        double custoMinimo = tsp.resolver();
        long fim = System.nanoTime();

        System.out.printf("Custo mínimo: %.2f%n", custoMinimo);
        System.out.printf("Tempo de execução: %.3f ms%n", (fim - inicio) / 1_000_000.0);

        tsp.imprimirCaminho();
    }
}
