package pages;

public class MyStack extends MyList {
    public void push(int item) {
        int[] newData = new int[data.length + 1];
        for (int i = 0; i < data.length; i++) {
            newData[i] = data[i];
        }
        newData[data.length] = item;
        data = newData;
    }
}
