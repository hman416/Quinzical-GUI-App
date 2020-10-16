package quinzical.main;


import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import quinzical.game.AllCat;
import quinzical.game.CatBank;
import quinzical.game.CurrentWinnings;
import quinzical.screens.CatSelectionScreen;
import quinzical.screens.GameModule;
import quinzical.screens.PracticeModule;
import quinzical.utilities.HelperMethods;

/**
 * Menu Class extends Application and produces the startup screen of quinzical. Menu Class also contains the main method
 */

public class Menu extends Application {
	private Button gameButton;
	private Button practiceButton;
	private Button resetButton;
	private Button exitButton;
	private Button backButton1;
	private Button backButton2;
	private Button backButton3;


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		// saved files initialisation process 
		HelperMethods.Initialisation();

		CurrentWinnings cw = CurrentWinnings.getObj();

		//initializes object states
		AllCat ac = AllCat.getObject();
		//CatBank cb = CatBank.getObject();

		primaryStage.setTitle("QUINZICAL");

		//create first line of title text
		Text welcomeText = new Text("\nWelcome to");
		welcomeText.setTextAlignment(TextAlignment.CENTER);//might not be needed
		welcomeText.setFont(Font.font("arial", FontWeight.BOLD,FontPosture.ITALIC,20));
		welcomeText.setFill(Color.WHITE);

		//create second line of title text
		Text quinzicalText = new Text();
		quinzicalText.setText("QUINZICAL");
		quinzicalText.setTextAlignment(TextAlignment.CENTER);//might not be needed
		quinzicalText.setFont(Font.font("arial", FontWeight.BOLD,FontPosture.ITALIC,50));
		quinzicalText.setFill(Color.GREEN);

		//container for title text lines
		VBox textPane = new VBox();

		StackPane centeredWelcome = new StackPane();
		centeredWelcome.getChildren().add(welcomeText);
		textPane.getChildren().add(centeredWelcome);

		StackPane centeredQuinzical = new StackPane();
		centeredQuinzical.getChildren().add(quinzicalText);
		textPane.getChildren().add(centeredQuinzical);

		//initialise and setup buttons
		gameButton = new Button("Play Game");
		gameButton.setMaxSize(200, 30);
		gameButton.setMinSize(200, 30);

		practiceButton = new Button("Practice Mode");
		practiceButton.setMaxSize(200, 30);
		practiceButton.setMinSize(200, 30);

		resetButton = new Button("Reset Game");
		resetButton.setMaxSize(200, 30);
		resetButton.setMinSize(200, 30);

		exitButton = new Button("Exit");
		exitButton.setMaxSize(200, 30);
		exitButton.setMinSize(200, 30);

		backButton1 = new Button("back");
		backButton1.setMaxSize(80, 40);
		backButton1.setMinSize(80, 40);

		backButton2 = new Button("back");
		backButton2.setMaxSize(80, 40);
		backButton2.setMinSize(80, 40);

		backButton3 = new Button("back");
		backButton3.setPrefSize(100,60);

		Text cWDisplay = new Text();
		cw.addListener(cWDisplay);

		//layout of all buttons in the menu
		VBox menuLayout = new VBox(10);
		menuLayout.setStyle("-fx-background-color: #4DC4FF");

		menuLayout.getChildren().add(gameButton);
		menuLayout.getChildren().add(practiceButton);
		menuLayout.getChildren().add(resetButton);
		menuLayout.getChildren().add(exitButton);
		menuLayout.getChildren().add(cWDisplay);

		Button testBtn = new Button("testCatSelect");
		menuLayout.getChildren().add(testBtn);

		/*
		testBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				CatSelectionScreen.getObject().setBack(backButton3);
				primaryStage.setScene(CatSelectionScreen.getObject().createLayout());
			}

		});*/


		menuLayout.setAlignment(Pos.CENTER);

		//layout of this scene
		BorderPane sceneLayout = new BorderPane();
		sceneLayout.setTop(textPane);
		sceneLayout.setCenter(menuLayout);
		sceneLayout.setStyle("-fx-background-color: #4DC4FF");


		Scene menu = new Scene(sceneLayout, 450, 350);
		primaryStage.setScene(menu);

		//creating GameModule Object 
		GameModule gm = GameModule.getObject();
		gm.setBackBtn(backButton1);
		gm.addStage(primaryStage);

		//creating PracticeModule Object
		PracticeModule pm = new PracticeModule();
		pm.setBackBtn(backButton2); //provide reference to back button
		pm.addStage(primaryStage);

		//creating Practice scene
		Scene pmScene = pm.createScene();

		//setting button functionalities
		gameButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				File cBFile = new File(".saved/savedCatBank");
				try {
					if (cBFile.createNewFile()) {
						CatSelectionScreen.getObject().setBack(backButton3);
						primaryStage.setScene(CatSelectionScreen.getObject().createLayout());
						cBFile.delete();
					}else {
						Scene gmScene = gm.createScene();
						primaryStage.setScene(gmScene);		
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//Scene gmScene = gm.createScene();
				//primaryStage.setScene(gmScene);			
			}
		});

		practiceButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				//create Practice scene
				Scene pmScene = pm.createScene();
				primaryStage.setScene(pmScene);
			}
		});

		resetButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to reset the game?", ButtonType.YES, ButtonType.NO);
				alert.setHeaderText("Reset Game?");
				alert.setTitle("Reset Game");
				alert.showAndWait();

				if(alert.getResult() == ButtonType.YES) {
					HelperMethods.ResetGame();
				}else {
					alert.close();
				}
			}

		});

		exitButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to leave", ButtonType.YES, ButtonType.NO);
				alert.setHeaderText("Exit?");
				alert.setTitle("Exit");
				alert.showAndWait();

				if(alert.getResult() == ButtonType.YES) {
					primaryStage.close();
				}else {
					alert.close();
				}
			}

		});

		backButton1.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent arg0) {
				primaryStage.setScene(menu);				
			}

		});

		backButton2.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent arg0) {
				primaryStage.setScene(menu);				
			}

		});

		backButton3.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent arg0) {				
				CatSelectionScreen.reset();
				primaryStage.setScene(menu);				
			}

		});

		primaryStage.show();

	}

}
