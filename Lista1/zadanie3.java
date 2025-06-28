// package Lista1;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Abstrakcyjna klasa bazowa dla wszystkich utworów
abstract class Utwor {
    protected String tytul;
    protected String wykonawca;
    protected int czasTrwania; // czas trwania w sekundach

    public Utwor(String tytul, String wykonawca, int czasTrwania) {
        this.tytul = tytul;
        this.wykonawca = wykonawca;
        this.czasTrwania = czasTrwania;
    }

    // Gettery i settery
    public String getTytul() {
        return tytul;
    }

    public void setTytul(String tytul) {
        this.tytul = tytul;
    }

    public String getWykonawca() {
        return wykonawca;
    }

    public void setWykonawca(String wykonawca) {
        this.wykonawca = wykonawca;
    }

    public int getCzasTrwania() {
        return czasTrwania;
    }

    public void setCzasTrwania(int czasTrwania) {
        this.czasTrwania = czasTrwania;
    }

    // Metoda do formatowania czasu trwania
    public String sformatujCzas() {
        int minuty = czasTrwania / 60;
        int sekundy = czasTrwania % 60;
        return String.format("%d:%02d", minuty, sekundy);
    }

    // Metoda abstrakcyjna - każdy typ utworu będzie ją implementował inaczej
    public abstract String getInfoTechniczne();
    
    // Metoda abstrakcyjna do obliczania rozmiaru utworu w bajtach
    public abstract long getRozmiarWBajtach();

    // Metoda do wyświetlania informacji o utworze
    @Override
    public String toString() {
        return "Tytuł: " + tytul + ", Wykonawca: " + wykonawca + ", Czas trwania: " + sformatujCzas();
    }
    
    // Metoda pomocnicza do formatowania rozmiaru w bardziej czytelnej formie
    public static String formatujRozmiar(long bajtow) {
        if (bajtow < 1024) {
            return bajtow + " B";
        } else if (bajtow < 1024 * 1024) {
            return String.format("%.2f KB", bajtow / 1024.0);
        } else if (bajtow < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bajtow / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", bajtow / (1024.0 * 1024.0 * 1024.0));
        }
    }
}

// Utwór z płyty CD
class UtworCD extends Utwor {
    private int numerSciezki;
    private boolean chroniony; // czy utwór ma ochronę przed kopiowaniem
    
    // Stała dla Audio CD: 44.1kHz, 16-bit, stereo = 176.4 kB/s
    private static final long ROZMIAR_NA_SEKUNDE = 176400; // bajty na sekundę dla standardowego CD Audio

    public UtworCD(String tytul, String wykonawca, int czasTrwania, int numerSciezki, boolean chroniony) {
        super(tytul, wykonawca, czasTrwania);
        this.numerSciezki = numerSciezki;
        this.chroniony = chroniony;
    }

    public int getNumerSciezki() {
        return numerSciezki;
    }

    public void setNumerSciezki(int numerSciezki) {
        this.numerSciezki = numerSciezki;
    }

    public boolean isChroniony() {
        return chroniony;
    }

    public void setChroniony(boolean chroniony) {
        this.chroniony = chroniony;
    }

    @Override
    public String getInfoTechniczne() {
        return "Format: CD Audio, Numer ścieżki: " + numerSciezki + 
               ", Ochrona przed kopiowaniem: " + (chroniony ? "Tak" : "Nie") + 
               ", Rozmiar: " + formatujRozmiar(getRozmiarWBajtach());
    }
    
    @Override
    public long getRozmiarWBajtach() {
        // Dla CD Audio: czasTrwania * 44.1kHz * 16-bit * 2 kanały / 8 (bajty)
        return czasTrwania * ROZMIAR_NA_SEKUNDE;
    }

    @Override
    public String toString() {
        return super.toString() + ", " + getInfoTechniczne();
    }
}

// Plik MP3
class UtworMP3 extends Utwor {
    private int bitrateKbps;
    private String kodek;

