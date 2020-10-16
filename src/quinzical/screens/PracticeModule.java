package quinzical.screens;

import java.util.Random;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import quinzical.game.AllCat;
import quinzical.game.Category;
import quinzical.game.Clue;

/**
 * PracticeModule class encapsulate the features of the practice screen. It provides functionalities such as providing a 
 * pane containing the layout of the screen with various elements such as a heading, a layout of buttons with category names
 * attached, and a back button to return to main screen.
 */

public class PracticeModule implements QModule{

	private BorderPane _pracModLayout; 
	private AllCat _allCatObj;
	private Button _bkBtn;
	private Stage _appStage;
	private Scene _thisScene;
	private PracticeModule _this = this;

	/*
	 * Constructor that sets/gets the AllCat field
	 */
	public PracticeModule() {
		_allCatObj = AllCat.getObject();
	}
	
	/*
	 * Adds a reference to the main app stage to allow for scene transitions
	 */
	public void addStage(Stage primaryStage) {
		_appStage = primaryStage;
	}

	/*
	 * Adds a reference to button that returns to the main menu scene
	 */
	public void setBackBtn(Button btn) {
		_bkBtn = btn;
	}
	
	/*
	 * Creates/updates layout for the practice module scene
	 */
	public Scene createScene() {
		_pracModLayout = new BorderPane();
		_thisScene = new Scene(_pracModLayout, 800, 600);
		
		_pracModLayout.setPadding(new Insets(20));

		//creating different layout panes
		GridPane buttonGrid = new GridPane();
		StackPane titlePane = new StackPane();
		BorderPane bottomBox = new BorderPane();


		//set title
		Text title = new Text("PRACTICE MODE");
		title.setFont(Font.font("arial", FontWeight.BOLD,FontPosture.ITALIC,50));
		title.setFill(Color.DARKGREEN);

		titlePane.getChildren().add(title);


		//set button layouts
		buttonGrid.setAlignment(Pos.CENTER);
		buttonGrid.setHgap(10);
		buttonGrid.setVgap(10);

		int counter = 0;
		
		//creating 3x3 grid for the 9 categories in the text file
		for(int i=0; i<3; i++) {
			for(int j=0; j<3;j++) {

				Category currentCat = _allCatObj.getCat(counter);
				Button btn = new Button(currentCat.toString());
				btn.setMinSize(120, 120);
				btn.setMaxSize(120, 120);

				Random rand = new Random();
				
				btn.setOnAction(new EventHandler<ActionEvent>() {
					
					@Override
					public void handle(ActionEvent event) {
						//generating a random clue from current category
						Clue randClue = currentCat.clueAt(rand.nextInt(currentCat.getNumOfQ()));
						
						QuestionModule ques = new QuestionModule(randClue, _thisScene);
						ques.addPreMod(_this);
						ques.addStage(_appStage);
						
						//changing scene
						Scene qmScene = new Scene(ques.createLayoutPractice(), 800, 600);
						qmScene.setRoot(ques.createLayoutPractice());
						_appStage.setScene(qmScene);
					}
				});
				
				//add button to grid given coordinates
				buttonGrid.add(btn, i, j);
				counter++;
			}
		}


		//add to outter layout of scene
		_pracModLayout.setTop(titlePane);
		_pracModLayout.setCenter(buttonGrid);
						
		bottomBox.setLeft(_bkBtn);
				
		
		
		_pracModLayout.setBottom(bottomBox);
		

		return _thisScene;
	}


}
