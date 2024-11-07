
package Sequencial_Paralelo;

/**
 *
 * @author Luiz Galhardo
 */

import java.util.Arrays;

public class CaixeiroViajante {
    private final int n;  // Número de cidades
    private final double[][] dist;  // Matriz de distâncias
    private final double[][] dp;  // DP para armazenar as menores distâncias
    private final int[][] caminho;  // Para reconstruir o caminho
    private static final double INF = Double.POSITIVE_INFINITY;

    // Construtor para inicializar as variáveis
    public CaixeiroViajante(double[][] dist) {
        this.n = dist.length;
        this.dist = dist;
        this.dp = new double[1 << n][n];
        this.caminho = new int[1 << n][n];

        for (double[] row : dp) {
            Arrays.fill(row, INF);
        }
    }

    // Método para resolver o TSP
    public double resolver() {
        // Definir o ponto de partida como a cidade 0
        dp[1][0] = 0;

        // Preencher a tabela dp usando programação dinâmica
        for (int mask = 1; mask < (1 << n); mask++) {
            for (int u = 0; u < n; u++) {
                if ((mask & (1 << u)) != 0) {  // Se u está no conjunto representado por mask
                    for (int v = 0; v < n; v++) {
                        if ((mask & (1 << v)) == 0) {  // Se v não está em mask
                            double novaDist = dp[mask][u] + dist[u][v];
                            int novaMask = mask | (1 << v);
                            if (novaDist < dp[novaMask][v]) {
                                dp[novaMask][v] = novaDist;
                                caminho[novaMask][v] = u;
                            }
                        }
                    }
                }
            }
        }

        // Finalizar o ciclo voltando ao ponto de partida
        double minCost = INF;
        int ultimo = -1;
        int finalMask = (1 << n) - 1;
        for (int i = 1; i < n; i++) {
            double custo = dp[finalMask][i] + dist[i][0];
            if (custo < minCost) {
                minCost = custo;
                ultimo = i;
            }
        }

        // Retornar o custo mínimo
        return minCost;
    }

    // Método para imprimir o caminho ótimo
    public void imprimirCaminho() {
        int mask = (1 << n) - 1;
        int u = 0;

        // Encontrar o último nó do caminho ótimo
        for (int i = 1; i < n; i++) {
            if (dp[mask][i] + dist[i][0] == dp[mask | (1 << i)][0]) {
                u = i;
                break;
            }
        }

        int[] caminhoOtimo = new int[n];
        for (int i = n - 1; i >= 0; i--) {
            caminhoOtimo[i] = u;
            int temp = caminho[mask][u];
            mask ^= (1 << u);
            u = temp;
        }

        // Exibir o caminho
        System.out.print("Caminho ótimo: ");
        for (int i = 0; i < n; i++) {
            System.out.print(caminhoOtimo[i] + " -> ");
        }
        System.out.println("0");
    }

    public static void main(String[] args) {
        // Matriz de distâncias entre as cidades
        double[][] dist = {
            {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 30},
            {20, 25, 30, 0}
        };

        CaixeiroViajante caixeiro = new CaixeiroViajante(dist);
        
        // Medir o tempo de execução
        long inicio = System.nanoTime();
        double custoMinimo = caixeiro.resolver();
        long fim = System.nanoTime();
        
        // Calcular e exibir o tempo de execução
        double tempoExecucao = (fim - inicio) / 1_000_000.0;  // Converter para milissegundos
        System.out.println("Custo mínimo: " + custoMinimo);
        System.out.printf("Tempo de execução: %.3f ms%n", tempoExecucao);

        // Imprimir o caminho ótimo
        caixeiro.imprimirCaminho();
    }
}
