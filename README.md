# CalcEncryptor

CalcEncryptor is a swing calculator that records the history of past calculations on a "history.txt" file.

The cool thing it brings to the table is that it encrypts the history.txt file with AES Symmetric Encryption, provided by the java.crypto package.

The private key is stored securely on a java keystore, which is accessed through the program.

## Before you run CalcEncryptor for yourself:

-Make sure to populate the variables for historyFilepath, keystoreFilepath and PASSWORD, found in lines 61, 67 and 68 accordingly.
