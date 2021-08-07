package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class Jpf_Ctl_Gui extends Application {

	private VBox root;
	private String path;

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
			lbl_file.setStyle("-fx-font-family: Clear Sans; -fx-fill: #eee4da;-fx-font-size: 16px; -fx-font-weight: bold;");

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
			root.getChildren().add(lbl_title);
			root.getChildren().add(h_mid);
			root.getChildren().add(lbl_file);
			root.getChildren().add(lbl_cmd);
			root.getChildren().add(txt_cmd);
			root.getChildren().add(h_run);

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