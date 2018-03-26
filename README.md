# DesktopEncryptionTool

This encryption tool for desktop user.

## Dependencies
1. Java 7 or higher

## Features
1. Display language supports English and Chinese.
2. Support AES/CBC/PKCS5Padding and DES/CBC/PKCS5Padding.
3. Encrypted file is tagged in the end with a specific string in which the status of this file is stored. So if a file was encrypted and then you close the program, you can decrypt this file by reopening this program and import it.
4. Use Salt. So this program is not universal. The file encrypted by this file maybe can not be decrypted by another program. At the sametime, it's harder to crack the password.

## Screenshots
![Alt Screenshot 1](https://github.com/hanyizhao/DesktopEncryptionTool/blob/master/screenshots/1.png)
![Alt Screenshot 2](https://github.com/hanyizhao/DesktopEncryptionTool/blob/master/screenshots/2.png)
