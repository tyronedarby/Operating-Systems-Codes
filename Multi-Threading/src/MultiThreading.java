import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultiThreading {

    public static void main(String[] args) {

        String fileName = "xyz";

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {

            String record;

            while ((record = reader.readLine()) != null) {

                if (record.trim().isEmpty()) {
                    continue;
                }

                String[] sections = separateSections(record);

                String section1 = sections[0];
                String section2 = sections[1];
                String section3 = sections[2];

                Thread personThread = new Person(section1);
                Thread scoresThread = new Scores(section2);
                Thread discoverThread = new Discover(section3);

                personThread.start();
                scoresThread.start();
                discoverThread.start();

                /*
                 * Wait for all three threads to finish before
                 * processing the next record.
                 */
                try {
                    personThread.join();
                    scoresThread.join();
                    discoverThread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Main thread interrupted.");
                }

                System.out.println(
                        "------------------------------------------------------------");
            }

        } catch (IOException e) {
            System.out.println("Error reading file xyz.");
            System.out.println(e.getMessage());
        }
    }

    public static String[] separateSections(String record) {

        String[] sections = new String[3];

        Pattern pattern =
                Pattern.compile("(\\*{1,3})([^$]*)\\$");

        Matcher matcher = pattern.matcher(record);

        while (matcher.find()) {

            String stars = matcher.group(1);
            String data = matcher.group(2).trim();

            int sectionNumber = stars.length();

            sections[sectionNumber - 1] = data;
        }

        // Protect against missing sections
        for (int i = 0; i < sections.length; i++) {
            if (sections[i] == null) {
                sections[i] = "";
            }
        }

        return sections;
    }
}

class Person extends Thread {

    private final String section;

    public Person(String section) {
        this.section = section;
    }

    @Override
    public void run() {

        String firstName = "Unknown";
        String lastName = "Unknown";
        String middleInitial = "Unknown";
        String age = "Unknown";

        Map<Character, String> fields = parseFields(section);

        if (fields.containsKey('F')) {
            String value = fields.get('F').trim();

            if (!value.isEmpty()) {
                firstName = value;
            }
        }

        if (fields.containsKey('L')) {
            String value = fields.get('L').trim();

            if (!value.isEmpty()) {
                lastName = value;
            }
        }

        if (fields.containsKey('M')) {
            String value = fields.get('M').trim();

            if (!value.isEmpty()) {
                middleInitial = value;
            }
        }

        if (fields.containsKey('D')) {

            String birthDate = fields.get('D').trim();

            age = calculateAge(birthDate);
        }

        String output =
                "Person - Section 1 is: " + section
                        + " - Last Name: " + lastName
                        + " - First Name: " + firstName
                        + " - MI: " + middleInitial
                        + " - Age: " + age;

        // Assignment requires Person to print 40 times.
        for (int i = 0; i < 40; i++) {
            System.out.println(output);
        }
    }

    private Map<Character, String> parseFields(String text) {

        Map<Character, String> fields = new HashMap<>();

        Pattern markerPattern =
                Pattern.compile("(?<![A-Za-z])([FLMD])");

        Matcher matcher = markerPattern.matcher(text);

        List<Integer> positions = new ArrayList<>();
        List<Character> markers = new ArrayList<>();

        while (matcher.find()) {

            positions.add(matcher.start(1));
            markers.add(matcher.group(1).charAt(0));
        }

        for (int i = 0; i < positions.size(); i++) {

            int start = positions.get(i) + 1;

            int end;

            if (i + 1 < positions.size()) {
                end = positions.get(i + 1);
            } else {
                end = text.length();
            }

            String value =
                    text.substring(start, end).trim();

            fields.put(markers.get(i), value);
        }

        return fields;
    }

    private String calculateAge(String dateText) {

        try {

            String[] parts = dateText.split(",");

            if (parts.length != 3) {
                return "Unknown";
            }

            int day = wordToNumber(parts[0].trim());

            int month = monthToNumber(parts[1].trim());

            int year =
                    Integer.parseInt(
                            parts[2]
                                    .trim()
                                    .replaceAll("[^0-9]", "")
                    );

            if (day <= 0 || month <= 0 || year <= 0) {
                return "Unknown";
            }

            LocalDate birthDate =
                    LocalDate.of(year, month, day);

            LocalDate today =
                    LocalDate.now();

            if (birthDate.isAfter(today)) {
                return "Unknown";
            }

            Period period =
                    Period.between(birthDate, today);

            return period.getYears()
                    + "y, "
                    + period.getMonths()
                    + "m and "
                    + period.getDays()
                    + "d";

        } catch (Exception e) {
            return "Unknown";
        }
    }

