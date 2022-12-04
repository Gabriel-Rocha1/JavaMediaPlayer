package app.control;

import app.JavaMediaPlayer;
import app.model.User;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;

public class LoginController implements Initializable {
	@FXML private AnchorPane paneDivider;

	@FXML private Button bttnCreateAccount;

	@FXML private CheckBox checkVIP;

	@FXML private PasswordField txtLoginPassword;
	@FXML private PasswordField txtRegisterPassword;
    @FXML private TextField txtLoginUsername;
    @FXML private TextField txtRegisterName;
    @FXML private TextField txtRegisterUsername;

	@FXML private Dialog<String> dialog;
	@FXML private ButtonType type;

	/**
	 * HashMap contendo as combinações de username e senha registrados no sistema
	 */
    private HashMap<String, String> credentials;

	/**
	 * ArrayList contendo todas as combinações de nome + username + status VIP registradas no sistema
	 */
	private ArrayList<User> accounts;

	/**
	 * Lê o arquivo accounts.dat e adiciona:
	 * no HashMap, combinações de usuário + senha,
	 * no ArrayList, combinações de nome + username + status VIP.
	 *
	 * @param location
	 * The location used to resolve relative paths for the root object, or
	 * {@code null} if the location is not known.
	 *
	 * @param resources
	 * The resources used to localize the root object, or {@code null} if
	 * the root object was not localized.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		credentials = new HashMap<>();
		accounts = new ArrayList<>();

		type = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
		dialog = new Dialog<>();
		dialog.setTitle("Erro");
		dialog.getDialogPane().getButtonTypes().add(type);

		try {
			File f = new File(JavaMediaPlayer.ACCOUNTS_FILE_PATH);
			if (f.exists()) {
				BufferedReader reader = new BufferedReader(new FileReader(JavaMediaPlayer.ACCOUNTS_FILE_PATH));

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
					if (vipStatus.equals("1"))
						u.setVip(true);

					accounts.add(u);

					currentLine = reader.readLine();
				}
				reader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Lê os campos de username e senha e verifica se as credenciais existem no HashMap.
	 * @throws IOException
	 */
	@FXML
    public void authenticate() throws IOException {
    	String username = txtLoginUsername.getText();
    	String password = hashPassword(txtLoginPassword.getText());

    	boolean authorized = false;
    	
    	if (credentials.containsKey(username))
    		if (credentials.get(username).equals(password))
    			authorized = true;

    	for (User account : accounts)
    		if (account.getUsername().equals(username))
    			JavaMediaPlayer.user = account;
    	
    	if (authorized) {
			JavaMediaPlayer.setRoot("view/Main");
    	} else {
			dialog.setContentText("Nome de Usuário ou Senha inválidos.");
			dialog.showAndWait();
			txtLoginUsername.clear();
			txtLoginPassword.clear();
    	}
    }

	/**
	 * Habilita os campos de registro e move o (Pane) que está os cobrindo à direita.
	 */
	@FXML
    public void register() {
    	txtRegisterName.setDisable(false);
    	txtRegisterUsername.setDisable(false);
    	txtRegisterPassword.setDisable(false);
    	bttnCreateAccount.setDisable(false);

    	paneDivider.setLayoutX(400);
    }

	/**
	 * Lê os campos de nome, username e senha e registra o usuário se
	 * todos os campos forem preenchidos corretamente e não
	 * houver nenhum usuário registrado com o username fornecido.
	 * @throws IOException
	 */
	@FXML
    public void createAccount() throws IOException {
		String name = txtRegisterName.getText();
		String username = txtRegisterUsername.getText();
		String password = txtRegisterPassword.getText();

		int vipStatus = 0;
		if (checkVIP.isSelected())
			vipStatus = 1;

		if (name.trim().length() == 0) {
			dialog.setContentText("O campo Nome está vazio.");
			dialog.showAndWait();
			return;
		}

		if (username.trim().length() == 0) {
			dialog.setContentText("O campo Nome de Usuário está vazio.");
			dialog.showAndWait();
			return;
		}

		if (password.trim().length() == 0) {
			dialog.setContentText("O campo Senha está vazio.");
			dialog.showAndWait();
			return;
		}

		if (credentials.containsKey(username)) {
			dialog.setContentText("Nome de usuário já registrado.");
			dialog.showAndWait();
			return;
		}

		if (vipStatus == 1) {
			File f = new File(JavaMediaPlayer.PLAYLISTS_DIRECTORY_PATH + "/" + username);
			if (!f.mkdir()) {
				dialog.setContentText("Erro ao criar pasta de Playlists no sistema. Verifique o nível de acesso do diretório atual e tente novamente.");
				dialog.showAndWait();
				return;
			}
		}

		String hashedPassword = hashPassword(password);
		credentials.put(username, hashedPassword);

		JavaMediaPlayer.user = new User(name, username);
		if (vipStatus == 1)
			JavaMediaPlayer.user.setVip(true);

		accounts.add(JavaMediaPlayer.user);

		byte[] credentials = (name + " " + username + " " + hashedPassword
				+ " " + vipStatus + "\n").getBytes();

		FileOutputStream fos = new FileOutputStream(JavaMediaPlayer.ACCOUNTS_FILE_PATH, true);
		fos.write(credentials);
		fos.close();

		txtRegisterName.clear();
		txtRegisterUsername.clear();
		txtRegisterPassword.clear();

		JavaMediaPlayer.setRoot("view/Main");
	}

	/**
	 * Criptografa a senha que será escrita no arquivo accounts.dat.
	 * @param password senha em texto.
	 * @return senha criptografada.
	 */
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