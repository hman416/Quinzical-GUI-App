package quinzical.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * AllCat Class manages all the categories that can be generated from the categories file and provides methods to use
 * the categories that is generated. AllCat is a singleton class.
 */
public class AllCat {
	private ArrayList<Category> _categories = new ArrayList<>();
	private String _catLocation = ".saved/savedCats";
	private static AllCat _allCat;
	
	private AllCat() {
		loadCategories();
		_allCat = this;
	}
	
	/*
	 * returns singleton allcat object if it is not null and creates one if it is
	 */
	public static AllCat getObject(){
		if (_allCat == null) {
			new AllCat();		
		}
		return _allCat;
	}
	
	/*
	 * load all the categories from the categories text file
	 */
	public void loadCategories(){
		String cmd = "cat " + _catLocation;

		ProcessBuilder builder = new ProcessBuilder("bash", "-c", cmd);
		try {
			Process process = builder.start();
			InputStream stdout = process.getInputStream();
			BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
			String line = null;

			//read lines (questions) in category file
			while ((line=stdoutBuffered.readLine()) != null) {
				line = line.trim();
				
				//make sure line is not an empty line and line does not contain and answer
				if (!line.contains("(") && (line.trim().length()>0)) {
					_categories.add(new Category(line, _catLocation));
				}	
			}
			stdout.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * return a Category Object at the index specified
	 */
	public Category getCat(int index) {
		return _categories.get(index);
	}
	
	/*
	 * return the total number of categories that exists
	 */
	public int numOfCats() {
		return _categories.size();
	}
}
