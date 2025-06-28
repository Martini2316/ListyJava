// package Lista2;

import javax.swing.*;
import java.io.*;
import java.util.*;

// Klasa reprezentująca pytanie testowe
class Pytanie implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int nrPytania;
    private String trescPytania;
    private String[] odpowiedzi;
    private int nrPrawidlowejOdpowiedzi;
    private int[] kolejnoscOdpowiedzi; // Tablica przechowująca losową kolejność odpowiedzi
    
    public Pytanie(int nrPytania, String trescPytania, String[] odpowiedzi, int nrPrawidlowejOdpowiedzi) {
        this.nrPytania = nrPytania;
        this.trescPytania = trescPytania;
        this.odpowiedzi = odpowiedzi;
        this.nrPrawidlowejOdpowiedzi = nrPrawidlowejOdpowiedzi;
        this.kolejnoscOdpowiedzi = new int[odpowiedzi.length];
        
        // Inicjalizacja tablicy kolejności
        for (int i = 0; i < kolejnoscOdpowiedzi.length; i++) {
            kolejnoscOdpowiedzi[i] = i;
        }
        
        // Losowe mieszanie kolejności odpowiedzi
        tasujOdpowiedzi();
    }
    
    // Metoda do tasowania kolejności odpowiedzi
    public void tasujOdpowiedzi() {
        Random rand = new Random();
        for (int i = kolejnoscOdpowiedzi.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int temp = kolejnoscOdpowiedzi[i];
            kolejnoscOdpowiedzi[i] = kolejnoscOdpowiedzi[j];
            kolejnoscOdpowiedzi[j] = temp;
        }
    }
    
    public int getNrPytania() {
        return nrPytania;
    }
    
    public String getTrescPytania() {
        return trescPytania;
    }
    
    public String getOdpowiedz(int index) {
        return odpowiedzi[kolejnoscOdpowiedzi[index]];
    }
    
    public int getNrPrawidlowejOdpowiedzi() {
        return nrPrawidlowejOdpowiedzi;
    }
    
    // Sprawdza, czy wybrana odpowiedź jest prawidłowa
    public boolean czyPrawidlowaOdpowiedz(int wybranaOdpowiedz) {
        return kolejnoscOdpowiedzi[wybranaOdpowiedz] == nrPrawidlowejOdpowiedzi - 1;
    }
    
    public int getIloscOdpowiedzi() {
        return odpowiedzi.length;
    }
}

// Klasa przechowująca wynik pojedynczego testu
class WynikTestu implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String login;
    private int iloscPytan;
    private int iloscPoprawnych;
    private Date dataTestu;
    private boolean czyZdany;
    
    public WynikTestu(String login, int iloscPytan, int iloscPoprawnych, int progZaliczenia) {
        this.login = login;
        this.iloscPytan = iloscPytan;
        this.iloscPoprawnych = iloscPoprawnych;
        this.dataTestu = new Date();
        this.czyZdany = iloscPoprawnych >= progZaliczenia;
    }
    
    public String getLogin() {
        return login;
    }
    
    public int getIloscPytan() {
        return iloscPytan;
    }
    
    public int getIloscPoprawnych() {
        return iloscPoprawnych;
    }
    
    public Date getDataTestu() {
        return dataTestu;
    }
    
    public boolean isCzyZdany() {
        return czyZdany;
    }
    
    public String toString() {
        return "Login: " + login + 
               ", Data testu: " + dataTestu + 
               ", Wynik: " + iloscPoprawnych + "/" + iloscPytan + 
               " (" + Math.round((double)iloscPoprawnych/iloscPytan * 100) + "%)" +
               ", Test " + (czyZdany ? "ZDANY" : "NIEZDANY");
    }
}

