package eg.edu.alexu.csd.oop.db;

import java.sql.SQLException;

public interface Commands {
	public boolean chooseCorrectCommand(String userCommand);
	public void excuteCommand(Database db,String userCommand) throws SQLException;
	public void accept(Visitor visitor);

}
