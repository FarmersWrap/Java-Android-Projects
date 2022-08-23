package ca.uwaterloo.cs349.pdfreader;

public class Job {

    int number;
    boolean isEraser;

    public Job(int number, boolean isEraser) {
        this.number = number;
        this.isEraser = isEraser;
    }


    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isEraser() {
        return isEraser;
    }

    public void setEraser(boolean isEraser) {
        this.isEraser = isEraser;
    }



}
