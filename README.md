# SCAR
Scatter Conceal and Recover, or SCAR for short, was the research project that two other students and I worked on during the 2015 summer semester.  &nbsp;
We also built an Android application to use the algorithm, on GitHub.  &nbsp;
This project uses the Reed-Solomon algorithm of bit redundancy, databases, and a splish-splash of cryptography to securely break files into N parts but only need k parts to recreate the file.  &nbsp;
In other words: magic.  &nbsp;
A high level overview of the project is that the algorithm will encrypt a file using and AES key that we get from SHA-256 hashing some unique information together.  &nbsp;
Then, the newly encrypted file will have redundancy added to it using the Reed-Solomon algorithm.  &nbsp;
This encrypted file is then broken up into N parts, which is also done by Reed-Solomon, and each of these N parts are stored randomly on the known databases(MySQL, Dropbox etc).  &nbsp;
A hash-chain is used to figure out what server each chunk gets stored on.  &nbsp;
The reverse is done to retrieve the file, but the interesting part is that we only need k out of N total file chunks to run the algorithm in reverse and get the original file back.  &nbsp;
To compile and run you will need an Android IDE with an emulator.  &nbsp;
The basic one that I used was Android Studio and Genymotion, respectively, and you would click the green play button to compile and run the program.  