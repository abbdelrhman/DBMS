package eg.edu.alexu.csd.oop.db.classes;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eg.edu.alexu.csd.oop.db.Commands;
import eg.edu.alexu.csd.oop.db.Database;
import eg.edu.alexu.csd.oop.db.Visitor;

public class InsertIntoTable implements Commands {

	@Override
	public boolean chooseCorrectCommand(String userCommand) {
		// TODO Auto-generated method stub
		// INSERT INTO table_name8(column_NAME1, COLUMN_name3, column_name2) VALUES ('value1', 4, 'value2')
		
				String regexInsertIntoTable = "\\binsert into\\b \\w+(\\(\\w+((, \\w+)+)?\\))? \\bvalues\\b (\\((('\\w+')|\\d+)((,( )?(('\\w+')|\\d+))+)?\\))";
				Pattern pattern = Pattern.compile(regexInsertIntoTable, Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(userCommand);
				
				if (matcher.find() && matcher.start() == 0 && matcher.end() == userCommand.length()) {
					return true;
				}
				return false;
	}

	@Override
	public void excuteCommand(Database db, String userCommand) throws SQLException {
		// TODO Auto-generated method stub
		int count = db.executeUpdateQuery(userCommand);
		if(count == 0) {
			System.out.println("ERROR INSERT INTO TABLE");
		}else {
			System.out.println("Number of changed rows is : " + count);
		}
	}
	@Override
	public void accept(Visitor visitor) {
		// TODO Auto-generated method stub
				visitor.visit(this);

	}

}
