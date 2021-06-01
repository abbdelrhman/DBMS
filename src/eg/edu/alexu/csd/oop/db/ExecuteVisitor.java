package eg.edu.alexu.csd.oop.db;



import java.io.File;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import eg.edu.alexu.csd.oop.db.classes.CreateDatabase;
import eg.edu.alexu.csd.oop.db.classes.CreateTable;
import eg.edu.alexu.csd.oop.db.classes.CurrentDatabase;
import eg.edu.alexu.csd.oop.db.classes.DeleteFromTable;
import eg.edu.alexu.csd.oop.db.classes.DropDatabase;
import eg.edu.alexu.csd.oop.db.classes.DropTable;
import eg.edu.alexu.csd.oop.db.classes.InsertIntoTable;
import eg.edu.alexu.csd.oop.db.classes.SelectFromTable;
import eg.edu.alexu.csd.oop.db.classes.UpdateTable;
import eg.edu.alexu.csd.oop.db.classes.XML;
import javafx.util.Pair;

public class ExecuteVisitor implements Visitor {

	
	private String query;
	private int changedRows = 0;;
	private Object[][] array;
	private boolean executed;
	private CurrentDatabase current = CurrentDatabase.getInstance();
	private XML files = new XML();
	private String table;
	private HashMap<String, String> map = new HashMap<>();
	
	
	public String getTable() {
		return table;
	}

