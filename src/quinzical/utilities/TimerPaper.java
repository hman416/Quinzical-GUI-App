package quinzical.utilities;

import quinzical.screens.QModule;
import quinzical.screens.QuestionModule;

public class TimerPaper implements Runnable{
	private QuestionModule _qm;
	private QModule _mod;
	
	public TimerPaper(QuestionModule qm, QModule mod) {
		_qm =qm;
		_mod = mod;
	}
	
	@Override
	public void run() {
		_qm.leave(_mod);
	}
	

}
