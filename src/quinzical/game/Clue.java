package quinzical.game;

import java.util.ArrayList;

import quinzical.utilities.HelperMethods;

/**
 * Clue class which represents a single clue in the categories text file. Each Clue will also keep track of the different
 *aspects a clue has (monetary value, the clue itself, acceptable answers).
 */

public class Clue {
	private String _value;
	private String _question; 
	private String _clue;
	private String _originalLine;
	private boolean _attempted;
	private ArrayList<String> _acceptableInputs;
	
	private String _mainAns;



	/*
	 * Creates a separate clone of clue object provided a clue object
	 */
	public Clue(Clue clue) {
		_originalLine = clue.toString();
		_value = clue.getValue();
		_question = clue.getQuestion();
		_clue = clue.getClue();
		_attempted = clue.isAttempted();

		_acceptableInputs = new ArrayList<String>();
		processQuestion(_question);
		
	}

	/*
	 * Create clue object from a string which is a line from the categories text file
	 */
	public Clue(String line) {
		//check if first two characters are "!!" 		
		//if yes then question has been marked to be attempted
		if (line.substring(0,2).equals("!!")) {
			_attempted = true;
			_clue = line.substring(2, line.lastIndexOf(".")).trim();
			
		}else {//if no then question is not attempted
			_attempted = false;
			_clue = line.substring(0, line.lastIndexOf(".")).trim();
			
		}
		
		_originalLine = line;
		
		_question = line.substring(line.indexOf("("), line.length()).trim();

		//load list of acceptable questions
		_acceptableInputs = new ArrayList<String>();
		processQuestion(_question);


	}

	/*
	 * Helper method that processes the "question/answer" part of the clue
	 */
	private void processQuestion(String question) {

		String subject;
		String prefix;
		String fullQuestion;

		if (question.contains("(")) {
			//locates the "what is"/"who is"/etc
			prefix = question.substring(question.indexOf("(")+1, question.indexOf(")")).trim();
			//locate the "subject" of the acceptable inputs
			subject = question.substring(question.indexOf(")")+1, question.length()).trim();

			//while there are still other options...
			while (subject.contains("/")) {
					
				fullQuestion = prefix + " " + subject.substring(0, subject.indexOf("/"));
				
				//adding answers in question form and answer form
				_acceptableInputs.add(subject.substring(0, subject.indexOf("/")));
				_acceptableInputs.add(fullQuestion);
				
				int begin =subject.indexOf("/");
				int end = subject.length();
				
				subject = subject.substring(begin + 1 , end).trim();
				
			}

			//adding when there is only one suffix left
			
			fullQuestion = prefix + " " + subject;
			_acceptableInputs.add(subject);
			_acceptableInputs.add(fullQuestion);
			
		}else {
			_acceptableInputs.add(question);

		}
		//main answer used for giving hints and giving answer
		_mainAns = _acceptableInputs.get(0);
	}

	/*
	 * check if provided answer is an acceptable/correct input
	 */
	public boolean checkInput(String input) {
		_attempted = true;				
		
		//iterate over list and determine whether there the input matches any of the strings inside the list
		for (String answer : _acceptableInputs) {
			if (input.equalsIgnoreCase(answer)) {
				return true;
			}
		}
		
		return false;
	}
	
	/*
	 * Set this clue as attempted
	 */
	public void setAttempted() {
		//set field in Clue
		_attempted = true;
		
		//update save file
		HelperMethods.markQuestion(_originalLine);
	}
	
	/*
	 * Check if clue is attempted
	 */
	public boolean isAttempted() {
		return _attempted;
	}
	
	/*
	 * set the monetary value of the clue
	 */
	public void setValue(String val) {
		_value = val;
	}

	/*
	 * return the main answer for this clue
	 */
	public String getMainAns() {
		return _mainAns;
	}
	
	/*
	 * return the monetary value of this clue
	 */
	public String getValue() {
		return _value;
	}

	public String getQuestion() {
		return _question;
	}

	public String getClue() {
		return _clue;
	}

	@Override
	public String toString() {
		return _originalLine;
	}
}
