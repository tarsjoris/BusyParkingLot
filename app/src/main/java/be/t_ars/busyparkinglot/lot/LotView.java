package be.t_ars.busyparkinglot.lot;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import be.t_ars.busyparkinglot.data.CarData;


public class LotView extends View implements OnTouchListener {
    @SuppressWarnings("unused")
    private static final String kTAG = "RHView";

    private static final int kBORDER = Color.rgb(150, 150, 150);
    private static final int kSHADOW1 = Color.WHITE;
    private static final int kSHADOW2 = Color.BLACK;
    private static final int kSHADOWSPACE = 2;
    private static final int kFIELD = Color.LTGRAY;

    public static final int[] kCAR_COLOR = new int[]{
            Color.rgb(242, 102, 49),
            Color.rgb(92, 42, 71),
            Color.rgb(206, 119, 40),
            Color.rgb(1, 113, 91),
            Color.rgb(240, 144, 32),
            Color.rgb(255, 210, 2),
            Color.rgb(0, 84, 61),
            Color.rgb(121, 107, 132),
            Color.rgb(175, 189, 34),
            Color.rgb(139, 0, 89),
            Color.rgb(119, 162, 47),
            Color.rgb(229, 231, 108),
            Color.rgb(1, 43, 93)
    };

    public static final int kINSET = 8;
    private static final int kCOLCOUNT = Field.kWIDTH + 2;
    private static final int kROWCOUNT = Field.kHEIGHT + 2;
    private static final int kBLOCK_COUNT = Math.max(kCOLCOUNT, kROWCOUNT);

    private final Field fField = new Field();

    private boolean fSolutionValid = false;
    private int fFieldStartX;
    private int fFieldStartY;
    private int fBlockSize;
    private Paint fPaint;
    private Rect fRectangle;

    private boolean fFinished = false;
    private Car fDraggingCar = null;
    private int fDragStartX;
    private int fDragStartY;

    public LotView(final Context context) {
        super(context);
        init();
    }

