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
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

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
    private boolean keystoreReady = false;

    public EncryptionUtility(String historyFilePath, String keystorePath, int keySize, String password) {
	this.historyFilepath = historyFilePath;
	this.keystorePath = keystorePath;
	this.KEY_SIZE = keySize;
	this.PASSWORD = password;
    }

    boolean initKeystore(String keystorePath, String password) {
	File keystore = new File(keystorePath);
	Path keystorePathObject = Path.of(keystorePath);
	System.out.println(keystore.exists());
	try {
	    if (Files.exists(keystorePathObject)) {
		System.out.println("loading from keystore");
		key = loadFromKeystore(keystorePath, password);
		System.out.println("key returned is " + key.getEncoded());
	    } else
		createKeystore();
	    return true;

	} catch (Exception e) {
	    System.out.println(e);
	    return false;
	}
    }

    public void createKeystore() {
	KeyGenerator generator;
	try {
	    generator = KeyGenerator.getInstance("AES");
	    generator.init(KEY_SIZE);
	    this.key = generator.generateKey();
	    storeToKeystore(keystorePath, key, PASSWORD);
	} catch (NoSuchAlgorithmException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    public void storeToKeystore(String keystorePath, SecretKey key, String password) {
	File file = new File(keystorePath);
	KeyStore keystore;
	try {
	    keystore = KeyStore.getInstance("JCEKS");
	    if (!file.exists()) {
		keystore.load(null, null);
	    }
	    keystore.setKeyEntry("alias", key, password.toCharArray(), null);
	    OutputStream writeStream = new FileOutputStream(keystorePath);
	    keystore.store(writeStream, password.toCharArray());
	} catch (KeyStoreException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (NoSuchAlgorithmException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (CertificateException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    public SecretKey loadFromKeystore(String keystorePath, String password) {
	KeyStore keystore;
	try {
	    keystore = KeyStore.getInstance("JCEKS");
	    InputStream readStream = new FileInputStream(keystorePath);
	    keystore.load(readStream, password.toCharArray());
	    SecretKey key = (SecretKey) keystore.getKey("alias", password.toCharArray());
	    System.out.println("key is " + key);
	    return key;
	} catch (KeyStoreException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (NoSuchAlgorithmException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (CertificateException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (UnrecoverableKeyException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;

    }

    public byte[] encryptArrayListToBytes(List<String> list) {
	ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
	DataOutputStream dataOutput = new DataOutputStream(byteOutput);
	for (String row : list)
	    try {
		dataOutput.writeUTF(row);
	    } catch (IOException e) {
		e.printStackTrace();
	    }

	byte[] ArrayListInBytes = byteOutput.toByteArray();
	try {
	    encrCipher = Cipher.getInstance("AES");
	    encrCipher.init(Cipher.ENCRYPT_MODE, key);
	    byte[] encryptedBytes = encrCipher.doFinal(ArrayListInBytes);

	    return encryptedBytes;
	} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IllegalBlockSizeException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (BadPaddingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (InvalidKeyException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;
    }

    public List<String> decryptBytesToArrayList(byte[] encr_history) {

	List<String> list = new ArrayList<>();
	Cipher decrCipher;
	String[] historyArray;
	try {
	    decrCipher = Cipher.getInstance("AES");
	    decrCipher.init(Cipher.DECRYPT_MODE, key);
	    byte[] decryptedBytes = decrCipher.doFinal(encr_history);
	    String historyData = new String(decryptedBytes, "utf-8");
	    historyArray = historyData.split(newline);
	    for (int i = 0; i < historyArray.length; i++) {
		if (historyArray[i].contains("="))
		    list.add(historyArray[i]);
	    }
	    return list;

	} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (UnsupportedEncodingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IllegalBlockSizeException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (BadPaddingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (InvalidKeyException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;
    }

    public List<String> loadHistory(String historyFilePath) {
	Path file = Paths.get(historyFilePath);
	List<String> list;

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

    public void writeHistory(List<String> list, String historyFilePath) {
	try {
	    FileOutputStream file_output = new FileOutputStream(historyFilePath);
	    file_output.write(encryptArrayListToBytes(list));
	    file_output.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public boolean getKeystoreReady() {
	return this.keystoreReady;
    }

}