	public HashMap<String, String> getMap() {
		return map;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public int getChangedRows() {
		return changedRows;
	}

	public Object[][] getArray() {
		return array;
	}

	public boolean isExecuted() {
		return executed;
	}
	

	
	@Override
	public void visit(CreateDatabase createddb) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(CreateTable createTable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(DropDatabase dropdb) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(DropTable dropTable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(SelectFromTable select) {
		// TODO Auto-generated method stub
		ArrayList<Pair<String,Pair<String,String>>> conditions = new ArrayList<>() ;
		HashMap<String,String> map = new HashMap<>();
		ArrayList<String> columns = new ArrayList<>();
		executed = true;
		String operator = "";
		
		String regex = " \\bfrom\\b ";
		Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(query);
		if(m.find()) {
			String column = query.substring(7, m.start());
			String where = " \\bwhere\\b ";
			Pattern p1 = Pattern.compile(where,Pattern.CASE_INSENSITIVE);
			Matcher m1 = p1.matcher(query);
			if(m1.find()) {
				table = query.substring(m.end(), m1.start());
				
				File tableFile = new File(current.getPath() + System.getProperty("file.separator") + table + ".xml");
				if(!tableFile.exists()) {
					executed = false;
					return;
					//throw new SQLException();
				}
				
				if(column.equals("*")) {
					map = files.readXSD(table);
					for(Entry<String,String> it : map.entrySet()){
						columns.add(it.getKey());
					}
				}else {
					while(column.contains(",")) {
						columns.add(column.substring(0, column.indexOf(',')));
						column = column.substring(column.indexOf(',') + 2);
					}
					columns.add(column);
				}
				String regexAndOr = " \\band\\b | \\bor\\b ";
				Pattern p2 = Pattern.compile(regexAndOr,Pattern.CASE_INSENSITIVE);
				Matcher m2 = p2.matcher(query);
				String regexNot = "\\bnot\\b ";
				Pattern p3 = Pattern.compile(regexNot,Pattern.CASE_INSENSITIVE);
				Matcher m3 = p3.matcher(query);
				if(m2.find()) {
					String condition1 = query.substring(m1.end(), m2.start());
					conditions.add(condition(condition1,table));
					String condition2 = query.substring(m2.end(), query.length());
					conditions.add(condition(condition2,table));
					operator = query.substring(m2.start()+1, m2.end()-1);
					operator = operator.toLowerCase();
					
				}else if(m3.find()) {
					String condition = query.substring(m3.end(), query.length());
					conditions.add(condition(condition,table));
					operator = "not";
				}else {
					operator = "noCondition";
					String condition = query.substring(m1.end(), query.length());
					conditions.add(condition(condition,table));
					//System.out.println(conditions.get(0).getKey() + "   " + conditions.get(0).getValue().getKey() + "   " + conditions.get(0).getValue().getValue());
				}
			}else {
				operator = "all";
				table = query.substring(m.end(), query.length());
				File tableFile = new File(current.getPath() + System.getProperty("file.separator") + table + ".xml");
				if(!tableFile.exists()) {
					executed = false;
					return;
					//throw new SQLException();
				}
				if(column.equals("*")) {
					//System.out.println("here*");
					map = files.readXSD(table);
					for(Entry<String,String> it : map.entrySet()) {
						columns.add(it.getKey());
					}
				}else {
					while(column.contains(",")) {
						columns.add(column.substring(0, column.indexOf(',')));
						column =column.substring(column.indexOf(',') + 2);
					}
					columns.add(column);
				}
			}
		
		}
		
		map = files.readXSD(table);
		int numberOfColumns = 0;
		for(Entry<String,String> it : map.entrySet()) {
			for(int i=0;i<columns.size();i++) {
				if(it.getKey().equalsIgnoreCase(columns.get(i))) {
					numberOfColumns++;
				}
			}
		}
		
		if(numberOfColumns != columns.size()) {
			array = null;
			return;
			//throw new SQLException();
			
		}
		
		
		
		try {
			array = files.selectFromTable(table,conditions,columns,operator);
			for(int i=0;i<columns.size();i++) {
				System.out.print(columns.get(i) + "   ");
			}
			System.out.println();
			for(int i=0;i<array.length;i++) {
				for(int j=0;j<columns.size();j++) {
					System.out.print(array[i][j] + "   ");
				}
				System.out.println();
			}
			
		
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	@Override
	public void visit(InsertIntoTable insert) {
		// TODO Auto-generated method stub
		ArrayList<Pair<String, Pair<String, String>>> conditions = new ArrayList<>();
		ArrayList<String> columns = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();	

		// 0 for int, 1 for varchar
		HashMap<String, Integer> check = new HashMap<>();
		ArrayList<String> allColumns = new ArrayList<>();
		HashMap<String, String> xsdMap = null;
		
		String regex = " \\w+ \\bvalues\\b";
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(query);
		if (m.find()) {
			String subRegex = " \\bvalues\\b";
			Pattern p2 = Pattern.compile(subRegex, Pattern.CASE_INSENSITIVE);
			Matcher m2 = p2.matcher(query);
			if (m2.find()) {
				table = query.substring(12, m2.start());
				File tableFile = new File(current.getPath() + System.getProperty("file.separator") + table + ".xml");
				if (!tableFile.exists()) {
					changedRows = -1;
					return;
				}
				xsdMap = files.readXSD(table);
				for (String col : xsdMap.keySet()) {
					allColumns.add(col);
				}
				for (int i = 0; i < allColumns.size(); i++) {
					columns.add(allColumns.get(i));
				}
			}
			// add all columns
		} else {
			table = query.substring(12, query.indexOf('('));
			File tableFile = new File(current.getPath() + System.getProperty("file.separator") + table + ".xml");
			if (!tableFile.exists()) {
				changedRows = -1;
				return;
			}
			xsdMap = files.readXSD(table);
			for (String col : xsdMap.keySet()) {
				allColumns.add(col);
			}
			String column = query.substring(query.indexOf('(') + 1, query.indexOf(')'));
			while (column.contains(",")) {
				columns.add(column.substring(0, column.indexOf(',')));
				column = column.substring(column.indexOf(',') + 2);
			}
			columns.add(column);
		}
		String valuesRegex = " \\bvalues\\b (\\()";
		Pattern valuesPattern = Pattern.compile(valuesRegex, Pattern.CASE_INSENSITIVE);
		Matcher valuesMatcher = valuesPattern.matcher(query);
		if (valuesMatcher.find()) {
			String value = query.substring(valuesMatcher.end(), query.length() - 1);
			while (value.contains(",")) {
				values.add(value.substring(0, value.indexOf(',')));
				value = value.substring(value.indexOf(',') + 2);
			}
			values.add(value);
			if (columns.size() != values.size()) {
				changedRows = -1;
				return;
			}
			for (int i = 0; i < values.size(); i++) {
				String v = values.get(i);
				String k = columns.get(i);
				if (values.get(i).contains("'")) {
					v = v.substring(1, v.length() - 1);
					check.put(k, 1);
				} else {
					check.put(k, 0);
				}
				map.put(k, v);
			}
			int numberOfColumns = 0;
			for (Entry<String, String> entry : xsdMap.entrySet()) {
				for (int i = 0; i < conditions.size(); i++) {
					if (entry.getKey().equalsIgnoreCase(conditions.get(i).getKey())) {
						numberOfColumns++;
					}
				}
			}
			if (numberOfColumns != conditions.size()) {
				changedRows = -1;
				return;
			}
			for (Entry<String, Integer> e : check.entrySet()) {
				for (int i = 0; i < allColumns.size(); i++) {
					if (e.getKey().equalsIgnoreCase(allColumns.get(i))) {
						String columnType = xsdMap.get(allColumns.get(i));
						// System.out.println(columnType);
						String[] columnTypeString = columnType.split(":");
						String type = columnTypeString[1];
						if (e.getValue() == 0 && !type.equals("int")) {
							changedRows = 0;
							return;
						} else if (e.getValue() == 1 && !type.equals("varchar")) {
							changedRows = 0;
							return;
						}
					}
				}
			}
			changedRows = files.insertIntoTable(table, map);
			// changedRows = 1;
		}
	}

	@Override
	public void visit(UpdateTable update) {
		// TODO Auto-generated method stub
		HashMap<String,String> map = new HashMap<>();
		HashMap<String,Integer> check = new HashMap<>();
		ArrayList<Pair<String,Pair<String,String>>> conditions = new ArrayList<>();
		
		
		String regex = " \\bset\\b ";
		Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(query);
		if(m.find()) {
			table = query.substring(7, m.start());
			File tableFile = new File(current.getPath() + System.getProperty("file.separator") + table + ".xml");
			if(!tableFile.exists()) {
				changedRows = -1;
				return;
			}
			
			String where = " \\bwhere\\b ";
			Pattern p1 = Pattern.compile(where,Pattern.CASE_INSENSITIVE);
			Matcher m1 = p1.matcher(query);
			if(m1.find()) {
				String column = query.substring(m.end(),m1.start());
				while(column.contains(",")) {
					String c = column.substring(0, column.indexOf(','));
					String key = c.substring(0, c.indexOf('='));
					String value = c.substring(c.indexOf('=') + 1,c.length() );
					if(value.contains("'")) {
						check.put(key,1);
						value = value.substring(1, value.length()-1); // varchar
					}else {
						check.put(key,0); // int
					}
					map.put(key,value);
					column = column.substring(column.indexOf(',') + 2,column.length());
				}
				String key = column.substring(0, column.indexOf('='));
				String value = column.substring(column.indexOf('=')+1, column.length());
				if(value.contains("'")) {
					check.put(key,1);
					value = value.substring(1, value.length()-1);
				}else {
					check.put(key,0);
				}
				map.put(key,value);
				
				//check data type
				HashMap<String,String> xsdMap = files.readXSD(table);
				ArrayList<String> allColumns = new ArrayList<>();
				for(String col : xsdMap.keySet()) {
					allColumns.add(col);
				}
				for(Entry<String,Integer> it : check.entrySet()) {
					for(int i=0;i<allColumns.size();i++) {
						if(it.getKey().equalsIgnoreCase(allColumns.get(i))) {
							String colType = xsdMap.get(allColumns.get(i));
							String[] split = colType.split(":");
							String type = split[1];
							if(it.getValue() == 0 && !type.equals("int")) {
								changedRows = 0;
								return;
							}else if(it.getValue() == 1 && !type.equals("varchar")) {
								changedRows = 0;
								return;
							}
						}
					}
				}
				
				String regexAndOr = " \\band\\b | \\bor\\b ";
				Pattern p2 = Pattern.compile(regexAndOr,Pattern.CASE_INSENSITIVE);
				Matcher m2 = p2.matcher(query);
				String regexNot = "\\bnot\\b ";
				Pattern p3 = Pattern.compile(regexNot,Pattern.CASE_INSENSITIVE);
				Matcher m3 = p3.matcher(query);
				if(m2.find()) {
					String condition1 = query.substring(m1.end(), m2.start());
					conditions.add(condition(condition1,table));
					String condition2 = query.substring(m2.end(), query.length());
					conditions.add(condition(condition2,table));
					String operator = query.substring(m2.start()+1,m2.end()-1);
					operator = operator.toLowerCase();
					
					if(condition(condition1,table) == null || condition(condition2,table) == null) {
						changedRows = -1;
						return;
					}
					int numberOfColumns = 0;
					for(Entry<String,String> it : xsdMap.entrySet()) {
						for(int i=0;i<conditions.size();i++) {
							if(it.getKey().equalsIgnoreCase(conditions.get(i).getKey())) {
								numberOfColumns++;
							}
						}
					}
					if(numberOfColumns != conditions.size()) {
						changedRows = -1;
						return;
					}
					
					try {
						changedRows += files.updateTable(table,map,conditions,operator);
					} catch (ParserConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else if (m3.find()) {
					String operator = "not";
					String condition = query.substring(m3.end(), query.length());
					conditions.add(condition(condition,table));
					if(condition(condition,table) == null) {
						changedRows = -1;
						return;
					}
					int numberOfColumns = 0;
					for(Entry<String,String> it : xsdMap.entrySet()) {
						for(int i=0;i<conditions.size();i++) {
							if(it.getKey().equalsIgnoreCase(conditions.get(i).getKey())) {
								numberOfColumns++;
							}
						}
					}
					if(numberOfColumns != conditions.size()) {
						changedRows = -1;
						return;
					}
					
					try {
						changedRows += files.updateTable(table, map,conditions, operator);
					} catch (ParserConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else {
					String operator = "noCondition";
					String condition = query.substring(m1.end(), query.length());
					conditions.add(condition(condition,table));
					if(condition(condition,table) == null) {
						changedRows = -1;
						return;
					}
					
					int numberOfColumns = 0;
					for(Entry<String,String> it : xsdMap.entrySet()) {
						for(int i=0;i<conditions.size();i++) {
							if(it.getKey().equalsIgnoreCase(conditions.get(i).getKey())) {
								numberOfColumns++;
							}
						}
					}
					if(numberOfColumns != conditions.size()) {
						changedRows = -1;
						return;
					}
					
					try {
						changedRows = files.updateTable(table, map,conditions, operator);
					} catch (ParserConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}else {
				String operator = "all";
				String column = query.substring(m.end(), query.length());
				while(column.contains(",")) {
					String c = column.substring(0, column.indexOf(','));
					String key = c.substring(0, c.indexOf('='));
					String value = c.substring(c.indexOf('=')+1, c.length());
					if(value.contains("'")) {
						check.put(key,1);
						value = value.substring(1, value.length()-1);
					}else {
						check.put(key,0);
					}
					map.put(key,value);
					column = column.substring(column.indexOf(',')+2, column.length());
				}
				String key = column.substring(0, column.indexOf('='));
				String value = column.substring(column.indexOf('=')+1, column.length());
				if(value.contains("'")) {
					check.put(key,1);
					value = value.substring(1,value.length()-1);
				}else {
					check.put(key,0);
				}
				map.put(key,value);
				
				HashMap<String,String> xsdMap = files.readXSD(table);
				int numberOfColumns = 0;
				for(Entry<String,String> it : xsdMap.entrySet()) {
					for(int i=0;i<conditions.size();i++) {
						if(it.getKey().equalsIgnoreCase(conditions.get(i).getKey())) {
							numberOfColumns++;
						}
					}
				}
				if(numberOfColumns != conditions.size()) {
					changedRows = -1;
					return;
				}
				try {
					changedRows = files.updateTable(table,map, conditions, operator);
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
	}

	@Override
	public void visit(DeleteFromTable delete) {
		// TODO Auto-generated method stub
		ArrayList<Pair<String, Pair<String, String>>> conditions = new ArrayList<>();
	
		String regex = "( )? \\bwhere\\b ";
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(query);
		if (m.find()) {
			table = query.substring(12, m.start());
			File tableFile = new File(current.getPath() + System.getProperty("file.separator") + table + ".xml");
			if (!tableFile.exists()) {
				changedRows = -1;
				
				return;
			}
			String regexOfAndOr = " \\band\\b | \\bor\\b ";
			Pattern p2 = Pattern.compile(regexOfAndOr, Pattern.CASE_INSENSITIVE);
			Matcher m2 = p2.matcher(query);
			String regexNot = "\\bnot\\b ";
			Pattern p3 = Pattern.compile(regexNot, Pattern.CASE_INSENSITIVE);
			Matcher m3 = p3.matcher(query);
			if (m2.find()) {
				String condition1 = query.substring(m.end(), m2.start());
				conditions.add(condition(condition1, table)); // return value
				String condition2 = query.substring(m2.end(), query.length());
				conditions.add(condition(condition2, table)); // return value
				String operator = query.substring(m2.start() + 1, m2.end() - 1);
				operator = operator.toLowerCase();
				// perform delete
				HashMap<String, String> xsdMap = files.readXSD(table);
				int numberOfColumns = 0;
				if(condition(condition1, table)==null||condition(condition2, table)==null) {
					changedRows=-1;
					return;
				}
				for (Entry<String, String> entry : xsdMap.entrySet()) {
					for (int i = 0; i < conditions.size(); i++) {
						if (entry.getKey().equalsIgnoreCase(conditions.get(i).getKey())) {
							numberOfColumns++;
						}
					}
				}
				if (numberOfColumns != conditions.size()) {
					changedRows = -1;
					return;
					
				}
				try {
					changedRows = files.deleteFromTable(table, conditions, operator);
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else if (m3.find()) {
				String operator = "not";
				String condition = query.substring(m3.end(), query.length());
				conditions.add(condition(condition, table)); // return value
				HashMap<String, String> xsdMap = files.readXSD(table);
				if(condition(condition, table)==null) {
					changedRows=-1;
					return;
				}
				int numberOfColumns = 0;
				for (Entry<String, String> entry : xsdMap.entrySet()) {
					for (int i = 0; i < conditions.size(); i++) {
						if (entry.getKey().equalsIgnoreCase(conditions.get(i).getKey())) {
							numberOfColumns++;
						}
					}
				}
				if (numberOfColumns != conditions.size()) {
					changedRows = -1;
					return;
				}
				try {
					changedRows = files.deleteFromTable(table, conditions, operator);
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// perform delete
			} else {
				// without AND,OR, NOT
				String operator = "noCondition";
				String condition = query.substring(m.end(), query.length());
				conditions.add(condition(condition, table)); // return value
				if(condition(condition, table)==null) {
					changedRows=-1;
					return;
				}
				HashMap<String, String> xsdMap = files.readXSD(table);
				int numberOfColumns = 0;
				for (Entry<String, String> entry : xsdMap.entrySet()) {
					for (int i = 0; i < conditions.size(); i++) {
						if (entry.getKey().equalsIgnoreCase(conditions.get(i).getKey())) {
							numberOfColumns++;
						}
					}
				}
				if (numberOfColumns != conditions.size()) {
					changedRows = -1;
					return;
				}
				try {
					changedRows = files.deleteFromTable(table, conditions, operator);
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// perform delete
			}
		} else {
			String operator = "all";
			table = query.substring(12, query.length());
			
			HashMap<String, String> xsdMap = files.readXSD(table);
			int numberOfColumns = 0;
			for (Entry<String, String> entry : xsdMap.entrySet()) {
				for (int i = 0; i < conditions.size(); i++) {
					if (entry.getKey().equalsIgnoreCase(conditions.get(i).getKey())) {
						numberOfColumns++;
					}
				}
			}
			if (numberOfColumns != conditions.size()) {
				changedRows = -1;
				return;
			}
			try {
				changedRows = files.deleteFromTable(table, conditions, operator);
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	
	public Pair<String,Pair<String,String>> condition(String condition, String table){
		String col,val,operator;
		boolean exist = false;
		Pair<String,Pair<String,String>> conditions = null;
		HashMap<String,String> xsdMap = files.readXSD(table);
		ArrayList<String> allColumns = new ArrayList<>();
		for(String column : xsdMap.keySet()) {
			allColumns.add(column);
		}
		if(condition.contains("=")) {
			col = condition.substring(0,condition.indexOf('='));
			val = condition.substring(condition.indexOf('=')+1, condition.length());
			for(String column : xsdMap.keySet()) {
				if(column.equalsIgnoreCase(col)) {
					exist = true;
				}
			}
			if(!exist) {
				try {
					throw new SQLException();
				}catch(SQLException e) {
					e.printStackTrace();
				}
			}else {
				if(val.contains("'")) {
					val = val.substring(1, val.length()-1);
					for(int i=0;i<allColumns.size();i++) {
						if(col.equalsIgnoreCase(allColumns.get(i))) {
							String colType = xsdMap.get(allColumns.get(i));
							String[] split = colType.split(":");
							String type = split[1];
							if(type.equals("varchar")) {
								operator = "==";
								Pair<String,String> pair = new Pair<>(val,operator);
								conditions = new Pair<>(col,pair);
							}else if(type.equals("int")) {
								try {
									throw new SQLException();
								}catch(SQLException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}else {
					for(int i=0;i<allColumns.size();i++) {
						if(col.equalsIgnoreCase(allColumns.get(i))) {
							String colType = xsdMap.get(allColumns.get(i));
							String[] split = colType.split(":");
							String type = split[1];
							if(type.equals("int")) {
								operator = "=";
								Pair<String,String> pair = new Pair<>(val,operator);
								conditions = new Pair<>(col,pair);
							}else if(type.equals("varchar")) {
								try {
									throw new SQLException();
								}catch(SQLException e){
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		}else if(condition.contains(">")) {
			col = condition.substring(0, condition.indexOf('>') - 1);
			val = condition.substring(condition.indexOf('>') + 2, condition.length());
			for(String column : xsdMap.keySet()) {
				if(column.equalsIgnoreCase(col)) {
					exist = true;
				}
			}
			if(!exist) {
				try {
					throw new SQLException();
				}catch(SQLException e) {
					e.printStackTrace();
				}
			}else {
				for(int i=0;i<allColumns.size();i++) {
					if(col.equalsIgnoreCase(allColumns.get(i))) {
						String colType = xsdMap.get(allColumns.get(i));
						String[] split = colType.split(":");
						String type = split[1];
						if(type.equals("int")) {
							operator = ">";
							Pair<String,String> pair = new Pair<>(val,operator);
							conditions = new Pair<>(col,pair);
						}else if(type.equals("varchar")) {
							try {
								throw new SQLException();
							}catch(SQLException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		
		}else if(condition.contains("<")) {
			col = condition.substring(0, condition.indexOf('<')-1);
			val = condition.substring(condition.indexOf('<') + 2, condition.length());
			for(String column : xsdMap.keySet()) {
				if(col.equalsIgnoreCase(column)) {
					exist = true;
				}
			}
			if(!exist) {
				try {
					throw new SQLException();
				}catch(SQLException e){
					e.printStackTrace();
				}
			}else {
				for(int i=0;i<allColumns.size();i++) {
					if(col.equalsIgnoreCase(allColumns.get(i))) {
						String colType = xsdMap.get(allColumns.get(i));
						String[] split = colType.split(":");
						String type = split[1];
						if(type.equals("int")) {
							operator = "<";
							Pair<String,String> pair = new Pair<>(val,operator);
							conditions = new Pair<>(col,pair);
						}else if(type.equals("varchar")) {
							try {
								throw new SQLException();
							}catch(SQLException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			
		}
		return conditions;
	}

}
