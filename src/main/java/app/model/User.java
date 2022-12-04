package app.model;

public class User {
	/**
	 * Nome de Usuário utilizado como identificação única de cada usuário do sistema
	 */
	private final String username;

	/**
	 * nome que será mostrado no painel da aplicação
	 */
	private final String name;

	/**
	 * status VIP que garante o acesso à funcionalidade de luxo do sistema
	 */
	private boolean vip;

	/**
	 * Construtor padrão da classe User.
	 * @param name nome do usuário
	 * @param username username do usuário
	 */
	public User(String name, String username) {
		this.username = username;
		this.name = name;
		this.vip = false;
	}

	/**
	 *
	 * @return username do usuário
	 */
	public String getUsername() {
		return username;
	}

	/**
	 *
	 * @return nome do usuário
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 * @return true se o usuário for VIP, false se for um usuário comum
	 */
	public boolean isVip() {
		return vip;
	}

	/**
	 *
	 * @param vip status VIP do usuário
	 */
	public void setVip(boolean vip) {
		this.vip = vip;
	}
}