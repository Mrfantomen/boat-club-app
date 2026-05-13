package views;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class LoginScene extends Scene {
	private TextField name;
	private PasswordField password;
	private Button loginBtn;
	
	public LoginScene() {
		super(new Group(), 500, 500); // The scence constructor
		name = new TextField("name");
		password = new PasswordField();
		loginBtn = new Button("ok");
		Label nameLabel = new Label("name: ");
		Label passwordLabel = new Label("password: ");
		name.setTranslateX(75);
		name.setTranslateY(50);
		password.setTranslateX(75);
		password.setTranslateY(80);
		nameLabel.setTranslateX(5);
		nameLabel.setTranslateY(50);
		passwordLabel.setTranslateX(5);
		passwordLabel.setTranslateY(80);
		loginBtn.setTranslateX(75);
		loginBtn.setTranslateY(110);
		Group parent = (Group)getRoot();
		parent.getChildren().addAll(nameLabel, passwordLabel, name, password, loginBtn);
	}

	public String getUsername() {
		return name.getText();
	}

	public String getPassword() {
		return password.getText();
	}

	public void setButtonHandler(EventHandler<MouseEvent> handler) {
		loginBtn.setOnMouseClicked(handler);
	}
}
