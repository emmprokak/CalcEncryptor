package test;

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

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class EncryptionUtility {

    private String historyFilepath;
    private final static String newline = "\n";
    private SecretKey key;
    private final int KEY_SIZE;
    private Cipher encrCipher;
    private String keystorePath;
    private final String PASSWORD;

    public EncryptionUtility(String historyFilePath, String keystorePath, int keySize, String password)
	    throws Exception {
	this.historyFilepath = historyFilePath;
	this.keystorePath = keystorePath;
	this.KEY_SIZE = keySize;
	this.PASSWORD = password;
	initKeystoreKey(keystorePath, password);
    }

    private boolean initKeystoreKey(String keystorePath, String password) {
	try {
	    if (new File(keystorePath).exists())
		key = loadFromKeystore(keystorePath, password);
	    else
		createKeystore();
	    return true;

	} catch (Exception e) {
	    System.out.println(e);
	    return false;
	}
    }

    public void createKeystore() throws Exception {
	KeyGenerator generator = KeyGenerator.getInstance("AES");
	generator.init(KEY_SIZE);
	this.key = generator.generateKey();
	storeToKeystore(keystorePath, key, PASSWORD);
    }

    public void storeToKeystore(String keystorePath, SecretKey key, String password) throws Exception {
	File file = new File(keystorePath);
	KeyStore keystore = KeyStore.getInstance("JCEKS");
	if (!file.exists()) {
	    keystore.load(null, null);
	}
	keystore.setKeyEntry("alias", key, password.toCharArray(), null);
	OutputStream writeStream = new FileOutputStream(keystorePath);
	keystore.store(writeStream, password.toCharArray());
    }

    public SecretKey loadFromKeystore(String keystorePath, String password) throws Exception {
	KeyStore keystore = KeyStore.getInstance("JCEKS");
	InputStream readStream = new FileInputStream(keystorePath);
	keystore.load(readStream, password.toCharArray());
	SecretKey key = (SecretKey) keystore.getKey("alias", password.toCharArray());
	return key;
    }

    public byte[] encryptArrayListToBytes(ArrayList<String> list) throws NoSuchAlgorithmException,
	    NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
	ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
	DataOutputStream dataOutput = new DataOutputStream(byteOutput);
	for (String row : list)
	    try {
		dataOutput.writeUTF(row);
	    } catch (IOException e) {
		e.printStackTrace();
	    }

	byte[] ArrayListInBytes = byteOutput.toByteArray();
	encrCipher = Cipher.getInstance("AES");
	encrCipher.init(Cipher.ENCRYPT_MODE, key);
	byte[] encryptedBytes = encrCipher.doFinal(ArrayListInBytes);

	return encryptedBytes;
    }

    public ArrayList<String> decryptBytesToArrayList(byte[] encr_history)
	    throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
	    BadPaddingException, UnsupportedEncodingException {

	ArrayList<String> list = new ArrayList<>();
	Cipher decrCipher = Cipher.getInstance("AES");
	decrCipher.init(Cipher.DECRYPT_MODE, key);
	byte[] decryptedBytes = decrCipher.doFinal(encr_history);

	String historyData = new String(decryptedBytes, "utf-8");
	String[] historyArray = historyData.split(newline);
	for (int i = 0; i < historyArray.length; i++) {
	    if (historyArray[i].contains("="))
		list.add(historyArray[i]);
	}
	return list;
    }

    public ArrayList<String> loadHistory(String historyFilePath) {
	Path file = Paths.get(historyFilePath);
	ArrayList<String> list;

	try {
	    byte[] encrBytes = Files.readAllBytes(file);
	    list = decryptBytesToArrayList(encrBytes);
	    /*
	     * get rid of random characters at start of history entries, this can definitely
	     * be done in a better way
	     */
	    char[] temp;
	    for (int i = 0; i < list.size(); i++) {
		temp = list.get(i).toCharArray();
		for (int j = 0; j < temp.length; j++) {
		    if (j <= 1)
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

    public void writeHistory(ArrayList<String> list, String historyFilePath) throws Exception {
	try {
	    FileOutputStream file_output = new FileOutputStream(historyFilePath);
	    file_output.write(encryptArrayListToBytes(list));
	    file_output.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

}
