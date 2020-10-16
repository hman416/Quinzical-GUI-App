package quinzical.screens;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import quinzical.game.Clue;
import quinzical.game.CurrentWinnings;
import quinzical.utilities.ClueSpeaker;
import quinzical.utilities.HelperThread;
import quinzical.utilities.TimerPaper;

/**
 * QuestionModule class encapsulate the features of the practice screen. It provides functionalities such as providing a 
 * pane containing the layout of the screen containing elements such as a textfield for the user to enter an answer, a
 * button for the user to activate text to speech and a slider to adjust the speed of the text to speed. There are also
 * buttons that help the user input macrons.
 */
public class QuestionModule{

	private BorderPane _quesModLayout; 
	private Stage _appStage;
	private Clue _quesClue;
	private Scene _previousScene;
	private GameModule _preGameMod;
	private PracticeModule _prePracMod;
	private int _pracAttempts;

	private ComboBox<String> _questionStarter;
	private ComboBox<String> _beVerbs;
	private ComboBox<String> _articles;

	private TextField _txtField;
	private Text _countDown = new Text("Countdown: 15");
	private int _count;
	private Timer _t;

	private QuestionModule _qm = this; //perhaps make this singleton

	private ClueSpeaker _cs;

	/*
	 * Constructs a new QuestionModule object
	 * Sets values for the Clue, Pre Scene and ClueSpeaker fields
	 */
	public QuestionModule(Clue clue, Scene preScene) {
		_quesClue = clue;
		_previousScene = preScene;
		_cs = ClueSpeaker.getCSObject();
	}

	/*
	 * Adds a reference to the Game Module that called the constructor
	 */
	public void addPreMod(GameModule gm) {
		_preGameMod = gm;
	}

	/*
	 * Adds a reference to the Practice Module that called the constructor
	 */
	public void addPreMod(PracticeModule pm) {
		_prePracMod = pm;
	}

	/*
	 * Adds a reference to the primary app stage so that scene transitions can be set
	 */
	public void addStage(Stage primaryStage) {
		_appStage = primaryStage;
	}

