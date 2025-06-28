// package Lista1;

//Zmiana
//

import java.util.Scanner;

public class zadanie1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Program do obliczania średniej ocen");
        System.out.print("Podaj ile ocen chcesz wprowadzić: ");
        int liczbaOcen = scanner.nextInt();
        
        // Sprawdzenie czy liczba ocen jest prawidłowa
        if (liczbaOcen <= 0) {
            System.out.println("Liczba ocen musi być większa od 0. Program zostanie zakończony.");
            scanner.close();
            return;
        }
        
        double sumaOcen = 0;
        
        // Pobieranie ocen od użytkownika
        for (int i = 1; i <= liczbaOcen; i++) {
            System.out.print("Podaj ocenę " + i + ": ");
            double ocena = scanner.nextDouble();
            
            // Sprawdzenie czy ocena jest w prawidłowym zakresie (np. 2.0-5.0)
            if (ocena < 2.0 || ocena > 5.0) {
                System.out.println("Podana ocena jest nieprawidłowa (powinna być w zakresie 2.0-5.0). Spróbuj ponownie.");
                i--; // Powtórzenie tej samej iteracji
                continue;
            }
            
            sumaOcen += ocena;
        }
        
        // Obliczenie średniej
        double srednia = sumaOcen / liczbaOcen;
        
        // Wyświetlenie wyników
        System.out.println("\nWyniki:");
        System.out.printf("Średnia ocen: %.2f\n", srednia);
        
        // Sprawdzenie czy przysługuje stypendium
        if (srednia > 4.1) {
            System.out.println("Gratulacje! Z taką średnią przysługuje Ci stypendium naukowe.");
        } else {
            System.out.println("Niestety, średnia jest zbyt niska, aby otrzymać stypendium naukowe (wymagane minimum: > 4.1).");
        }
        
        scanner.close();
    }
}