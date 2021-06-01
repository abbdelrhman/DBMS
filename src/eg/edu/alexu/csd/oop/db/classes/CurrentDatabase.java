package eg.edu.alexu.csd.oop.db.classes;


public class CurrentDatabase {
	private static CurrentDatabase db = null;
	private String path;

	private CurrentDatabase() {}

	public static  CurrentDatabase getInstance() {
		if (db == null) {
			db = new CurrentDatabase();
		}
		return db;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
