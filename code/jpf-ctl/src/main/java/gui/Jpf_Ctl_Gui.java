package gui;

import java.io.File;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class Jpf_Ctl_Gui extends Application {

	private VBox root;
	private File file;

	@Override
	public void start(Stage primaryStage) {
		try {
			root = new VBox(15);
			root.setAlignment(Pos.TOP_CENTER);
			root.setPadding(new Insets(10,25,10,25));
			Scene scene = new Scene(root,600,400);

			//label title
			Label lbl_title = new Label("Jpf-Ctl Java Model Checker");
			lbl_title.setPrefWidth(scene.getWidth());
			lbl_title.setAlignment(Pos.CENTER);
			lbl_title.setStyle("-fx-font-family: Clear Sans; -fx-fill: #eee4da;-fx-font-size: 24px; -fx-font-weight: bold;");

			//middle section
			Label lbl_formula = new Label("Enter Formula");
			lbl_formula.setPrefWidth(scene.getWidth()/2 );
			lbl_formula.setStyle("-fx-font-family: Clear Sans; -fx-fill: #eee4da;-fx-font-size: 16px; -fx-font-weight: bold;");

			TextArea txt_formula = new TextArea();
			txt_formula.setPrefSize(scene.getWidth()/2 , 50);

			VBox v_formula = new VBox(10);
			v_formula.setAlignment(Pos.CENTER);
			v_formula.getChildren().addAll(lbl_formula, txt_formula);

			Button btn_chooseFile = new Button("Choose File");
			btn_chooseFile.setPrefWidth(scene.getWidth()/2);
			btn_chooseFile.setStyle("-fx-font-family: Clear Sans; -fx-fill: #eee4da;-fx-font-size: 16px; -fx-font-weight: bold;");

			CheckBox chk_random = new CheckBox("Consider Randomness");
			chk_random.setPrefWidth(scene.getWidth()/2 );
			chk_random.setAlignment(Pos.CENTER);
			chk_random.setStyle("-fx-font-family: Clear Sans; -fx-fill: #eee4da;-fx-font-size: 16px; -fx-font-weight: bold;");

			VBox v_buttons = new VBox(10);
			v_buttons.setAlignment(Pos.CENTER);
			v_buttons.getChildren().addAll(btn_chooseFile, chk_random);

			HBox h_mid= new HBox(20);
			h_mid.setAlignment(Pos.CENTER);
			h_mid.getChildren().addAll(v_formula, v_buttons);

			// file label
			Label lbl_file = new Label("No File Chosen");
			lbl_file.setPrefWidth(scene.getWidth());
			lbl_file.setStyle("-fx-font-family: Clear Sans; -fx-fill: #eee4da;-fx-font-size: 12px; -fx-font-weight: bold;");

			//command line inputs
			Label lbl_cmd = new Label("Command Line Input (Optional)");
			lbl_cmd.setStyle("-fx-font-family: Clear Sans; -fx-fill: #eee4da;-fx-font-size: 16px; -fx-font-weight: bold;");
			lbl_cmd.setPrefWidth(scene.getWidth());
			
			TextArea txt_cmd = new TextArea();
			txt_cmd.setPrefSize(scene.getWidth() , 50);

			//run button 
			Button btn_run = new Button("Run");
			btn_run.setPrefWidth(scene.getWidth()/4);
			btn_run.setStyle("-fx-font-family: Clear Sans; -fx-fill: #eee4da;-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color:green");
			HBox h_run = new HBox();
			h_run.setAlignment(Pos.CENTER_RIGHT);
			h_run.getChildren().add(btn_run);

			//adding components to the root
			root.getChildren().addAll(lbl_title, h_mid, lbl_file, lbl_cmd, txt_cmd, h_run);

			//choose file button functionality
			btn_chooseFile.setOnAction(e->{
				FileChooser fc = new FileChooser();
				fc.getExtensionFilters().add(new ExtensionFilter("Class Files", "*.class"));
				
				file = fc.showOpenDialog(primaryStage);
				
				if(file != null)
					lbl_file.setText("File: "+file.getPath());
				else
					lbl_file.setText("No File Chosen");
			});
			
			//run button functionality
			btn_run.setOnAction(e->{
				
				//get all the info needed
				String formula = txt_formula.getText().trim();
				String cmd = txt_cmd.getText().trim();
				Boolean rand = chk_random.isSelected();
				
				//error msg
				Alert error = new Alert(AlertType.ERROR);
				error.setHeaderText(null);
				error.setTitle("Error!");
				
				
				//check if a file is chosen and there is a formula
				if (file == null)
				{
					//error msg
					error.setContentText("Please Choose a File");
					error.showAndWait();

				} 
				else if (txt_formula.getText().trim().isEmpty())
				{
					//error msg
					error.setContentText("Please Enter a Formula");
					error.showAndWait();
				}
				else
				{
					// check the other fields and call the model checking

//					error.setAlertType(AlertType.INFORMATION);
//					error.setTitle("Info");
//					error.setContentText("Running With the Settings:\nFormula: "+formula+"\nConsider Randomness: "+ rand
//							+"\nCommand Line Input:"+ cmd);
//					error.showAndWait();
				}
				
			});
			

			primaryStage.setScene(scene);
			primaryStage.getIcons().add(new Image("file:src/main/java/gui/images/jpf-ctl-logo.png"));
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}