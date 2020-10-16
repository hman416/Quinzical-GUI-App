package quinzical.utilities;

import javafx.scene.control.Button;
import quinzical.screens.QModule;
import quinzical.screens.QuestionModule;

public class CompletedPaper implements Runnable{
private Button _btn;
private QuestionModule _qm;

	public CompletedPaper(Button btn, QuestionModule qm) {
		_btn = btn;
		_qm = qm;
	}
	
	@Override
	public void run() {
		//update the gui and enable the "listen to clue" button
		_qm.countDownTimer();
		_btn.setDisable(false);
	}

}
