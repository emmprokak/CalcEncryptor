# CalcEncryptor

CalcEncryptor is a swing calculator that records the history of past calculations on a "history.txt" file.

The cool thing it brings to the table is that it encrypts the "history.txt" file with AES Symmetric Encryption, provided by the java.crypto package.

The private key used for encryption is stored securely on a java keystore, which is accessed through the program.

![Screenshot2](https://user-images.githubusercontent.com/89413115/147878251-d5bc25ce-f705-4440-8ffa-5ad275baa184.png)

## Before you run CalcEncryptor for yourself:

-Make sure to populate the package name and the variables for historyFilepath, keystoreFilepath and PASSWORD, found in lines 1, 64, 70 and 71 accordingly.
