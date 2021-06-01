package eg.edu.alexu.csd.oop.db;

import java.sql.SQLException;
import java.util.Scanner;

import eg.edu.alexu.csd.oop.db.classes.CreateDatabase;
import eg.edu.alexu.csd.oop.db.classes.CreateTable;
import eg.edu.alexu.csd.oop.db.classes.DBMS;
import eg.edu.alexu.csd.oop.db.classes.DeleteFromTable;
import eg.edu.alexu.csd.oop.db.classes.DropDatabase;
import eg.edu.alexu.csd.oop.db.classes.DropTable;
import eg.edu.alexu.csd.oop.db.classes.InsertIntoTable;
import eg.edu.alexu.csd.oop.db.classes.SelectFromTable;
import eg.edu.alexu.csd.oop.db.classes.UpdateTable;

/*
 * ID : 4 - 36 - 55 - 56
 */


public class MAIN {

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		Scanner sc = new Scanner(System.in);
		String input = "";
		DBMS db = new DBMS();
		CreateDatabase createdb = new CreateDatabase();
		CreateTable createTable = new CreateTable();
		DropDatabase dropdb = new DropDatabase();
		DropTable dropTable = new DropTable();
		InsertIntoTable insert = new InsertIntoTable();
		DeleteFromTable delete = new DeleteFromTable();
		UpdateTable update = new UpdateTable();
		SelectFromTable select = new SelectFromTable();
		
		System.out.println("ENTER SQL COMMAND LINE OR -1 TO EXIT");
		
		
		
		while(!input.equals("-1")) {
			input = sc.nextLine();
		
			if(createdb.chooseCorrectCommand(input)) {
					try {
						createdb.excuteCommand(db, input);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			
			else if(createTable.chooseCorrectCommand(input)) {
				try {
					createTable.excuteCommand(db, input);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(dropdb.chooseCorrectCommand(input)) {
				try {
					dropdb.excuteCommand(db, input);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			else if(dropTable.chooseCorrectCommand(input)) {
				try {
					dropTable.excuteCommand(db, input);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(insert.chooseCorrectCommand(input)) {
				try {
					insert.excuteCommand(db, input);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(delete.chooseCorrectCommand(input)) {
				try {
					delete.excuteCommand(db, input);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(update.chooseCorrectCommand(input)) {
				try {
					update.excuteCommand(db, input);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(select.chooseCorrectCommand(input)) {
				try {
					select.excuteCommand(db, input);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		}
	}

}
