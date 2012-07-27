package ca.ubc.cs304.main;

// File: MvbView.java

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import ca.ubc.cs304.tables.BranchController;

/*
 * MvbView allows a user to view and manipulate the branch table.
 * Additional functionality may be added in the future, such as
 * viewing and manipulating the driver, license, and exam tables.
 */
public class MvbView extends JFrame {
	// initial position of the main frame
	private int framePositionX;
	private int framePositionY;

	// initial size of main frame
	private Rectangle frameBounds = null;

	// the status text area for displaying error messages
	private JTextArea statusField = new JTextArea(5, 0);

	// the scrollpane that will hold the table of database data
	private JScrollPane tableScrPane = new JScrollPane();

	// the branch admin menu
	private JMenu branchAdmin;

	// the customer admin menu
	private JMenu custAdmin;

	/*
	 * Default constructor. Constructs the main window.
	 */
	public MvbView() {
		// should call the constructor of the superclass first
		super("Motor Vehicle Branch Administration");
		setSize(650, 450);

		// the content pane;
		// components will be spaced vertically 10 pixels apart
		JPanel contentPane = new JPanel(new BorderLayout(0, 10));
		setContentPane(contentPane);

		// leave some space around the content pane
		contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// setup the menubar
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		// indent first menu
		menuBar.add(Box.createRigidArea(new Dimension(10, 0)));

		// sets up the branch administration menu and adds it to the
		// menu bar
		setupBranchAdminMenu(menuBar);

		// sets up the customer administration menu and adds it to the
		// menu bar
		setupCustAdminMenu(menuBar);

		/*// Setup some other menus. You may be asked to add functionality
		// to these menus in an assignment.
		JMenu menu = new JMenu("Customer Admin");
		menu.setMnemonic(KeyEvent.VK_D);
		menuBar.add(menu);
		menu = new JMenu("License Admin");
		menu.setMnemonic(KeyEvent.VK_L);
		menuBar.add(menu);
		menu = new JMenu("Exam Admin");
		menu.setMnemonic(KeyEvent.VK_E);
		menuBar.add(menu);
		*/

		// the scrollpane for the status text field
		JScrollPane statusScrPane = new JScrollPane(statusField);
		statusScrPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		statusScrPane.setBorder(BorderFactory.createLoweredBevelBorder());

		// set status field properties
		statusField.setEditable(false);
		statusField.setLineWrap(true);
		statusField.setWrapStyleWord(true);

		// add the panes to the content pane
		contentPane.add(tableScrPane, BorderLayout.CENTER);
		contentPane.add(statusScrPane, BorderLayout.NORTH);

		// center the main window
		Dimension screenSize = getToolkit().getScreenSize();
		frameBounds = getBounds();
		framePositionX = (screenSize.width - frameBounds.width) / 2;
		framePositionY = (screenSize.height - frameBounds.height) / 2;
		setLocation(framePositionX, framePositionY);

		// anonymous inner class to terminate program
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	/*
	 * Adds menu items to the Branch Admin menu and then adds the menu to the
	 * menubar
	 */
	private void setupBranchAdminMenu(JMenuBar mb) {
		branchAdmin = new JMenu("Branch Admin");

		// when alt-b is pressed on the keyboard, the menu will appear
		branchAdmin.setMnemonic(KeyEvent.VK_B);

		createMenuItem(branchAdmin, "Insert Branch...", KeyEvent.VK_I,
				"Insert Branch");

		createMenuItem(branchAdmin, "Update Branch Name...", KeyEvent.VK_U,
				"Update Branch");

		createMenuItem(branchAdmin, "Delete Branch...", KeyEvent.VK_D,
				"Delete Branch");

		JMenuItem menuItem = createMenuItem(branchAdmin, "Show All Branches",
				KeyEvent.VK_S, "Show Branch");
		// setup a short cut key for this menu item
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B,
				ActionEvent.CTRL_MASK));

		createMenuItem(branchAdmin, "Edit All Branches", KeyEvent.VK_E,
				"Edit Branch");

