import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        Pattern Datum = Pattern.compile("^[0-9]{1,2}\\.\\s?[0-9]{1,2}\\.\\s?[0-9]{4}$");
        Pattern Telefon = Pattern.compile("^[0-9]{3}\\s?[0-9]{3}\\s?[0-9]{3}$");
        Pattern Email = Pattern.compile("^$");

        Path soubor = Path.of("data", "studenti_1000.csv");

        try (BufferedReader reader = Files.newBufferedReader(soubor)) {
            String radek = reader.readLine();

            if (radek != null) {
                String[] cols = radek.split(",");
                for (String col : cols) {
                    String upravenaHodnota = col.trim();

                    if (Datum.matcher(upravenaHodnota).matches()) {
                        upravenaHodnota = upravenaHodnota.replace(" ", "");
                    }

                    System.out.println(upravenaHodnota);
                }
            } else {
                System.out.println("Soubor je prázdný.");
            }

        } catch (IOException e) {
            System.out.println("Chyba:" + e.getMessage());
        }
    }
}