// Klasa przechowująca stan aktualnego testu
class StanTestu implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String login;
    private List<Pytanie> pytania;
    private int aktualnyIndeks;
    private int iloscPoprawnych;
    private int progZaliczenia;
    
    public StanTestu(String login, List<Pytanie> pytania, int progZaliczenia) {
        this.login = login;
        this.pytania = pytania;
        this.aktualnyIndeks = 0;
        this.iloscPoprawnych = 0;
        this.progZaliczenia = progZaliczenia;
    }
    
    public String getLogin() {
        return login;
    }
    
    public Pytanie getAktualnePytanie() {
        if (aktualnyIndeks < pytania.size()) {
            return pytania.get(aktualnyIndeks);
        }
        return null;
    }
    
    public void nastepnePytanie(boolean czyPoprawna) {
        if (czyPoprawna) {
            iloscPoprawnych++;
        }
        aktualnyIndeks++;
    }
    
    public boolean czyKoniecTestu() {
        return aktualnyIndeks >= pytania.size();
    }
    
    public int getIloscPoprawnych() {
        return iloscPoprawnych;
    }
    
    public int getIloscPytan() {
        return pytania.size();
    }
    
    public int getAktualnyIndeks() {
        return aktualnyIndeks;
    }
    
    public int getProgZaliczenia() {
        return progZaliczenia;
    }
}

// Główna klasa aplikacji
public class zadanie1 {
    private static final String PLIK_PYTANIA = "pytania.txt";
    private static final String PLIK_WYNIKI = "wyniki.dat";
    private static final String PLIK_STAN = "stan_testu.ser";
    
    private List<Pytanie> pytania;
    private List<WynikTestu> wyniki;
    private StanTestu aktualnyTest;
    private int progZaliczenia; // Próg zaliczenia testu
    
    public zadanie1() {
        pytania = new ArrayList<>();
        wyniki = new ArrayList<>();
        
        // Wczytanie pytań z pliku wraz z progiem zaliczenia
        wczytajPytania();
        
        // Wczytanie wyników, jeśli plik istnieje
        wczytajWyniki();
    }
    
