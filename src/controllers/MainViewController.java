package controllers;

import java.io.IOException;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.BoatType;
import model.Member;
import model.MemberStorage;
import model.service.SqlOperator;
import views.GuiApplication;
import views.LoginScene;
import views.MainMenuScene;
import views.helper.MainMenuOptions;

public class MainViewController {
	public static final int SUCCESS = 1;
	public static final int FAILURE = -1;
	private GuiApplication Gui;
	private MemberStorage memberStorage;

	public MainViewController() throws IOException {
		this.memberStorage = new MemberStorage();
	}

	public MemberStorage getMemberStorage() {
		return memberStorage;
	}

	public void register(GuiApplication Gui) {
		this.Gui = Gui;
		Gui.gotoLoginScreen();
		LoginScene scene = (LoginScene) Gui.getScene();
		scene.setButtonHandler(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (SqlOperator.login(scene.getUsername(), scene.getPassword())) {
					Gui.gotoMainMenuScene();
				}
			}
		});
	}

	public void updateMember(Member member, String name, String personalNumber) throws IOException {
		member.setName(name);
		member.setPersonNum(personalNumber);
		memberStorage.updateMember(member);
		this.Gui.gotoMainMenuScene();
	}

	public void addMember(String name, String personalNum) throws IOException {
		memberStorage.addMember(name, personalNum);
		this.Gui.gotoMainMenuScene();
	}

	public void addBoat(BoatType type, double lengthOfBoat, int memberId) throws IOException {
		memberStorage.addBoat(type, lengthOfBoat, memberId);
		this.Gui.gotoMainMenuScene();
	}

	public void handleButton(int i) {
		switch (i) {
		case MainMenuScene.ADDMEMBER:
			AddMemberScene(Gui);
			break;
		case MainMenuScene.UPDATEMEMBER:
			UpdateMemberScene(Gui);
			break;
		case MainMenuScene.STATISTICS:
			StatisticsScene(Gui);
			break; // BUG FIX (bonus): missing break caused fall-through into default,
			       // printing "Invalid selection" every time Statistics was clicked.
		default:
			System.out.println("Invalid selection");
			break;
		}
	}

	private void StatisticsScene(GuiApplication Gui) {
		this.Gui = Gui;
		Gui.gotoStatisticsScreen();
	}

	private void UpdateMemberScene(GuiApplication Gui) {
		this.Gui = Gui;
		Gui.gotoUpdateMemberScreen();
	}

	private void AddMemberScene(GuiApplication Gui) {
		this.Gui = Gui;
		Gui.gotoAddMemberScreen();
	}

	public void goBack() {
		Gui.gotoMainMenuScene();
	}
}
