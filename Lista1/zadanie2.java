// package Lista1;

//Zmiana
//

import java.util.Scanner;

public class zadanie2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Program obliczający n! oraz sumę ciągu 1/(1+n) - 1/(2+n) + 1/(3+n) - ...");
        System.out.print("Podaj wartość n: ");
        int n = scanner.nextInt();
        
        // Sprawdzenie czy n jest prawidłowe
        if (n < 0) {
            System.out.println("Wartość n musi być nieujemna. Program zostanie zakończony.");
            scanner.close();
            return;
        }
        
        // Obliczenie silni
        long silnia = 1;
        for (int i = 1; i <= n; i++) {
            silnia *= i;
        }
        
        // Obliczenie sumy ciągu z wizualizacją
        double sumaCiagu = 0;
        System.out.println("\nWizualizacja obliczeń dla ciągu:");
        System.out.println("----------------------------------");
        
        StringBuilder wyrazenie = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            double wartoscElementu = 1.0 / (i + n);
            
            // Zmieniamy znak dla nieparzystych i
            if (i % 2 == 1) {
                sumaCiagu += wartoscElementu;
                
                // Dodanie do wizualizacji
                if (i == 1) {
                    wyrazenie.append(String.format("1/%d", (i + n)));
                } else {
                    wyrazenie.append(String.format(" + 1/%d", (i + n)));
                }
                
                System.out.printf("Krok %d: %s = %.6f (suma częściowa: %.6f)\n", 
                    i, wyrazenie.toString(), wartoscElementu, sumaCiagu);
                
            } else {
                sumaCiagu -= wartoscElementu;
                
                // Dodanie do wizualizacji
                wyrazenie.append(String.format(" - 1/%d", (i + n)));
                
                System.out.printf("Krok %d: %s = %.6f (suma częściowa: %.6f)\n", 
                    i, wyrazenie.toString(), -wartoscElementu, sumaCiagu);
            }
        }
        
        System.out.println("----------------------------------");
        
        // Wyświetlenie wyników
        System.out.println("\nWyniki:");
        System.out.println("Wartość " + n + "! = " + silnia);
        System.out.printf("Suma ciągu: %.6f\n", sumaCiagu);
        
        scanner.close();
    }
}