    /*
     * Converts both numbers and English number words
     * into integers.
     */
    private int wordToNumber(String value) {

        value =
                value.toLowerCase()
                        .trim();

        /*
         * Handles something such as B3 in the supplied
         * assignment data by locating the numeric part.
         */
        Matcher numberMatcher =
                Pattern.compile("\\d+").matcher(value);

        if (numberMatcher.find()) {
            return Integer.parseInt(numberMatcher.group());
        }

        return switch (value) {

            case "one", "first" -> 1;
            case "two", "second" -> 2;
            case "three", "third" -> 3;
            case "four", "fourth" -> 4;
            case "five", "fifth" -> 5;
            case "six", "sixth" -> 6;
            case "seven", "seventh" -> 7;
            case "eight", "eighth" -> 8;
            case "nine", "ninth" -> 9;
            case "ten", "tenth" -> 10;
            case "eleven" -> 11;
            case "twelve" -> 12;
            case "thirteen" -> 13;
            case "fourteen" -> 14;
            case "fifteen" -> 15;
            case "sixteen" -> 16;
            case "seventeen" -> 17;
            case "eighteen" -> 18;
            case "nineteen" -> 19;
            case "twenty" -> 20;
            case "twenty-one", "twenty one" -> 21;
            case "twenty-two", "twenty two" -> 22;
            case "twenty-three", "twenty three" -> 23;
            case "twenty-four", "twenty four" -> 24;
            case "twenty-five", "twenty five" -> 25;
            case "twenty-six", "twenty six" -> 26;
            case "twenty-seven", "twenty seven" -> 27;
            case "twenty-eight", "twenty eight" -> 28;
            case "twenty-nine", "twenty nine" -> 29;
            case "thirty" -> 30;
            case "thirty-one", "thirty one" -> 31;

            default -> -1;
        };
    }

    /*
     * Converts month name or month number into
     * its numeric month.
     */
    private int monthToNumber(String monthText) {

        monthText =
                monthText.toLowerCase()
                        .trim();

        // Numeric month
        try {

            int month = Integer.parseInt(monthText);

            if (month >= 1 && month <= 12) {
                return month;
            }

        } catch (NumberFormatException ignored) {
        }

        /*
         * startsWith is intentional because the supplied
         * xyz data contains spellings such as "Januarry".
         */
        if (monthText.startsWith("jan")) {
            return Month.JANUARY.getValue();
        }

        if (monthText.startsWith("feb")) {
            return Month.FEBRUARY.getValue();
        }

        if (monthText.startsWith("mar")) {
            return Month.MARCH.getValue();
        }

        if (monthText.startsWith("apr")) {
            return Month.APRIL.getValue();
        }

        if (monthText.startsWith("may")) {
            return Month.MAY.getValue();
        }

        if (monthText.startsWith("jun")) {
            return Month.JUNE.getValue();
        }

        if (monthText.startsWith("jul")) {
            return Month.JULY.getValue();
        }

        if (monthText.startsWith("aug")) {
            return Month.AUGUST.getValue();
        }

        if (monthText.startsWith("sep")) {
            return Month.SEPTEMBER.getValue();
        }

        if (monthText.startsWith("oct")) {
            return Month.OCTOBER.getValue();
        }

        if (monthText.startsWith("nov")) {
            return Month.NOVEMBER.getValue();
        }

        if (monthText.startsWith("dec")) {
            return Month.DECEMBER.getValue();
        }

        return -1;
    }
}

class Scores extends Thread {

    private final String section;

    public Scores(String section) {
        this.section = section;
    }

    @Override
    public void run() {

        List<Double> scores =
                getScores(section);

        if (scores.isEmpty()) {

            for (int i = 0; i < 60; i++) {

                System.out.println(
                        "Scores - Section 2 is: "
                                + section
                                + " - No valid scores"
                );
            }

            return;
        }

        double average =
                calculateAverage(scores);

        double standardDeviation =
                calculateStandardDeviation(
                        scores,
                        average
                );

        List<Double> outliers =
                findOutliers(
                        scores,
                        average,
                        standardDeviation
                );

        String output =
                "Scores - Section 2 is: "
                        + section
                        + " - average: "
                        + formatNumber(average)
                        + " - SD: "
                        + String.format("%.2f",
                        standardDeviation)
                        + " - Outliers: "
                        + formatOutliers(outliers);

        // Assignment requires Scores to print 60 times.
        for (int i = 0; i < 60; i++) {
            System.out.println(output);
        }
    }

    private List<Double> getScores(String text) {

        List<Double> scores =
                new ArrayList<>();

        String[] values =
                text.split(",");

        for (String value : values) {

            try {

                double score =
                        Double.parseDouble(
                                value.trim()
                        );

                scores.add(score);

            } catch (NumberFormatException ignored) {
            }
        }

        return scores;
    }

    private double calculateAverage(
            List<Double> scores) {

        double sum = 0;

        for (double score : scores) {
            sum += score;
        }

        return sum / scores.size();
    }

