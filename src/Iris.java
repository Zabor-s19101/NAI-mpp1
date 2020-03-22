import java.util.Arrays;

public class Iris {
    private String attribute;
    private double[] numbers;

    public Iris(String attribute, double[] numbers) {
        this.attribute = attribute;
        this.numbers = numbers;
    }

    public String getAttribute() {
        return attribute;
    }

    public double[] getNumbers() {
        return numbers;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
}
