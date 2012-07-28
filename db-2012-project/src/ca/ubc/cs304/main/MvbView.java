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

	// The top level admin menu
	private JMenu admin;
	
	// the admin menus
	private JMenu custAdmin;
	private JMenu itemAdmin;
	private JMenu hasSongAdmin;
	private JMenu leadSingerAdmin;
	private JMenu purchaseItemAdmin;
	private JMenu purchaseAdmin;
	private JMenu returnItemAdmin;
	private JMenu returnAdmin;
	private JMenu shipItemAdmin;
	private JMenu shipmentAdmin;

	/*
	 * Default constructor. Constructs the main window.
	 */
	public MvbView() {
		// should call the constructor of the superclass first
		super("Allegro Music Store");
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

		// sets up the administration menus and adds them to the menu bar
		setupBranchAdminMenu(menuBar);
		setupAdmins(menuBar);

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

	private void setupAdmins(JMenuBar mb) {
		admin = new JMenu("Admin");
		
		admin.setMnemonic(KeyEvent.VK_A);
		mb.add(admin);
		setupCustAdminMenu(admin);
		setupHasSongAdminMenu(admin);
		setupItemAdminMenu(admin);
		setupLeadSingerAdminMenu(admin);
		setupPurchaseItemAdminMenu(admin);
		setupPurchaseAdminMenu(admin);
		setupReturnItemAdminMenu(admin);
		setupReturnAdminMenu(admin);
		setupShipItemAdminMenu(admin);
		setupShipmentAdminMenu(admin);
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
	 * Adds menu items to the Customer menu and then adds the menu to the
	 * menubar
	 */
	private void setupCustAdminMenu(JMenu admin) {
		custAdmin = new JMenu("Customer Admin");

		// when c is pressed on the keyboard, the menu will appear
		custAdmin.setMnemonic(KeyEvent.VK_C);

		createMenuItem(custAdmin, "Insert Customer...", KeyEvent.VK_I,
				"Insert Customer");

		createMenuItem(custAdmin, "Delete Customer...", KeyEvent.VK_D,
				"Delete Customer");

		JMenuItem menuItem = createMenuItem(custAdmin, "Show All Customers",
				KeyEvent.VK_S, "Show Customer");
		// setup a short cut key for this menu item
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				ActionEvent.CTRL_MASK));

		admin.add(custAdmin);
	}

	/*
<<<<<<< HEAD
	 * Adds menu items to the HasSong menu and then adds the menu to the
	 * menubar
	 */
	private void setupHasSongAdminMenu(JMenu admin) {
		hasSongAdmin = new JMenu("HasSong Admin");

		// when h is pressed on the keyboard, the menu will appear
		hasSongAdmin.setMnemonic(KeyEvent.VK_H);

		createMenuItem(hasSongAdmin, "Insert HasSong...", KeyEvent.VK_I,
				"Insert HasSong");

		createMenuItem(hasSongAdmin, "Delete HasSong...", KeyEvent.VK_D,
				"Delete HasSong");

		JMenuItem menuItem = createMenuItem(hasSongAdmin, "Show All HasSongs",
				KeyEvent.VK_S, "Show HasSong");
		// setup a short cut key for this menu item
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,
				ActionEvent.CTRL_MASK));

		admin.add(hasSongAdmin);
	}
	
	/*
	 * Adds menu items to the Item menu and then adds the menu to the
	 * menubar
	 */
	private void setupItemAdminMenu(JMenu admin) {
		itemAdmin = new JMenu("Item Admin");

		// when i is pressed on the keyboard, the menu will appear
		itemAdmin.setMnemonic(KeyEvent.VK_I);

		createMenuItem(itemAdmin, "Insert Item...", KeyEvent.VK_I,
				"Insert Item");

		createMenuItem(itemAdmin, "Delete Item...", KeyEvent.VK_D,
				"Delete Item");

		JMenuItem menuItem = createMenuItem(itemAdmin, "Show All Items",
				KeyEvent.VK_S, "Show Item");
		// setup a short cut key for this menu item
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
				ActionEvent.CTRL_MASK));

		admin.add(itemAdmin);
	}
	
	/*
	 * Adds menu items to the LeadSinger menu and then adds the menu to the
	 * menubar
	 */
	private void setupLeadSingerAdminMenu(JMenu admin) {
		leadSingerAdmin = new JMenu("LeadSinger Admin");

		// when l is pressed on the keyboard, the menu will appear
		leadSingerAdmin.setMnemonic(KeyEvent.VK_L);

		createMenuItem(leadSingerAdmin, "Insert LeadSinger...", KeyEvent.VK_I,
				"Insert LeadSinger");

		createMenuItem(leadSingerAdmin, "Delete LeadSinger...", KeyEvent.VK_D,
				"Delete LeadSinger");

		JMenuItem menuItem = createMenuItem(leadSingerAdmin, "Show All LeadSingers",
				KeyEvent.VK_S, "Show LeadSinger");
		// setup a short cut key for this menu item
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
				ActionEvent.CTRL_MASK));

		admin.add(leadSingerAdmin);
	}
	
	/*
	 * Adds menu items to the PurchaseItem menu and then adds the menu to the
	 * menubar
	 */
	private void setupPurchaseItemAdminMenu(JMenu admin) {
		purchaseItemAdmin = new JMenu("PurchaseItem Admin");

		// when u is pressed on the keyboard, the menu will appear
		purchaseItemAdmin.setMnemonic(KeyEvent.VK_U);

		createMenuItem(purchaseItemAdmin, "Insert PurchaseItem...", KeyEvent.VK_I,
				"Insert PurchaseItem");

		createMenuItem(purchaseItemAdmin, "Delete PurchaseItem...", KeyEvent.VK_D,
				"Delete PurchaseItem");

		JMenuItem menuItem = createMenuItem(purchaseItemAdmin, "Show All PurchaseItems",
				KeyEvent.VK_S, "Show PurchaseItem");
		// setup a short cut key for this menu item
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U,
				ActionEvent.CTRL_MASK));

		admin.add(purchaseItemAdmin);
	}
	

	/*
	 * Adds menu items to the Purchase menu and then adds the menu to the
	 * menubar
	 */
	private void setupPurchaseAdminMenu(JMenu admin) {
		purchaseAdmin = new JMenu("Purchase Admin");

		// when p is pressed on the keyboard, the menu will appear
		purchaseAdmin.setMnemonic(KeyEvent.VK_P);

		createMenuItem(purchaseAdmin, "Insert Purchase...", KeyEvent.VK_I,
				"Insert Purchase");

		createMenuItem(purchaseAdmin, "Delete Purchase...", KeyEvent.VK_D,
				"Delete Purchase");

		JMenuItem menuItem = createMenuItem(purchaseAdmin, "Show All Purchases",
				KeyEvent.VK_S, "Show Purchase");
		// setup a short cut key for this menu item
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
				ActionEvent.CTRL_MASK));

		admin.add(purchaseAdmin);
	}
	
	/*
	 * Adds menu items to the ReturnItem menu and then adds the menu to the
	 * menubar
	 */
	private void setupReturnItemAdminMenu(JMenu admin) {
		returnItemAdmin = new JMenu("ReturnItem Admin");

		// when e is pressed on the keyboard, the menu will appear
		returnItemAdmin.setMnemonic(KeyEvent.VK_E);

		createMenuItem(returnItemAdmin, "Insert ReturnItem...", KeyEvent.VK_I,
				"Insert ReturnItem");

		createMenuItem(returnItemAdmin, "Delete ReturnItem...", KeyEvent.VK_D,
				"Delete ReturnItem");

		JMenuItem menuItem = createMenuItem(returnItemAdmin, "Show All ReturnItems",
				KeyEvent.VK_S, "Show ReturnItem");
		// setup a short cut key for this menu item
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
				ActionEvent.CTRL_MASK));

		admin.add(returnItemAdmin);
	}
	
	/*
	 * Adds menu items to the Return menu and then adds the menu to the
	 * menubar
	 */
	private void setupReturnAdminMenu(JMenu admin) {
		returnAdmin = new JMenu("Return Admin");

		// when r is pressed on the keyboard, the menu will appear
		returnAdmin.setMnemonic(KeyEvent.VK_R);

		createMenuItem(returnAdmin, "Insert Return...", KeyEvent.VK_I,
				"Insert Return");

		createMenuItem(returnAdmin, "Delete Return...", KeyEvent.VK_D,
				"Delete Return");

		JMenuItem menuItem = createMenuItem(returnAdmin, "Show All Returns",
				KeyEvent.VK_S, "Show Return");
		// setup a short cut key for this menu item
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
				ActionEvent.CTRL_MASK));

		admin.add(returnAdmin);
	}
	
	/*
	 * Adds menu items to the ShipItem menu and then adds the menu to the
	 * menubar
	 */
	private void setupShipItemAdminMenu(JMenu admin) {
		shipItemAdmin = new JMenu("ShipItem Admin");

		// when t is pressed on the keyboard, the menu will appear
		shipItemAdmin.setMnemonic(KeyEvent.VK_T);

		createMenuItem(shipItemAdmin, "Insert ShipItem...", KeyEvent.VK_I,
				"Insert ShipItem");

		createMenuItem(shipItemAdmin, "Delete ShipItem...", KeyEvent.VK_D,
				"Delete ShipItem");

		JMenuItem menuItem = createMenuItem(shipItemAdmin, "Show All ShipItems",
				KeyEvent.VK_S, "Show ShipItem");
		// setup a short cut key for this menu item
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
				ActionEvent.CTRL_MASK));

		admin.add(shipItemAdmin);
	}
	
	/*
	 * Adds menu items to the Shipment menu and then adds the menu to the
	 * menubar
	 */
	private void setupShipmentAdminMenu(JMenu admin) {
		shipmentAdmin = new JMenu("Shipment Admin");

		// when s is pressed on the keyboard, the menu will appear
		shipmentAdmin.setMnemonic(KeyEvent.VK_S);

		createMenuItem(shipmentAdmin, "Insert Shipment...", KeyEvent.VK_I,
				"Insert Shipment");

		createMenuItem(shipmentAdmin, "Delete Shipment...", KeyEvent.VK_D,
				"Delete Shipment");

		JMenuItem menuItem = createMenuItem(shipmentAdmin, "Show All Shipments",
				KeyEvent.VK_S, "Show Shipment");
		// setup a short cut key for this menu item
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				ActionEvent.CTRL_MASK));

		admin.add(shipmentAdmin);
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