    private double calculateStandardDeviation(
            List<Double> scores,
            double average) {

        double sumSquares = 0;

        for (double score : scores) {
            sumSquares += score * score;
        }

        double variance =
                (sumSquares / scores.size())
                        - (average * average);

        /*
         * Protects against tiny negative floating-point
         * rounding errors.
         */
        if (variance < 0 && variance > -0.0000001) {
            variance = 0;
        }

        return Math.sqrt(variance);
    }

    /*
     * A score is an outlier when it is more than
     * 1.9 standard deviations away from the average.
     */
    private List<Double> findOutliers(
            List<Double> scores,
            double average,
            double standardDeviation) {

        List<Double> outliers =
                new ArrayList<>();

        double limit =
                1.9 * standardDeviation;

        for (double score : scores) {

            if (Math.abs(score - average) > limit) {
                outliers.add(score);
            }
        }

        return outliers;
    }

    private String formatOutliers(
            List<Double> outliers) {

        if (outliers.isEmpty()) {
            return "None";
        }

        StringBuilder result =
                new StringBuilder();

        for (int i = 0;
             i < outliers.size();
             i++) {

            if (i > 0) {
                result.append(", ");
            }

            result.append(
                    formatNumber(
                            outliers.get(i)
                    )
            );
        }

        return result.toString();
    }

    private String formatNumber(
            double value) {

        if (value == Math.floor(value)) {
            return String.valueOf((long) value);
        }

        return String.format("%.2f", value);
    }
}

class Discover extends Thread {

    private final String section;

    private static final Map<String, String>
            NUMBER_WORDS = new HashMap<>();

    static {

        NUMBER_WORDS.put("zero", "0");
        NUMBER_WORDS.put("one", "1");
        NUMBER_WORDS.put("two", "2");
        NUMBER_WORDS.put("three", "3");
        NUMBER_WORDS.put("four", "4");
        NUMBER_WORDS.put("five", "5");
        NUMBER_WORDS.put("six", "6");
        NUMBER_WORDS.put("seven", "7");
        NUMBER_WORDS.put("eight", "8");
        NUMBER_WORDS.put("nine", "9");
    }

    public Discover(String section) {
        this.section = section;
    }

    @Override
    public void run() {

        String preliminaryPassword =
                buildPreliminaryPassword(section);

        String password =
                discoverPassword(
                        preliminaryPassword
                );

        boolean valid =
                isValidPassword(password);

        String output;

        if (valid) {

            output =
                    "Discover - Section 3 is: "
                            + section
                            + " - passwd is: "
                            + password
                            + " - valid";

        } else {

            /*
             * If neither end produces a valid password,
             * display the preliminary password as invalid.
             */
            output =
                    "Discover - Section 3 is: "
                            + section
                            + " - passwd is: "
                            + preliminaryPassword
                            + " - invalid";
        }

        // Assignment requires Discover to print 50 times.
        for (int i = 0; i < 50; i++) {
            System.out.println(output);
        }
    }

    private String buildPreliminaryPassword(
            String text) {

        String[] tokens =
                text.trim().split("\\s+");

        StringBuilder password =
                new StringBuilder();

        for (String token : tokens) {

            if (token.isEmpty()) {
                continue;
            }

            /*
             * Remove punctuation from the beginning/end
             * only for checking number words.
             */
            String cleaned =
                    token.replaceAll(
                            "^[^A-Za-z0-9]+|[^A-Za-z0-9]+$",
                            ""
                    );

            String lowercase =
                    cleaned.toLowerCase();

            /*
             * Step 1:
             * Convert number words into digits.
             */
            if (NUMBER_WORDS.containsKey(lowercase)) {

                cleaned =
                        NUMBER_WORDS.get(lowercase);
            }

            if (cleaned.isEmpty()) {
                continue;
            }

            char first =
                    cleaned.charAt(0);

            if ((first >= 'a' && first <= 'z')
                    || Character.isDigit(first)) {

                password.append(first);
            }
        }

        return password.toString();
    }

    private String discoverPassword(
            String preliminary) {

        if (preliminary.length() < 6) {
            return preliminary;
        }

        if (preliminary.length() == 6) {

            if (isValidPassword(preliminary)) {
                return preliminary;
            }

            return preliminary;
        }

        String firstSix =
                preliminary.substring(0, 6);

        if (isValidPassword(firstSix)) {
            return firstSix;
        }

        String lastSix =
                preliminary.substring(
                        preliminary.length() - 6
                );

        if (isValidPassword(lastSix)) {
            return lastSix;
        }

        return preliminary;
    }

    private boolean isValidPassword(
            String password) {

        if (password == null
                || password.length() != 6) {
            return false;
        }

        char first =
                password.charAt(0);

        if (first < 'a'
                || first > 'z') {
            return false;
        }

        boolean containsDigit = false;

        for (char c :
                password.toCharArray()) {

            if (Character.isDigit(c)) {

                containsDigit = true;

            } else if (c < 'a'
                    || c > 'z') {

                return false;
            }
        }

        return containsDigit;
    }
}