package views;

import java.io.IOException;

import controllers.MainViewController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Boat;
import model.BoatType;
import model.Member;
import model.service.SqlOperator;
import views.helper.MainMenuOptions;

public class StatisticsScene extends Scene {
	public static final int BTN1 = 0;
	private Button[] menuOptions;
	// private int opt = -1;
	private MainViewController controller;

	public StatisticsScene(MainViewController controller) {
		super(new Group(), 500, 500);
		this.controller = controller;

		SqlOperator operator = new SqlOperator();

		int[] maxMin = operator.getMaxAndMin();
		
		Button back = new Button("Go back");
		
		back.setOnAction(ignore -> {
			controller.goBack();
			
		});

		Label ratio = new Label("The member to boat ratio is " + String.valueOf(operator.getRatio()));
		Label max = new Label("Member with most has: "+ String.valueOf(maxMin[0])+ " boat(s)");
		Label min = new Label("Member with least boat has: "+ String.valueOf(maxMin[1]) + " boat(s)");

		VBox vBox = new VBox();

		vBox.getChildren().addAll(ratio, max, min, back);

		Group parent = (Group) getRoot();
		parent.getChildren().add(vBox);

	}

}
