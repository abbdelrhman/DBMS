package eg.edu.alexu.csd.oop.db.classes;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eg.edu.alexu.csd.oop.db.Commands;
import eg.edu.alexu.csd.oop.db.Database;
import eg.edu.alexu.csd.oop.db.Visitor;

public class CreateTable implements Commands {

	@Override
	public boolean chooseCorrectCommand(String userCommand) {
		// TODO Auto-generated method stub
		//CREATE TABLE table_name8(column_name1 varchar, column_name2 int, column_name3 varchar)
				String regexTable = "(\\bcreate table\\b) \\w+(\\()\\w+( \\bint\\b| \\bvarchar\\b)((, \\w+( \\bint\\b| \\bvarchar\\b))+)?\\)";
				Pattern p = Pattern.compile(regexTable, Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(userCommand);
				
				if(m.find() && m.start() == 0 && m.end() == userCommand.length()) {
					return true;
				}
				return false;
	}

	@Override
	public void excuteCommand(Database db, String userCommand) throws SQLException {
		// TODO Auto-generated method stub
		boolean check = db.executeStructureQuery(userCommand);
		if(check) {
			String tableName = userCommand.substring(13, userCommand.indexOf('('));
			System.out.println(tableName + " successfully created");
			
		}else {
			System.out.println("ERROR CREATE TABLE!");
		}
	}
	@Override
	public void accept(Visitor visitor) {
		// TODO Auto-generated method stub
				visitor.visit(this);

	}

}
