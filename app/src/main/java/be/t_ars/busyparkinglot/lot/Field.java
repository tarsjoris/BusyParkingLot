package be.t_ars.busyparkinglot.lot;

import java.util.ArrayList;
import java.util.List;

public class Field {
    private static final String kTAG = "Field";

    public static final int kWIDTH = 6;
    public static final int kHEIGHT = 6;
    public static final int kEXIT_X = 6;
    public static final int kEXIT_Y = 2;
    public static final int kEXITPOS_X = 4;
    public static final int kEXITPOS_Y = 2;

    private long fConfiguration = 0;
    private final List<Car> fCars = new ArrayList<>();

    public void clear() {
        fConfiguration = 0L;
        fCars.clear();
    }

    public void addCar(final Car car) {
        int xPos = car.getX();
        int yPos = car.getY();
        for (int i = 0; i < car.getLength(); i++) {
            long bit = getBit(xPos, yPos);
            assert (fConfiguration & bit) == 0L : "There already is a car on [" + xPos + ", " + yPos + "]: " + car;
            fConfiguration |= bit;
            if (car.isHorizontal())
                xPos++;
            else
                yPos++;
        }

        fCars.add(car);
    }

    public List<Car> getCars() {
        return fCars;
    }

    public boolean isFinished() {
        return fCars.get(0).getX() == 5;
    }

    public boolean moveCar(final Car car, final boolean forward) {
        int oldPosX = car.getX();
        int oldPosY = car.getY();
        int newPosX;
        int newPosY;
        int oldX;
        int oldY;
        int newX;
        int newY;
        if (car.isHorizontal()) {
            newPosY = oldPosY;
            oldY = oldPosY;
            newY = oldPosY;
            if (forward) {
                newPosX = oldPosX + 1;
                oldX = oldPosX;
                newX = oldPosX + car.getLength();
            } else {
                newPosX = oldPosX - 1;
                oldX = (oldPosX + car.getLength()) - 1;
                newX = oldPosX - 1;
            }
        } else {
            newPosX = oldPosX;
            oldX = oldPosX;
            newX = oldPosX;
            if (forward) {
                newPosY = oldPosY + 1;
                oldY = oldPosY;
                newY = oldPosY + car.getLength();
            } else {
                newPosY = oldPosY - 1;
                oldY = (oldPosY + car.getLength()) - 1;
                newY = oldPosY - 1;
            }
        }
        if ((newX >= 0 && newX < kWIDTH && newY >= 0 && newY < kHEIGHT) || (newX == kEXIT_X && newY == kEXIT_Y)) {
            long newBit = getBit(newX, newY);
            if ((fConfiguration & newBit) == 0L) {
                fConfiguration |= newBit;
                long oldBit = getBit(oldX, oldY);
                fConfiguration &= ~oldBit;
                car.setX(newPosX);
                car.setY(newPosY);
                return true;
            }
        }
        return false;
    }

    public Car getCarAt(final int x, final int y) {
        for (final Car car : fCars) {
            if (car.isHorizontal()) {
                if (x >= car.getX() && x < car.getX() + car.getLength() && y == car.getY()) {
                    return car;
                }
            } else {
                if (x == car.getX() && y >= car.getY() && y < car.getY() + car.getLength()) {
                    return car;
                }
            }
        }
        return null;
    }

    private static long getBit(final int x, final int y) {
        return 1L << y * (kWIDTH + 1) + x;
    }
}
