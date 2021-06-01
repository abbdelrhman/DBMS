package eg.edu.alexu.csd.oop.db;

import eg.edu.alexu.csd.oop.db.classes.CreateDatabase;
import eg.edu.alexu.csd.oop.db.classes.CreateTable;
import eg.edu.alexu.csd.oop.db.classes.DeleteFromTable;
import eg.edu.alexu.csd.oop.db.classes.DropDatabase;
import eg.edu.alexu.csd.oop.db.classes.DropTable;
import eg.edu.alexu.csd.oop.db.classes.InsertIntoTable;
import eg.edu.alexu.csd.oop.db.classes.SelectFromTable;
import eg.edu.alexu.csd.oop.db.classes.UpdateTable;


/*
 * ID : 4 - 36 - 55 - 56
 */

public interface Visitor {
	public void visit(CreateDatabase createddb);
	public void visit(CreateTable createTable);
	public void visit(DropDatabase dropdb);
	public void visit(DropTable dropTable);
	public void visit(SelectFromTable select);
	public void visit(InsertIntoTable insert);
	public void visit(UpdateTable update);
	public void visit(DeleteFromTable delete);
}
