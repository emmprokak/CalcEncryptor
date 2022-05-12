/*
 * @author: Prokakis Emmanouil 2022
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Path;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class FirstRunUtility extends JDialog {

    private JPanel contentPane;
    private JFrame mainWindow;
    private EncryptionUtility encrypt;
    private String historyPath;
    private String keystorePath;
    private final String CURRENT_WORKING_DIR = Path.of("").toAbsolutePath().toString();

    public FirstRunUtility() {

    }

    public FirstRunUtility(JFrame main, EncryptionUtility aes, String historyPath, String keystorePath) {
	this.mainWindow = main;
	this.encrypt = aes;
	this.historyPath = historyPath;
	this.keystorePath = keystorePath;

    }

    public void initGUI() {
	setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	setBounds(100, 100, 450, 326);
	contentPane = new JPanel();
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	setContentPane(contentPane);
	contentPane.setLayout(null);
	this.setResizable(false);

	JLabel sadLabel = new JLabel(":(");
	sadLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 40));
	sadLabel.setBounds(22, 19, 28, 49);
	contentPane.add(sadLabel);

	JTextArea displayMessageTextArea = new JTextArea();
	displayMessageTextArea.setBackground(SystemColor.activeCaptionBorder);
	displayMessageTextArea.setWrapStyleWord(true);
	displayMessageTextArea.setFont(new Font("Courier New", Font.PLAIN, 18));
	displayMessageTextArea.setLineWrap(true);
	displayMessageTextArea.setText("Seems like there is no history.txt or secret.keystore files on "
		+ CURRENT_WORKING_DIR + "\n\nWould like me to create them for you?");
	displayMessageTextArea.setEditable(false);
	displayMessageTextArea.setBounds(22, 80, 406, 144);

	Border border = BorderFactory.createLineBorder(Color.GRAY);
	displayMessageTextArea
		.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(5, 5, 5, 5)));

	contentPane.add(displayMessageTextArea);

	JButton noButton = new JButton("Quit");
	noButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		quitApplication();
	    }
	});
	noButton.setBackground(SystemColor.activeCaption);
	noButton.setFont(new Font("Courier New", Font.PLAIN, 14));
	noButton.setBounds(181, 250, 117, 29);
	contentPane.add(noButton);

	JButton yesButton = new JButton("Yes please");
	yesButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		createNeededFiles(historyPath, keystorePath);
	    }
	});
	yesButton.setBackground(SystemColor.activeCaption);
	yesButton.setFont(new Font("Courier New", Font.PLAIN, 14));
	yesButton.setBounds(311, 250, 117, 29);
	contentPane.add(yesButton);

	JLabel lblNewLabel = new JLabel("First time running the program?");
	lblNewLabel.setFont(new Font("Courier New", Font.PLAIN, 18));
	lblNewLabel.setBounds(72, 32, 356, 29);
	contentPane.add(lblNewLabel);

	this.contentPane.setVisible(true);
	this.setVisible(true);
    }

    public void createNeededFiles(String historyPath, String keystorePath) {
	((CalcEncryptor) this.mainWindow).getFrame().setVisible(true);
	if (!encrypt.getKeystoreReady())
	    try {
		this.encrypt.initKeystore(historyPath, keystorePath);
	    } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException | CertificateException
		    | IOException e) {
		e.printStackTrace();
	    }
	this.setVisible(false);
    }

    public void quitApplication() {
	System.exit(0);
    }

    public void setHistoryPath(String historyPath) {
	this.historyPath = historyPath;
    }

    public void setKeystorePath(String keystorePath) {
	this.keystorePath = keystorePath;
    }
}
