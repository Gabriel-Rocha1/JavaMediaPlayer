package app.model;

public class SongQueue extends SongList {
	private int current;
	public SongQueue() {
		super();
		this.current = -1;
	}
	
	public Song next() {
		if (this.current < this.songs.size() - 1) {
			this.current++;
			return this.songs.get(this.current);
		}
		else
			return null;
	}

	public Song previous() {
		if (this.current > 0) {
			this.current--;
			return this.songs.get(this.current);
		} else {
			return null;
		}
	}

	public Song first() {
		this.current = 0;
		return this.songs.get(this.current);
	}

	public Song last() {
		this.current = this.songs.size() - 1;
		return this.songs.get(this.current);
	}
}
