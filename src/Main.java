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


















import java.io.BufferedReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        Pattern JMENO = Pattern.compile("^[\\p{L}][\\p{L}'-]{1,}$");
        Pattern DATUM_PATTERN = Pattern.compile("^((3[0-1]|2[0-9]|1[0-9]|[1-9])\\. )" + "((1)[0-2]|[1-9]\\. )" + "[0-9]{1,4}$");
        Pattern TELEFON_PATTERN = Pattern.compile("^[1-9]\\d{2} \\d{3} \\d{3}$");
        Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");
        String hlavicka = "";
        Path soubor = Path.of("data", "studenti_1000.csv");
        Path vystupni_soubor = Path.of("data", "vystupni_soubor.csv");

        try {
            // Otevření BufferedWriter v try-with-resources
            try (BufferedReader reader = Files.newBufferedReader(soubor);
                 BufferedWriter writer = Files.newBufferedWriter(vystupni_soubor))
            {
                hlavicka = reader.readLine();

                writer.write(hlavicka);
                writer.newLine();
                String radek;
                while ((radek = reader.readLine()) != null) {  // ← uloží řádek do proměnné
                    String[] cols = radek.split(",");           // ← použiješ tu samou proměnnou

                    String jmeno    = cols[0].trim();
                    String prijmeni = cols[1].trim();
                    String datum    = cols[2].trim();
                    String telefon  = cols[3].trim();
                    String email    = cols[4].trim();

                    if (DATUM_PATTERN.matcher(datum).matches()) {
                        String[] datumSplit = datum.split("\\. "); // ← split podle tečky s mezerou
                        datum = datumSplit[1] + ". " + datumSplit[0] + ". " + datumSplit[2];
                    } else {
                        String normalized = datum.replaceAll("[.\\-/]", " ").trim();
                        String[] datumSplit = normalized.split("\\s+");
                        if (datumSplit.length == 3) {
                            if (datumSplit[0].length() == 4) {
                                datum = Integer.parseInt(datumSplit[2]) + ". " + Integer.parseInt(datumSplit[1]) + ". " + datumSplit[0];
                            } else {
                                datum = Integer.parseInt(datumSplit[1]) + ". " + Integer.parseInt(datumSplit[0]) + ". " + datumSplit[2];
                            }
                        } else {
                            System.out.println(datum);
                            datum = "*";
                        }
                    }
                    if (!TELEFON_PATTERN.matcher(telefon).matches()) {
                        String cifry = telefon.replaceAll("[^0-9]", "");
                        if (cifry.length() == 9 && !cifry.startsWith("0")) {
                            telefon = cifry.substring(0, 3) + " "
                                    + cifry.substring(3, 6) + " "
                                    + cifry.substring(6, 9);
                        }else{
                            telefon = "*";
                        }
                    }
                    if (!EMAIL_PATTERN.matcher(email).matches()) {
                        email = Normalizer.normalize(email, Normalizer.Form.NFD)
                                .replaceAll("\\p{InCombiningDiacriticalMarks}", "");
                    }

                    /*for (String col : cols) {
                        System.out.println(col);
                    }
                    writer.write(jmeno+", "+prijmeni+", "+datum+", "+telefon+", "+email);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Chyba při čtení souboru:");
            e.printStackTrace();
        }
    }
}











import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {
    public static void main(String[] args) {
        Path input = Path.of("studenti_1000.csv");
        Path output = Path.of("studenti_output.csv");

        String delimiter = ",";

        String czDatumRegex = "\\b([1-9]|[12][0-9]|3[01])\\.\\s([1-9]|1[0-2])\\.\\s\\d{4}\\b";
        String mailRegex = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";

        try {
            List<String> lines = Files.readAllLines(input);
            List<String> outputLines = new ArrayList<>();

            for (String line : lines) {
                String[] values = line.split(delimiter, -1);

                for (int i = 0; i < values.length; i++) {
                    String value = values[i].trim();

                    if (value.matches(mailRegex)) {
                        value = value.toLowerCase();
                    }

                    if (i == 2) { // pokud je řádek 3 (protože řádky začínají od 0)
                        value = repairDate(value, czDatumRegex);
                    }

                    if (i == 3) { // pokud je řádek 4
                        value = repairPhoneNumber(value);
                    }

                    if (i == 4) { // pokud je řádek 5
                        String convertedString = Normalizer
                                .normalize(value, Normalizer.Form.NFD)
                                .replaceAll("[^\\p{ASCII}]", "");

                        value = convertedString;

                        Pattern patternMailu = Pattern.compile("@");
                        Matcher matcher = patternMailu.matcher(value);

                        if (!matcher.find()) {
                            value = "#ERROR!";
                        }
                    }

                    values[i] = value;
                }

                // java.nio: tohle přepíše soubor, kdyžtak se ptejte AI
                String newLine = String.join(delimiter, values);
                outputLines.add(newLine);
            }

            Files.write(output, outputLines);

            System.out.println("Output: " + output.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static String repairDate(String dateString, String czDatumRegex) {
        if (dateString == null || dateString.isEmpty()) {
            return dateString;
        }

        if (dateString.matches(czDatumRegex)) {
            return dateString;
        }

        Pattern patternDatum = Pattern.compile("(\\d+)[.\\s/\\-]+(\\d+)[.\\s/\\-]+(\\d+)");
        Matcher matcher = patternDatum.matcher(dateString);

        if (matcher.find()) {
            int num1 = Integer.parseInt(matcher.group(1));
            int num2 = Integer.parseInt(matcher.group(2));
            int num3 = Integer.parseInt(matcher.group(3));

            int day, month, year;

            if (num1 > 31) {
                year = num1; month = num2; day = num3;
            } else if (num2 > 31) {
                year = num2; day = num1; month = num3;
            } else if (num3 > 31) {
                year = num3; day = num1; month = num2;
            } else {
                day = num1; month = num2; year = num3;
            }

            if (month > 12 && day <= 12) {
                int temp = day;
                day = month;
                month = temp;
            }

            return day + ". " + month + ". " + year;
        }

        return dateString;
    }

    private static String repairPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return "#ERROR!";
        }

        phoneNumber = phoneNumber.trim();

        if (phoneNumber.matches("\\d{3} \\d{3} \\d{3}")) {
            return phoneNumber;
        }

        if (phoneNumber.matches("\\d{3}-\\d{3}-\\d{3}")) {
            return phoneNumber.replace("-", " ");
        }

        if (!phoneNumber.matches("\\d+")) {
            return "#ERROR!";
        }

        return "#ERROR!";
    }
}
