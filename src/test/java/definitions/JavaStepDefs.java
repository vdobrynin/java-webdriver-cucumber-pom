package definitions;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import pages.Animal;
import pages.Cat;
import pages.Dog;
import pages.MyList;
import pages.MyQueue;
import pages.MyStack;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JavaStepDefs {
    @Given("I hello world")
    public void iHelloWorld() {

        String message = "Say hello!";
        System.out.println(message);
        System.out.println("Hello World!");

        String greeting = "Hello";
        String text = "I'm an engineer";
        System.out.println(greeting + ", " + text);

        String firstName = "slava";
        System.out.println(firstName.toUpperCase());
        System.out.println(firstName.getClass());
        System.out.println(firstName.length());
        System.out.println(firstName.equals("Slava"));
        System.out.println(firstName.contains("va"));
    }

    @Given("I perform actions with {string} and {string}")
    public void iPerformActionsWithAnd(String str1, String str2) {

        System.out.println("String 1: " + str1);
        System.out.println("String 2: " + str2);
        System.out.println("String 1: " + str1.toUpperCase());
        System.out.println("String 2: " + str2.toUpperCase());
        System.out.println("String 1: " + str1.length());
        System.out.println("String 2: " + str2.length());
        System.out.println("Exact comparison: == " + (str1 == str2));
        System.out.println("Exact comparison: equals " + str1.equals(str2));
        System.out.println("Exact comparison: equals ignoring case " + str1.equalsIgnoreCase(str2));
        System.out.println("Partial comparison: contains " + str1.toUpperCase()
                .contains(str2.toUpperCase()));
    }

    @And("I operate {int} and {int} numbers")
    public void iOperateAndNumbers(int num1, int num2) {

        System.out.println(num1 / num2);
        System.out.println(num1 % num2);

    }

    @Given("I print url for {string} page")
    public void iPrintUrlForPage(String page) {

        // Using if statement:
        if (page.equals("google")) {
            System.out.println("https://www.google.com/");
        } else if (page.equals("yahoo")) {
            System.out.println("https://www.yahoo.com/");
        } else {
            System.out.println("Unsupported page: " + page);
        }

        // Using switch statement:
        switch (page) {
            case "google":
                System.out.println("https://www.google.com/");
                break;
            case "yahoo":
                System.out.println("https://www.yahoo.com/");
                break;
            default:
                System.out.println("Unsupported page: " + page);
                break;
        }
    }

    @Given("I work with arrays")
    public void iWorkWithArrays() {

        String[] fruits = {"plum", "apple", "kiwi"};
        int[] nums = {7, 3, 5, 8, 2, 9, 11};
        System.out.println(fruits[0]);
        System.out.println(nums[0]);

        for (String item : fruits) {
            System.out.println(item);
        }

        for (int i = 0; i < fruits.length; i++) {
            System.out.println(fruits[i]);
        }

        List<String> list = Arrays.asList("java", "plum", "apple");
        System.out.println(list);

        for (String item : list) {
            System.out.println(item);
        }

        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }

        // Extra: some lambdas and streams
        list.sort(Comparator.comparingInt(String::length));
        System.out.println("Sorted by length ascending order:" + list);

        var intList = Arrays.asList(3, 8, 11, 2, 3);
        System.out.println("Original list:" + intList);

        // Example of lambda replacing old style Java

        // Sort descending order old style:
        intList.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer i1, Integer i2) {
                return i2 - i1;
            }
        });
        System.out.println("Sorted descending order:" + intList);

        // Sort with Lambda (line does the same as above "Sort old style")
        intList.sort((i1, i2) -> i2 - i1);
        System.out.println("Sorted descending order:" + intList);

        // Sort ascending order with Lambda
        intList.sort(Comparator.comparingInt(Integer::intValue));
        System.out.println("Sorted ascending order:" + intList);

        // Streams:

        // Take int list, filter out only even numbers, add "Even: " before them and print each item single line
        intList.stream()
                .filter(i -> i % 2 == 0)
                .map(i -> "Even: " + i + " ")
                .forEach(System.out::print);

        // Take string list, filter out only those starting with j or a, make them upper case, concatenate all separating by comma
        String result = list.stream()
                .filter(str -> str.startsWith("j") || str.startsWith("a"))
                .map(String::toUpperCase)
                .collect(Collectors.joining(", "));
        System.out.println("\n" + result);
    }

    @Given("I work with maps")
    public void iWorkWithMaps() {

        Map<String, String> user = Map.of(
                "username", "skryabin",
                "email", "slava@skryabin",
                "password", "welcome"
        );

        Map<String, String> admin = Map.of(
                "username", "jdoe",
                "email", "john@doe.com",
                "password", "jdoe"
        );

        System.out.println(user);
        System.out.println(admin);
        System.out.println(admin.get("username"));
        System.out.println(admin.get("email"));
        System.out.println(admin.get("password"));
    }

    @Given("I solve FizzBuzz for {int}")
    public void iSolveFizzBuzzFor(int max) {
        for (int i = 1; i <= max; i++) {
            if (i % 3 == 0 && i % 5 == 0) {
                System.out.print("FizzBuzz ");
            } else if (i % 3 == 0) {
                System.out.print("Fizz ");
            } else if (i % 5 == 0) {
                System.out.print("Buzz ");
            } else {
                System.out.print(i + " ");
            }
        }
    }

    @Given("I solve coding tasks")
    public void iSolveCodingTasks() {
        // Simple search
        int[] arr = {5, 2, 4, 4, 8, 1};
        System.out.println(contains(arr, 8));
        // Print reverse
        printReverse("WebDriver");
        // Palindrome
        System.out.println(isPalindrome("madam"));
        System.out.println(isPalindrome("Web"));
        // Sort array
        arr = sort(arr);
        System.out.println(Arrays.toString(arr));
        // Print two biggest numbers in an array
        print2MaxNumbers(arr);
        // Calculate Factorial
        System.out.println(factorial(5));
        // Find if number is Prime - simple algorithm
        System.out.println(isPrimeSimple(17));
        // Find if number is Prime
        System.out.println(isPrime(17));
    }

    void print2MaxNumbers(int[] arr) {
        int max = Integer.MIN_VALUE;
        int max2 = Integer.MIN_VALUE;

        for (int i = 0; i < arr.length; i++) {
            if (max < arr[i]) {
                max2 = max;
                max = arr[i];
            } else if (max2 < arr[i]) {
                max2 = arr[i];
            }
        }
        System.out.println("Max: " + max + ", next max: " + max2);
    }

    long factorial(long number) {
        if (number == 0) {
            return 1;
        }
        return number * factorial(number - 1);
    }

    boolean isPrimeSimple(int num) {
        if (num < 2) {
            return false;
        }
        for (int i = 2; i < num; i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }

    boolean isPrime(int num) {
        if (num < 2) {
            return false;
        }
        if (num != 2 && num % 2 == 0) {
            return false;
        }
        double sqrt = Math.sqrt(num);
        for (int i = 3; i <= sqrt; i += 2) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }

    boolean isPalindrome(String word) {
        int j = 0;
        int length = word.length();
        for (int i = length - 1; i >= length / 2; i--) {
            if (word.charAt(i) != word.charAt(j)) {
                return false;
            }
            j++;
        }
        return true;
    }

    int[] sort(int[] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = i + 1; j < array.length; j++) {
                if (array[i] > array[j]) {
                    int temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                }
            }
        }
        return array;
    }

    void printReverse(String str) {
        for (int i = str.length() - 1; i >= 0; i--) {
            System.out.print(str.charAt(i));
        }
        System.out.println();
    }

    boolean contains(int[] arr, int num) {
        for (int el : arr) {
            if (el == num) {
                return true;
            }
        }
        return false;
    }

    @Given("I work with stack and queue")
    public void iWorkWithStackAndQueue() {
        System.out.println("=====> Stack:");
        System.out.println("Last In - First Out");
        MyList stack = new MyStack();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.print();

        System.out.println("stack.pop(): " + stack.pop());
        stack.print();

        System.out.println("=====> Queue:");
        System.out.println("First In - First Out");
        MyList queue = new MyQueue();
        queue.push(1);
        queue.push(2);
        queue.push(3);
        queue.print();
        System.out.println("queue.pop(): " + queue.pop());
        System.out.println(queue.pop());
        queue.print();
    }

    @Given("I work with classes")
    public void iWorkWithClasses() {
        Animal tom = new Cat("Tom");
        System.out.println(tom.getName());
        tom.walk();
        tom.eat("fish");
        tom.speak();

        Animal charlie = new Dog();
        System.out.println(charlie.getName());
        charlie.walk();
        charlie.eat("bone");
        charlie.speak();
    }
}
