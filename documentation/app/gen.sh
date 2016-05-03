mkdir src/
mkdir src/com
mkdir src/com/scar
cp ../../app/src/main/java/com/scar/android/* src/com/scar/
cp ../../app/src/main/java/com/scar/android/Services/* src/com/scar/
cp ../../app/src/main/java/com/scar/android/Activities/* src/com/scar/
cp ../../app/src/main/java/com/scar/android/Fragments/* src/com/scar/
cp ../../app/src/main/java/com/scar/android/ServerImpl/* src/com/scar/
javadoc -Xdoclint:none -private -sourcepath src/ com.scar
rm -r src/