    public UtworMP3(String tytul, String wykonawca, int czasTrwania, int bitrateKbps, String kodek) {
        super(tytul, wykonawca, czasTrwania);
        this.bitrateKbps = bitrateKbps;
        this.kodek = kodek;
    }

    public int getBitrateKbps() {
        return bitrateKbps;
    }

    public void setBitrateKbps(int bitrateKbps) {
        this.bitrateKbps = bitrateKbps;
    }

    public String getKodek() {
        return kodek;
    }

    public void setKodek(String kodek) {
        this.kodek = kodek;
    }

    @Override
    public String getInfoTechniczne() {
        return "Format: MP3, Bitrate: " + bitrateKbps + " kbps, Kodek: " + kodek + 
               ", Rozmiar: " + formatujRozmiar(getRozmiarWBajtach());
    }
    
    @Override
    public long getRozmiarWBajtach() {
        // Dla MP3: czasTrwania * bitrate / 8 (bajty)
        // Dodajemy 1% na nagłówki i metadane
        long podstawowyRozmiar = czasTrwania * bitrateKbps * 1000L / 8;
        return podstawowyRozmiar + (podstawowyRozmiar / 100);
    }

    @Override
    public String toString() {
        return super.toString() + ", " + getInfoTechniczne();
    }
}

// Plik nieskompresowany (np. WAV)
class UtworNieskompresowany extends Utwor {
    private int czestotliwoscProbkowania;
    private int bitowaProbka;
    private boolean stereo;

    public UtworNieskompresowany(String tytul, String wykonawca, int czasTrwania, 
                                int czestotliwoscProbkowania, int bitowaProbka, boolean stereo) {
        super(tytul, wykonawca, czasTrwania);
        this.czestotliwoscProbkowania = czestotliwoscProbkowania;
        this.bitowaProbka = bitowaProbka;
        this.stereo = stereo;
    }

    public int getCzestotliwoscProbkowania() {
        return czestotliwoscProbkowania;
    }

    public void setCzestotliwoscProbkowania(int czestotliwoscProbkowania) {
        this.czestotliwoscProbkowania = czestotliwoscProbkowania;
    }

    public int getBitowaProbka() {
        return bitowaProbka;
    }

    public void setBitowaProbka(int bitowaProbka) {
        this.bitowaProbka = bitowaProbka;
    }

    public boolean isStereo() {
        return stereo;
    }

    public void setStereo(boolean stereo) {
        this.stereo = stereo;
    }

    @Override
    public String getInfoTechniczne() {
        return "Format: Nieskompresowany, Częstotliwość próbkowania: " + czestotliwoscProbkowania + " Hz, " +
               "Bitowa próbka: " + bitowaProbka + " bit, Kanały: " + (stereo ? "Stereo" : "Mono") + 
               ", Rozmiar: " + formatujRozmiar(getRozmiarWBajtach());
    }
    
    @Override
    public long getRozmiarWBajtach() {
        // Dla nieskompresowanego audio (WAV):
        // czasTrwania * częstotliwość próbkowania * bitowa próbka * liczba kanałów / 8 (bajty)
        // Dodajemy 44 bajty na nagłówek WAV
        int kanaly = stereo ? 2 : 1;
        return 44 + (czasTrwania * czestotliwoscProbkowania * bitowaProbka * kanaly / 8);
    }

    @Override
    public String toString() {
        return super.toString() + ", " + getInfoTechniczne();
    }
}

// Klasa Album
class Album {
    private String tytul;
    private String wykonawca; // null jeśli składanka
    private int rokWydania;
    private double cenaZakupu;
    private List<Utwor> utwory;

    public Album(String tytul, String wykonawca, int rokWydania, double cenaZakupu) {
        this.tytul = tytul;
        this.wykonawca = wykonawca;
        this.rokWydania = rokWydania;
        this.cenaZakupu = cenaZakupu;
        this.utwory = new ArrayList<>();
    }

    // Gettery i settery
    public String getTytul() {
        return tytul;
    }

