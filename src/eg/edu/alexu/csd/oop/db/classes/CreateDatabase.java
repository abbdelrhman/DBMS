package eg.edu.alexu.csd.oop.db.classes;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eg.edu.alexu.csd.oop.db.Commands;
import eg.edu.alexu.csd.oop.db.Database;
import eg.edu.alexu.csd.oop.db.Visitor;

public class CreateDatabase implements Commands {

	@Override
	public boolean chooseCorrectCommand(String userCommand) {
		// TODO Auto-generated method stub
		//CREATE DATABASE TestDB
				String regexDataBase = "\\bcreate database\\b+( \\w+)";
				Pattern p = Pattern.compile(regexDataBase, Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(userCommand);
				
				if(m.find() && m.start() == 0 && m.end() == userCommand.length()) {
					return true;
				}
				return false;
	}

	@Override
	public void excuteCommand(Database db, String userCommand) throws SQLException {
		// TODO Auto-generated method stub
		String nameOfDatabase = userCommand.substring(16,userCommand.length());
		String path = db.createDatabase("sample" + System.getProperty("file.separator") + nameOfDatabase , false);
		//System.out.println(path);
	}
	@Override
	public void accept(Visitor visitor) {
		// TODO Auto-generated method stub
				visitor.visit(this);

	}

}
