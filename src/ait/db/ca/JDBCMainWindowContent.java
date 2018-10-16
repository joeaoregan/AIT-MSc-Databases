package ait.db.ca;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.sql.*;

@SuppressWarnings("serial")
public class JDBCMainWindowContent extends JInternalFrame implements ActionListener {
	// DB Connectivity Attributes
	private Connection con = null;
	private Statement stmt = null;
	private ResultSet rs = null;

	private Container content;

	private JPanel detailsPanel;
	private JPanel exportButtonPanel;
//	private JPanel exportConceptDataPanel;
	private JScrollPane dbContentsPanel;

	private Border lineBorder;

	private JLabel RecordIDLabel = new JLabel("Record ID: (int)           ");
	private JLabel SSIDLabel = new JLabel("SSID: (Decimal)");
	private JLabel dateLabel = new JLabel("Date: (yyyy-mm-dd hh-mm-ss)");
	private JLabel RSSLabel = new JLabel("RSS:        ");
	private JLabel macLossLabel = new JLabel("MAC Loss:                 ");
	private JLabel delayLabel = new JLabel("Delay:               ");
	private JLabel channelLabel = new JLabel("Channel:      ");
	private JLabel secLabel = new JLabel("Security: (String)");
	private JLabel swLabel = new JLabel("Sw Version: (String)");
	private JLabel gpsLongLabel = new JLabel("GPS Longitude: (float)");
	private JLabel gpsLatLabel = new JLabel("GPS Latitude: (float)");

	private JTextField RecordIDTF = new JTextField(10);
	private JTextField SSIDTF = new JTextField(10);
	private JTextField dateTF = new JTextField(10);
	private JTextField RSSTF = new JTextField(10);
	private JTextField macLossTF = new JTextField(10);
	private JTextField delayTF = new JTextField(10);
	private JTextField channelTF = new JTextField(10);
	private JTextField secTF = new JTextField(10);
	private JTextField swTF = new JTextField(10);
	private JTextField gpsLongTF = new JTextField(10);
	private JTextField gpsLatTF = new JTextField(10);

	private static QueryTableModel TableModel = new QueryTableModel();

	// Add the models to JTabels
	private JTable TableofDBContents = new JTable(TableModel);

	// Buttons for inserting, and updating members
	// also a clear button to clear details panel
	private JButton updateButton = new JButton("Update");
	private JButton insertButton = new JButton("Insert");
	private JButton exportButton = new JButton("Export");
	private JButton deleteButton = new JButton("Delete");
	private JButton clearButton = new JButton("Clear");

	private JButton last3LossRates = new JButton("3 Previous Loss Rates per AP");
	private JTextField last3LossRatesTF = new JTextField(12);
	private JButton avgofRSS = new JButton("Avg Loss for last 3 Rec per AP");
	private JTextField avgofRSSTF = new JTextField(12);
	private JButton overLappingAP = new JButton("AP Location");
	private JButton overLappingChannels = new JButton("AP Channel");

	// JOR
//	public void addStuffToDetailsPanel(Component[] components) {
//		for (Component component : components)
//			detailsPanel.add(component);
//	}
//
//	public void addStuffToExportButtonPanel(Component[] components) {
//		for (Component component : components)
//			exportButtonPanel.add(component);
//	}
	public void addStuffToJPanel(JPanel panel, Component[] components) {
		for (Component component : components)
			panel.add(component);
	}
	public void addStuffToContainer(Container container, Component[] components) {
		for (Component component : components)
			container.add(component);
	}
	public void setButtonSizeAndLocation(JButton button, int w, int h, int x, int y) {
		button.setSize(w, h);
		button.setLocation(x, y);
		button.addActionListener(this);
	}

	public JDBCMainWindowContent(String aTitle) {
		// setting up the GUI
		super(aTitle, false, false, false, false);
		setEnabled(true);

		initiate_db_conn();
		// add the 'main' panel to the Internal Frame
		content = getContentPane();
		content.setLayout(null);
		content.setBackground(Color.lightGray);
		lineBorder = BorderFactory.createEtchedBorder(15, Color.red, Color.black);

		// setup details panel and add the components to it
		detailsPanel = new JPanel();
		detailsPanel.setLayout(new GridLayout(11, 2));
		detailsPanel.setBackground(Color.lightGray);
		detailsPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "AP Details"));