    public void setTytul(String tytul) {
        this.tytul = tytul;
    }

    public String getWykonawca() {
        return wykonawca;
    }

    public void setWykonawca(String wykonawca) {
        this.wykonawca = wykonawca;
    }

    public int getRokWydania() {
        return rokWydania;
    }

    public void setRokWydania(int rokWydania) {
        this.rokWydania = rokWydania;
    }

    public double getCenaZakupu() {
        return cenaZakupu;
    }

    public void setCenaZakupu(double cenaZakupu) {
        this.cenaZakupu = cenaZakupu;
    }

    public List<Utwor> getUtwory() {
        return utwory;
    }

    // Metody do zarządzania utworami
    public void dodajUtwor(Utwor utwor) {
        utwory.add(utwor);
    }

    public boolean usunUtwor(int index) {
        if (index >= 0 && index < utwory.size()) {
            utwory.remove(index);
            return true;
        }
        return false;
    }

    public int getLiczbaUtworow() {
        return utwory.size();
    }

    public int getCzasTrwaniaAlbumu() {
        int suma = 0;
        for (Utwor utwor : utwory) {
            suma += utwor.getCzasTrwania();
        }
        return suma;
    }
    
    // Metoda obliczająca całkowity rozmiar albumu w bajtach
    public long getCalkowityRozmiarWBajtach() {
        long suma = 0;
        for (Utwor utwor : utwory) {
            suma += utwor.getRozmiarWBajtach();
        }
        return suma;
    }
    
    // Metoda zwracająca sformatowany rozmiar albumu
    public String getSformatowanyRozmiar() {
        return Utwor.formatujRozmiar(getCalkowityRozmiarWBajtach());
    }

    // Formatowanie czasu trwania albumu
    public String sformatujCzasAlbumu() {
        int calkowityCzas = getCzasTrwaniaAlbumu();
        int godziny = calkowityCzas / 3600;
        int minuty = (calkowityCzas % 3600) / 60;
        int sekundy = calkowityCzas % 60;
        
        if (godziny > 0) {
            return String.format("%d:%02d:%02d", godziny, minuty, sekundy);
        } else {
            return String.format("%d:%02d", minuty, sekundy);
        }
    }

    @Override
    public String toString() {
        return "Album: " + tytul + 
               (wykonawca != null ? ", Wykonawca: " + wykonawca : ", Składanka") +
               ", Rok wydania: " + rokWydania +
               ", Cena zakupu: " + String.format("%.2f zł", cenaZakupu) +
               ", Liczba utworów: " + getLiczbaUtworow() +
               ", Czas trwania: " + sformatujCzasAlbumu() + 
               ", Całkowity rozmiar: " + getSformatowanyRozmiar();
    }
}

// Klasa do zarządzania kolekcją albumów
class KolekcjaAlbumow {
    private List<Album> albumy;
    private Scanner scanner;

    public KolekcjaAlbumow() {
        this.albumy = new ArrayList<>();
        this.scanner = new Scanner(System.in);
    }

    public void dodajAlbum() {
        System.out.println("\n=== DODAWANIE NOWEGO ALBUMU ===");
        
        System.out.print("Podaj tytuł albumu: ");
        String tytul = scanner.nextLine();
        
        System.out.print("Czy album jest składanką? (tak/nie): ");
        boolean jestSkladanka = scanner.nextLine().toLowerCase().startsWith("t");
        
        String wykonawca = null;
        if (!jestSkladanka) {
            System.out.print("Podaj wykonawcę albumu: ");
            wykonawca = scanner.nextLine();
        }
        
        System.out.print("Podaj rok wydania: ");
        int rokWydania = Integer.parseInt(scanner.nextLine());
        
        System.out.print("Podaj cenę zakupu: ");
        double cenaZakupu = Double.parseDouble(scanner.nextLine());
        
        Album album = new Album(tytul, wykonawca, rokWydania, cenaZakupu);
        albumy.add(album);
        
        System.out.println("Album został dodany. Czy chcesz teraz dodać utwory? (tak/nie)");
        if (scanner.nextLine().toLowerCase().startsWith("t")) {
            System.out.print("Ile utworów chcesz dodać? ");
            int liczbaUtworow = Integer.parseInt(scanner.nextLine());
            
            for (int i = 0; i < liczbaUtworow; i++) {
                dodajUtworDoAlbumu(album);
            }
        }
        
        System.out.println("Album został dodany do kolekcji.");
        System.out.println("Całkowity rozmiar albumu: " + album.getSformatowanyRozmiar());
    }

