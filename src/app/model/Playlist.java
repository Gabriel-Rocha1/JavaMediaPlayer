package app.model;

public class Playlist extends SongList {
	private User owner;
	private String name;

	public Playlist(User owner, String name) {
		super();
		this.owner = owner;
		this.name = name;
	}
	
	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void sortByNameAscending() {
		this.songs.sort((firstSong, secondSong) -> firstSong.getTitle().compareToIgnoreCase(secondSong.getTitle()));
	}
	
	public void sortByNameDescending() {
		this.songs.sort((firstSong, secondSong) -> secondSong.getTitle().compareToIgnoreCase(firstSong.getTitle()));
	}
}
