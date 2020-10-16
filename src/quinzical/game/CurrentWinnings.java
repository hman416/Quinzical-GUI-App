package quinzical.game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * CurrentWinnings class manages current winnings. This class updates current winnings field and save file, and manages 
 * format current winnings is displayed in. This class also loads values from the saved file upon initialisation.
 */

public class CurrentWinnings {
	private static CurrentWinnings _thisObj;
	private String _value;
	private ArrayList<Text> _listeners;

	private CurrentWinnings(){
		_listeners = new ArrayList<>();

		//read from file
		File saveCW = new File(".saved/savedCW");

		try {
			//if file does not exist then create file and write to file
			if (saveCW.createNewFile()) {
				FileWriter FW = new FileWriter(".saved/savedCW");
				FW.write("0");
				FW.flush();
				FW.close();
			}
			//read from file

			BufferedReader reader = new BufferedReader(new FileReader(".saved/savedCW"));
			_value = reader.readLine();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/*
	 * sets the current value of winnings
	 */
	public void setValue(int num) {
		_value = ""+num;
	}

	/*
	 * increase the winnings by an amount
	 */
	public void increaseWinning(int value) {
		_value ="" + (Integer.parseInt(_value) + value);
		updateCWSave();
		update();
	}

	/*
	 * decrease the winnings by an amount
	 */
	public void decreaseWinning(int value) {
		_value ="" + (Integer.parseInt(_value) - value);
		updateCWSave();
		update();
	}

	/*
	 * reset the value of the winnings
	 */
	public static void clearCW() {
		getObj().setValue(0);
		getObj().updateCWSave();
		getObj().update();
	}

	/*
	 * return a string of the current winnings with appropriate formatting eg $500
	 */
	public String displayCWValue() {
		if(Integer.parseInt(_value) < 0) {
			return "-$" + (Integer.parseInt(_value)*-1);
		}else {
			return "$" + _value;
		}
	}

	/*
	 * update the current winnings file
	 */
	private void updateCWSave() {
		try {
			FileWriter writer = new FileWriter(".saved/savedCW");
			writer.write(_value);
			writer.flush();
			writer.close();
		} catch (IOException e) {			
			e.printStackTrace();
		}

	}

	/*
	 * add a listener object
	 */
	public void addListener(Text l) {
		_listeners.add(l);
		update();
	}
	
	/*
	 * fire an update event
	 */
	private void update() {
		for (Text l : _listeners) {
			l.setText("Current Winnings: " + displayCWValue());
			l.setFont(Font.font("Arial", FontWeight.MEDIUM, 17));
		}
	}

	/*
	 * returns a singleton object of this class
	 */
	public static CurrentWinnings getObj() {
		if (_thisObj == null) {
			_thisObj = new CurrentWinnings();
		}

		return _thisObj;
	}
}