    private void dodajUtworDoAlbumu(Album album) {
        System.out.println("\n=== DODAWANIE UTWORU ===");
        
        System.out.print("Podaj tytuł utworu: ");
        String tytul = scanner.nextLine();
        
        System.out.print("Podaj wykonawcę utworu: ");
        String wykonawca = scanner.nextLine();
        
        System.out.print("Podaj czas trwania (w formacie MM:SS): ");
        String czasString = scanner.nextLine();
        String[] czasParts = czasString.split(":");
        int czasTrwania = Integer.parseInt(czasParts[0]) * 60 + Integer.parseInt(czasParts[1]);
        
        System.out.println("Wybierz format utworu:");
        System.out.println("1. CD Audio");
        System.out.println("2. MP3");
        System.out.println("3. Nieskompresowany (WAV)");
        System.out.print("Twój wybór: ");
        int wybor = Integer.parseInt(scanner.nextLine());
        
        Utwor utwor = null;
        
        switch(wybor) {
            case 1:
                System.out.print("Podaj numer ścieżki: ");
                int numerSciezki = Integer.parseInt(scanner.nextLine());
                
                System.out.print("Czy utwór jest chroniony przed kopiowaniem? (tak/nie): ");
                boolean chroniony = scanner.nextLine().toLowerCase().startsWith("t");
                
                utwor = new UtworCD(tytul, wykonawca, czasTrwania, numerSciezki, chroniony);
                break;
                
            case 2:
                System.out.print("Podaj bitrate (kbps): ");
                int bitrate = Integer.parseInt(scanner.nextLine());
                
                System.out.print("Podaj kodek (np. LAME, FRAUNHOFER): ");
                String kodek = scanner.nextLine();
                
                utwor = new UtworMP3(tytul, wykonawca, czasTrwania, bitrate, kodek);
                break;
                
            case 3:
                System.out.print("Podaj częstotliwość próbkowania (Hz): ");
                int czestotliwosc = Integer.parseInt(scanner.nextLine());
                
                System.out.print("Podaj bitową próbkę (8, 16, 24): ");
                int proba = Integer.parseInt(scanner.nextLine());
                
                System.out.print("Czy utwór jest stereo? (tak/nie): ");
                boolean stereo = scanner.nextLine().toLowerCase().startsWith("t");
                
                utwor = new UtworNieskompresowany(tytul, wykonawca, czasTrwania, czestotliwosc, proba, stereo);
                break;
                
            default:
                System.out.println("Nieprawidłowy wybór!");
                return;
        }
        
        album.dodajUtwor(utwor);
        System.out.println("Utwór został dodany do albumu.");
        System.out.println("Rozmiar utworu: " + Utwor.formatujRozmiar(utwor.getRozmiarWBajtach()));
    }

    public void usunAlbum() {
        if (albumy.isEmpty()) {
            System.out.println("Kolekcja jest pusta.");
            return;
        }
        
        wyswietlListeAlbumow();
        
        System.out.print("Podaj numer albumu do usunięcia: ");
        int index = Integer.parseInt(scanner.nextLine()) - 1;
        
        if (index >= 0 && index < albumy.size()) {
            Album usuniety = albumy.remove(index);
            System.out.println("Album \"" + usuniety.getTytul() + "\" został usunięty.");
        } else {
            System.out.println("Nieprawidłowy numer albumu!");
        }
    }

