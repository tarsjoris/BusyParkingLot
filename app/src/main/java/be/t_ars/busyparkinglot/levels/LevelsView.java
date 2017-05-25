package be.t_ars.busyparkinglot.levels;

import java.util.Set;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import be.t_ars.busyparkinglot.R;

public class LevelsView extends TableLayout implements OnClickListener {
    private final int kPADDING = 3;

    private final LevelsActivity fActivity;
    private Button[] fButtons = null;
    private int fColumns = 3;

    public LevelsView(final Context context) {
        super(context);
        fActivity = (LevelsActivity) context;
    }

    public LevelsView(final Context context, final AttributeSet attributeSet) {
        super(context, attributeSet);
        fActivity = (LevelsActivity) context;

        final TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.LevelsView);
        fColumns = a.getInt(R.styleable.LevelsView_cols, 2);
    }

    public void setLevels(final int count) {
        fButtons = new Button[count];
        removeAllViews();
        setPadding(kPADDING, kPADDING, kPADDING, kPADDING);

        for (int i = 0; i < Math.min(fColumns, count); ++i) {
            setColumnStretchable(i, true);
        }
        TableRow row = null;
        for (int i = 0; i < count; ++i) {
            if (i % fColumns == 0) {
                row = new TableRow(fActivity);
                addView(row);
            }
            final Button button = new Button(fActivity);
            button.setId(i);
            button.setText("Level " + (i + 1));
            button.setPadding(kPADDING, kPADDING, kPADDING, kPADDING);
            button.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            button.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            button.setOnClickListener(this);
            row.addView(button);
            fButtons[i] = button;
        }
    }

    public void updateLevels(final Set<Integer> finishedLevels) {
        if (fButtons == null) {
            return;
        }
        for (int i = 0; i < fButtons.length; ++i) {
            fButtons[i].setTextColor(finishedLevels.contains(Integer.valueOf(i)) ? Color.GRAY : Color.WHITE);
        }
    }

    @Override
    public void onClick(final View view) {
        final int number = view.getId();
        fActivity.start(number);
    }
}
