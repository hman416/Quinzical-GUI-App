package quinzical.utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;

/**
 * ClueSpeaker implements functionalities for adding text to speech to quinzical. A slider can also be provided that 
 * is linked to the _sliderValue field.
 */
public class ClueSpeaker {
	private double _sliderValue;
	private static ClueSpeaker _clueSpeakerObject;

	private ClueSpeaker() {
		_sliderValue = 1;
		_clueSpeakerObject = this;
		
		String cmd = "chmod +xw .tts.scm";
		
		//create scheme file
		File scheme = new File(".tts.scm");
				
		try {
			scheme.createNewFile();
			
			//setting executable permissions
			ProcessBuilder builder = new ProcessBuilder("bash", "-c", cmd);
			Process p = builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/*
	 * singleton return method for a single instance of ClueSpeaker
	 */
	public static ClueSpeaker getCSObject() {
		if (_clueSpeakerObject != null) {
			return _clueSpeakerObject;
		} else {
			_clueSpeakerObject = new ClueSpeaker();
			return _clueSpeakerObject;
		}

	}

	/*
	 * Returns a Slider object that can be used to adjust the speed of the tts
	 */
	public Slider getSlider() {
		
		Slider ttsSlider = new Slider(0.5,2,_sliderValue);
		
		//slider presets
		ttsSlider.setMinWidth(200);
		ttsSlider.setMaxWidth(200);
		ttsSlider.setBlockIncrement(0.125);
		ttsSlider.setShowTickMarks(true);
		ttsSlider.setShowTickLabels(true);
		ttsSlider.setSnapToTicks(true);
		ttsSlider.setMajorTickUnit(0.5);
		
		ttsSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				_sliderValue = ttsSlider.getValue();
				
			}
			
		});

		return ttsSlider;
	}
	
	/*
	 * takes a string and produces a speech synthesis
	 */
	public void say(String line) {
		
		String nzSpeaker = "(voice_akl_nz_jdt_diphone)";
		String setSpeed = "(Parameter.set 'Duration_Stretch " + (1/_sliderValue) + ")";
		String sayLine = "(SayText\"" + line +"\")";
		
		String content = nzSpeaker + System.lineSeparator() + setSpeed + System.lineSeparator() + sayLine + System.lineSeparator();
		try {
			//write festival settings to scheme file
			FileWriter writer = new FileWriter(new File(".tts.scm"),false);
			
			//everything in scheme file previously is overwritten
			writer.write(content);
			writer.flush();
			writer.close();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		//command to run tts
		String cmd = "festival -b .tts.scm";
		
		ProcessBuilder builder = new ProcessBuilder("bash", "-c", cmd);
		
		try {
		
			Process process = builder.start();
			//wait for process to finish
			process.waitFor();
			
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			
		} 

	}
	
}