    public void edytujAlbum() {
        if (albumy.isEmpty()) {
            System.out.println("Kolekcja jest pusta.");
            return;
        }
        
        wyswietlListeAlbumow();
        
        System.out.print("Podaj numer albumu do edycji: ");
        int index = Integer.parseInt(scanner.nextLine()) - 1;
        
        if (index >= 0 && index < albumy.size()) {
            Album album = albumy.get(index);
            
            System.out.println("\n=== EDYCJA ALBUMU ===");
            System.out.println("Obecne dane: " + album);
            
            System.out.println("\nCo chcesz edytować?");
            System.out.println("1. Tytuł");
            System.out.println("2. Wykonawcę");
            System.out.println("3. Rok wydania");
            System.out.println("4. Cenę zakupu");
            System.out.println("5. Utwory");
            System.out.print("Twój wybór: ");
            
            int wybor = Integer.parseInt(scanner.nextLine());
            
            switch(wybor) {
                case 1:
                    System.out.print("Podaj nowy tytuł: ");
                    album.setTytul(scanner.nextLine());
                    break;
                    
                case 2:
                    System.out.print("Podaj nowego wykonawcę (lub 'składanka' jeśli to kompilacja): ");
                    String nowyWykonawca = scanner.nextLine();
                    if (nowyWykonawca.equalsIgnoreCase("składanka")) {
                        album.setWykonawca(null);
                    } else {
                        album.setWykonawca(nowyWykonawca);
                    }
                    break;
                    
                case 3:
                    System.out.print("Podaj nowy rok wydania: ");
                    album.setRokWydania(Integer.parseInt(scanner.nextLine()));
                    break;
                    
                case 4:
                    System.out.print("Podaj nową cenę zakupu: ");
                    album.setCenaZakupu(Double.parseDouble(scanner.nextLine()));
                    break;
                    
                case 5:
                    edytujUtworAlbumu(album);
                    break;
                    
                default:
                    System.out.println("Nieprawidłowy wybór!");
                    return;
            }
            
            System.out.println("Album został zaktualizowany.");
            System.out.println("Całkowity rozmiar albumu: " + album.getSformatowanyRozmiar());
        } else {
            System.out.println("Nieprawidłowy numer albumu!");
        }
    }

    private void edytujUtworAlbumu(Album album) {
        List<Utwor> utwory = album.getUtwory();
        
        if (utwory.isEmpty()) {
            System.out.println("Album nie zawiera żadnych utworów.");
            System.out.println("Czy chcesz dodać utwory? (tak/nie)");
            if (scanner.nextLine().toLowerCase().startsWith("t")) {
                dodajUtworDoAlbumu(album);
            }
            return;
        }
        
        System.out.println("\n=== UTWORY ALBUMU ===");
        for (int i = 0; i < utwory.size(); i++) {
            System.out.println((i + 1) + ". " + utwory.get(i));
        }
        
        System.out.println("\nCo chcesz zrobić?");
        System.out.println("1. Dodać nowy utwór");
        System.out.println("2. Usunąć utwór");
        System.out.println("3. Edytować utwór");
        System.out.print("Twój wybór: ");
        
        int wybor = Integer.parseInt(scanner.nextLine());
        
        switch(wybor) {
            case 1:
                dodajUtworDoAlbumu(album);
                break;
                
            case 2:
                System.out.print("Podaj numer utworu do usunięcia: ");
                int indexUsun = Integer.parseInt(scanner.nextLine()) - 1;
                
                if (album.usunUtwor(indexUsun)) {
                    System.out.println("Utwór został usunięty.");
                } else {
                    System.out.println("Nieprawidłowy numer utworu!");
                }
                break;
                
            case 3:
                System.out.print("Podaj numer utworu do edycji: ");
                int indexEdytuj = Integer.parseInt(scanner.nextLine()) - 1;
                
                if (indexEdytuj >= 0 && indexEdytuj < utwory.size()) {
                    Utwor utwor = utwory.get(indexEdytuj);
                    edytujUtwor(utwor);
                } else {
                    System.out.println("Nieprawidłowy numer utworu!");
                }
                break;
                
            default:
                System.out.println("Nieprawidłowy wybór!");
        }
    }

