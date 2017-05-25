package be.t_ars.busyparkinglot.levels;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import be.t_ars.busyparkinglot.R;
import be.t_ars.busyparkinglot.data.LotOpenHelper;
import be.t_ars.busyparkinglot.lot.LotActivity;

public class LevelsActivity extends Activity
{
	private static final String kTAG = "Levels";

	private int fLevelCount = 0;
	private final LotOpenHelper fLotOpenHelper;
	private LevelsView fLevelsView = null;

	public LevelsActivity()
	{
		fLotOpenHelper = new LotOpenHelper(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.levels);
		fLevelsView = (LevelsView)findViewById(R.id.levelsview);
		try
		{
			final String[] list = getResources().getAssets().list("levels");
			fLevelCount = list.length;
		}
		catch (final IOException e)
		{
			Log.e(kTAG, "Could not list levels", e);
		}

		fLevelsView.setLevels(fLevelCount);
		updateLevels();
	}

	@Override
	protected void onRestart()
	{
		super.onRestart();
		updateLevels();
	}
	
	@Override
	protected void onStop()
	{
	    super.onStop();
	    fLotOpenHelper.close();
	}

	public void start(final int number)
	{
		final Intent intent = new Intent(this, LotActivity.class);
		intent.putExtra("level", number);
		intent.putExtra("count", fLevelCount);
		startActivity(intent);
	}

	private void updateLevels()
	{
		if (fLevelsView != null)
		{
			fLevelsView.updateLevels(fLotOpenHelper.getFinishedLevels());
		}
	}
}