		mb.add(branchAdmin);
	}

	/*
	 * Adds menu items to the Branch Admin menu and then adds the menu to the
	 * menubar
	 */
	private void setupCustAdminMenu(JMenuBar mb) {
		custAdmin = new JMenu("Customer Admin");

		// when alt-b is pressed on the keyboard, the menu will appear
		custAdmin.setMnemonic(KeyEvent.VK_D);

		createMenuItem(custAdmin, "Insert Customer...", KeyEvent.VK_I,
				"Insert Customer");

		createMenuItem(custAdmin, "Delete Customer...", KeyEvent.VK_D,
				"Delete Customer");

		JMenuItem menuItem = createMenuItem(custAdmin, "Show All Customers",
				KeyEvent.VK_S, "Show Customer");
		// setup a short cut key for this menu item
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B,
				ActionEvent.CTRL_MASK));


		mb.add(custAdmin);
	}

	/*
	 * Creates a menu item and adds it to the given menu. If the menu item has
	 * no mnemonic, set mnemonicKey to a negative integer. If it has no action
	 * command, set actionCommand to the empty string "". By setting the menu
	 * item's action command, the event handler can determine which menu item
	 * was selected by the user. This method returns the menu item.
	 */
	private JMenuItem createMenuItem(JMenu menu, String label, int mnemonicKey,
			String actionCommand) {
		JMenuItem menuItem = new JMenuItem(label);

		if (mnemonicKey > 0) {
			menuItem.setMnemonic(mnemonicKey);
		}

		if (actionCommand.length() > 0) {
			menuItem.setActionCommand(actionCommand);
		}

		menu.add(menuItem);

		return menuItem;
	}

	/*
	 * Places the given window approximately at the center of the screen
	 */
	public void centerWindow(Window w) {
		Rectangle winBounds = w.getBounds();
		w.setLocation(framePositionX + (frameBounds.width - winBounds.width)
				/ 2, framePositionY + (frameBounds.height - winBounds.height)
				/ 2);
	}

	/*
	 * This method adds the given string to the status text area
	 */
	public void updateStatusBar(String s) {
		// trim() removes whitespace and control characters at both ends of the
		// string
		statusField.append(s.trim() + "\n");

		// This informs the scroll pane to update itself and its scroll bars.
		// The scroll pane does not always automatically scroll to the message
		// that was
		// just added to the text area. This line guarantees that it does.
		statusField.revalidate();
	}

	/*
	 * This method adds the given JTable into tableScrPane
	 */
	public void addTable(JTable data) {
		tableScrPane.setViewportView(data);
	}

	/*
	 * This method registers the controllers for all items in each menu. This
	 * method should only be executed once.
	 */
	public void registerControllers() {
		JMenuItem menuItem;

		// BranchController handles events on the branch admin menu items (i.e.
		// when they are clicked)
		BranchController bc = new BranchController(this);

		for (int i = 0; i < branchAdmin.getItemCount(); i++) {
			menuItem = branchAdmin.getItem(i);
			menuItem.addActionListener(bc);
		}
	}

	public static void main(String[] args) {
		MvbView mvb = new MvbView();

		// we will not call pack() on the main frame
		// because the size set by setSize() will be ignored
		mvb.setVisible(true);

		// create the login window
		LoginWindow lw = new LoginWindow(mvb);

		lw.addWindowListener(new ControllerRegister(mvb));

		// pack() has to be called before centerWindow()
		// and setVisible()
		lw.pack();

		mvb.centerWindow(lw);

		lw.setVisible(true);
	}
}

/*
 * Event handler for login window. After the user logs in (after login window
 * closes), the controllers that handle events on the menu items are created.
 * The controllers cannot be created before the user logs in because the
 * database connection is not valid at that time. The models that are created by
 * the controllers require a valid database connection.
 */
class ControllerRegister extends WindowAdapter {
	private MvbView mvb;

	public ControllerRegister(MvbView mvb) {
		this.mvb = mvb;
	}

	public void windowClosed(WindowEvent e) {
		mvb.registerControllers();
	}
}
