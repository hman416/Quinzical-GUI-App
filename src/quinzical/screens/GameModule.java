package quinzical.screens;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import quinzical.game.CatBank;
import quinzical.game.Category;
import quinzical.game.Clue;
import quinzical.game.CurrentWinnings;
import quinzical.utilities.HelperMethods;

/**
 * GameModule class encapsulate the features of the game screen. It provides functionalities such as providing a pane 
 * containing the layout of the screen with various elements such as a heading a layout of buttons with values attached,
 * a back button, as well a display for the user's current winnings. Once all Questions are answered, a "reward" screen 
 * is displayed instead.
 */

public class GameModule implements QModule{
	private BorderPane _GModLayout; 
	private Scene _GMScene;
	private CatBank _catBankObj;
	private Button _bkBtn;
	private Stage _appStage;
	private CurrentWinnings _cW;
	private GameModule _this = this;
	private int _attemptedQues;
	private static GameModule _gMObject;

	//goes through entire catbank and counts all attempted clues
	private void incrementAttempted() {
		_attemptedQues=0;
		for (int i=0; i<5;i++) {
			for (int j=0; j<5; j++) {
				if (_catBankObj.getCat(i).clueAt(j).isAttempted()) {
					_attemptedQues++;
					
				}
			}
		}
	}
	
	/*
	 * Constructor that sets/gets the winnings field
	 */
	private GameModule() {
		_cW = CurrentWinnings.getObj();
	}
	
	public static GameModule getObject() {
		if (_gMObject == null) {
			_gMObject = new GameModule();
		}
		return _gMObject;
	}

	/*
	 * Adds a reference to the main app stage to allow for scene transitions
	 */
	public void addStage(Stage stage) {
		_appStage = stage;
	}

	/*
	 * Adds a reference to button that returns to the main menu scene
	 */
	public void setBackBtn(Button btn) {
		_bkBtn = btn;
	}

	/*
	 * Creates/updates layout for the game module scene
	 * If all the questions have been attempted, then this will return
	 * a scene for the reward screen before backing into the main menu
	 */
	public Scene createScene() {
		_catBankObj = CatBank.getObject();
		_GModLayout = new BorderPane();
		
		//incrememnt _attemptedQues for all clues attempted in catBank
		incrementAttempted();

		if (_attemptedQues < 25) {
			setUpGameScreen();
		} else {
			setUpRewardScreen();
		}

		return _GMScene;


	}

	/*
	 * Helper method to create the reward scene
	 */
	private void setUpRewardScreen() {

		Text heading = new Text("Congratulations!");
		heading.setFont(Font.font("arial", FontWeight.BOLD,FontPosture.ITALIC,50));
		heading.setFill(Color.RED);

		Text message = new Text("You've attempted all the questions. "+ "Your winnings are " + _cW.displayCWValue());
		message.setFont(Font.font("Arial", FontWeight.MEDIUM, 17));

		StackPane header = new StackPane();
		header.getChildren().add(heading);

		StackPane msgPane = new StackPane();
		msgPane.getChildren().add(message);

		Button replayBtn = new Button("Play Again!");
		replayBtn.setOnAction(_bkBtn.getOnAction());
		replayBtn.setMinSize(120,70);
		replayBtn.setMaxSize(120,70);

		VBox stack = new VBox(20);
		stack.getChildren().addAll(header ,msgPane, replayBtn);
		stack.setAlignment(Pos.CENTER);

		_GModLayout.setCenter(stack);

		_GMScene = new Scene(_GModLayout, 800, 600);

		_attemptedQues = 0;
		HelperMethods.ResetGame();
	}


	/*
	 * Helper method to set up the main game scene
	 */
	private void setUpGameScreen() {

		//creating different layout panes
		HBox column = new HBox();
		column.setSpacing(50);
		column.setAlignment(Pos.BASELINE_CENTER);

		StackPane titlePane = new StackPane();

		//set title
		Text title = new Text("\nGAME MODE");
		title.setFont(Font.font("arial", FontWeight.BOLD,FontPosture.ITALIC,50));
		title.setFill(Color.DARKGREEN);

		titlePane.getChildren().add(title);

		BorderPane bottomBox = new BorderPane();
		Text cWDisplay = new Text("Current Winnings: "+ _cW.displayCWValue());
		_cW.addListener(cWDisplay);
		bottomBox.setLeft(_bkBtn);
		bottomBox.setRight(cWDisplay);

		//add to outer layout of scene
		_GModLayout.setTop(titlePane);
		_GModLayout.setCenter(column);
		_GModLayout.setBottom(bottomBox);
		_GModLayout.setPadding(new Insets(20));

		for(int i=0; i<5; i++) {

			Category currentCat = _catBankObj.getCat(i);
			Text catHeading = new Text(currentCat.toString());
			catHeading.setFont(Font.font("arial", FontWeight.LIGHT,FontPosture.ITALIC,18));

			VBox colBtn = new VBox(20);

			colBtn.getChildren().add(catHeading);

			boolean validBtnNotReached = true;
			for (int j=0; j<currentCat.getNumOfQ(); j++) {

				//create clue
				Clue currentClue = currentCat.clueAt(j);

				//add clue button
				Button clueButton = new Button("$" + currentClue.getValue());
				clueButton.setMaxSize(70,50);
				clueButton.setMinSize(70,50);

				StackPane stack = new StackPane();

				stack.getChildren().add(clueButton);

				//set up valid question button
				if (!currentClue.isAttempted() && validBtnNotReached) {
					validBtnNotReached = false;
					clueButton.setStyle("-fx-background-color: #0040FF");//dark blue
					clueButton.setOnAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {
							//Deduct current first incase user exits after entering question screen
							CurrentWinnings.getObj().decreaseWinning(Integer.parseInt(currentClue.getValue()));
							//mark clue as attempted
							_attemptedQues++;
							currentClue.setAttempted();
							QuestionModule ques = new QuestionModule(currentClue, _GMScene);
							ques.addPreMod(_this);
							ques.addStage(_appStage);
							Scene qmScene = new Scene(ques.createLayoutGame(), 800, 600);
							qmScene.setRoot(ques.createLayoutGame());
							_appStage.setScene(qmScene);

						}
					});
				} 
				//set up already answered question
				else if (currentClue.isAttempted() && validBtnNotReached){
					clueButton.setStyle("-fx-background-color: #848484");//gray
					clueButton.setDisable(true);
				} 
				//set up not yet reached question
				else {
					clueButton.setStyle("-fx-background-color: #2E9AFE");//light blue
					clueButton.setDisable(true);
				}


				colBtn.getChildren().add(stack);

			}
			column.getChildren().add(colBtn);

		}
		_GMScene = new Scene(_GModLayout, 800, 600);

	}

}

