package ait.db.cell;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.PrintWriter;

class JDBCCellExample extends JFrame implements ActionListener {

	private JButton exportButton = new JButton("Export All Data");
	private JButton chartButton = new JButton("Chart Network Statistics");
	private JButton numRecForCellButton = new JButton("Number of Records for Cell : ");
	private JButton recordsAfterButton = new JButton("List Records After :");
	private JTextField cellIDTF = new JTextField(12);
	private JTextField timeTF = new JTextField(12);
	private Connection con = null;
	private Statement stmt = null;
	private PreparedStatement ps = null;

	public JDBCCellExample(String str) {
		super(str);
		getContentPane().setLayout(new GridLayout(3, 2));
		initDBConnection();
		getContentPane().add(exportButton);
		getContentPane().add(chartButton);
		getContentPane().add(numRecForCellButton);
		getContentPane().add(cellIDTF);
		getContentPane().add(recordsAfterButton);
		getContentPane().add(timeTF);
		exportButton.addActionListener(this);
		chartButton.addActionListener(this);
		numRecForCellButton.addActionListener(this);
		recordsAfterButton.addActionListener(this);
		setSize(500, 200);
		setVisible(true);
	}

	private void initDBConnection() {
		try {
			// Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/pm";
			con = DriverManager.getConnection(url, "root", "admin");
			stmt = con.createStatement();
			ps = con.prepareStatement("select count(*) from pm.perf where Cell_ID =  ?");
		} catch (Exception e) {
			System.out.print("Failed to initialise DB Connection");
		}
	}

	private void writeToFile(ResultSet rs) {
		try {
			FileWriter outputFile = new FileWriter("CellOutput.csv");
			PrintWriter printWriter = new PrintWriter(outputFile);
			ResultSetMetaData rsmd = rs.getMetaData();
			int numColumns = rsmd.getColumnCount();

			for (int i = 0; i < numColumns; i++) {
				printWriter.print(rsmd.getColumnLabel(i + 1) + ",");
			}
			printWriter.print("\n");
			while (rs.next()) {
				for (int i = 0; i < numColumns; i++) {
					printWriter.print(rs.getString(i + 1) + ",");
				}
				printWriter.print("\n");
				printWriter.flush();
			}
			printWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void pieGraph(ResultSet rs, String title) {
		try {
			DefaultPieDataset dataset = new DefaultPieDataset();

			while (rs.next()) {
				String category = rs.getString(1);
				String value = rs.getString(2);
				dataset.setValue(category + " " + value, new Double(value));
			}
			JFreeChart chart = ChartFactory.createPieChart(title, dataset, false, true, true);

			ChartFrame frame = new ChartFrame(title, chart);
			chart.setBackgroundPaint(Color.WHITE);
			frame.pack();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e) {
		Object target = e.getSource();
		ResultSet rs = null;
		String cmd = null;
		try {
			if (target.equals(exportButton)) {
				// set cmd to write out all the table data to the csv
				// select * from pm.
			} else if (target.equals(chartButton)) {
				cmd = "select Record_Description, sum(value)from pm.perf group by Record_Description;";
				rs = stmt.executeQuery(cmd);
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
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public static void main(String args[]) {
		new JDBCCellExample("Cell Performance Data Export");
	}
}
