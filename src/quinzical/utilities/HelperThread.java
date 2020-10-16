package quinzical.utilities;

import javafx.application.Platform;
import javafx.scene.control.Button;
import quinzical.screens.QModule;
import quinzical.screens.QuestionModule;

public class HelperThread extends Thread{
	private ClueSpeaker _clueSpeaker;
	private String _line;
	private Button _btn;
	private QuestionModule _qm;
	
	public HelperThread(ClueSpeaker cs, String line, Button btn, QuestionModule qm) {
		_clueSpeaker = cs;
		_line = line;
		_btn = btn;
		_qm = qm;
	}

	@Override
	public void run() {
		//get tts to say the clue
		_clueSpeaker.say(_line);
		
		//update gui
		CompletedPaper cp = new CompletedPaper(_btn, _qm);
		Platform.runLater(cp);
	}
}
