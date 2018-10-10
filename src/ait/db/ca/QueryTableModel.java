package ait.db.ca;

//import javax.swing.*;
import javax.swing.table.*;
//import java.awt.event.*;
//import java.awt.*;
import java.sql.*;
//import java.io.*;
import java.util.*;

@SuppressWarnings("serial")
class QueryTableModel extends AbstractTableModel {
	Vector<String[]> modelData; // will hold String[] objects
	int colCount;
	String[] headers = new String[0];
	Connection con;
	Statement stmt = null;
	String[] record;
	ResultSet rs = null;

	public QueryTableModel() {
		modelData = new Vector<String[]>();
	}// end constructor QueryTableModel

	public String getColumnName(int i) {
		return headers[i];
	}

	public int getColumnCount() {
		return colCount;
	}

	public int getRowCount() {
		return modelData.size();
	}

	public Object getValueAt(int row, int col) {
		return ((String[]) modelData.elementAt(row))[col];
	}

	public void refreshFromDB(Statement stmt1) {
		// modelData is the data stored by the table
		// when set query is called the data from the
		// DB is queried using “SELECT * FROM myInfo”
		// and the data from the result set is copied
		// into the modelData. Every time refreshFromDB is
		// called the DB is queried and a new
		// modelData is created

		modelData = new Vector<String[]>();
		stmt = stmt1;
		try {
			// Execute the query and store the result set and its metadata
			rs = stmt.executeQuery("SELECT * FROM APPERFDATA");
			ResultSetMetaData meta = rs.getMetaData();

			// to get the number of columns
			colCount = meta.getColumnCount();
			// Now must rebuild the headers array with the new column names
			headers = new String[colCount];

			for (int h = 0; h < colCount; h++) {
				headers[h] = meta.getColumnName(h + 1);
			} // end for loop

			// fill the cache with the records from the query, ie get all the rows

			while (rs.next()) {
				record = new String[colCount];
				for (int i = 0; i < colCount; i++) {
					record[i] = rs.getString(i + 1);
				} // end for loop
				modelData.addElement(record);
			} // end while loop
			fireTableChanged(null); // Tell the listeners a new table has arrived.
		} // end try clause
		catch (Exception e) {
			System.out.println("Error with refreshFromDB Method\n" + e.toString());
		} // end catch clause to query table
	}// end refreshFromDB method
}// end class QueryTableModel