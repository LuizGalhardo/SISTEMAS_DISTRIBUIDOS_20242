/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Sequencial_Paralelo;

/**
 *
 * @author Luiz Galhardo
 */
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CaixeiroViajanteParalelo {
    private final int n;  // Número de cidades
    private final double[][] dist;  // Matriz de distâncias
    private final double[][] dp;  // DP para armazenar as menores distâncias
    private final int[][] caminho;  // Para reconstruir o caminho
    private static final double INF = Double.POSITIVE_INFINITY;
    private final ExecutorService executor; // Pool de threads

    // Construtor para inicializar as variáveis
    public CaixeiroViajanteParalelo(double[][] dist) {
        this.n = dist.length;
        this.dist = dist;
        this.dp = new double[1 << n][n];
        this.caminho = new int[1 << n][n];
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (double[] row : dp) {
            Arrays.fill(row, INF);
        }
    }

    // Método para resolver o TSP de forma paralela
    public double resolver() {
        dp[1][0] = 0;  // Define o ponto de partida como a cidade 0

        // Para cada conjunto de cidades visitadas (máscara de bits)
        for (int mask = 1; mask < (1 << n); mask++) {
            for (int u = 0; u < n; u++) {
                if ((mask & (1 << u)) != 0) {  // Se u está no conjunto representado por mask
                    final int finalMask = mask;
                    final int finalU = u;

                    // Executa paralelamente para cada cidade v que ainda não foi visitada
                    executor.submit(() -> {
                        for (int v = 0; v < n; v++) {
                            if ((finalMask & (1 << v)) == 0) {  // Se v não está em mask
                                double novaDist = dp[finalMask][finalU] + dist[finalU][v];
                                int novaMask = finalMask | (1 << v);

                                synchronized (dp) {  // Sincroniza para atualizar dp
                                    if (novaDist < dp[novaMask][v]) {
                                        dp[novaMask][v] = novaDist;
                                        caminho[novaMask][v] = finalU;
                                    }
                                }
                            }
                        }
                    });
                }
            }
        }

        // Aguarda o término das threads
        try {
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Finalizar o ciclo voltando ao ponto de partida
        double minCost = INF;
        int finalMask = (1 << n) - 1;
        for (int i = 1; i < n; i++) {
            double custo = dp[finalMask][i] + dist[i][0];
            if (custo < minCost) {
                minCost = custo;
            }
        }

        return minCost;
    }

    // Método para imprimir o caminho ótimo
    public void imprimirCaminho() {
        int mask = (1 << n) - 1;
        int u = 0;

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

        System.out.print("Caminho ótimo: ");
        for (int i = 0; i < n; i++) {
            System.out.print(caminhoOtimo[i] + " -> ");
        }
        System.out.println("0");
    }

    public static void main(String[] args) {
        double[][] dist = {
            {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 30},
            {20, 25, 30, 0}
        };

        CaixeiroViajanteParalelo caixeiro = new CaixeiroViajanteParalelo(dist);

        long inicio = System.nanoTime();
        double custoMinimo = caixeiro.resolver();
        long fim = System.nanoTime();

        double tempoExecucao = (fim - inicio) / 1_000_000.0;  // Converter para milissegundos
        System.out.println("Custo mínimo: " + custoMinimo);
        System.out.printf("Tempo de execução: %.3f ms%n", tempoExecucao);

        caixeiro.imprimirCaminho();
    }
}

