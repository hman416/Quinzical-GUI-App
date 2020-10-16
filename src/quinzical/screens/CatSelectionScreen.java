package quinzical.screens;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import quinzical.game.AllCat;
import quinzical.game.Category;
public class CatSelectionScreen {

	private AllCat _allCat;
	private ArrayList<Category> _selectedCat;
	private static CatSelectionScreen _cSSObject;
	private Button _bkBtn;
	private Button _confirmBtn;

	private CatSelectionScreen(AllCat ac) {
		_allCat = ac;
		_cSSObject = this;
		_selectedCat = new ArrayList<>();
	}

	public static CatSelectionScreen getObject() {
		if (_cSSObject == null) {
			_cSSObject = new CatSelectionScreen(AllCat.getObject());
		}
		return _cSSObject;
	}
	/**
	 * Setting back button to return to main menu
	 * @param bkBtn
	 */
	public void setBack(Button bkBtn) {
		_bkBtn = bkBtn;
	}
	/**
	 * Produce scene for category selection
	 * @return 
	 */
	public Scene createLayout() {
		BorderPane overallLayout = new BorderPane();
		overallLayout.setPadding(new Insets(20));

		BorderPane bottomLayout = new BorderPane();

		Text catCount = new Text("Categories left to select: 5");

		_confirmBtn = new Button("Confirm");
		_confirmBtn.setPrefSize(100, 50);
		_confirmBtn.setVisible(false);

		bottomLayout.setLeft(_bkBtn);
		bottomLayout.setCenter(catCount);
		bottomLayout.setRight(_confirmBtn);

		_confirmBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				File cBFile = new File(".saved/savedCatBank");
				try {
					cBFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//create clone and put into list
				ArrayList<Category> temp = new ArrayList<>();
				for (Category cat: _selectedCat) {
					//cloning category
					temp.add(new Category(cat));
				}
				_selectedCat = temp;
				printSelected();
				System.out.println("-----------------------------------------------------------------------------------------------");
				skimCategories();
				printSelected();

				//write to .saved/savedCatBank
				writeCatBank();
				
				goTo(GameModule.getObject().createScene());

			}

		});



		Text titleText = new Text("Please Select 5 categories");
		titleText.setFont(Font.font("arial", FontWeight.BOLD,FontPosture.ITALIC,50));
		StackPane title = new StackPane();
		title.getChildren().add(titleText);
		title.setAlignment(Pos.CENTER);

		overallLayout.setTop(title);
		overallLayout.setBottom(bottomLayout);

		//generate buttons 
		FlowPane catButtons = new FlowPane();
		catButtons.setPadding(new Insets(50));
		catButtons.setPrefWrapLength(550);
		catButtons.setAlignment(Pos.CENTER);

		//overallLayout.setCenter(centerLayout);
		overallLayout.setCenter(catButtons);
		catButtons.setVgap(10);
		catButtons.setHgap(10);

		for (int i = 0; i<_allCat.numOfCats(); i++) {
			Category currentCat = _allCat.getCat(i);
			Button catBtn = new Button(currentCat.toString());
			catBtn.textAlignmentProperty().set(TextAlignment.CENTER);

			catBtn.setPrefSize(120, 120);
			catBtn.setWrapText(true);
			catButtons.getChildren().add(catBtn);
			catBtn.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent arg0) {
					System.out.println("\nclick!");


					if (catBtn.getStyle().equals("") && _selectedCat.size() < 5) {
						catBtn.setStyle("-fx-background-color: #0040FF");
						_selectedCat.add(currentCat);
						catCount.setText("Categories left to select: " + (5-_selectedCat.size()) );

					}else {
						catBtn.setStyle("");
						_selectedCat.remove(currentCat);
						catCount.setText("Categories left to select: " + (5-_selectedCat.size()) );

					}

					if (_selectedCat.size()<5) {
						_confirmBtn.setVisible(false);
					}else {
						_confirmBtn.setVisible(true);
					}
				}

			});
		}
		return new Scene(overallLayout, 800, 600);
	}

	private void printSelected() {
		
		for (int j=0; j<_selectedCat.size(); j++) {
			System.out.println(_selectedCat.get(j).toString() + ", ");
			Category cat = _selectedCat.get(j);
			
			for(int i=0; i<cat.getNumOfQ();i++) {
				System.out.println(cat.clueAt(i));
			}
		}
	}
	/**
	 * Randomly reduce number of clues in each category to 5
	 */
	private void skimCategories() {
		Random rand = new Random();
		
		for(Category catClone: _selectedCat) {
			while (catClone.getNumOfQ()>5) {

				//randomly remove clue from from category 
				catClone.removeClue(rand.nextInt(catClone.getNumOfQ()));
			}
		}
	}
	
	/**
	 * Method that writes to the save file for catBank
	 */
	public void writeCatBank() {
		StringBuffer content = new StringBuffer();

		for (Category cat : _selectedCat) {
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
	
	/**
	 * Method for changing scenes
	 * @param scene: scene which the stage is to display
	 */
	private void goTo(Scene scene) {
		Scene currentScene = _confirmBtn.getScene();
		Stage stage = (Stage) currentScene.getWindow();
		stage.setScene(scene);
	}
	
	/**
	 * new object is stored
	 */
	public static void reset() {
		_cSSObject = null;
	}


}