    private void edytujUtwor(Utwor utwor) {
        System.out.println("\n=== EDYCJA UTWORU ===");
        System.out.println("Obecne dane: " + utwor);
        
        System.out.println("\nCo chcesz edytować?");
        System.out.println("1. Tytuł");
        System.out.println("2. Wykonawcę");
        System.out.println("3. Czas trwania");
        System.out.println("4. Informacje techniczne");
        System.out.print("Twój wybór: ");
        
        int wybor = Integer.parseInt(scanner.nextLine());
        
        switch(wybor) {
            case 1:
                System.out.print("Podaj nowy tytuł: ");
                utwor.setTytul(scanner.nextLine());
                break;
                
            case 2:
                System.out.print("Podaj nowego wykonawcę: ");
                utwor.setWykonawca(scanner.nextLine());
                break;
                
            case 3:
                System.out.print("Podaj nowy czas trwania (w formacie MM:SS): ");
                String czasString = scanner.nextLine();
                String[] czasParts = czasString.split(":");
                int nowyCzas = Integer.parseInt(czasParts[0]) * 60 + Integer.parseInt(czasParts[1]);
                utwor.setCzasTrwania(nowyCzas);
                break;
                
            case 4:
                edytujInfoTechniczne(utwor);
                break;
                
            default:
                System.out.println("Nieprawidłowy wybór!");
                return;
        }
        
        System.out.println("Utwór został zaktualizowany.");
        System.out.println("Aktualny rozmiar utworu: " + Utwor.formatujRozmiar(utwor.getRozmiarWBajtach()));
    }

    private void edytujInfoTechniczne(Utwor utwor) {
        if (utwor instanceof UtworCD) {
            UtworCD utworCD = (UtworCD) utwor;
            
            System.out.print("Podaj nowy numer ścieżki: ");
            utworCD.setNumerSciezki(Integer.parseInt(scanner.nextLine()));
            
            System.out.print("Czy utwór jest chroniony przed kopiowaniem? (tak/nie): ");
            utworCD.setChroniony(scanner.nextLine().toLowerCase().startsWith("t"));
            
        } else if (utwor instanceof UtworMP3) {
            UtworMP3 utworMP3 = (UtworMP3) utwor;
            
            System.out.print("Podaj nowy bitrate (kbps): ");
            utworMP3.setBitrateKbps(Integer.parseInt(scanner.nextLine()));
            
            System.out.print("Podaj nowy kodek: ");
            utworMP3.setKodek(scanner.nextLine());
            
        } else if (utwor instanceof UtworNieskompresowany) {
            UtworNieskompresowany utworNS = (UtworNieskompresowany) utwor;
            
            System.out.print("Podaj nową częstotliwość próbkowania (Hz): ");
            utworNS.setCzestotliwoscProbkowania(Integer.parseInt(scanner.nextLine()));
            
            System.out.print("Podaj nową bitową próbkę (8, 16, 24): ");
            utworNS.setBitowaProbka(Integer.parseInt(scanner.nextLine()));
            
            System.out.print("Czy utwór jest stereo? (tak/nie): ");
            utworNS.setStereo(scanner.nextLine().toLowerCase().startsWith("t"));
        }
    }

