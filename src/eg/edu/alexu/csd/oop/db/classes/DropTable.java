package eg.edu.alexu.csd.oop.db.classes;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eg.edu.alexu.csd.oop.db.Commands;
import eg.edu.alexu.csd.oop.db.Database;
import eg.edu.alexu.csd.oop.db.Visitor;

public class DropTable implements Commands {

	@Override
	public boolean chooseCorrectCommand(String userCommand) {
		// TODO Auto-generated method stub
		//DROP TABLE TestDB
		
				String regexDropTable = "\\bdrop table\\b+( \\w+)";
				Pattern pattern = Pattern.compile(regexDropTable, Pattern.CASE_INSENSITIVE);
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
			String tableName = userCommand.substring(11, userCommand.length());
			System.out.println(tableName + " successfully Dropped!");
		}else {
			System.out.println("Erroe in drop the table!");
		}
	}
	@Override
	public void accept(Visitor visitor) {
		// TODO Auto-generated method stub
				visitor.visit(this);

	}

}
