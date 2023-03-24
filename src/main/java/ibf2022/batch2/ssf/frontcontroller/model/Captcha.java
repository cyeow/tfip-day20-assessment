package ibf2022.batch2.ssf.frontcontroller.model;

import java.security.SecureRandom;

public class Captcha {
    private Integer num1;
    private Integer num2;
    private String operation;

    public Captcha() {
    }

    public Captcha(Integer num1, Integer num2, String operation) {
        this.num1 = num1;
        this.num2 = num2;
        this.operation = operation;
    }

    public Integer getNum1() {
        return num1;
    }

    public void setNum1(Integer num1) {
        this.num1 = num1;
    }

    public Integer getNum2() {
        return num2;
    }

    public void setNum2(Integer num2) {
        this.num2 = num2;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return num1 + " " + operation + " " + num2;
    }

    public Double getResult() {
        Double result = 0d;

        switch (operation) {
            case "+":
                result = (double) (num1 + num2);
                break;
            case "-":
                result = (double) (num1 - num2);
                break;
            case "/":
                result = (double) num1 / (double) num2;
                break;
            case "*":
                result = (double) num1 * num2;
                break;
        }

        return result;
    }

    public static Captcha generateCaptcha() {
        SecureRandom sr = new SecureRandom();
        Integer lowerBound = 1;
        Integer upperBound = 51;

        Integer num1 = sr.nextInt(lowerBound, upperBound);
        Integer num2 = sr.nextInt(lowerBound, upperBound);

        String operation = "";
        switch (sr.nextInt(4)) {
            case 0:
                operation = "+";
                break;
            case 1:
                operation = "-";
                break;
            case 2:
                operation = "/";
                break;
            case 3:
                operation = "*";
                break;
        }

        return new Captcha(num1, num2, operation);
    }
}