    public void wyswietlSzczegolyAlbumu() {
        if (albumy.isEmpty()) {
            System.out.println("Kolekcja jest pusta.");
            return;
        }
        
        wyswietlListeAlbumow();
        
        System.out.print("Podaj numer albumu, którego szczegóły chcesz zobaczyć: ");
        int index = Integer.parseInt(scanner.nextLine()) - 1;
        
        if (index >= 0 && index < albumy.size()) {
            Album album = albumy.get(index);
            List<Utwor> utwory = album.getUtwory();
            
            System.out.println("\n=== SZCZEGÓŁY ALBUMU ===");
            System.out.println(album);
            
            if (utwory.isEmpty()) {
                System.out.println("Album nie zawiera żadnych utworów.");
            } else {
                System.out.println("\nLista utworów:");
                for (int i = 0; i < utwory.size(); i++) {
                    Utwor utwor = utwory.get(i);
                    System.out.println((i + 1) + ". " + utwor);
                    System.out.println("   Rozmiar: " + Utwor.formatujRozmiar(utwor.getRozmiarWBajtach()));
                }
                
                // Podsumowanie rozmiaru
                System.out.println("\nPodsumowanie rozmiaru albumu:");
                long calkowityRozmiar = album.getCalkowityRozmiarWBajtach();
                System.out.println("Całkowity rozmiar: " + Utwor.formatujRozmiar(calkowityRozmiar));
            }
        } else {
            System.out.println("Nieprawidłowy numer albumu!");
        }
    }

    public void wyswietlListeAlbumow() {
        if (albumy.isEmpty()) {
            System.out.println("Kolekcja jest pusta.");
            return;
        }
        
        System.out.println("\n=== LISTA ALBUMÓW ===");
        for (int i = 0; i < albumy.size(); i++) {
            Album album = albumy.get(i);
            System.out.println((i + 1) + ". " + album);
        }
    }
    
    // Metoda analizująca zużycie miejsca przez kolekcję
    public void analizujZuzycieMiejsca() {
        if (albumy.isEmpty()) {
            System.out.println("Kolekcja jest pusta.");
            return;
        }
        
        System.out.println("\n=== ANALIZA ZUŻYCIA MIEJSCA ===");
        
        long calkowitaWielkoscKolekcji = 0;
        Album najwiekszyAlbum = null;
        Utwor najwiekszyUtwor = null;
        
        for (Album album : albumy) {
            long rozmiarAlbumu = album.getCalkowityRozmiarWBajtach();
            calkowitaWielkoscKolekcji += rozmiarAlbumu;
            
            if (najwiekszyAlbum == null || rozmiarAlbumu > najwiekszyAlbum.getCalkowityRozmiarWBajtach()) {
                najwiekszyAlbum = album;
            }
            
            for (Utwor utwor : album.getUtwory()) {
                if (najwiekszyUtwor == null || utwor.getRozmiarWBajtach() > najwiekszyUtwor.getRozmiarWBajtach()) {
                    najwiekszyUtwor = utwor;
                }
            }
        }
        
        System.out.println("Całkowity rozmiar kolekcji: " + Utwor.formatujRozmiar(calkowitaWielkoscKolekcji));
        
        if (najwiekszyAlbum != null) {
            System.out.println("Największy album: " + najwiekszyAlbum.getTytul() + 
                              " (" + Utwor.formatujRozmiar(najwiekszyAlbum.getCalkowityRozmiarWBajtach()) + ")");
        }
        
        if (najwiekszyUtwor != null) {
            System.out.println("Największy utwór: " + najwiekszyUtwor.getTytul() + 
                              " (" + Utwor.formatujRozmiar(najwiekszyUtwor.getRozmiarWBajtach()) + ")");
        }
        
        // Analiza typów plików
        System.out.println("\nAnaliza typów plików:");
        
        int liczbaUtworowCD = 0;
        int liczbaUtworowMP3 = 0;
        int liczbaUtworowNieskompresowanych = 0;
        
        long rozmiarUtworowCD = 0;
        long rozmiarUtworowMP3 = 0;
        long rozmiarUtworowNieskompresowanych = 0;
        
        for (Album album : albumy) {
            for (Utwor utwor : album.getUtwory()) {
                if (utwor instanceof UtworCD) {
                    liczbaUtworowCD++;
                    rozmiarUtworowCD += utwor.getRozmiarWBajtach();
                } else if (utwor instanceof UtworMP3) {
                    liczbaUtworowMP3++;
                    rozmiarUtworowMP3 += utwor.getRozmiarWBajtach();
                } else if (utwor instanceof UtworNieskompresowany) {
                    liczbaUtworowNieskompresowanych++;
                    rozmiarUtworowNieskompresowanych += utwor.getRozmiarWBajtach();
                }
            }
        }
        
        System.out.println("Utwory CD: " + liczbaUtworowCD + " (łącznie: " + Utwor.formatujRozmiar(rozmiarUtworowCD) + ")");
        System.out.println("Utwory MP3: " + liczbaUtworowMP3 + " (łącznie: " + Utwor.formatujRozmiar(rozmiarUtworowMP3) + ")");
        System.out.println("Utwory nieskompresowane: " + liczbaUtworowNieskompresowanych + 
                           " (łącznie: " + Utwor.formatujRozmiar(rozmiarUtworowNieskompresowanych) + ")");
    }

