package ait.db.cell;

// Provide the relevant JDBC Functionality
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import java.sql.PreparedStatement;

// Provide the Java Graphical User Interface functionality
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Used for the pie chart
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

// Used for exporting the data to CSV format
import java.io.FileWriter;
import java.io.PrintWriter;

// JFrame is a type of window in Java
// ActionListener: concerned with button clicks, tells Jframe when buttons clicked
class JDBCCellExample extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;	// default
	private JButton exportButton = new JButton("Export All Data"); // Create a button
	private JButton chartButton = new JButton("Chart Network Statistics");
	private JButton numRecForCellButton = new JButton("Number of Records for Cell : ");
	private JButton recordsAfterButton = new JButton("List Records After :");
	private JTextField cellIDTF = new JTextField(12);
	private JTextField timeTF = new JTextField(12);
	private Connection con = null; // stores details of connection to database
	private Statement stmt = null; // represent the SQL query
	private PreparedStatement ps = null;

	// Constructor
	public JDBCCellExample(String str) {
		super(str); // call parent class constructor, set the title
		getContentPane().setLayout(new GridLayout(3, 2)); // 3 rows, 2 cols
		initDBConnection();
		getContentPane().add(exportButton); // add buttons to the frame
		getContentPane().add(chartButton);
		getContentPane().add(numRecForCellButton);
		getContentPane().add(cellIDTF);
		getContentPane().add(recordsAfterButton);
		getContentPane().add(timeTF);
		exportButton.addActionListener(this); // add action listeners to the buttons, to monitor button clicks
		chartButton.addActionListener(this);
		numRecForCellButton.addActionListener(this);
		recordsAfterButton.addActionListener(this);
		setSize(500, 200); // Dimensions: 500 x 200 pixels
		setVisible(true); // Frame is visible to user
	}

	private void initDBConnection() {
		try {
			// Class.forName("com.mysql.jdbc.Driver");
			// String url = "jdbc:mysql://localhost:3306/pm2";
			// String url = "jdbc:mysql://127.0.0.1:3306/pm";
			// jdbc:mysql://<localhost>:<port>/<database>?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

			// Newer connection string needed for Workbench 8.0
			String url = "jdbc:mysql://localhost:3306/pm2?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

			con = DriverManager.getConnection(url, "root", "admin"); // create a connection to the db
			stmt = con.createStatement(); // statement object to run db queries
			ps = con.prepareStatement("select count(*) from pm.perf where Cell_ID =  ?");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.print("Failed to initialise DB Connection");
		}
	}

	// write to CSV file
	private void writeToFile(ResultSet rs) {
		try {
			FileWriter outputFile = new FileWriter("CellOutput.csv"); // create CSV file
			PrintWriter printWriter = new PrintWriter(outputFile);
			ResultSetMetaData rsmd = rs.getMetaData(); // extract meta data from result set
			int numColumns = rsmd.getColumnCount(); // get the number of columns in the result set

			for (int i = 0; i < numColumns; i++) {
				printWriter.print(rsmd.getColumnLabel(i + 1) + ","); // write the column headings to file
			}
			printWriter.print("\n"); // add new line
			while (rs.next()) { // get next row in result set, return false if result set empty
				for (int i = 0; i < numColumns; i++) {
					printWriter.print(rs.getString(i + 1) + ","); // cells start at 1 (not 0), separate with ","
				}
				printWriter.print("\n");
				printWriter.flush(); // immediately write to file, flushing the buffer
			}
			printWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Takes result set and title parameters
	public void pieGraph(ResultSet rs, String title) {
		try {
			DefaultPieDataset dataset = new DefaultPieDataset();

			while (rs.next()) {
				String category = rs.getString(1);
				String value = rs.getString(2);
				dataset.setValue(category + " " + value, new Double(value));
			}
			// JFreeChart chart = ChartFactory.createPieChart(title, dataset, false, true,
			// true); // No legend displayed
			JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, true); // display legend

			ChartFrame frame = new ChartFrame(title, chart);
			chart.setBackgroundPaint(Color.WHITE);
			frame.pack();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ActionEvent e identifies the button that was clicked
	public void actionPerformed(ActionEvent e) {
		Object target = e.getSource(); // determine the button clicked
		ResultSet rs = null; // store results returned from the db
		String cmd = null; // store the SQL query
		try {
			// What to do when each button is pressed
			if (target.equals(exportButton)) {
				// set cmd to write out all the table data to the csv
				cmd = "select * from pm.perf;";
				rs = stmt.executeQuery(cmd); // run the SQL statement
				writeToFile(rs); // pass result set to method
			} else if (target.equals(chartButton)) {
				cmd = "select Record_Description, sum(value)from pm.perf group by Record_Description;";
				rs = stmt.executeQuery(cmd); // run the SQL statement
				pieGraph(rs, "Network Statistics");
			} else if (target.equals(numRecForCellButton)) {
				String idOfCell = cellIDTF.getText(); // cell id text field
				// Using the Prepared statement
				ps.setString(1, idOfCell); // embed into command
				rs = ps.executeQuery();
				// Without a Prepared Statement we could have used the following two lines of
				// code
				/*
				 * cmd="select count(*) from pm.perf where Cell_ID = "+idOfCell+";"; rs=
				 * stmt.executeQuery(cmd);
				 */
				writeToFile(rs);

			} else if (target.equals(recordsAfterButton)) {
				// set cmd here
				String time = timeTF.getText();
				cmd="select * from  pm.perf where Date > '"+time+"';";	
				rs= stmt.executeQuery(cmd); 
				writeToFile(rs);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public static void main(String args[]) {
		new JDBCCellExample("Cell Performance Data Export");
	}
}
