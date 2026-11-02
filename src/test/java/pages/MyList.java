package pages;

public abstract class MyList {
    protected int[] data;
    public MyList(int... args) {
        data = args;
    }

    public void print() {
        for (int el : data) {
            System.out.print(el + " ");
        }
        System.out.println();
    }

    public int pop() {
        int last = data[data.length - 1];
        int[] newData = new int[data.length - 1];
        for (int i = 0; i < newData.length; i++) {
            newData[i] = data[i];
        }
        data = newData;
        return last;
    }

    abstract public void push(int item);
}
