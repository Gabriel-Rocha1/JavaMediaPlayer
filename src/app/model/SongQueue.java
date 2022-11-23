package app.model;

public class SongQueue extends SongList {
	
	public SongQueue() {
		super();
		
	}
	
	public void push(Song song) {
		super.add(song);
	}
	
	public Song pop() {
		return this.songs.remove(0);
	}
}