//		detailsPanel.add(RecordIDLabel);
//		detailsPanel.add(RecordIDTF);
//		detailsPanel.add(SSIDLabel);
//		detailsPanel.add(SSIDTF);
//		detailsPanel.add(dateLabel);
//		detailsPanel.add(dateTF);
//		detailsPanel.add(RSSLabel);
//		detailsPanel.add(RSSTF);
//		detailsPanel.add(macLossLabel);
//		detailsPanel.add(macLossTF);
//		detailsPanel.add(delayLabel);
//		detailsPanel.add(delayTF);
//		detailsPanel.add(channelLabel);
//		detailsPanel.add(channelTF);
//		detailsPanel.add(secLabel);
//		detailsPanel.add(secTF);
//		detailsPanel.add(swLabel);
//		detailsPanel.add(swTF);
//		detailsPanel.add(gpsLongLabel);
//		detailsPanel.add(gpsLongTF);
//		detailsPanel.add(gpsLatLabel);
//		detailsPanel.add(gpsLatTF);
//		addStuffToDetailsPanel(
		addStuffToJPanel(detailsPanel, new Component[] { RecordIDLabel, RecordIDTF, SSIDLabel, SSIDTF, dateLabel, dateTF,
				RSSLabel, RSSTF, macLossLabel, macLossTF, delayLabel, delayTF, channelLabel, channelTF, secLabel, secTF,
				swLabel, swTF, gpsLongLabel, gpsLongTF, gpsLatLabel, gpsLatTF });

		// setup details panel and add the components to it
		exportButtonPanel = new JPanel();
		exportButtonPanel.setLayout(new GridLayout(3, 2));
		exportButtonPanel.setBackground(Color.lightGray);
		exportButtonPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Export Data"));
		// exportButtonPanel.add(last3LossRates);
		// exportButtonPanel.add(last3LossRatesTF);
		// exportButtonPanel.add(avgofRSS);
		// exportButtonPanel.add(avgofRSSTF);
		// exportButtonPanel.add(overLappingAP);
		// exportButtonPanel.add(overLappingChannels);
		//addStuffToExportButtonPanel(
		addStuffToJPanel(exportButtonPanel, new Component[] { last3LossRates, last3LossRatesTF, avgofRSS, avgofRSSTF,
				overLappingAP, overLappingChannels });
		exportButtonPanel.setSize(500, 200);
		exportButtonPanel.setLocation(3, 300);
		//content.add(exportButtonPanel);

//		insertButton.setSize(100, 30);
//		updateButton.setSize(100, 30);
//		exportButton.setSize(100, 30);
//		deleteButton.setSize(100, 30);
//		clearButton.setSize(100, 30);
//
//		insertButton.setLocation(370, 10);
//		updateButton.setLocation(370, 110);
//		exportButton.setLocation(370, 160);
//		deleteButton.setLocation(370, 60);
//		clearButton.setLocation(370, 210);
		setButtonSizeAndLocation(insertButton,100,30,370,10);
		setButtonSizeAndLocation(updateButton,100,30,370,110);
		setButtonSizeAndLocation(exportButton,100,30,370,160);
		setButtonSizeAndLocation(deleteButton,100,30,370,60);
		setButtonSizeAndLocation(clearButton,100,30,370,210);

//		insertButton.addActionListener(this);
//		updateButton.addActionListener(this);
//		exportButton.addActionListener(this);
//		deleteButton.addActionListener(this);
//		clearButton.addActionListener(this);

