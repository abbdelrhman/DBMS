package eg.edu.alexu.csd.oop.db.classes;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eg.edu.alexu.csd.oop.db.Commands;
import eg.edu.alexu.csd.oop.db.Database;
import eg.edu.alexu.csd.oop.db.Visitor;

public class DropDatabase implements Commands {

	@Override
	public boolean chooseCorrectCommand(String userCommand) {
		// TODO Auto-generated method stub
		//DROP DATABASE TestDB
		
				String regexDropDatabase = "\\bdrop database\\b+( \\w+)";
				Pattern pattern = Pattern.compile(regexDropDatabase, Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(userCommand);
				
				if (matcher.find() && matcher.start() == 0 && matcher.end() == userCommand.length()) {
					return true;
				}
				return false;
	}

	@Override
	public void excuteCommand(Database db, String userCommand) throws SQLException {
		// TODO Auto-generated method stub
		boolean check = db.executeStructureQuery(userCommand);
		if(check) {
			String nameOfDatabase = userCommand.substring(14, userCommand.length());
			System.out.println(nameOfDatabase + " successfully Dropped!");
		}else {
			System.out.println("Error in drop database!");
		}
	}
	@Override
	public void accept(Visitor visitor) {
		// TODO Auto-generated method stub
				visitor.visit(this);

	}

}
