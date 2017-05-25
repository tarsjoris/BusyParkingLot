package be.t_ars.busyparkinglot.solve;

import be.t_ars.busyparkinglot.data.CarData;

public class Car extends CarData
{
	private final byte fNumber;

	public Car(final CarData car, final byte number)
	{
		super(car);
		fNumber = number;
	}

	public String toString()
	{
		return (new StringBuilder("[number: ")).append(fNumber).append(", length: ").append(fLength).append(", ").append(fHorizontal ? "horizontal" : "vertical").append(", x: ").append(fX).append(", y: ").append(fY).append("]").toString();
	}

	public byte getNumber()
	{
		return fNumber;
	}

	public void setX(int x)
	{
		fX = x;
	}

	public void setY(int y)
	{
		fY = y;
	}
}
