package Sequencial_Paralelo;

/**
 * Solução para o Problema do Caixeiro Viajante usando Programação Dinâmica.
 */

import java.util.Arrays;

public class CaixeiroViajanteParalelo {
    private final int numCidades;
    private final double[][] matrizDistancias;
    private final double[][] dp;
    private final int[][] caminhoReconstruido;
    private static final double INFINITO = Double.POSITIVE_INFINITY;

    // Construtor que inicializa as estruturas de dados
    public CaixeiroViajanteParalelo(double[][] matrizDistancias) {
        this.numCidades = matrizDistancias.length;
        this.matrizDistancias = matrizDistancias;
        this.dp = new double[1 << numCidades][numCidades];
        this.caminhoReconstruido = new int[1 << numCidades][numCidades];

        // Inicializa todas as distâncias como infinito
        for (double[] linha : dp) {
            Arrays.fill(linha, INFINITO);
        }
    }

    // Resolve o problema do Caixeiro Viajante e retorna o custo mínimo
    public double resolver() {
        dp[1][0] = 0; // Cidade inicial

        // Preenchimento da tabela DP
        for (int mascara = 1; mascara < (1 << numCidades); mascara++) {
            for (int u = 0; u < numCidades; u++) {
                if ((mascara & (1 << u)) != 0) { // Verifica se a cidade 'u' está no conjunto atual
                    for (int v = 0; v < numCidades; v++) {
                        if ((mascara & (1 << v)) == 0) { // Verifica se a cidade 'v' ainda não foi visitada
                            double novaDistancia = dp[mascara][u] + matrizDistancias[u][v];
                            int novaMascara = mascara | (1 << v);
                            if (novaDistancia < dp[novaMascara][v]) {
                                dp[novaMascara][v] = novaDistancia;
                                caminhoReconstruido[novaMascara][v] = u;
                            }
                        }
                    }
                }
            }
        }

        // Calcula o custo mínimo do ciclo completo
        double custoMinimo = INFINITO;
        int ultimoVisitado = -1;
        int mascaraFinal = (1 << numCidades) - 1;
        for (int i = 1; i < numCidades; i++) {
            double custoAtual = dp[mascaraFinal][i] + matrizDistancias[i][0];
            if (custoAtual < custoMinimo) {
                custoMinimo = custoAtual;
                ultimoVisitado = i;
            }
        }

        return custoMinimo;
    }

    // Reconstrói e exibe o caminho ótimo
    public void imprimirCaminho() {
        int mascara = (1 << numCidades) - 1;
        int cidadeAtual = 0;

        for (int i = 1; i < numCidades; i++) {
            if (dp[mascara][i] + matrizDistancias[i][0] == dp[mascara | (1 << i)][0]) {
                cidadeAtual = i;
                break;
            }
        }

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