    // Metoda wczytująca pytania z pliku tekstowego
    private void wczytajPytania() {
        try (BufferedReader reader = new BufferedReader(new FileReader(PLIK_PYTANIA))) {
            String linia;
            
            if ((linia = reader.readLine()) != null) {
                if (linia.startsWith("PROG_ZALICZENIA:")) {
                    progZaliczenia = Integer.parseInt(linia.substring("PROG_ZALICZENIA:".length()).trim());
                } else {
                    przetworzLiniePytania(linia);
                }
            }
            
            // Wczytujemy pozostałe pytania
            while ((linia = reader.readLine()) != null) {
                przetworzLiniePytania(linia);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Błąd podczas wczytywania pytań: " + e.getMessage(), 
                                        "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Pomocnicza metoda do przetwarzania linii pytania
    private void przetworzLiniePytania(String linia) {
        String[] elementy = linia.split(";");
        if (elementy.length >= 7) {
            int nrPytania = Integer.parseInt(elementy[0].trim());
            String trescPytania = elementy[1].trim();
            String[] odpowiedzi = new String[4];
            for (int i = 0; i < 4; i++) {
                odpowiedzi[i] = elementy[i + 2].trim();
            }
            int nrPrawidlowejOdpowiedzi = Integer.parseInt(elementy[6].trim());
            
            pytania.add(new Pytanie(nrPytania, trescPytania, odpowiedzi, nrPrawidlowejOdpowiedzi));
        }
    }
    
    // Metoda zapisująca pytania do pliku tekstowego (dla generowania przykładowych pytań)
    private void zapiszPrzykladowePytania() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PLIK_PYTANIA))) {
            // Zapisujemy próg zaliczenia jako pierwszą linię
            writer.println("PROG_ZALICZENIA:3");
            
            // Zapisujemy pytania
            writer.println("1;Jaka jest stolica Polski?;Warszawa;Kraków;Gdańsk;Poznań;1");
            writer.println("2;Ile wynosi 2 + 2?;3;4;5;6;2");
            writer.println("3;Kto napisał 'Pan Tadeusz'?;Juliusz Słowacki;Henryk Sienkiewicz;Adam Mickiewicz;Bolesław Prus;3");
            writer.println("4;W którym roku Polska wstąpiła do UE?;2000;2002;2004;2006;3");
            writer.println("5;Który pierwiastek ma symbol 'O'?;Złoto;Tlen;Ołów;Osm;2");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Błąd podczas zapisywania przykładowych pytań: " + e.getMessage(), 
                                        "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Metoda wczytująca wyniki z pliku binarnego
    private void wczytajWyniki() {
        File plik = new File(PLIK_WYNIKI);
        if (!plik.exists()) {
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PLIK_WYNIKI))) {
            wyniki = (List<WynikTestu>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Błąd podczas wczytywania wyników: " + e.getMessage(), 
                                        "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Metoda zapisująca wyniki do pliku binarnego
    private void zapiszWyniki() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PLIK_WYNIKI))) {
            oos.writeObject(wyniki);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Błąd podczas zapisywania wyników: " + e.getMessage(), 
                                        "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Metoda zapisująca aktualny stan testu przy użyciu serializacji
    private void zapiszStanTestu() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PLIK_STAN))) {
            oos.writeObject(aktualnyTest);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Błąd podczas zapisywania stanu testu: " + e.getMessage(), 
                                        "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Metoda wczytująca stan testu przy użyciu serializacji
    private boolean wczytajStanTestu(String login) {
        File plik = new File(PLIK_STAN);
        if (!plik.exists()) {
            return false;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PLIK_STAN))) {
            StanTestu stan = (StanTestu) ois.readObject();
            
            // Sprawdzamy, czy wczytany stan testu należy do tego samego użytkownika
            if (stan.getLogin().equals(login)) {
                aktualnyTest = stan;
                return true;
            }
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Błąd podczas wczytywania stanu testu: " + e.getMessage(), 
                                        "Błąd", JOptionPane.ERROR_MESSAGE);
        }
        
        return false;
    }
    
    // Metoda usuwająca plik ze stanem testu
    private void usunStanTestu() {
        File plik = new File(PLIK_STAN);
        if (plik.exists()) {
            plik.delete();
        }
    }
    
    // Metoda prezentująca pytanie użytkownikowi
    private void prezentujPytanie() {
        Pytanie pytanie = aktualnyTest.getAktualnePytanie();
        if (pytanie == null) {
            // Koniec testu
            zakonczTest();
            return;
        }
        
        // Budujemy opcje do wyboru
        String[] opcje = new String[pytanie.getIloscOdpowiedzi()];
        for (int i = 0; i < opcje.length; i++) {
            opcje[i] = pytanie.getOdpowiedz(i);
        }
        
        // Wyświetlamy pytanie w oknie dialogowym
        int odpowiedzIndex = JOptionPane.showOptionDialog(
            null,
            "Pytanie " + (aktualnyTest.getAktualnyIndeks() + 1) + " z " + aktualnyTest.getIloscPytan() + ":\n" + pytanie.getTrescPytania(),
            "Test",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opcje,
            opcje[0]
        );
        
        // Sprawdzamy, czy użytkownik zamknął okno
        if (odpowiedzIndex == JOptionPane.CLOSED_OPTION) {
            // Zapisujemy stan testu
            zapiszStanTestu();
            JOptionPane.showMessageDialog(null, "Test został przerwany. Możesz go kontynuować później.", 
                                         "Test przerwany", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
        
        // Sprawdzamy odpowiedź
        boolean czyPoprawna = pytanie.czyPrawidlowaOdpowiedz(odpowiedzIndex);
        String komunikat = czyPoprawna ? "Odpowiedź poprawna!" : "Odpowiedź niepoprawna!";
        JOptionPane.showMessageDialog(null, komunikat, "Wynik odpowiedzi", 
                                     czyPoprawna ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        
        // Przechodzimy do następnego pytania
        aktualnyTest.nastepnePytanie(czyPoprawna);
        
        // Zapisujemy aktualny stan testu
        zapiszStanTestu();
        
        // Sprawdzamy, czy to koniec testu
        if (aktualnyTest.czyKoniecTestu()) {
            zakonczTest();
        } else {
            // Prezentujemy kolejne pytanie
            prezentujPytanie();
        }
    }
    
    // Metoda kończąca test i zapisująca wyniki
    private void zakonczTest() {
        // Tworzymy nowy wynik testu
        WynikTestu wynik = new WynikTestu(
            aktualnyTest.getLogin(),
            aktualnyTest.getIloscPytan(),
            aktualnyTest.getIloscPoprawnych(),
            aktualnyTest.getProgZaliczenia()
        );
        
        // Dodajemy wynik do listy wyników
        wyniki.add(wynik);
        
        // Zapisujemy wyniki
        zapiszWyniki();
        
        // Usuwamy plik ze stanem testu
        usunStanTestu();
        
        // Wyświetlamy komunikat o zakończeniu testu
        JOptionPane.showMessageDialog(null, 
            "Test zakończony!\n\n" +
            "Twój wynik: " + wynik.getIloscPoprawnych() + " / " + wynik.getIloscPytan() + "\n" +
            "Procentowo: " + Math.round((double)wynik.getIloscPoprawnych()/wynik.getIloscPytan() * 100) + "%\n" +
            "Wynik testu: " + (wynik.isCzyZdany() ? "ZDANY" : "NIEZDANY"), 
            "Koniec testu", 
            wynik.isCzyZdany() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }
    
    // Metoda wyświetlająca wyniki testów
    private void wyswietlWyniki() {
        if (wyniki.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Brak wyników do wyświetlenia.", 
                                         "Wyniki", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder sb = new StringBuilder("Lista wyników testów:\n\n");
        for (int i = 0; i < wyniki.size(); i++) {
            WynikTestu wynik = wyniki.get(i);
            sb.append(i + 1).append(". ").append(wynik.toString()).append("\n");
        }
        
        JOptionPane.showMessageDialog(null, sb.toString(), "Wyniki testów", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Metoda tasująca kolejność pytań
    private void tasujPytania() {
        Collections.shuffle(pytania);
    }
    
    // Główna metoda uruchamiająca aplikację
    public void uruchom() {
        // Tworzenie przykładowych pytań, jeśli plik nie istnieje
        File plikPytania = new File(PLIK_PYTANIA);
        if (!plikPytania.exists()) {
            zapiszPrzykladowePytania();
            wczytajPytania();
        }
        
        // Sprawdzamy, czy mamy pytania
        if (pytania.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Brak pytań w bazie danych. Aplikacja zostanie zamknięta.", 
                                         "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Pytamy użytkownika o login
        String login = JOptionPane.showInputDialog(null, "Podaj swój login:", "Logowanie", JOptionPane.QUESTION_MESSAGE);
        if (login == null || login.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Login jest wymagany. Aplikacja zostanie zamknięta.", 
                                         "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Jeśli użytkownik to Admin, wyświetlamy wyniki
        if (login.equals("Admin")) {
            wyswietlWyniki();
            return;
        }
        
        // Sprawdzamy, czy istnieje niedokończony test dla tego użytkownika
        if (wczytajStanTestu(login)) {
            int odpowiedz = JOptionPane.showConfirmDialog(null, 
                "Znaleziono niedokończony test. Czy chcesz go kontynuować?",
                "Test niedokończony", 
                JOptionPane.YES_NO_OPTION);
            
            if (odpowiedz == JOptionPane.NO_OPTION) {
                // Usuwamy plik ze stanem testu
                usunStanTestu();
                aktualnyTest = null;
            }
        }
        
        // Jeśli nie ma niedokończonego testu, tworzymy nowy
        if (aktualnyTest == null) {
            // Tasujemy pytania
            tasujPytania();
            
            // Tworzymy nowy stan testu
            aktualnyTest = new StanTestu(login, pytania, progZaliczenia);
        }
        
        // Rozpoczynamy test
        prezentujPytanie();
    }
    
    public static void main(String[] args) {
        zadanie1 aplikacja = new zadanie1();
        aplikacja.uruchom();
    }
}