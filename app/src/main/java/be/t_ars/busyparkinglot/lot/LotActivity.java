package be.t_ars.busyparkinglot.lot;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import be.t_ars.busyparkinglot.R;
import be.t_ars.busyparkinglot.data.CarData;
import be.t_ars.busyparkinglot.data.LevelReader;
import be.t_ars.busyparkinglot.data.LotOpenHelper;
import be.t_ars.busyparkinglot.solve.Solution;

public class LotActivity extends Activity {
    private static final String kTAG = "Lot";

    private final LotOpenHelper fLotOpenHelper;
    private int fCurrentLevel = -1;
    private Solution fSolution = null;
    private MediaPlayer fMediaPlayer;


    public LotActivity() {
        fLotOpenHelper = new LotOpenHelper(this);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.lot);

        fMediaPlayer = MediaPlayer.create(this, R.raw.horn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fMediaPlayer != null) {
            fMediaPlayer.release();
            fMediaPlayer = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        reset();
    }

    @Override
    protected void onStop() {
        super.onStop();
        fLotOpenHelper.close();
    }

    public void setFinished() {
        fLotOpenHelper.levelFinished(fCurrentLevel);
        if (fMediaPlayer != null) {
            fMediaPlayer.start();
        }
        if (hasNextLevel()) {
            findViewById(R.id.next).setVisibility(View.VISIBLE);
        }
    }

    public void help(final View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getText(R.string.lot_help_title));
        builder.setMessage(getText(R.string.lot_help_message));
        builder.setPositiveButton(getText(R.string.lot_help_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    public void reset(final View view) {
        reset();
    }

    private void reset() {
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            fCurrentLevel = bundle.getInt("level");
            setTitle(getString(R.string.app_name) + ": Level " + (fCurrentLevel + 1));
            try {
                final InputStream inputStream = getResources().getAssets().open("levels/" + fCurrentLevel + ".bpl");
                try {
                    final List<CarData> cars = LevelReader.readLevel(inputStream);
                    final LotView lotView = (LotView) findViewById(R.id.lotview);
                    lotView.setCars(cars);
                } finally {
                    inputStream.close();
                }
            } catch (final IOException e) {
                Log.e(kTAG, "Could not read level " + fCurrentLevel, e);
            }
        }
    }

    public void hint(final View view) {
        final LotView lotView = (LotView) findViewById(R.id.lotview);
        if (!lotView.solutionValid()) {
            final View hintButton = findViewById(R.id.hint);
            hintButton.setEnabled(false);
            new SolveTask(this).execute(lotView.getCarsForSolution());
        } else {
            doHint();
        }
    }

    public void setSolution(final Solution solution) {
        final LotView lotView = (LotView) findViewById(R.id.lotview);
        if (lotView.solutionValid()) {
            fSolution = solution;
            doHint();
        }
        final View hintButton = findViewById(R.id.hint);
        hintButton.setEnabled(true);
    }

    private void doHint() {
        if (fSolution != null) {
            final Solution.Step step = fSolution.removeFirstStep();
            if (step != null) {
                final LotView lotView = (LotView) findViewById(R.id.lotview);
                lotView.move(step.getCar(), step.isForward());
            }
        }
    }

    public void next(final View view) {
        finish();
        if (hasNextLevel()) {
            final Intent intent = new Intent(this, LotActivity.class);
            intent.putExtra("level", fCurrentLevel + 1);
            intent.putExtra("count", getLevelCount());
            startActivity(intent);
        }
    }

    private boolean hasNextLevel() {
        return fCurrentLevel + 1 < getLevelCount();
    }

    private int getLevelCount() {
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            return bundle.getInt("count");
        }
        return -1;
    }
}