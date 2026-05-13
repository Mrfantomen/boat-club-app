package views;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import controllers.MainViewController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import views.helper.MainMenuOptions;
import model.service.SqlOperator;

public class AddMemberScene extends Scene {
	public static final int BTN1 = 0;
	private Button[] menuOptions;
	// private int opt = -1;
	private MainViewController controller;
	private SqlOperator sqlOp;

	public AddMemberScene(MainViewController controller) {
		super(new Group(), 500, 500);
		this.controller = controller;
		sqlOp = new SqlOperator();
		VBox vBox = new VBox();
		Button OK = new Button("OK");
		Button back = new Button("Back");
		TextField name = new TextField("Name");
		TextField personalNumber = new TextField("Personal Number: YYYYMMDDXXXX");
		OK.setText("OK");
		vBox.getChildren().addAll(name, personalNumber, OK , back);

		Group parent = (Group) getRoot();
		parent.getChildren().add(vBox);
		
		back.setOnAction(ignore -> {
			controller.goBack();
			
		});

		OK.setOnAction(ignore -> {
			try {
				if (name.getText().length() > 0  && personalNumber.getText().replaceAll("[^0-9]", "").length() == 12) {
					String pNum = personalNumber.getText().replaceAll("[^0-9]", "");
					String pNumDate = pNum.substring(0, 8);
					DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
					try {
						LocalDate date = LocalDate.parse(pNumDate, dateFormatter);
					if (sqlOp.ValidatePNum(pNum) && (date.isBefore(LocalDate.now()) || date.isEqual(LocalDate.now()))) {
						this.controller.addMember(name.getText(), personalNumber.getText());
						}
					} catch (DateTimeParseException e) {

					}

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	

	

}
