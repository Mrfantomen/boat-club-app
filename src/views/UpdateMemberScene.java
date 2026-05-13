package views;

import java.io.IOException;

import controllers.MainViewController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Boat;
import model.BoatType;
import model.Member;
import views.helper.MainMenuOptions;

public class UpdateMemberScene extends Scene {
	public static final int BTN1 = 0;
	private Button[] menuOptions;
	// private int opt = -1;
	private MainViewController controller;

	public UpdateMemberScene(MainViewController controller) {
		super(new Group(), 500, 500);
		this.controller = controller;
		VBox vBox = new VBox();
		ComboBox<Member> selectMember = new ComboBox<>(); // SHOULD BE LOADED WITH REAL MEMBERS
		for (Member member : controller.getMemberStorage().getMemberlist()) {
			selectMember.getItems().add(member);
		}

		Button OK = new Button("OK");
		Button OKupdate = new Button("Update member");
		Button deleteMember = new Button("Remove Selected Member");
		Button addBoat = new Button("Add boat");
		Button addBoatOK = new Button("OK - Add boat");

		deleteMember.setDisable(true);
		addBoat.setDisable(true);
		addBoatOK.setDisable(true);
		OKupdate.setDisable(true);

		TextField name = new TextField("Name");
		TextField personalNumber = new TextField("Personal Number: YYYYMMDDXXXX");

		ComboBox<Boat> selectBoat = new ComboBox<>();

		ComboBox<BoatType> boatType = new ComboBox<>();
		boatType.getItems().addAll(BoatType.values());

		TextField boatLength = new TextField();
		Button deleteBoat = new Button("Remove selected Boat");
		selectBoat.setDisable(true);
		boatType.setDisable(true);
		boatLength.setDisable(true);
		deleteBoat.setDisable(true);

		selectMember.setOnAction(ignore -> {
			deleteMember.setDisable(false);
			addBoat.setDisable(false);
			OKupdate.setDisable(false);
			name.setText(selectMember.getSelectionModel().getSelectedItem().getName());
			personalNumber.setText(selectMember.getSelectionModel().getSelectedItem().getPersonNum());
			selectBoat.setDisable(false);
			selectBoat.getItems().clear();
			for (Boat boat : selectMember.getSelectionModel().getSelectedItem().getBoatList()) {
				selectBoat.getItems().add(boat);
			}
		});

		selectBoat.setOnAction(ignore -> {
			if (selectBoat.getSelectionModel().getSelectedItem() != null) {
				boatType.getSelectionModel().select(selectBoat.getSelectionModel().getSelectedItem().getType());
				boatLength.setText(String.valueOf(selectBoat.getSelectionModel().getSelectedItem().getLength()));
				boatType.setDisable(false);
				boatLength.setDisable(false);
				deleteBoat.setDisable(false);

			}
		});

		addBoat.setOnAction(ignore -> {
			boatType.setDisable(false);
			boatLength.setDisable(false);
			addBoatOK.setDisable(false);
			// boatType.getSelectionModel().select(selectBoat.getSelectionModel().getSelectedItem().getType());
			// boatLength.setText(String.valueOf(selectBoat.getSelectionModel().getSelectedItem().getLength()));

		});

		addBoatOK.setOnAction(ignore -> {
			if (addBoat.isDisable() == false && boatLength.getText().length() != 0) {
				try {
					this.controller.addBoat(boatType.getSelectionModel().getSelectedItem(),
							Double.valueOf(boatLength.getText()),
							selectMember.getSelectionModel().getSelectedItem().getId());
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

		vBox.getChildren().addAll(selectMember, name, personalNumber, addBoat, selectBoat, boatType, boatLength,
				addBoatOK, deleteMember, deleteBoat, OKupdate, OK);

		Group parent = (Group) getRoot();
		parent.getChildren().add(vBox);

		OK.setOnAction(ignore -> {
			try {
				Member selectedMember = selectMember.getSelectionModel().getSelectedItem();
				if (selectedMember == null) {
					return; // No member selected, do nothing
				}
				this.controller.updateMember(selectedMember, name.getText(), personalNumber.getText());

				Boat boat = selectBoat.getSelectionModel().getSelectedItem();
				if (boat != null) { // Only update boat if one is selected
					boat.setLength(Double.parseDouble(boatLength.getText()));
					boat.setType(boatType.getSelectionModel().getSelectedItem());
					this.controller.getMemberStorage().updateBoat(boat);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		OKupdate.setOnAction(ignore -> {
			try {

				this.controller.updateMember(selectMember.getSelectionModel().getSelectedItem(), name.getText(),
						personalNumber.getText());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		deleteMember.setOnAction(ignore -> {
			this.controller.getMemberStorage().deleteMember(selectMember.getSelectionModel().getSelectedItem());

		});

		deleteBoat.setOnAction(ignore -> {
			this.controller.getMemberStorage().deleteBoat(selectBoat.getSelectionModel().getSelectedItem());

		});

	}

}
