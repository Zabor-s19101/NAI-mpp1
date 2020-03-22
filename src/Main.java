import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Main {
    private static final Random R = new Random();

    public static void main(String[] args) throws Exception {
        List<Iris> trainingIrises = new ArrayList<>();
        Scanner sc = new Scanner(new File("iris_training.txt"));
        readIrises(sc, trainingIrises);

        List<Iris> testIrises = new ArrayList<>();
        sc = new Scanner(new File("iris_test.txt"));
        readIrises(sc, testIrises);

        System.out.print("Podaj parametr kParam: ");
        int kParam;
        sc = new Scanner(System.in);
        kParam = sc.nextInt();

        int successfulPredicate = 0;
        for (Iris testIris : testIrises) {
            if (testIris.getAttribute().equals(predicateIrisAttribute(trainingIrises, testIris, kParam))) {
                successfulPredicate++;
            }
        }

        System.out.println("Prawidłowo zaklasyfikowane przypadki: " + successfulPredicate + "/" + testIrises.size() + "\n" +
                "Dokładnośc eksperymentu: " + ((double)successfulPredicate / testIrises.size() * 100) + "%\n");

        System.out.println("Wprowadź atrybuty numeryczne by odtrzymać wynik klasyfikacji\n" +
                "(" + trainingIrises.get(0).getNumbers().length + " liczby z przecinkiem oddzielone białymi znakami)\n" +
                "jeśli chcesz zakończyć wpisz 0:");
        sc = new Scanner(System.in);
        String line;
        while (!(line = sc.nextLine()).equals("0")) {
            String[] data = line.replaceAll(",", ".").trim().split("\\s+");
            if (data.length != trainingIrises.get(0).getNumbers().length) {
                throw new IllegalArgumentException("Nieprawidłowa liczba atrybutów");
            }
            double[] numbers = new double[data.length];
            for (int i = 0; i < numbers.length; i++) {
                numbers[i] = Double.parseDouble(data[i]);
            }
            Iris iris = new Iris(null, numbers);
            iris.setAttribute(predicateIrisAttribute(trainingIrises, iris, kParam));
            System.out.println(iris.getAttribute());
        }

        /*//File generator with results for excel
        FileWriter fw = new FileWriter(new File("firstResults1.txt"));
        fw.write("k\tPrawidłowo zaklasyfikowane przypadki\tDokładnośc eksperymentu %\n");
        for (int kParam = 1; kParam <= trainingIrises.size(); kParam++) {
            int successfulPredicate = 0;
            for (Iris testIris : testIrises) {
                if (testIris.getAttribute().equals(predicateIrisAttribute(trainingIrises, testIris, kParam))) {
                    successfulPredicate++;
                }
            }

            fw.write(kParam + "\t" + successfulPredicate + "/" + testIrises.size() + "\t" + ((double)successfulPredicate / testIrises.size() * 100) + "\n");
            System.out.println(kParam + " Prawidłowo zaklasyfikowane przypadki: " + successfulPredicate + "/" + testIrises.size() + "\n" +
                    "Dokładnośc eksperymentu: " + ((double)successfulPredicate / testIrises.size() * 100) + "%");
        }
        fw.close();*/
    }

    private static String predicateIrisAttribute(List<Iris> trainingIrises, Iris testIris, int kParam) {
        List<String> distancesFromTest = new ArrayList<>();
        for (Iris trainingIris : trainingIrises) {
            double distance = 0;
            for (int k = 0; k < trainingIrises.get(0).getNumbers().length; k++) {
                distance += Math.pow(trainingIris.getNumbers()[k] - testIris.getNumbers()[k], 2);
            }
            distancesFromTest.add(distance + " " + trainingIris.getAttribute());
        }
        distancesFromTest.sort(Comparator.comparing(o -> o.split(" ")[0]));
        Map<String, Integer> kIrises = new HashMap<>();
        for (int i = 0; i < kParam; i++) {
            String key = distancesFromTest.get(i).split(" ")[1];
            if (kIrises.containsKey(key)) {
                kIrises.put(key, kIrises.get(key) + 1);
            } else {
                kIrises.put(key, 1);
            }
        }
        return getRandomMax(kIrises);
    }

    private static String getRandomMax(Map<String, Integer> data) {
        if ((null == data) || data.isEmpty()) {
            return (null);
        }

        int max = data.values().stream().max(Comparator.naturalOrder()).get();

        Predicate<String> reducer = name -> (data.get(name) == max);

        List<String> names = data.keySet()
                .stream()
                .filter(reducer)
                .collect(Collectors.toList());

        if (1 == names.size()) {
            return (names.get(0));
        }
        int idx = R.nextInt(names.size());
        return (names.get(idx));
    }

    private static void readIrises(Scanner sc, List<Iris> irises) {
        while (sc.hasNextLine()) {
            String[] data = sc.nextLine().replaceAll(",", ".").trim().split("\\s+");
            String attribute = data[data.length - 1];
            double[] numbers = new double[data.length - 1];
            for (int i = 0; i < numbers.length; i++) {
                numbers[i] = Double.parseDouble(data[i]);
            }
            irises.add(new Iris(attribute, numbers));
        }
    }
}
