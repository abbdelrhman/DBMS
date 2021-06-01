package eg.edu.alexu.csd.oop.db.classes;

import java.sql.SQLException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eg.edu.alexu.csd.oop.db.Commands;
import eg.edu.alexu.csd.oop.db.Database;
import eg.edu.alexu.csd.oop.db.Visitor;

public class SelectFromTable implements Commands {
	@Override
	public boolean chooseCorrectCommand(String userCommand) {
		// TODO Auto-generated method stub
		//SELECT * FROM table_name1 WHERE coluMN_NAME2 > 4
		
				String regexSelectFromTable = "\\bselect\\b (\\*|(\\w+((, \\w+)+)?)) \\bfrom\\b \\w+( \\bwhere\\b( \\bnot\\b)? \\w+((=(\\d+|'\\w+'))| ((>|<) \\d+))( (\\band\\b|\\bor\\b) \\w+((=(\\d+|'\\w+'))| ((>|<) \\d+)))?)?";
				Pattern pattern = Pattern.compile(regexSelectFromTable, Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(userCommand);
				
				if (matcher.find() && matcher.start() == 0 && matcher.end() == userCommand.length()) {
					return true;
				}
				return false;
	}

	@Override
	public void excuteCommand(Database db, String userCommand) throws SQLException {
		// TODO Auto-generated method stub
		Object[][] selected = db.executeQuery(userCommand);
		
	}
	@Override
	public void accept(Visitor visitor) {
		// TODO Auto-generated method stub
				visitor.visit(this);

	}

}
