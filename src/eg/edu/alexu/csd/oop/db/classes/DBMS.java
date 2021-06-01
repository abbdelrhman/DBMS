package eg.edu.alexu.csd.oop.db.classes;

import java.io.File;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;


import eg.edu.alexu.csd.oop.db.Commands;
import eg.edu.alexu.csd.oop.db.Database;
import eg.edu.alexu.csd.oop.db.ExecuteVisitor;

import javafx.util.Pair;
/*
 * ID : 4 - 36 - 55 - 56
 */

public class DBMS implements Database {
	
	private XML xmlFile = new XML();
	private String currentDatabase = null;
	private CurrentDatabase current = null;
	private boolean databaseCreated = false;
	
	private ExecuteVisitor visitor;

	@Override
	public String createDatabase(String databaseName, boolean dropIfExists) {
		// TODO Auto-generated method stub
		databaseName = databaseName.toLowerCase();

		//System.out.println(databaseName);

		File file = new File(databaseName);

		if (file.exists() && file.isDirectory()) {
			if (dropIfExists) {
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					files[i].delete();
				}
			}
		} else {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdir();
			}
			file.mkdir();
			System.out.println("DataBase Created successfully");
		}


		// to know which database we save into currently
		currentDatabase = file.getAbsolutePath();
		current = CurrentDatabase.getInstance();
		current.setPath(file.getAbsolutePath());
		databaseCreated = true;

		try {
			executeStructureQuery("create database " + file.getName());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return file.getAbsolutePath();
	}

	@Override
	public boolean executeStructureQuery(String query) throws SQLException {
		// TODO Auto-generated method stub
		String exception = query.toLowerCase();
		if (exception.contains("create table") && !exception.contains("(")) {
			throw new SQLException();
		}
		
		Commands createdb = new CreateDatabase();
		Commands dropdp = new DropDatabase();
		Commands createTable = new CreateTable();
		Commands dropTable = new DropTable();
		
		if (createdb.chooseCorrectCommand(query)) {

			String nameOfDatabase = query.substring(16, query.length());
			nameOfDatabase = nameOfDatabase.toLowerCase();

			File file = new File("sample" + System.getProperty("file.separator") + nameOfDatabase);

			if (file.exists()) {
				currentDatabase = file.getAbsolutePath();
				current = CurrentDatabase.getInstance();
				current.setPath(file.getAbsolutePath());
				databaseCreated = true;
				System.out.println("Current Database is : " + nameOfDatabase);
				return true;
			}

		} else if (createTable.chooseCorrectCommand(query)) {

			if (!databaseCreated) {
				System.out.println("ERROR DATABASE NOT CREATED!");
				throw new SQLException();
			}

			if (!current.getPath().equals(null)) {
				HashMap<String, String> map = new HashMap<>();
				String tableName = query.substring(13, query.indexOf('('));
				File tableFile = new File(currentDatabase + System.getProperty("file.separator") + tableName + ".xml");

				if (!tableFile.exists()) {//CREATE TABLE table_name8(column_name1 varchar, column_name2 int, column_name3 varchar)

					String info = query.substring(query.indexOf('(') + 1, query.length() - 1);
					String regexInt = " \\bint\\b";
					Pattern pInt = Pattern.compile(regexInt, Pattern.CASE_INSENSITIVE);
					String regexVarChar = " \\bvarchar\\b";
					Pattern pVarChar = Pattern.compile(regexVarChar, Pattern.CASE_INSENSITIVE);
					while (info.contains(",")) {
						String c = info.substring(0, info.indexOf(','));
						Matcher mInt = pInt.matcher(c);
						Matcher mVarChar = pVarChar.matcher(c);
						if (mInt.find()) {
							map.put(c.substring(0, mInt.start()), "int");
						} else if (mVarChar.find()) {
							map.put(c.substring(0, mVarChar.start()), "varchar");
						}
						info = info.substring(info.indexOf(',') + 2);
					}
					Matcher mInt = pInt.matcher(info);
					Matcher mVarChar = pVarChar.matcher(info);
					if (mInt.find()) {
						map.put(info.substring(0, mInt.start()), "int");
					} else if (mVarChar.find()) {
						map.put(info.substring(0, mVarChar.start()), "varchar");
					}
					xmlFile.createXML(tableName);
					xmlFile.createXSD(tableName, map);

				//	xmlFile.readXSD(tableName);

					return true;
				}
			}

		} else if (dropdp.chooseCorrectCommand(query)) {

			String db = query.substring(14, query.length());
			db = db.toLowerCase();
			File f = new File("sample" + System.getProperty("file.separator") + db);
			if (f.exists()) {
				File[] files = f.listFiles();
				for (int i = 0; i < files.length; i++) {
					files[i].delete();
				}
				return true;
			}

		} else if (dropTable.chooseCorrectCommand(query)) {
			String tableName = query.substring(11, query.length());
			File xml = new File(currentDatabase + System.getProperty("file.separator") + tableName + ".xml");
			File xsd = new File(currentDatabase + System.getProperty("file.separator") + tableName + ".xsd");

			if (xml.exists()) {
				xml.delete();
				if (xsd.exists()) {
					xsd.delete();
				}
				return true;
			}
		} else {
			return false;
		}

		return false;
	}

	@Override
	public Object[][] executeQuery(String query) throws SQLException {
		// TODO Auto-generated method stub
		Commands select = new SelectFromTable();
		if(!select.chooseCorrectCommand(query)) {
			throw new SQLException();
		}
		visitor = new ExecuteVisitor();
		visitor.setQuery(query);
		select.accept(visitor);
		if(!visitor.isExecuted()) {
			System.out.println("ERROR!");
			throw new SQLException();
		}
		return visitor.getArray();
	
	}

	@Override
	public int executeUpdateQuery(String query) throws SQLException {
		// TODO Auto-generated method stub
		Commands insert = new InsertIntoTable();
		Commands delete = new DeleteFromTable();
		Commands update = new UpdateTable();
		
		if (insert.chooseCorrectCommand(query)) {
			visitor = new ExecuteVisitor();
			visitor.setQuery(query);
			insert.accept(visitor);
			if(visitor.getChangedRows() == -1) {
				throw new SQLException();
			}
			return visitor.getChangedRows();

		}else if(delete.chooseCorrectCommand(query)) {
			visitor = new ExecuteVisitor();
			visitor.setQuery(query);
			delete.accept(visitor);
			if(visitor.getChangedRows() == -1) {
				throw new SQLException();
			}
			
			return visitor.getChangedRows();
			
		}else if (update.chooseCorrectCommand(query)) {
			visitor = new ExecuteVisitor();
			visitor.setQuery(query);
			update.accept(visitor);
			if(visitor.getChangedRows() == -1) {
				throw new SQLException();
			}
			return visitor.getChangedRows();
			
		}else {
			throw new SQLException();
		}
		
	}
	
	


}
