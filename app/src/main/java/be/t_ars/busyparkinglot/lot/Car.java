package be.t_ars.busyparkinglot.lot;

import be.t_ars.busyparkinglot.data.CarData;

public class Car extends CarData {
    private final int fNumber;

    public Car(final CarData car, final int number) {
        super(car);
        fNumber = number;
    }

    public String toString() {
        return "[number: " + fNumber + ", length: " + fLength + ", " + (fHorizontal ? "horizontal" : "vertical") + ", x: " + fX + ", y: " + fY + "]";
    }

    public int getNumber() {
        return fNumber;
    }

    public void setX(int x) {
        fX = x;
    }

    public void setY(int y) {
        fY = y;
    }
}
