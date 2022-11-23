package app.model;

public class User {
	private String username;
	private String name;
	private boolean vip;
	
	public User(String name, String username) {
		this.username = username;
		this.name = name;
		this.vip = false;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public boolean isVip() {
		return vip;
	}

	public void setVip(boolean vip) {
		this.vip = vip;
	}
}
