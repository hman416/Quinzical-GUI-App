package quinzical.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Category Class represents a single category in the category text file and manages a list of the associated clues
 * in said category.
 */
public class Category {
	private List<Clue> _clues;
	private String _category;

	public Category(String line, String path) {
		_category = line;
		_clues = new ArrayList<Clue>();
		loadQuestions(_category, path);
	}
	
	
	/*
	 * Produces a separate clone of Category object given another Category Object
	 */
	public Category(Category cat) {
		_category = cat.toString();
		_clues = new ArrayList<Clue>();
		for (int i=0; i<cat.getNumOfQ();i++) {
			this._clues.add(new Clue(cat.clueAt(i)));
		}
	}
	

	/*
	 * load questions from saved file into list
	 */
	public void loadQuestions(String category, String path){
		_clues.clear();

		try {
			//use bash to access lines in category file
			String cmd = "cat " + path;
			
			ProcessBuilder builder = new ProcessBuilder("bash", "-c", cmd);
			Process process = builder.start();
			InputStream stdout = process.getInputStream();
			BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
			String line = null;
			
			//read lines (questions) in category file
			while ((line=stdoutBuffered.readLine()) != null) {
				line = line.trim();
				if (line.equals(_category)) {
					
					while((line = stdoutBuffered.readLine())!=null && line.contains("(")) {		
						addClue(new Clue(line)); //add category to question board list
					}
				}	
			}
			stdout.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void addClue(Clue line) { 
		_clues.add(line);
	}

	/*
	 * return number of questions
	 */
	public int getNumOfQ() {
		return _clues.size();
	}

	/*
	 * return clue at particular index
	 */
	public Clue clueAt(int index) {
		return _clues.get(index);
	}
	
	/*
	 * remove clue from list
	 */
	public void removeClue(int index) {
		_clues.remove(index);
	}
	
	/*
	 * get the name of this category
	 */
	public String toString() {
		return _category;
	}

}
