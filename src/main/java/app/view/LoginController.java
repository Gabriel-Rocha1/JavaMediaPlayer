package app.view;

import app.model.SongDirectory;
import app.model.User;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class LoginController {
	@FXML
    private Button bttnCreateAccount;

    @FXML
    private Button bttnEntrar;

    @FXML
    private Hyperlink bttnRegister;

    @FXML
    private CheckBox checkVIP;
    
    @FXML
    private AnchorPane paneDivider;

    @FXML
    private TextField txtLoginPassword;

    @FXML
    private TextField txtLoginUsername;

    @FXML
    private TextField txtRegisterName;

    @FXML
    private TextField txtRegisterPassword;

    @FXML
    private TextField txtRegisterUsername;
    
    private HashMap<String, String> credentials;
    private ArrayList<User> accounts;
    private static final String ACCOUNTS_FILE_PATH = "src\\data\\accounts.dat";

    
    @FXML
    public void initialize() throws IOException {
    	credentials = new HashMap<String, String>();
    	accounts = new ArrayList<User>();
    	
    	File f = new File(ACCOUNTS_FILE_PATH);
    	if (f.exists()) {
    	    BufferedReader reader = new BufferedReader(new FileReader(ACCOUNTS_FILE_PATH));
    	    
    	    String currentLine = reader.readLine();		
    	    String name, username, password, vipStatus;
    	    String[] splittedLine;
    	    
    	    while (currentLine != null) {
				splittedLine = currentLine.split(" ");
				
				name 	  = splittedLine[0];
				username  = splittedLine[1];
				password  = splittedLine[2];
				vipStatus = splittedLine[3];
				
				credentials.put(username, password);			
				
				User u = new User(name, username);
				if (vipStatus == "1")
					u.setVip(true);
				
				accounts.add(u);
				
				currentLine = reader.readLine();
			}		
			reader.close();
    	}
    }
    
    @FXML
    public void authenticate(ActionEvent e) throws IOException {
    	String username = txtLoginUsername.getText();
    	String password = hashPassword(txtLoginPassword.getText());

    	boolean authorized = false;
    	
    	if (credentials.containsKey(username))
    		if (credentials.get(username).equals(password))
    			authorized = true;

    	for (User account : accounts)
    		if (account.getUsername().equals(username))
    			MainController.user = account;		
    	
    	if (authorized) {
			JavaMediaPlayer.setRoot("Main");
    	} else {
    		//TODO: lidar com credenciais não autorizadas
    	}
    }
    
    @FXML
    public void register(ActionEvent e) {
    	txtRegisterName.setDisable(false);
    	txtRegisterUsername.setDisable(false);
    	txtRegisterPassword.setDisable(false);
    	bttnCreateAccount.setDisable(false);
    	
    	//TODO: criar animação de deslizar o painel para a direita
    	paneDivider.setLayoutX(375);
    }
    
    @FXML
    public void createAccount(ActionEvent e) throws IOException, NoSuchAlgorithmException {
		String name = txtRegisterName.getText();
		String username = txtRegisterUsername.getText();
		String password = txtRegisterPassword.getText();

		int vipStatus = 0;
		if (checkVIP.isSelected())
			vipStatus = 1;

		if (name.trim().length() == 0) {
			//TODO: sinalizar que o campo "Nome" está vazio
			return;
		}

		if (username.trim().length() == 0) {
			//TODO: sinalizar que o campo "Nome de Usuário" está vazio
			return;
		}

		if (password.trim().length() == 0) {
			//TODO: sinalizar que o campo "Senha" está vazio
			return;
		}

		if (credentials.containsKey(username)) {
			//TODO: lidar com usernames já registrados
			return;
		}

		String hashedPassword = hashPassword(password);

		credentials.put(username, hashedPassword);

		MainController.user = new User(name, username);
		if (vipStatus == 1)
			MainController.user.setVip(true);

		accounts.add(MainController.user);

		byte[] credentials = (name + " " + username + " " + hashedPassword
				+ " " + vipStatus + "\n").getBytes();

		FileOutputStream fos = new FileOutputStream(ACCOUNTS_FILE_PATH, true);
		fos.write(credentials);
		fos.close();

		txtRegisterName.clear();
		txtRegisterUsername.clear();
		txtRegisterPassword.clear();

		JavaMediaPlayer.setRoot("Main");
	}
    
    private String hashPassword(String password) {
    	MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(password.getBytes());
	    	byte[] bytes = messageDigest.digest();
	    	
	    	StringBuilder stringBuilder = new StringBuilder();
			for (byte aByte : bytes)
				stringBuilder.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
	        
	        return stringBuilder.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
    }   
}