package views;

import controllers.MainViewController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import model.service.SqlOperator;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class GuiApplication extends Application {
	private Group root;
	private Stage stage;
	private Scene scene;
	private MainViewController controller;

	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		root = new Group();
		scene = new Scene(root);

		stage.setTitle("Boat Club Application");
		stage.setScene(scene);
		stage.show();
		controller = new MainViewController();
		controller.register(this);
	}
	
	public Scene getScene() {
		return scene;
	}

	public void gotoMainMenuScene() { //was void
		scene = new MainMenuScene(controller);
		
		stage.setScene(scene);
	}

	public void gotoLoginScreen() {
		scene = new LoginScene();
		stage.setScene(scene);
	}
	
	public void gotoAddMemberScreen() {
		scene = new AddMemberScene(controller);
		stage.setScene(scene);
	}
	
	public static void doLaunch(String[] args) {
		launch(args);
	}

	public void gotoUpdateMemberScreen() {
		scene = new UpdateMemberScene(controller);
		stage.setScene(scene);
		
	}

	public void gotoStatisticsScreen() {
		scene = new StatisticsScene(controller);
		stage.setScene(scene);
		
	}
}
