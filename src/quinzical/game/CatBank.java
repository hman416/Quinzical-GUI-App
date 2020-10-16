package quinzical.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * CatBank singleton class that manages the categories and questions that will be presented to the user in the game 
 * screen. There will only be 5 categories, each with 5 questions.
 */
public class CatBank {
	private ArrayList<Category> _bank = new ArrayList<Category>(); 
	private static CatBank _catBankObject;

	/*
	 * initialises cat bank object and generates 5 random category objects
	 */
	private CatBank() {
		File bankFile = new File(".saved/savedCatBank");


		//for each line without brackets make it a category
		String line;
		Scanner sc;
		try {
			sc = new Scanner(bankFile);
			while(sc.hasNextLine()) {
				line = sc.nextLine();
				if (!line.contains("(")) {

					Category disCat = new Category(line, ".saved/savedCatBank");
					_bank.add(disCat);

				}
			}
			sc.close();	

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		//set values to clues after _bank has been generated
		for (Category category : _bank) {
			for (int i=0; i<5;i++) {
				category.clueAt(i).setValue("" + (i+1)*100);
			}
		}

		//setting static CatBank object
		_catBankObject = this;
	}

	public static CatBank getObject() {
		if (_catBankObject == null) {
			new CatBank();		
		}
		return _catBankObject;
	}

	/*
	 * writes save file for catBank
	 */
	private void writeToSave() {
		StringBuffer content = new StringBuffer();

		for (Category cat : _bank) {
			content.append(cat.toString() + System.lineSeparator());
			for (int i = 0; i<5; i++) {

				//value of questions is not saved as it is indicated by the question order
				//processing of original string will be done in Clue.java
				content.append(cat.clueAt(i).toString() + System.lineSeparator());
			}
		}

		try {
			FileWriter writer = new FileWriter(".saved/savedCatBank");
			writer.append(content);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/* 
	 *return Category object at specified index 
	 */
	public Category getCat(int i) {
		return _bank.get(i);
	}

	/*
	 * destroys current catbank object and generates a new one
	 */
	public static void reset() {
		_catBankObject = null;

		//delete save file
		File savedF = new File(".saved/savedCatBank");
		savedF.delete();

		//reinitialise object
		//getObject();
	}

}
