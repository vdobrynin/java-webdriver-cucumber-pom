package pages;

abstract public class Animal {
    protected String name;
    public Animal() {
        this.name = "nameless one";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void walk() {
        System.out.println(getClass() + " " + name + " is walking!");
    }

    public void eat(String what) {
        System.out.println(getClass() + " " + name + " is eating " + what);
    }

    abstract public void speak();
}
