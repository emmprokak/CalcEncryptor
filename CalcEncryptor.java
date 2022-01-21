package newCalc;
/* Before you run CalcEncryptor for yourself:
 Make sure to populate the package name and the variables for historyFilepath,
 keystoreFilepath and PASSWORD, found in lines 1, 64, 70 and 71 accordingly
*/
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class CalcEncryptor {

	private final static String newline = "\n";
	private JFrame frame;
	private JTextField textField;
	private JTextArea historyfield;
	private JButton button_0, button_1, button_2, button_3,
					button_4, button_5, button_6, button_7,
					button_8, button_9, button_equals,
					button_plus, button_minus, button_mult,
					button_div, button_expand, button_delete,
					button_comma, button_negative;
	
	private double num1 = 0,
				   num2 = 0,
				   result = 0;
	
	private char operator;
	private boolean isExpanded = false;
	
	ArrayList<String> putToHistory = new ArrayList<>();
	String current_calculation = "";
	private String historyFilepath = "<set to project dir>/history.txt";
	
	// encryption stuff
	private SecretKey key;
	private final int KEY_SIZE = 128;
	private Cipher encrCipher;	
	private String keystorePath = "<set to project dir>/secret.keystore";
	private final String PASSWORD = "<set to custom password>";
	

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CalcEncryptor window = new CalcEncryptor();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public CalcEncryptor() throws Exception {
		initializeGUI();
		
		if(new File(keystorePath).exists()) 
			this.key = loadFromKeystore(keystorePath, PASSWORD);		
		else
			createKeystore();
	}
	
	private void createKeystore() throws Exception{
		KeyGenerator generator = KeyGenerator.getInstance("AES");
		generator.init(KEY_SIZE);
		this.key = generator.generateKey();
		storeToKeystore(keystorePath,key,PASSWORD);
	}
	
	private static void storeToKeystore(String keystorePath, SecretKey key, String password) throws Exception {
		File file = new File(keystorePath);
		KeyStore keystore = KeyStore.getInstance("JCEKS");
		if(!file.exists()) {
			keystore.load(null,null);
		}
		keystore.setKeyEntry("alias",key,password.toCharArray(),null);
		OutputStream writeStream = new FileOutputStream(keystorePath);
		keystore.store(writeStream,password.toCharArray());
	}
	
	private static SecretKey loadFromKeystore(String keystorePath,String password) throws Exception{
		KeyStore keystore = KeyStore.getInstance("JCEKS");
		InputStream readStream = new FileInputStream(keystorePath);
		keystore.load(readStream,password.toCharArray());
		SecretKey key = (SecretKey) keystore.getKey("alias", password.toCharArray());
		return key;
	}
	
	private byte[] encryptArrayListToBytes(ArrayList<String> list)
			throws NoSuchAlgorithmException,
				   NoSuchPaddingException,
				   InvalidKeyException,
				   IllegalBlockSizeException,
				   BadPaddingException {	
		/** converts ArrayList<String> to byte array and then encrypts the byte array */
		ByteArrayOutputStream byte_output = new ByteArrayOutputStream();
		DataOutputStream data_output = new DataOutputStream(byte_output);
		for(String row : list)
			try {
				data_output.writeUTF(row);
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		byte[] ArrayListInBytes = byte_output.toByteArray();		
		encrCipher = Cipher.getInstance("AES");
		encrCipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encryptedBytes =  this.encrCipher.doFinal(ArrayListInBytes);
		
		return encryptedBytes;	
	}
	
	private ArrayList<String> decryptBytesToArrayList(byte[] encr_history)
			throws NoSuchAlgorithmException,
				   NoSuchPaddingException,
				   InvalidKeyException,
				   IllegalBlockSizeException,
				   BadPaddingException,
				   UnsupportedEncodingException{
		/** takes encrypted bytes, decrypts them and then creates an ArrayList<String> */		
		// decrypt bytes to normal bytes
		ArrayList<String> list = new ArrayList<>();
		Cipher decrCipher = Cipher.getInstance("AES");
		decrCipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decryptedBytes = decrCipher.doFinal(encr_history);
		
		// convert bytes to ArrayList<String>			 
		String history_data = new String(decryptedBytes,"utf-8");
		String[] history_array = history_data.split(newline);
		for(int i = 0; i < history_array.length; i++) {
			if(history_array[i].contains("="))
				list.add(history_array[i]);
		}
		return list;
	}
	
	public void numberButtonPressed(JButton button) {
		if(num1 == 0) { 
			textField.setText(textField.getText().concat(button.getText()));
			num1 = Double.parseDouble(textField.getText());
		}else					
			textField.setText(textField.getText().concat(String.valueOf(button.getText())));
	}
	
	public void operatorPressed(JButton button) {
		char[] temp = button.getText().toCharArray();
		operator = temp[0];
		num1 = Double.parseDouble(textField.getText());
		textField.setText("");		
	}
	
	public void performCalculation() throws Exception {
		num2 = Double.parseDouble(textField.getText());
		switch (operator) {
		case '+':
			result = num1 + num2;
			current_calculation = String.valueOf(num1) + " + " + String.valueOf(num2) + " = " + String.valueOf((double) Math.round(result * 100) / 100);
			break;
		case '-':
			result = num1 - num2;
			current_calculation = String.valueOf(num1) + " - " + String.valueOf(num2) + " = " + String.valueOf((double) Math.round(result * 100) / 100);
			break;
		case '*':
			result = num1 * num2;
			current_calculation = String.valueOf(num1) + " * " + String.valueOf(num2) + " = " + String.valueOf((double) Math.round(result * 100) / 100);
			break;
		case '/':
			result = num1 / num2;
			current_calculation = String.valueOf(num1) + " / " + String.valueOf(num2) + " = " + String.valueOf((double) Math.round(result * 100) / 100);
			break;			
		}
		num1 = result;
		textField.setText(String.valueOf(result));
		putToHistory.add(current_calculation + newline);
		writeHistory(putToHistory,historyFilepath);
	}
	
	private ArrayList<String> loadHistory(String history_file){
		Path file = Paths.get(history_file);		
		ArrayList<String> list;
		
		try {
			byte[] encrBytes = Files.readAllBytes(file);
			list = decryptBytesToArrayList(encrBytes);
			// get rid of random characters at start of history entries,
			// this can definitely be done in a better way
			char [] temp;			
			for(int i = 0; i < list.size(); i++) {
				 temp = list.get(i).toCharArray();
				for(int j = 0; j < temp.length; j++) {
					if(j <= 1)
						temp[j] = ' ';
				}				
				list.set(i, String.copyValueOf(temp));
			}
			return list;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void writeHistory(ArrayList<String> list, String history_file) throws Exception{
		try {
			FileOutputStream file_output = new FileOutputStream(history_file);
			file_output.write(encryptArrayListToBytes(list));
			file_output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	private void initializeGUI() {
		frame = new JFrame();
		frame.setBounds(100, 100, 315, 473);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("CalcEncryptor");
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);
		
		Font font = new Font("Courier",Font.PLAIN,20);
		Font small_font = new Font("Dialog",Font.PLAIN,15);
		
		textField = new JTextField();
		textField.setBounds(10, 11, 279, 64);
		textField.setEditable(false);
		textField.setFont(font);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		button_0 = new JButton("0");
		button_0.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				numberButtonPressed(button_0);
			}
		});
		button_0.setBounds(10, 379, 120, 50);
		button_0.setFont(font);
		frame.getContentPane().add(button_0);
		
		button_1 = new JButton("1");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				numberButtonPressed(button_1);
			}
		});
		button_1.setBounds(10, 318, 50, 50);
		button_1.setFont(font);
		frame.getContentPane().add(button_1);
		
		button_2 = new JButton("2");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				numberButtonPressed(button_2);
			}
		});
		button_2.setBounds(81, 318, 50, 50);
		button_2.setFont(font);
		frame.getContentPane().add(button_2);
		
		button_3 = new JButton("3");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				numberButtonPressed(button_3);
			}
		});
		button_3.setBounds(154, 318, 50, 50);
		button_3.setFont(font);
		frame.getContentPane().add(button_3);
		
		button_4 = new JButton("4");
		button_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				numberButtonPressed(button_4);
			}
		});
		button_4.setBounds(10, 243, 50, 50);
		button_4.setFont(font);
		frame.getContentPane().add(button_4);
		
		button_5 = new JButton("5");
		button_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				numberButtonPressed(button_5);
			}
		});
		button_5.setBounds(81, 243, 50, 50);
		button_5.setFont(font);
		frame.getContentPane().add(button_5);
		
		button_6 = new JButton("6");
		button_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				numberButtonPressed(button_6);
			}
		});
		button_6.setBounds(154, 243, 50, 50);
		button_6.setFont(font);
		frame.getContentPane().add(button_6);
		
		button_7 = new JButton("7");
		button_7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				numberButtonPressed(button_7);
			}
		});
		button_7.setBounds(10, 173, 50, 50);
		button_7.setFont(font);
		frame.getContentPane().add(button_7);
		
		button_8 = new JButton("8");
		button_8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				numberButtonPressed(button_8);
			}
		});
		button_8.setBounds(81, 173, 50, 50);
		button_8.setFont(font);
		frame.getContentPane().add(button_8);
		
		button_9 = new JButton("9");
		button_9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				numberButtonPressed(button_9);
			}
		});
		button_9.setFont(font);
		button_9.setBounds(154, 173, 50, 50);
		frame.getContentPane().add(button_9);
		
		button_minus = new JButton("-");
		button_minus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				operatorPressed(button_minus);
			}
		});
		button_minus.setFont(font);
		button_minus.setBounds(226, 243, 50, 50);
		frame.getContentPane().add(button_minus);
		
		button_plus = new JButton("+");
		button_plus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				operatorPressed(button_plus);
			}
		});
		button_plus.setFont(font);
		button_plus.setBounds(226, 318, 50, 50);
		frame.getContentPane().add(button_plus);
		
		button_mult = new JButton("*");
		button_mult.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				operatorPressed(button_mult);
			}
		});
		button_mult.setFont(font);
		button_mult.setBounds(226, 173, 50, 50);
		frame.getContentPane().add(button_mult);
		
		button_div = new JButton("/");
		button_div.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				operatorPressed(button_div);
			}
		});
		button_div.setFont(font);
		button_div.setBounds(226, 112, 50, 50);
		frame.getContentPane().add(button_div);
		
		button_comma = new JButton(".");
		button_comma.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textField.setText(textField.getText() + ".");
			}
		});
		button_comma.setFont(font);
		button_comma.setBounds(154, 379, 50, 50);
		frame.getContentPane().add(button_comma);
		
		/* the expand button will expand the calculator
		 window, revealing the history of calculations	*/	
		button_expand = new JButton(">>");
		button_expand.setFont(font);
		button_expand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				if(!isExpanded) {
					button_expand.setText("<<");					
					frame.setSize(new Dimension(540,473));
					isExpanded = true;
					
					// add expand components
					JLabel history_label = new JLabel("History");
					history_label.setFont(new Font("Courrier",Font.BOLD,30));
					history_label.setBounds(320,8,190,45);
					history_label.setHorizontalAlignment(SwingConstants.CENTER);
					frame.getContentPane().add(history_label);
					
					historyfield = new JTextArea();
					historyfield.setBounds(320,50,190,460);
					historyfield.setEditable(false);
					historyfield.setLineWrap(true);
					historyfield.setWrapStyleWord(true);
					historyfield.setFont(small_font);
					
					// load calculations in JTextArea for display
					// and in the putToHistory ArrayList in order to 
					// save them to file again later
					ArrayList<String> history = loadHistory(historyFilepath);	
					for(int i = 0; i < history.size(); i++) {
						history.set(i, history.get(i) + newline);
						historyfield.append(history.get(i).trim() + newline);				
					}
					putToHistory = history;
					frame.getContentPane().add(historyfield);				
				}				
				else if(isExpanded) {
					button_expand.setText(">>");
					frame.setSize(new Dimension(315,473));
					isExpanded = false;
					frame.remove(historyfield);
				}			
			}
		});
		button_expand.setBounds(226, 79, 63, 30);
		frame.getContentPane().add(button_expand);
		
		button_negative = new JButton("-(x)");
		button_negative.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(textField.getText().contains("-")) 
					textField.setText(textField.getText().replace('-', ' ').trim());				
				else 
					textField.setText("-" + textField.getText());								
			}
		});
		button_negative.setFont(font);
		button_negative.setBounds(113, 112, 83, 50);
		frame.getContentPane().add(button_negative);
		
		button_delete = new JButton("del");
		button_delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				num1 = 0;
				num2 = 0;
				result = 0;
				textField.setText("");
			}
		});
		button_delete.setFont(font);
		button_delete.setBounds(10, 112, 83, 50);
		frame.getContentPane().add(button_delete);
		
		button_equals = new JButton("=");
		button_equals.setFont(font);
		button_equals.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				try {
					performCalculation();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		button_equals.setBounds(226, 386, 63, 38);
		frame.getContentPane().add(button_equals);
	
	}
	
}
