package pages;

public class Dog extends Animal {
    public void speak() {
        System.out.println(getClass() + " " + name + " is barking!");
    }

}
