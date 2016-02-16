package ca.mcgill.ecse321.HomeAudioSystem.view;

import java.awt.Color;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.ComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.SqlDateModel;

import ca.mcgill.ecse321.HomeAudioSystem.controller.HomeAudioSystemController;
import ca.mcgill.ecse321.HomeAudioSystem.controller.InvalidInputException;
import ca.mcgill.ecse321.HomeAudioSystem.model.Album;
import ca.mcgill.ecse321.HomeAudioSystem.model.Artist;
import ca.mcgill.ecse321.HomeAudioSystem.model.HAS;
import ca.mcgill.ecse321.HomeAudioSystem.model.Location;
import ca.mcgill.ecse321.HomeAudioSystem.model.Playlist;
import ca.mcgill.ecse321.HomeAudioSystem.model.Song;

public final class HomeAudioSystemPage extends JFrame {
	private static final long serialVersionUID = -8062635784771606869L;

	// UI elements
	private JLabel errorMessage;
	private JComboBox<String> albumList;
	private JLabel albumLabel;
	private JButton addButton;

	private JTextField albumTitleTextField;
	private JLabel albumTitleLabel;
	private JTextField albumGenreTextField;
	private JLabel albumGenreLabel;
	private JDatePickerImpl albumDatePicker;
	private JLabel albumDateLabel;
	private JButton addAlbumButton;
	private JButton deleteAlbumButton;

	// data elements
	private String error = null;
	private Integer selectedAlbum = -1;
	private HashMap<Integer, Album> albums;

	/** Creates new form EventRegistrationPage */
	public HomeAudioSystemPage() {
		initComponents();
		refreshData();
	}

	/** This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {
		// elements for error message
		errorMessage = new JLabel();
		errorMessage.setForeground(Color.RED);

		// elements for creating album
		albumList = new JComboBox<String>(new String[0]);
		albumList.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JComboBox<String> cb = (JComboBox<String>) evt.getSource();
				selectedAlbum = cb.getSelectedIndex();
			}
		});
		albumLabel = new JLabel();

		addButton = new JButton();
		
		// elements for album
		albumTitleTextField = new JTextField();
		albumTitleLabel = new JLabel();
		albumGenreTextField = new JTextField();
		albumGenreLabel = new JLabel();

		SqlDateModel model = new SqlDateModel();
		Properties p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");
		JDatePanelImpl datePanel = new JDatePanelImpl(model,p);
		albumDatePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
		addAlbumButton = new JButton();
		deleteAlbumButton = new JButton();
		albumDateLabel = new JLabel();

		// global settings and listeners
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("HAS - Home Audio System");

		albumLabel.setText("Select Album:");
		addButton.setText("Add to Playlist");
		addButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addButtonActionPerformed(evt);
			}
		});

		albumTitleLabel.setText("Title:");
		albumGenreLabel.setText("Genre:");
		albumDateLabel.setText("Release Date:");
		addAlbumButton.setText("Add Album");
		addAlbumButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addAlbumButtonActionPerformed(evt);
			}
		});
		deleteAlbumButton.setText("Delete Album");
		deleteAlbumButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				deleteAlbumButtonActionPerformed(evt);
			}
		});

		// layout
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(
				layout.createParallelGroup()
				.addComponent(errorMessage)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup()
								.addComponent(albumLabel)
								.addComponent(albumTitleLabel)
								.addComponent(albumGenreLabel)
								.addComponent(albumDateLabel))
						.addGroup(layout.createParallelGroup()
								.addComponent(albumList)
								.addComponent(albumTitleTextField, 200, 200, 400)
								.addComponent(albumGenreTextField, 200, 200, 400)
								.addComponent(albumDatePicker)
								.addComponent(addAlbumButton)
								.addComponent(deleteAlbumButton)))
				);

		layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] {albumLabel});
		layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] {albumTitleTextField, albumGenreTextField, addAlbumButton, deleteAlbumButton});

		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addComponent(errorMessage)
				.addGroup(layout.createParallelGroup()
						.addComponent(albumLabel)
						.addComponent(albumList))
				.addGroup(layout.createParallelGroup()
						.addComponent(albumTitleLabel)
						.addComponent(albumTitleTextField))
				.addGroup(layout.createParallelGroup()		
						.addComponent(albumGenreLabel)
						.addComponent(albumGenreTextField))
				.addGroup(layout.createParallelGroup()
						.addComponent(albumDateLabel)
						.addComponent(albumDatePicker))
				.addGroup(layout.createParallelGroup()
						.addComponent(addAlbumButton))
				.addGroup(layout.createParallelGroup()
						.addComponent(deleteAlbumButton))
						
				);

		pack();
	}

	private void refreshData() {
		HAS has = HAS.getInstance();
		// error
		errorMessage.setText(error);
		if (error == null || error.length() == 0) {
			// Album list
			albums = new HashMap<Integer, Album>();
			albumList.removeAllItems();
			Iterator<Album> abIt = has.getAlbums().iterator();
			Integer index = 0;
			while (abIt.hasNext()) {
				Album ab = abIt.next();
				albums.put(index, ab);
				albumList.addItem(ab.getTitle());
				index++;
			}
			selectedAlbum = -1;
			albumList.setSelectedIndex(selectedAlbum);
			// participant
			albumTitleTextField.setText("");
			albumGenreTextField.setText("");
			albumDatePicker.getModel().setValue(null);
		}

		// this is needed because the size of the window change depending on whether an error message is shown or not
		pack();
	}
	
	private void deleteAlbumButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// remove selected album from the list
		int selectedIndex = albumList.getSelectedIndex();
		if (selectedIndex != -1) {
		albumList.removeItemAt(selectedIndex);
		}
	}
	
	private void addAlbumButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// call the controller
		HomeAudioSystemController hasc = new HomeAudioSystemController();
		error = null;
		try {
			hasc.add_Album(albumTitleTextField.getText(), albumGenreTextField.getText(), (java.sql.Date) albumDatePicker.getModel().getValue());
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}
		// update visuals
		refreshData();
	}

	private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {
		error = "";
		if (selectedAlbum < 0)
			error = error + "Album needs to be selected for playlist! ";
		error = error.trim();
		if (error.length() == 0) {
			// call the controller
			HomeAudioSystemController hasc = new HomeAudioSystemController();
			try {
				hasc.addToPlaylist(albums.get(selectedAlbum));
			} catch (InvalidInputException e) {
				error = e.getMessage();
			}
		}
		// update visuals
		refreshData();
	}


}