//		content.add(insertButton);
//		content.add(updateButton);
//		content.add(exportButton);
//		content.add(deleteButton);
//		content.add(clearButton);
		addStuffToContainer(content, new Component[] {exportButtonPanel,insertButton,updateButton,exportButton,deleteButton,clearButton});

		TableofDBContents.setPreferredScrollableViewportSize(new Dimension(900, 300));

		dbContentsPanel = new JScrollPane(TableofDBContents, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		dbContentsPanel.setBackground(Color.lightGray);
		dbContentsPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Database Content"));

		detailsPanel.setSize(360, 300);
		detailsPanel.setLocation(3, 0);
		dbContentsPanel.setSize(700, 300);
		dbContentsPanel.setLocation(477, 0);

		content.add(detailsPanel);
		content.add(dbContentsPanel);

		setSize(982, 645);
		setVisible(true);

		TableModel.refreshFromDB(stmt);
	}

	public void initiate_db_conn() {
		try {
			// Load the JConnector Driver
			Class.forName("com.mysql.jdbc.Driver");
			// Specify the DB Name
			// String url = "jdbc:mysql://localhost:3306/Assign";
			String url = "jdbc:mysql://localhost:3306/Assign?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

			// Connect to DB using DB URL, Username and password
			con = DriverManager.getConnection(url, "root", "admin");
			// Create a generic statement which is passed to the TestInternalFrame1
			stmt = con.createStatement();
		} catch (Exception e) {
			System.out.println("Error: Failed to connect to database\n" + e.getMessage());
		}
	}

	// event handling for members desktop
	public void actionPerformed(ActionEvent e) {
		Object target = e.getSource();
		if (target == clearButton) {
			RecordIDTF.setText("");
			SSIDTF.setText("");
			dateTF.setText("");
			RSSTF.setText("");
			macLossTF.setText("");
			delayTF.setText("");
			channelTF.setText("");
			secTF.setText("");
			swTF.setText("");
			gpsLongTF.setText("");
			gpsLatTF.setText("");

		}

		if (target == insertButton) {
			try {
				String updateTemp = "INSERT INTO APPERFDATA VALUES ('" + RecordIDTF.getText() + "','" + SSIDTF.getText()
						+ "','" + dateTF.getText() + "','" + RSSTF.getText() + "','" + macLossTF.getText() + "','"
						+ delayTF.getText() + "','" + channelTF.getText() + "','" + secTF.getText() + "','"
						+ swTF.getText() + "','" + gpsLongTF.getText() + "','" + gpsLatTF.getText() + "');";

				stmt.executeUpdate(updateTemp);

			} catch (SQLException sqle) {
				System.err.println("Error with members insert:\n" + sqle.toString());
			} finally {
				TableModel.refreshFromDB(stmt);
			}
		}
		if (target == deleteButton) {

			try {
				String updateTemp = "DELETE FROM APPERFDATA WHERE Rec_id = " + RecordIDTF.getText() + ";";
				stmt.executeUpdate(updateTemp);

			} catch (SQLException sqle) {
				System.err.println("Error with delete:\n" + sqle.toString());
			} finally {
				TableModel.refreshFromDB(stmt);
			}
		}
		if (target == updateButton) {
			try {
				String updateTemp = "UPDATE APPERFDATA SET SSID = '" + SSIDTF.getText() +

						"', Date = " + "'" + dateTF.getText() + "'" + ", RSS = " + RSSTF.getText() + ", MAC_Loss = "
						+ macLossTF.getText() + ", Delay = " + delayTF.getText() + ", Channel = " + channelTF.getText()
						+ ", Sec = " + "'" + secTF.getText() + "'" + ", Software_Version = " + swTF.getText()
						+ ", GPS_Long = " + gpsLongTF.getText() + ", GPS_Lat = " + gpsLatTF.getText()
						+ " where Rec_id = " + RecordIDTF.getText();

				System.out.println(updateTemp);
				stmt.executeUpdate(updateTemp);
				// these lines do nothing but the table updates when we access the db.
				rs = stmt.executeQuery("SELECT * from APPERFDATA ");
				rs.next();
				rs.close();
			} catch (SQLException sqle) {
				System.err.println("Error with members insert:\n" + sqle.toString());
			} finally {
				TableModel.refreshFromDB(stmt);
			}
		}
	}

}