    public void menu() {
        int wybor = 0;
        boolean koniec = false;
        
        while (!koniec) {
            System.out.println("\n=== ZARZĄDZANIE KOLEKCJĄ ALBUMÓW MUZYCZNYCH ===");
            System.out.println("1. Wyświetl listę albumów");
            System.out.println("2. Wyświetl szczegóły albumu");
            System.out.println("3. Dodaj nowy album");
            System.out.println("4. Edytuj album");
            System.out.println("5. Usuń album");
            System.out.println("6. Dodaj przykładowe dane do kolekcji");
            System.out.println("7. Analizuj zużycie miejsca");
            System.out.println("0. Wyjście");
            System.out.print("Twój wybór: ");
            
            try {
                wybor = Integer.parseInt(scanner.nextLine());
                
                switch(wybor) {
                    case 1:
                        wyswietlListeAlbumow();
                        break;
                        
                    case 2:
                        wyswietlSzczegolyAlbumu();
                        break;
                        
                    case 3:
                        dodajAlbum();
                        break;
                        
                    case 4:
                        edytujAlbum();
                        break;
                        
                    case 5:
                        usunAlbum();
                        break;
                        
                    case 6:
                        dodajPrzykladoweDane();
                        break;
                        
                    case 7:
                        analizujZuzycieMiejsca();
                        break;
                        
                    case 0:
                        koniec = true;
                        System.out.println("Do widzenia!");
                        break;
                        
                    default:
                        System.out.println("Nieprawidłowy wybór. Spróbuj ponownie.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Błąd: Wprowadź liczbę.");
            } catch (Exception e) {
                System.out.println("Błąd: " + e.getMessage());
            }
        }
    }
    
    // Metoda dodająca przykładowe dane do kolekcji
    public void dodajPrzykladoweDane() {
        // Album 1 - pojedynczy wykonawca
        Album album1 = new Album("Thriller", "Michael Jackson", 1982, 29.99);
        album1.dodajUtwor(new UtworCD("Thriller", "Michael Jackson", 358, 1, true));
        album1.dodajUtwor(new UtworCD("Beat It", "Michael Jackson", 258, 2, true));
        album1.dodajUtwor(new UtworMP3("Billie Jean", "Michael Jackson", 294, 320, "LAME"));
        albumy.add(album1);
        
        // Album 2 - składanka
        Album album2 = new Album("Największe Hity Lat 80", null, 2010, 39.99);
        album2.dodajUtwor(new UtworMP3("Take On Me", "A-ha", 225, 256, "FRAUNHOFER"));
        album2.dodajUtwor(new UtworNieskompresowany("Sweet Dreams", "Eurythmics", 216, 44100, 16, true));
        album2.dodajUtwor(new UtworCD("Girls Just Want to Have Fun", "Cyndi Lauper", 305, 3, false));
        albumy.add(album2);
        
        System.out.println("Dodano przykładowe dane do kolekcji.");
        System.out.println("Całkowity rozmiar przykładowych albumów:");
        System.out.println("- " + album1.getTytul() + ": " + album1.getSformatowanyRozmiar());
        System.out.println("- " + album2.getTytul() + ": " + album2.getSformatowanyRozmiar());
    }
}