package projekti;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import utils.BCrypt;
import utils.DBConnection;

public class SignUpForm extends GridPane {
	
	public static Label firstName = new Label("First Name:");
	public static TextField firstNameTextField = new TextField();
	public static Label lastName = new Label("Last Name:");
	public static TextField lastNameTextField = new TextField();
	public static Label email = new Label("Email:");
	public static TextField emailTextField = new TextField();
	public static Label password = new Label("Password:");
	public static PasswordField passwordTextField = new PasswordField();
	public static Label errorLabel = new Label("");
	public static Scene gameScene;

	public GridPane getSignUp() {
		
		Game game = new Game();
		gameScene = new Scene(game.getGameView(), 400, 400);
		GridPane pane = new GridPane();
		pane.setAlignment(Pos.CENTER);
		pane.setPadding(new Insets(20));
		pane.setHgap(10);
		pane.setVgap(10);
		pane.setStyle("-fx-background-color:linear-gradient(pink, lightblue); ");

		Button backBtn = new Button("<-");
		backBtn.setOnAction(e -> {
			cleanForm();
			(Main.window).setScene(Main.indexScene);
		});

		Text formName = new Text("Sign In");
		formName.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.ITALIC, 20));

		HBox title = new HBox(100);
		title.getChildren().addAll(formName, backBtn);

		pane.add(title, 0, 0, 2, 1);
		pane.add(firstName, 0, 1);
		pane.add(firstNameTextField, 1, 1);
		pane.add(lastName, 0, 2);
		pane.add(lastNameTextField, 1, 2);
		pane.add(email, 0, 3);
		pane.add(emailTextField, 1, 3);
		pane.add(password, 0, 4);
		pane.add(passwordTextField, 1, 4);
		pane.add(errorLabel, 0, 6, 2, 1);

		Button signUpBtn = new Button("Sign Up");
		signUpBtn.setStyle("-fx-text-fill: black; " + "-fx-font-family:'Arial'; "
				+ "-fx-background-color: linear-gradient(lightblue, pink); "
				+ "-fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
		signUpBtn.setOnAction(e -> signUpAction());
		pane.setOnKeyPressed(e -> {
									if(e.getCode() == KeyCode.ENTER) 
									signUpAction();});
		pane.add(signUpBtn, 1, 5);
		GridPane.setHalignment(signUpBtn, HPos.RIGHT);

		final Text actionTarget = new Text();
		pane.add(actionTarget, 1, 9);

		return pane;
	}

	public void cleanForm() {
		firstNameTextField.setText("");
		firstNameTextField.setStyle("-fx-background:white;");
		firstNameTextField.setPromptText("");
		lastNameTextField.setText("");
		lastNameTextField.setStyle("-fx-background:white;");
		lastNameTextField.setPromptText("");
		emailTextField.setText("");
		emailTextField.setStyle("-fx-background:white;");
		emailTextField.setPromptText("");
		passwordTextField.setText("");
		passwordTextField.setStyle("-fx-background:white;");
		passwordTextField.setPromptText("");
		errorLabel.setText("");
	}

	public void removeErrors() {
		firstNameTextField.setStyle("-fx-background:white;");
		firstNameTextField.setPromptText("");
		lastNameTextField.setStyle("-fx-background:white;");
		lastNameTextField.setPromptText("");
		emailTextField.setStyle("-fx-background:white;");
		emailTextField.setPromptText("");
		passwordTextField.setStyle("-fx-background:white;");
		passwordTextField.setPromptText("");
	}
	public void signUpAction()
	{
		removeErrors();
		if(valid())
		{
			if(checkEmail(emailTextField.getText())) {
				addUser(firstNameTextField.getText(), lastNameTextField.getText(), emailTextField.getText(), passwordTextField.getText());
				(Main.window).setScene(gameScene);
				(Main.window).setTitle("Yahtzee");
			} else {
				errorLabel.setStyle("-fx-text-fill: red");
				errorLabel.setText("This email is already in use");
			}
		}
		else
		{
			FormValidation.textFieldNotEmpty(SignUpForm.firstNameTextField, "Sh�no emrin!");
			FormValidation.textFieldNotEmpty(SignUpForm.lastNameTextField, "Sh�no mbiemrin!");
			FormValidation.emailValidate(SignUpForm.emailTextField, "Sh�no email-in valid!");
		}
	}
	public static boolean valid()
	{
		return FormValidation.emailValidate(SignUpForm.emailTextField,"Sheno email valid") && FormValidation.textFieldNotEmpty(SignUpForm.firstNameTextField) && FormValidation.textFieldNotEmpty(SignUpForm.lastNameTextField) && FormValidation.textFieldNotEmpty(SignUpForm.passwordTextField);
	}
	public static boolean addUser(String firstName, String lastName, String email, String password) {
		try 
		{
			String hash = BCrypt.hashpw(password, BCrypt.gensalt());
			String query = "INSERT INTO users(firstName, lastName, email, password) VALUES(?, ?, ?, ?)";
			PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, firstName);
			preparedStatement.setString(2, lastName);
			preparedStatement.setString(3, email);
			preparedStatement.setString(4, hash);
			
			if (preparedStatement.executeUpdate() > 0) {
				ResultSet rs = preparedStatement.getGeneratedKeys();
				rs.next();
			    String query2 = "INSERT INTO player_statistics(userId) VALUES(?)";
			    PreparedStatement preparedStatement2 = DBConnection.getConnection().prepareStatement(query2);
			    preparedStatement2.setInt(1, rs.getInt(1));
			    if (preparedStatement2.executeUpdate() > 0) {
			    	return true;
			    } else {
			    	return false;
			    }
			}
			return false;
		}
		catch(SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	public boolean checkEmail(String email) {
		try {
			String query = "SELECT * FROM users WHERE email = ?";
			PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
			preparedStatement.setString(1, email);
			ResultSet rez = preparedStatement.executeQuery();
	
			return rez.next();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}
}