	/*
	 * Creates the layout for a question from a game module
	 */
	public Pane createLayoutGame() {
		_quesModLayout = new BorderPane();
		_quesModLayout.setPadding(new Insets(20));

		HBox sliderLayout = new HBox(5);
		Text sText = new Text("Speaker speed:\nDefault: 1");
		sliderLayout.getChildren().addAll(sText, _cs.getSlider());
		_quesModLayout.setBottom(sliderLayout);

		Button sayClueBtn = getClueButton(_quesClue.getClue());

		Button enterBtn = new Button("Submit");
		HBox macronList = setUpMacrons();

		HBox userInput = setUpUserInput();

		VBox stack = new VBox(8);
		stack.getChildren().addAll(sayClueBtn, userInput, macronList, enterBtn, _countDown);
		stack.setAlignment(Pos.CENTER);


		VBox.setMargin(userInput, new Insets(20, 20, 20, 20));
		VBox.setMargin(enterBtn, new Insets(20, 20, 20, 20));

		enterBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				//stops countdowntimer
				if (_t != null) {
					endCountDown();
				}

				String theirAns = _questionStarter.getValue() + _beVerbs.getValue() + _articles.getValue()
				+ _txtField.getText().trim();
				System.out.println(theirAns);

				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Result");
				if ((_quesClue.checkInput(theirAns))) {
					alert.setHeaderText("Correct!! You won $"+_quesClue.getValue());
					//increase current winnings field by twice the value to make up for initial deduction
					CurrentWinnings.getObj().increaseWinning(2*Integer.parseInt(_quesClue.getValue()));
					alert.showAndWait();
				} else {
					alert.setHeaderText("Sorry! Wrong answer \nThe answer is: " + _quesClue.getMainAns());
					//No deduction of current winnings as it has already been done

					alert.showAndWait();
				}
				alert.close();
				_previousScene = _preGameMod.createScene();
				_appStage.setScene(_previousScene);
			}
		});


		_quesModLayout.setCenter(stack);

		return _quesModLayout;
	}

	/*
	 * Creates the layout for a question from a practice module
	 */
	public Pane createLayoutPractice() {
		_quesModLayout = new BorderPane();
		_quesModLayout.setPadding(new Insets(20));

		HBox sliderLayout = new HBox(5);
		Text sText = new Text("Speaker speed:\nDefault: 1");
		sliderLayout.getChildren().addAll(sText, _cs.getSlider());
		_quesModLayout.setBottom(sliderLayout);

		Text _hint = new Text("Hint: " + _quesClue.getMainAns().charAt(0));
		_hint.setVisible(false);

		Button sayClueBtn = getClueButton(_quesClue.getClue());

		Text title = new Text(_quesClue.getClue());
		title.setFont(Font.font("arial",FontPosture.ITALIC,14));

		Button enterBtn = new Button("Submit");
		HBox macronList = setUpMacrons();
		HBox userInput = setUpUserInput();


		VBox stack = new VBox(8);
		stack.getChildren().addAll(sayClueBtn, title, userInput, macronList, enterBtn, _hint);
		stack.setAlignment(Pos.CENTER);

		VBox.setMargin(title, new Insets(20, 20, 20, 20));
		VBox.setMargin(_txtField, new Insets(20, 20, 20, 20));
		VBox.setMargin(enterBtn, new Insets(20, 20, 20, 20));


		_pracAttempts = 3;

		enterBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				_t.cancel();
				_t.purge();

				if (_pracAttempts > 0) {
					String theirAns = _questionStarter.getValue() + _beVerbs.getValue() + _articles.getValue()
					+ _txtField.getText().trim();
					System.out.println(theirAns);

					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Result");

					if ((_quesClue.checkInput(theirAns))) {
						alert.setHeaderText("Correct!!");
						alert.showAndWait();
						alert.close();
						_appStage.setScene(_previousScene);

					} else if (_pracAttempts == 1) {
						alert.setHeaderText("Sorry! Wrong answer."
								+ "\nYou have run out of attempts. Better luck next time!");
						alert.setContentText("Answer: " +_quesClue.getMainAns());
						alert.showAndWait();
						alert.close();
						_previousScene = _prePracMod.createScene();
						_appStage.setScene(_previousScene);

					}else {
						_pracAttempts--;
						alert.setHeaderText("Sorry! Wrong answer."
								+ "\nYou have "+_pracAttempts+" attempt(s) left");
						alert.showAndWait();
						alert.close();

						//on the final attempt the player is given a hint
						if (_pracAttempts == 1) {
							_hint.setVisible(true);
						}
					}
				}
			}
		});

		_quesModLayout.setCenter(stack);

		return _quesModLayout;
	}

	/*
	 * Helper method used to set up macrons for maori characters
	 */
	private HBox setUpMacrons(){

		//helper buttons for maori macrons ā, ē, ī, ō, ū 
		Button aMacron = new Button("ā");
		aMacron.setMaxWidth(50);
		aMacron.setMinWidth(50);

		Button eMacron = new Button("ē");
		eMacron.setMaxWidth(50);
		eMacron.setMinWidth(50);

		Button iMacron = new Button("ī");
		iMacron.setMaxWidth(50);
		iMacron.setMinWidth(50);

		Button oMacron = new Button("ō");
		oMacron.setMaxWidth(50);
		oMacron.setMinWidth(50);

		Button uMacron = new Button("ū");
		uMacron.setMaxWidth(50);
		uMacron.setMinWidth(50);

		HBox macronList = new HBox(40);
		macronList.getChildren().addAll(aMacron, eMacron, iMacron, oMacron, uMacron);
		macronList.setAlignment(Pos.CENTER);

		aMacron.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				_txtField.appendText("ā");

			}
		});

		eMacron.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				_txtField.appendText("ē");

			}
		});

		iMacron.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				_txtField.appendText("ī");

			}
		});

		oMacron.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				_txtField.appendText("ō");

			}
		});

		uMacron.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				_txtField.appendText("ū");

			}
		});

		return macronList;
	}

	private Button getClueButton(String line) {
		Button sayClueBtn = new Button("Listen to Clue!");

		sayClueBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				sayClueBtn.setDisable(true);
				//create new thread to run text to speech
				HelperThread thread = new HelperThread(_cs, line, sayClueBtn, _qm); 
				thread.start();

			}

		});
		return sayClueBtn;
	}

	/*
	 * Helper method to set up a text field
	 */
	private HBox setUpUserInput() {
		Text qMark = new Text("?");

		_txtField = new TextField();
		_txtField.setAlignment(Pos.CENTER);
		_txtField.setPrefWidth(250);

		ObservableList<String> qStarter = FXCollections.observableArrayList(
				"What ", "Where ", "When ", "Who ", "How ");

		_questionStarter = new ComboBox<>(qStarter);
		_questionStarter.setValue(qStarter.get(0));


		ObservableList<String> beVerbs = FXCollections.observableArrayList(
				"is ", "are ", "was ", "were ");

		_beVerbs = new ComboBox<>(beVerbs);
		_beVerbs.setValue(beVerbs.get(0));

		ObservableList<String> articles = FXCollections.observableArrayList(
				"", "a ", "an ", "the ");

		_articles = new ComboBox<>(articles);
		_articles.setValue(articles.get(0));

		HBox userInput = new HBox(10);
		userInput.getChildren().addAll(_questionStarter, _beVerbs, _articles, _txtField, qMark);
		userInput.setAlignment(Pos.CENTER);

		return userInput;
	}

	public void countDownTimer() {

		//timer is only assigned once hence countdown only runs once
		if(_t == null) {
			_count = 15;

			_t = new Timer("timerThread");
			long interval = 1000; // 1 sec interval
			long period = 1000; //15 seconds

			_t.schedule(new TimerTask() {

				@Override
				public void run() {
					_count--;
					_countDown.setText("Countdown: " + _count);
					System.out.println(_count);
					//_count--;
					if (_count == 5) {
						_countDown.setFill(Color.RED);
					}

					if (_count <= 0) {
						Platform.runLater(new TimerPaper(_qm, _preGameMod));

						endCountDown();

					};

				}

			}, interval, period);


		} 

	}

	public void leave(QModule module) {

		System.out.println("exiting screen");
		Alert a = new Alert(AlertType.NONE, "Oh no! you've run out of time to answer! \nThe answer was ", ButtonType.CLOSE);
		a.showAndWait();
		_previousScene = _preGameMod.createScene();
		_appStage.setScene(_previousScene);
	}

	private void endCountDown() {
		_t.cancel();
		_t.purge();
	}

}