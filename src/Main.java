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


/*
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        Path vstup = Path.of("data","studenti_1000.csv");
        Path vystup = Path.of("data","studenti_opraveno.csv");

        try (BufferedReader reader = Files.newBufferedReader(vstup, StandardCharsets.UTF_8);
             BufferedWriter writer = Files.newBufferedWriter(vystup, StandardCharsets.UTF_8)) {

            String radek;
            boolean jePrvniRadek = true;

            while ((radek = reader.readLine()) != null) {
                if (radek.isBlank()) continue;

                // 1. ROZDĚLENÍ (v ukázce jsou tabulátory \t)
                String[] sloupce = radek.split("\t");

                // Pokud řádek nemá dost sloupců, zkusíme ho rozdělit aspoň podle čárky
                if (sloupce.length < 4) {
                    sloupce = radek.split(",");
                }

                if (sloupce.length < 4) continue;

                if (jePrvniRadek) {
                    // Záhlaví spojíme čárkou
                    writer.write("Jmeno,Prijmeni,Datum_narozeni,Telefon,Email");
                    writer.newLine();
                    jePrvniRadek = false;
                    continue;
                }

                String jmeno = sloupce[0].trim();
                String prijmeni = sloupce[1].trim();
                String datum = opravDatum(sloupce[2].trim());
                String telefon = opravTelefon(sloupce[3].trim());

                // E-mail (index 4), pokud existuje
                String emailRaw = (sloupce.length > 4) ? sloupce[4].trim() : "";
                String email = odstranDiakritiku(emailRaw.toLowerCase());

                // 2. ZÁPIS (spojeno čárkou podle přání)
                String opravenyRadek = String.join(",", jmeno, prijmeni, datum, telefon, email);
                writer.write(opravenyRadek);
                writer.newLine();
            }

            System.out.println("Hotovo! Soubor uložen do: " + vystup.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("Chyba: " + e.getMessage());
        }
    }

    private static String opravDatum(String datum) {
        // Formát rrrr/mm/dd -> d. m. rrrr
        if (datum.contains("/")) {
            String[] casti = datum.split("/");
            if (casti.length == 3) {
                return Integer.parseInt(casti[2]) + ". " + Integer.parseInt(casti[1]) + ". " + casti[0];
            }
        }

        // Formát m. d. rrrr -> d. m. rrrr (ošetření amerického formátu)
        Pattern p = Pattern.compile("(\\d+)\\.\\s*(\\d+)\\.\\s*(\\d{4})");
        Matcher m = p.matcher(datum);
        if (m.find()) {
            int d1 = Integer.parseInt(m.group(1));
            int d2 = Integer.parseInt(m.group(2));
            String rok = m.group(3);
            if (d1 > 12) return d1 + ". " + d2 + ". " + rok; // Už je to správně
            return d2 + ". " + d1 + ". " + rok; // Prohození měsíce a dne
        }
        return datum;
    }

    private static String opravTelefon(String tel) {
        String cista = tel.replaceAll("[^0-9]", "");
        if (cista.length() >= 9) {
            String poslednich9 = cista.substring(cista.length() - 9);
            return poslednich9.substring(0, 3) + " " + poslednich9.substring(3, 6) + " " + poslednich9.substring(6);
        }
        return tel;
    }

    private static String odstranDiakritiku(String text) {
        if (text == null) return "";
        // Normalizace na NFD (rozloží znaky na základ + diakritické znaménko)
        String nfd = Normalizer.normalize(text, Normalizer.Form.NFD);
        // Vymaže všechna diakritická znaménka (kategorie Mark)
        return nfd.replaceAll("\\p{M}", "");
    }
}
/*
