package be.t_ars.busyparkinglot.lot;

import java.util.List;

import android.os.AsyncTask;

import be.t_ars.busyparkinglot.data.CarData;
import be.t_ars.busyparkinglot.solve.Solution;
import be.t_ars.busyparkinglot.solve.Solver;

public class SolveTask extends AsyncTask<List<CarData>, Void, Solution>
{
	private final LotActivity fLotActivity;
	
	public SolveTask(final LotActivity activity)
	{
		fLotActivity = activity;
	}
	
	@Override
    protected Solution doInBackground(final List<CarData>... params)
    {
	    return Solver.solve(params[0]);
    }
	
	@Override
	protected void onPostExecute(final Solution result)
	{
	    super.onPostExecute(result);
	    fLotActivity.setSolution(result);
	}
	
}
