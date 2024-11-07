/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Distribuido;

/**
 *
 * @author Luiz Galhardo
 */
import java.rmi.Naming;
import java.util.Arrays;

public class TSPClient {
    private final int n;  // Número de cidades
    private final double[][] dist;  // Matriz de distâncias
    private final double[][] dp;  // DP para armazenar as menores distâncias
    private static final double INF = Double.POSITIVE_INFINITY;
    
    public TSPClient(double[][] dist) {
        this.n = dist.length;
        this.dist = dist;
        this.dp = new double[1 << n][n];
        
        for (double[] row : dp) {
            Arrays.fill(row, INF);
        }
        dp[1][0] = 0;  // Define o ponto de partida como a cidade 0
    }
    
    public double resolver() {
        try {
            TSPService tspService = (TSPService) Naming.lookup("rmi://localhost/TSPService");
            
            for (int mask = 1; mask < (1 << n); mask++) {
                for (int u = 0; u < n; u++) {
                    if ((mask & (1 << u)) != 0) {
                        double resultado = tspService.calcularDistancia(mask, u, dist, dp);
                        dp[mask][u] = Math.min(dp[mask][u], resultado);
                    }
                }
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
        } catch (Exception e) {
            e.printStackTrace();
            return INF;
        }
    }

    public static void main(String[] args) {
        double[][] dist = {
            {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 30},
            {20, 25, 30, 0}
        };

        TSPClient caixeiro = new TSPClient(dist);
        
        long inicio = System.nanoTime();
        double custoMinimo = caixeiro.resolver();
        long fim = System.nanoTime();

        double tempoExecucao = (fim - inicio) / 1_000_000.0;  // Converter para milissegundos
        System.out.println("Custo mínimo: " + custoMinimo);
        System.out.printf("Tempo de execução: %.3f ms%n", tempoExecucao);
    }
}

