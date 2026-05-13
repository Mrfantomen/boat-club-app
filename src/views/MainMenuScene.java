package views;

import controllers.MainViewController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import views.helper.MainMenuOptions;

public class MainMenuScene extends Scene {
	public static final int ADDMEMBER = 0;
	public static final int UPDATEMEMBER = 1;
	public static final int STATISTICS = 2;
	private Button[] menuOptions;
	// private int opt = -1;
	private MainViewController controller;

	public MainMenuScene(MainViewController controller) {
		super(new Group(), 500, 500);
		this.controller = controller;
		menuOptions = new Button[3];
		menuOptions[0] = new Button("Add Member");
		menuOptions[1] = new Button("Change Member");
		menuOptions[2] = new Button("Statistics");

		// Disable Change Member if there are no members
		boolean hasMembers = controller.getMemberStorage().getMemberlist().size() > 0;
		menuOptions[1].setDisable(!hasMembers);

		HBox Hbox = new HBox();
		Hbox.getChildren().addAll(menuOptions);
		Group parent = (Group) getRoot();
		parent.getChildren().add(Hbox);

		for (int i = 0; i < menuOptions.length; i++) {
			final int j = i;
			menuOptions[i].setOnAction(e -> {
				System.out.println("You pressed "+ j);
				this.controller.handleButton(j);
			});
		}
	}

}
