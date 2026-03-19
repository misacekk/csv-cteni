import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        Path vstup = Path.of("data","studenti_1000.csv");
        Path vystup = Path.of("data","studenti_opraveno.csv");

        try (BufferedReader reader = Files.newBufferedReader(vstup, StandardCharsets.UTF_8);
             BufferedWriter writer = Files.newBufferedWriter(vystup, StandardCharsets.UTF_8)) {

            String radek;
            while ((radek = reader.readLine()) != null) {
                String[] sloupce = radek.split(",");
                if (sloupce.length < 4) continue;


                String jmeno = sloupce[0].trim();
                String prijmeni = sloupce[1].trim();


                String datum = sloupce[2].trim().replaceAll("\\s+", "").replace(".", ". ");
                datum = datum.replace(".  ", ". ").trim();


                String telefon = sloupce[3].trim().replaceAll("\\s+", "");
                if (telefon.length() == 9) {
                    telefon = telefon.substring(0, 3) + " " + telefon.substring(3, 6) + " " + telefon.substring(6);
                }


                String email = odstranDiakritiku(sloupce[4].trim().toLowerCase());


                String opravenyRadek = String.join(",", jmeno, prijmeni, datum, telefon, email);
                writer.write(opravenyRadek);
                writer.newLine();
            }
            System.out.println("Soubor byl úspěšně opraven a uložen do: " + vystup.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("Chyba při práci se souborem: " + e.getMessage());
        }
    }


    private static String odstranDiakritiku(String text) {
        String nfdNormalizedString = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }
}