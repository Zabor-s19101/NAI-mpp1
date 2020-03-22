import java.io.File;
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
        for (int i = 0; i < testIrises.size(); i++) {
            List<String> distancesFromTest = new ArrayList<>();
            for (int j = 0; j < trainingIrises.size(); j++) {
                double distance = 0;
                for (int k = 0; k < trainingIrises.get(0).getNumbers().length; k++) {
                    distance += Math.pow(trainingIrises.get(j).getNumbers()[k] - testIrises.get(i).getNumbers()[k], 2);
                }
                distancesFromTest.add(distance + " " + trainingIrises.get(j).getAttribute());
            }
            distancesFromTest.sort(Comparator.comparing(o -> o.split(" ")[0]));
            Map<String, Integer> kIrises = new HashMap<>();
            for (int j = 0; j < kParam; j++) {
                String key = distancesFromTest.get(j).split(" ")[1];
                if (kIrises.containsKey(key)) {
                    kIrises.put(key, kIrises.get(key) + 1);
                } else {
                    kIrises.put(key, 1);
                }
            }
            if (testIrises.get(i).getAttribute().equals(getRandomMax(kIrises))) {
                successfulPredicate++;
            }
        }

        System.out.println("Prawidłowo zaklasyfikowane przypadki: " + successfulPredicate + "/" + testIrises.size() + "\n" +
                "Dokładnośc eksperymentu: " + ((double)successfulPredicate / testIrises.size() * 100) + "%");
    }

    public static String getRandomMax(Map<String, Integer> data) {
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
