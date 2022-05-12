package test;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class CalcEncryptor extends JFrame {

    private final static String newline = "\n";
    private final String CURRENT_WORKING_DIR = Path.of("").toAbsolutePath().toString();
    private String historyFilepath = CURRENT_WORKING_DIR + "/history.txt";
    private String keystorePath = CURRENT_WORKING_DIR + "/secret.keystore";
    private final int KEY_SIZE = 128;
    private final String PASSWORD = "test";
    private EncryptionUtility aes;

    private JFrame frame;
    private JTextField textField;
    private JTextArea historyfield;
    private JButton button0, button1, button2, button3, button4, button5, button6, button7, button8, button9,
	    buttonEquals, buttonPlus, buttonMinus, buttonMultiply, buttonDivision, buttonExpand, buttonDelete,
	    buttonComma, buttonNegative;

    private double num1 = 0, num2 = 0, result = 0;
    private char operator;
    private boolean isExpanded = false;

    private List<String> putToHistory = new ArrayList<>();
    private String current_calculation = "";

    public CalcEncryptor() {
	initJFrame();
	try {
	    aes = new EncryptionUtility(historyFilepath, keystorePath, KEY_SIZE, PASSWORD);

	    if (!new File(keystorePath).isFile()) {

		FirstRunUtility popup = new FirstRunUtility(this, aes, historyFilepath, keystorePath);
		this.setVisible(false);
		this.frame.setVisible(false);
		popup.initGUI();

	    }
	} catch (Exception e) {
	    System.out.println(e);
	}
	Path keystorePathObject = Path.of(keystorePath);
	if (Files.exists(keystorePathObject))
	    aes.initKeystore(keystorePath, PASSWORD);
	initializeGUI();
    }

    public JFrame getFrame() {
	return this.frame;
    }

    public void numberButtonPressed(JButton button) {
	if (num1 == 0) {
	    textField.setText(textField.getText().concat(button.getText()));
	    num1 = Double.parseDouble(textField.getText());
	} else
	    textField.setText(textField.getText().concat(String.valueOf(button.getText())));
    }

    public void operatorPressed(JButton button) {
	operator = button.getText().charAt(0);
	num1 = Double.parseDouble(textField.getText());
	textField.setText("");
    }

    public void performCalculation() throws Exception {
	num2 = Double.parseDouble(textField.getText());
	switch (operator) {
	case '+':
	    result = num1 + num2;
	    current_calculation = String.valueOf(num1) + " + " + String.valueOf(num2) + " = "
		    + String.valueOf((double) Math.round(result * 100) / 100);
	    break;
	case '-':
	    result = num1 - num2;
	    current_calculation = String.valueOf(num1) + " - " + String.valueOf(num2) + " = "
		    + String.valueOf((double) Math.round(result * 100) / 100);
	    break;
	case '*':
	    result = num1 * num2;
	    current_calculation = String.valueOf(num1) + " * " + String.valueOf(num2) + " = "
		    + String.valueOf((double) Math.round(result * 100) / 100);
	    break;
	case '/':
	    result = num1 / num2;
	    current_calculation = String.valueOf(num1) + " / " + String.valueOf(num2) + " = "
		    + String.valueOf((double) Math.round(result * 100) / 100);
	    break;
	}
	num1 = result;
	textField.setText(String.valueOf(result));
	putToHistory.add(current_calculation + newline);
	aes.writeHistory(putToHistory, historyFilepath);
    }

    private boolean isFileEmpty(String historyPath) {
	BufferedReader br;
	try {
	    br = new BufferedReader(new FileReader(historyPath));
	    return br.readLine() == null;

	} catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return false;
    }

    private void initJFrame() {
	frame = new JFrame();
	frame.setBounds(100, 100, 315, 473);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setTitle("CalcEncryptor");
	frame.setLocationRelativeTo(null);
	frame.setResizable(false);
	frame.getContentPane().setLayout(null);
    }

    private void initializeGUI() {

	Font font = new Font("Courier", Font.PLAIN, 20);
	Font small_font = new Font("Dialog", Font.PLAIN, 15);

	initializeOperationButtons();
	initializeNumberButtons();

	textField = new JTextField();
	textField.setBounds(10, 11, 279, 64);
	textField.setEditable(false);
	textField.setFont(font);
	frame.getContentPane().add(textField);
	textField.setColumns(10);

	if (new File(keystorePath).isFile()) {
	    this.frame.setVisible(true);
	}
    }

    private void initializeOperationButtons() {
	Font font = new Font("Courier", Font.PLAIN, 20);
	Font small_font = new Font("Dialog", Font.PLAIN, 15);

	buttonMinus = new JButton("-");
	buttonMinus.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		operatorPressed(buttonMinus);
	    }
	});
	buttonMinus.setFont(font);
	buttonMinus.setBounds(226, 243, 50, 50);
	frame.getContentPane().add(buttonMinus);

	buttonPlus = new JButton("+");
	buttonPlus.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		operatorPressed(buttonPlus);
	    }
	});
	buttonPlus.setFont(font);
	buttonPlus.setBounds(226, 318, 50, 50);
	frame.getContentPane().add(buttonPlus);

	buttonMultiply = new JButton("*");
	buttonMultiply.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		operatorPressed(buttonMultiply);
	    }
	});
	buttonMultiply.setFont(font);
	buttonMultiply.setBounds(226, 173, 50, 50);
	frame.getContentPane().add(buttonMultiply);

	buttonDivision = new JButton("/");
	buttonDivision.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		operatorPressed(buttonDivision);
	    }
	});
	buttonDivision.setFont(font);
	buttonDivision.setBounds(226, 112, 50, 50);
	frame.getContentPane().add(buttonDivision);

	buttonComma = new JButton(".");
	buttonComma.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		textField.setText(textField.getText() + ".");
	    }
	});
	buttonComma.setFont(font);
	buttonComma.setBounds(154, 379, 50, 50);
	frame.getContentPane().add(buttonComma);

	/*
	 * the expand button will expand the calculator window, revealing the history of
	 * calculations
	 */
	buttonExpand = new JButton(">>");
	buttonExpand.setFont(font);
	buttonExpand.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (!isExpanded) {
		    buttonExpand.setText("<<");
		    frame.setSize(new Dimension(540, 473));
		    isExpanded = true;

		    // add expand components
		    JLabel history_label = new JLabel("History");
		    history_label.setFont(new Font("Courrier", Font.BOLD, 30));
		    history_label.setBounds(320, 8, 190, 45);
		    history_label.setHorizontalAlignment(SwingConstants.CENTER);
		    frame.getContentPane().add(history_label);

		    historyfield = new JTextArea();
		    historyfield.setBounds(320, 50, 190, 460);
		    historyfield.setEditable(false);
		    historyfield.setLineWrap(true);
		    historyfield.setWrapStyleWord(true);
		    historyfield.setFont(small_font);

		    /*
		     * load calculations in JTextArea for display and in the putToHistory ArrayList
		     * in order to save them to file again later
		     */
		    List<String> history = new ArrayList<>();
		    if (new File(historyFilepath).exists() && !isFileEmpty(historyFilepath))
			history = aes.loadHistory(historyFilepath);
		    for (int i = 0; i < history.size(); i++) {
			history.set(i, history.get(i) + newline);
			historyfield.append(history.get(i).trim() + newline);
		    }
		    putToHistory = history;
		    frame.getContentPane().add(historyfield);

		} else if (isExpanded) {
		    buttonExpand.setText(">>");
		    frame.setSize(new Dimension(315, 473));
		    isExpanded = false;
		    frame.remove(historyfield);
		}
	    }
	});
	buttonExpand.setBounds(226, 79, 63, 30);
	frame.getContentPane().add(buttonExpand);

	buttonNegative = new JButton("-(x)");
	buttonNegative.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (textField.getText().contains("-"))
		    textField.setText(textField.getText().replace('-', ' ').trim());
		else
		    textField.setText("-" + textField.getText());
	    }
	});
	buttonNegative.setFont(font);
	buttonNegative.setBounds(113, 112, 83, 50);
	frame.getContentPane().add(buttonNegative);

	buttonDelete = new JButton("del");
	buttonDelete.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		num1 = 0;
		num2 = 0;
		result = 0;
		textField.setText("");
	    }
	});
	buttonDelete.setFont(font);
	buttonDelete.setBounds(10, 112, 83, 50);
	frame.getContentPane().add(buttonDelete);

	buttonEquals = new JButton("=");
	buttonEquals.setFont(font);
	buttonEquals.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		try {
		    performCalculation();
		} catch (Exception e1) {
		    e1.printStackTrace();
		}
	    }
	});
	buttonEquals.setBounds(226, 386, 63, 38);
	frame.getContentPane().add(buttonEquals);

    }

    private void initializeNumberButtons() {
	Font font = new Font("Courier", Font.PLAIN, 20);
	Font small_font = new Font("Dialog", Font.PLAIN, 15);

	button0 = new JButton("0");
	button0.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		numberButtonPressed(button0);
	    }
	});
	button0.setBounds(10, 379, 120, 50);
	button0.setFont(font);
	frame.getContentPane().add(button0);

	button1 = new JButton("1");
	button1.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		numberButtonPressed(button1);
	    }
	});
	button1.setBounds(10, 318, 50, 50);
	button1.setFont(font);
	frame.getContentPane().add(button1);

	button2 = new JButton("2");
	button2.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		numberButtonPressed(button2);
	    }
	});
	button2.setBounds(81, 318, 50, 50);
	button2.setFont(font);
	frame.getContentPane().add(button2);

	button3 = new JButton("3");
	button3.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		numberButtonPressed(button3);
	    }
	});
	button3.setBounds(154, 318, 50, 50);
	button3.setFont(font);
	frame.getContentPane().add(button3);

	button4 = new JButton("4");
	button4.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		numberButtonPressed(button4);
	    }
	});
	button4.setBounds(10, 243, 50, 50);
	button4.setFont(font);
	frame.getContentPane().add(button4);

	button5 = new JButton("5");
	button5.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		numberButtonPressed(button5);
	    }
	});
	button5.setBounds(81, 243, 50, 50);
	button5.setFont(font);
	frame.getContentPane().add(button5);

	button6 = new JButton("6");
	button6.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		numberButtonPressed(button6);
	    }
	});
	button6.setBounds(154, 243, 50, 50);
	button6.setFont(font);
	frame.getContentPane().add(button6);

	button7 = new JButton("7");
	button7.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		numberButtonPressed(button7);
	    }
	});
	button7.setBounds(10, 173, 50, 50);
	button7.setFont(font);
	frame.getContentPane().add(button7);

	button8 = new JButton("8");
	button8.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		numberButtonPressed(button8);
	    }
	});
	button8.setBounds(81, 173, 50, 50);
	button8.setFont(font);
	frame.getContentPane().add(button8);

	button9 = new JButton("9");
	button9.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		numberButtonPressed(button9);
	    }
	});
	button9.setFont(font);
	button9.setBounds(154, 173, 50, 50);
	frame.getContentPane().add(button9);
    }

}