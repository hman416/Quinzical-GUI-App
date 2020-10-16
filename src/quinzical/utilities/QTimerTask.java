package quinzical.utilities;

import java.util.Timer;
import java.util.TimerTask;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Text;
import javafx.scene.control.Alert.AlertType;

public class QTimerTask  extends TimerTask{
	private int _count;
	private Text _countDown;
	private Timer _t;

	@Override
	public void run() {
		_count--;
		_countDown.setText("Countdown: " + _count);
		System.out.println(_count);
		//_count--;
		if (_count <= 0) {
			_t.cancel();
			_t.purge();

			Alert alert = new Alert(AlertType.INFORMATION, "You've run out of time to attempt this clue! \n"
					+ "The answer was...", ButtonType.OK);
			alert.showAndWait();

			//_previousScene = _preGameMod.createScene();
			//_appStage.setScene(_previousScene);

			return;		
		}

	}
}
