package be.t_ars.busyparkinglot.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by samsung on 25/05/2017.
 */

public class LevelReader {
    public static List<CarData> readLevel(final InputStream inputStream) throws NumberFormatException, IOException {
        final List<CarData> cars = new ArrayList<>();
        int length;
        while ((length = readNumber(inputStream)) != -1) {
            final boolean horizontal = readNumber(inputStream) == 0;
            final int x = readNumber(inputStream);
            final int y = readNumber(inputStream);
            cars.add(new CarData(length, horizontal, x, y));
        }
        return cars;
    }

    private static int readNumber(InputStream inputStream) throws NumberFormatException, IOException {
        final int ch = inputStream.read();
        if (ch == -1) {
            return -1;
        } else {
            return ch - '0';
        }
    }
}
