package be.t_ars.busyparkinglot.data;

/**
 * Created by samsung on 25/05/2017.
 */

public class CarData {
    protected final int fLength;
    protected final boolean fHorizontal;
    protected int fX;
    protected int fY;

    public CarData(final int length, final boolean horizontal, final int x, final int y)
    {
        fLength = length;
        fHorizontal = horizontal;
        fX = x;
        fY = y;
    }

    public CarData(final CarData car)
    {
        fLength = car.getLength();
        fHorizontal = car.isHorizontal();
        fX = car.getX();
        fY = car.getY();
    }

    public int getLength()
    {
        return fLength;
    }

    public boolean isHorizontal()
    {
        return fHorizontal;
    }

    public int getX()
    {
        return fX;
    }

    public int getY()
    {
        return fY;
    }
}