    public LotView(final Context context, final AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    private void init() {
        setFocusable(false);
        fPaint = new Paint();
        fRectangle = new Rect();

        setFocusable(true);
        setFocusableInTouchMode(true);

        setOnTouchListener(this);
    }

    public void setCars(final List<CarData> cars) {
        fSolutionValid = false;
        synchronized (fField) {
            fField.clear();
            int number = 0;
            for (final CarData car : cars) {
                fField.addCar(new Car(car, number++));
            }
            updateFinished();
        }
        invalidate();
    }

    public List<CarData> getCarsForSolution() {
        fSolutionValid = true;
        final List<CarData> cars = new ArrayList<CarData>();
        synchronized (fField) {
            for (final Car car : fField.getCars()) {
                cars.add(new CarData(car));
            }
        }
        return cars;
    }

    public boolean solutionValid() {
        return fSolutionValid;
    }

    public void move(final int number, final boolean forward) {
        synchronized (fField) {
            final List<Car> cars = fField.getCars();
            if (number >= 0 && number < cars.size()) {
                fField.moveCar(cars.get(number), forward);
                updateFinished();
                invalidate();
            }
        }
    }

    public boolean isFinished() {
        return fFinished;
    }

    public static int getBlockSize(final int width, final int height) {
        return Math.min(width / kBLOCK_COUNT, height / kBLOCK_COUNT);
    }

    @Override
    public void onDraw(final Canvas canvas) {
        canvas.save();

        final int width = getWidth();
        final int height = getHeight();
        fBlockSize = getBlockSize(width, height);

        fFieldStartX = (width - (kCOLCOUNT * fBlockSize)) / 2;
        fFieldStartY = (height - (kROWCOUNT * fBlockSize)) / 2;
        canvas.translate(fFieldStartX, fFieldStartY);

        paintFence(canvas);

        canvas.translate(fBlockSize, fBlockSize);
        // paint grooves
        for (int x = 0; x < Field.kWIDTH; ++x) {
            for (int y = 0; y < Field.kHEIGHT; ++y) {
                drawBlock(canvas, x, y);
            }
        }
        drawBlock(canvas, Field.kEXIT_X, Field.kEXIT_Y);

        synchronized (fField) {
            fPaint.setStyle(Paint.Style.FILL);
            final int radius = fBlockSize / 8;
            for (final Car car : fField.getCars()) {
                fPaint.setShadowLayer(2, 2, 2, Color.BLACK);
                fPaint.setColor(kCAR_COLOR[car.getNumber() % kCAR_COLOR.length]);
                final Path path = new Path();
                final int endX = car.getX() + (car.isHorizontal() ? car.getLength() : 1);
                final int endY = car.getY() + (car.isHorizontal() ? 1 : car.getLength());
                final RectF rect = new RectF(car.getX() * fBlockSize + kINSET, car.getY() * fBlockSize + kINSET, endX * fBlockSize - kINSET, endY * fBlockSize - kINSET);
                path.addRoundRect(rect, radius, radius, Direction.CW);
                path.close();
                canvas.drawPath(path, fPaint);
                if (car.getNumber() == 0) {
                    fPaint.setShadowLayer(0, 0, 0, Color.BLACK);
                    fPaint.setColor(Color.WHITE);
                    canvas.drawCircle((endX - 0.5f) * fBlockSize, (endY - 0.5f) * fBlockSize, fBlockSize / 6, fPaint);
                }
            }
        }
        if (fFinished) {
            fPaint.setTextSize(40);
            fPaint.setShadowLayer(50, 10, 10, Color.BLACK);
            fPaint.setColor(Color.WHITE);
            canvas.drawText("Finished", fBlockSize * 2, (kROWCOUNT - 2) * fBlockSize / 2, fPaint);
        }
        fPaint.setShadowLayer(0, 0, 0, Color.WHITE);

        canvas.restore();

    }

    private void paintFence(final Canvas canvas) {
        final int outsideRight = kCOLCOUNT * fBlockSize;
        final int outsideBottom = kROWCOUNT * fBlockSize;

        fPaint.setColor(kBORDER);
        fRectangle.set(1, 1, outsideRight - 1, outsideBottom - 1);
        canvas.drawRect(fRectangle, fPaint);

        paintShadow(canvas, 0, 0, outsideRight - 1, outsideBottom - 1, true);
        paintShadow(canvas, fBlockSize - kSHADOWSPACE, fBlockSize - kSHADOWSPACE, outsideRight - fBlockSize + kSHADOWSPACE, outsideBottom - fBlockSize + kSHADOWSPACE, false);

        // paint background
        fPaint.setColor(kFIELD);
        fRectangle.set(fBlockSize - kSHADOWSPACE + 1, fBlockSize - kSHADOWSPACE + 1, outsideRight - fBlockSize + kSHADOWSPACE, outsideBottom - fBlockSize + kSHADOWSPACE);
        canvas.drawRect(fRectangle, fPaint);

        // paint exit
        final int exitX1 = (Field.kEXIT_X + 1) * fBlockSize;
        final int exitX2 = (Field.kEXIT_X + 2) * fBlockSize;
        final int exitY1 = (Field.kEXIT_Y + 1) * fBlockSize;
        final int exitY2 = (Field.kEXIT_Y + 2) * fBlockSize;

        fRectangle.set(exitX1 + kSHADOWSPACE, exitY1 - kSHADOWSPACE + 1, exitX2, exitY2 + kSHADOWSPACE);
        canvas.drawRect(fRectangle, fPaint);

        fPaint.setColor(kSHADOW2);
        canvas.drawLine(exitX1 + kSHADOWSPACE, exitY1 - kSHADOWSPACE, exitX2 - 1, exitY1 - kSHADOWSPACE, fPaint);
        fPaint.setColor(kSHADOW1);
        canvas.drawLine(exitX1 + kSHADOWSPACE, exitY2 + kSHADOWSPACE, exitX2 - 1, exitY2 + kSHADOWSPACE, fPaint);
    }

    private void drawBlock(final Canvas canvas, final int x, final int y) {
        canvas.save();
        canvas.translate(x * fBlockSize, y * fBlockSize);
        paintShadow(canvas, kSHADOWSPACE, kSHADOWSPACE, fBlockSize - kSHADOWSPACE, fBlockSize - kSHADOWSPACE, true);
        canvas.restore();
    }

    private void paintShadow(final Canvas canvas, final int x1, final int y1, final int x2, final int y2, final boolean up) {
        fPaint.setColor(up ? kSHADOW1 : kSHADOW2);
        canvas.drawLine(x1, y1, x2, y1, fPaint);
        canvas.drawLine(x1, y1, x1, y2, fPaint);

        fPaint.setColor(up ? kSHADOW2 : kSHADOW1);
        canvas.drawLine(x2, y1, x2, y2, fPaint);
        canvas.drawLine(x1, y2, x2 + 1, y2, fPaint);
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (fFinished) {
            return false;
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                final int x = Math.round(event.getX());
                final int y = Math.round(event.getY());
                final Car car = fField.getCarAt(getCol(x), getRow(y));
                if (car != null) {
                    fDraggingCar = car;
                    fDragStartX = x;
                    fDragStartY = y;
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (fDraggingCar != null) {
                    final int x = Math.round(event.getX());
                    final int y = Math.round(event.getY());
                    int startV;
                    final int newV;
                    if (fDraggingCar.isHorizontal()) {
                        startV = fDragStartX;
                        newV = x;
                    } else {
                        startV = fDragStartY;
                        newV = y;
                    }
                    int d = newV - startV;
                    while ((Math.abs(d) > fBlockSize / 2) && fField.moveCar(fDraggingCar, d > 0)) {
                        fSolutionValid = false;
                        updateFinished();
                        invalidate();
                        if (fDraggingCar.isHorizontal()) {
                            startV = (fDragStartX += (d > 0 ? fBlockSize : -fBlockSize));
                        } else {
                            startV = (fDragStartY += (d > 0 ? fBlockSize : -fBlockSize));
                        }
                        d = newV - startV;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                fDraggingCar = null;
                break;
            }
        }
        return true;
    }

    private void updateFinished() {
        if (fFinished = fField.isFinished()) {
            ((LotActivity) getContext()).setFinished();
        }
    }

    private int getCol(final int x) {
        return (x - fFieldStartX) / fBlockSize - 1;
    }

    private int getRow(final int y) {
        return (y - fFieldStartY) / fBlockSize - 1;
    }
}
