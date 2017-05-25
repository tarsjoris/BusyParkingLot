package be.t_ars.busyparkinglot.solve;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.t_ars.busyparkinglot.data.CarData;
import be.t_ars.busyparkinglot.data.LevelReader;


public class Solver {
    private static final int kLOG_LEVEL_VERBOSE = 3;
    private static final int kLOG_LEVEL_DEBUG = 2;
    private static final int kLOG_LEVEL_INFO = 1;
    private static final int kLOG_LEVEL_NONE = 0;

    private static final int kLOG_LEVEL = kLOG_LEVEL_INFO;
    private static final int kSTARTLEVEL = 1;

    private static final int kWIDTH = 6;
    private static final int kHEIGHT = 6;
    private static final int kEXIT_X = 4;
    private static final int kEXIT_Y = 2;
    private long fOccupied;
    private long fSetting;
    private final Car[] fCars;

    private Solver(final List<CarData> cars) {
        fOccupied = 0L;
        fSetting = 0L;
        fCars = new Car[cars.size()];
        for (int number = 0; number < fCars.length; ++number) {
            final Car car = new Car(cars.get(number), (byte) number);
            fCars[number] = car;
            int xPos = car.getX();
            int yPos = car.getY();
            for (int i = 0; i < car.getLength(); i++) {
                final long bit = 1L << (yPos * kWIDTH + xPos);
                assert (fOccupied & bit) != 0L : "There already is a car on [" + xPos + ", " + yPos + "]: " + car;
                fOccupied |= bit;
                if (car.isHorizontal()) {
                    xPos++;
                } else {
                    yPos++;
                }
            }
            fSetting |= (long) (car.isHorizontal() ? car.getX() : car.getY()) << (car.getNumber() * 3L);
        }
    }

    public static Solution solve(final List<CarData> cars) {
        return new Solver(cars).solve();
    }

    public Solution solve() {
        if (fCars.length == 0) {
            return null;
        }
        final Car car = fCars[0];
        if (car.getX() == kEXIT_X && car.getY() == kEXIT_Y) {
            final Solution solution = new Solution();
            solution.appendStep((byte) 0x0, true);
            return solution;
        }
        if (car.getX() >= kEXIT_X && car.getY() == kEXIT_Y) {
            return null;
        }
        int maxDepth = kSTARTLEVEL;
        final Map<Long, Integer> history = new HashMap<Long, Integer>();
        history.put(Long.valueOf(fSetting), Integer.valueOf(Integer.MAX_VALUE));
        for (; ; ) {
            if (kLOG_LEVEL >= kLOG_LEVEL_DEBUG) {
                System.out.println("Max depth: " + maxDepth);
            }
            final Solution solution = solve(maxDepth, history);
            if (solution != null) {
                return solution;
            }
            maxDepth++;
        }
    }

    private Solution solve(final int maxDepth, final Map<Long, Integer> history) {
        if (kLOG_LEVEL >= kLOG_LEVEL_VERBOSE) {
            print();
        }
        final Solution solution = new Solution();

        int currentDepth = 1;
        Car car = fCars[0];
        boolean forward = true;
        search:
        for (; ; ) {
            if (moveCar(car, forward)) {
                final Long setting = Long.valueOf(fSetting);
                final Integer depth = history.get(setting);
                if (depth == null || depth.intValue() < (maxDepth - currentDepth)) {
                    final byte number = car.getNumber();
                    if (number == 0 && car.getX() == kEXIT_X && car.getY() == kEXIT_Y) {
                        solution.appendStep(number, forward);
                        solution.appendStep((byte) 0x0, true);
                        return solution;
                    }
                    history.put(setting, Integer.valueOf(maxDepth - currentDepth));
                    if (currentDepth < maxDepth) {
                        solution.appendStep(number, forward);
                        if (kLOG_LEVEL >= kLOG_LEVEL_VERBOSE) {
                            System.out.println(solution.toString());
                        }
                        ++currentDepth;
                        car = fCars[0];
                        forward = true;
                        continue search;
                    } else {
                        final Solution.Step parentStep = solution.getLastStep();
                        if (parentStep != null) {
                            parentStep.setLimited();
                        }
                    }
                }
                moveCar(car, !forward);
            }
            for (; ; ) {
                if (forward) {
                    forward = false;
                    continue search;
                }
                final int index = car.getNumber() + 1;
                if (index < fCars.length) {
                    car = fCars[index];
                    forward = true;
                    continue search;
                }
                --currentDepth;
                final Solution.Step lastStep = solution.removeLastStep();
                if (lastStep == null) {
                    return null;
                } else {
                    car = fCars[lastStep.getCar()];
                    forward = lastStep.isForward();
                    if (lastStep.isLimited()) {
                        final Solution.Step parentStep = solution.getLastStep();
                        if (parentStep != null) {
                            parentStep.setLimited();
                        }
                    } else {
                        history.put(Long.valueOf(fSetting), Integer.MAX_VALUE);
                    }
                    moveCar(car, !forward);
                    if (kLOG_LEVEL >= kLOG_LEVEL_VERBOSE) {
                        System.out.println(solution.toString());
                    }
                }
            }
        }
    }

