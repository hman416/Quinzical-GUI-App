package quinzical.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import quinzical.game.AllCat;
import quinzical.game.CatBank;
import quinzical.game.CurrentWinnings;
import quinzical.screens.CatSelectionScreen;

/**
 * HelperMethods Class contain helpful static methods that other classes can call upon This helps reduce the size of 
 * other methods and class. 
 *
 */

public class HelperMethods {
	
	/*
	 * Used to initialise all the saved files and object states
	 */
	public static void Initialisation() {

		//create saveDir if it doesnt exist
		File saveDir = new File(".saved");
		saveDir.mkdirs(); 
		
		//make a copy of the category file but doesnt overwrite if one already exists
		String cmd = "cp -n categories .saved/savedCats";
		
		ProcessBuilder builder = new ProcessBuilder("bash", "-c", cmd);
		Process p;
		try {
			p = builder.start();
			p.waitFor();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//initialize allcat
		AllCat.getObject();

		//initialising current winnings object and file
		CurrentWinnings.getObj();

		//Initialize CatBank object
		//CatBank.getObject();
		//File savedCW = new File(".saved/savedCW");

	}

	/*
	 * clears the catbank and current winnings and re-initialise all the saved files
	 */
	public static void ResetGame() { 
		//deletes saved category file so new one can be generated
		File savedCats = new File(".saved/savedCats");
		savedCats.delete();
		
		CatBank.reset();
		CurrentWinnings.clearCW();
		CatSelectionScreen.reset();
		
		
		HelperMethods.Initialisation();

	}

	/*
	 * Produce .saved/savedCatBank with the desired line marked with "!!"
	 */
	public static void markQuestion(String clueLine) {

		File f = new File(".saved/savedCatBank");
		StringBuffer content = new StringBuffer();
		String line;

		//Save all lines that arent the inputted clue line and save them into string buffer 
		try {
			Scanner sc = new Scanner(f);
			while (sc.hasNextLine()) {
				line=sc.nextLine();

				//append line to buffer only if its not a match
				if(line.equals(clueLine)) {
					line = "!!"+line;
				}
				content.append(line + System.lineSeparator());
			}
			sc.close();

			//overwrite the file with what is in the string buffer
			try {
				FileWriter writer = new FileWriter(".saved/savedCatBank");
				writer.append(content);
				writer.flush();
				writer.close();

			}catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
