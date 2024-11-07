/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Distribuido;

/**
 *
 * @author Luiz Galhardo
 */
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TSPService extends Remote {
    double calcularDistancia(int mask, int u, double[][] dist, double[][] dp) throws RemoteException;
}