    private boolean moveCar(final Car car, final boolean forward) {
        final boolean horizontal = car.isHorizontal();
        int newPosX;
        int newPosY;
        int oldX;
        int oldY;
        int newX;
        int newY;
        {
            int oldPosX = car.getX();
            int oldPosY = car.getY();
            if (horizontal) {
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
        }
        if (newX >= 0 && newX < kWIDTH && newY >= 0 && newY < kHEIGHT) {
            final long bit = 1L << (newY * kWIDTH + newX);
            if ((fOccupied & bit) == 0L) {
                fOccupied |= bit;
                fOccupied &= ~(1L << (oldY * kWIDTH + oldX));
                car.setX(newPosX);
                car.setY(newPosY);
                final int shift = car.getNumber() * 3;
                fSetting &= ~(7L << shift);
                fSetting |= ((long) (horizontal ? newPosX : newPosY)) << shift;
                if (kLOG_LEVEL >= kLOG_LEVEL_VERBOSE) {
                    System.out.println(car.getNumber() + ": " + (forward ? "forward" : "backward"));
                    print();
                }
                return true;
            }
        }
        return false;
    }

    private void print() {
        System.out.print('+');
        for (int x = 0; x < kWIDTH; ++x) {
            System.out.print('-');
        }
        System.out.println('+');
        for (int y = 0; y < kHEIGHT; ++y) {
            System.out.print('|');
            for (int x = 0; x < kWIDTH; ++x) {
                final long bit = 1L << (y * kWIDTH + x);
                if ((fOccupied & bit) != 0L) {
                    System.out.print('*');
                } else {
                    System.out.print(' ');
                }
            }
            System.out.println('|');
        }
        System.out.print('+');
        for (int x = 0; x < kWIDTH; ++x) {
            System.out.print('-');
        }
        System.out.println('+');
        System.out.print("Setting: ");
        for (int i = 0; i < fCars.length; ++i) {
            System.out.print("[" + i + ":" + ((fSetting >> (i * 3)) & 7L) + "]");
        }
        System.out.println();
    }

    private static void solveFile(final File file) throws NumberFormatException, IOException {
        final List<CarData> cars;
        final InputStream stream = new BufferedInputStream(new FileInputStream(file));
        try {
            cars = LevelReader.readLevel(stream);
        } finally {
            stream.close();
        }
        final long startTime = System.currentTimeMillis();
        final Solution solution = solve(cars);
        if (kLOG_LEVEL >= kLOG_LEVEL_DEBUG) {
            System.out.println(solution.toString());
        }
        if (kLOG_LEVEL >= kLOG_LEVEL_INFO) {
            System.out.println("Time: " + (System.currentTimeMillis() - startTime));
        }
        Solution.Step step = null;
        while ((step = solution.removeFirstStep()) != null) {
            if (step.getCar() <= 9) {
                System.out.print(step.getCar());
            } else {
                System.out.print((char) ('a' + step.getCar() - 10));
            }
            System.out.print(step.isForward() ? '1' : '0');
        }
        System.out.println();
    }

    public static void main(final String[] args) {
        try {
            final File file = new File(args[0]);
            if (file.isDirectory()) {
                final File[] children = file.listFiles();
                Arrays.sort(children, new Comparator<File>() {
                    @Override
                    public int compare(final File file1, final File file2) {
                        final String name1 = file1.getName();
                        final String name2 = file2.getName();
                        return Integer.valueOf(name1.substring(0, name1.indexOf('.'))).compareTo(Integer.valueOf(name2.substring(0, name2.indexOf('.'))));
                    }
                });
                for (final File child : children) {
                    solveFile(child);
                }
            } else {
                solveFile(file);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
