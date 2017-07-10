
# Tun/Tap Interface Testing

This project is meant to show the feasibility of gathering TUN/TAP frame on linux.

### Building java program

1. Compile java file. `javac TestingJNI.java`

2. Generate java header file. `javah TestingJNI`

3. Using the ctest.c file, create the library file for java to use.

	`gcc -o libctest.so -shared -I /usr/lib/jvm/java-8-openjdk-amd64/include/linux 
	-I /usr/lib/jvm/java-8-openjdk-amd64/include ctest.c -fPIC`
	
	Note: Linux uses .so files as library files. This is different in mac/windows. Mac: .dylib; Windows: .dll
	
4. Running java class. `sudo java -Djava.library.path=. TestingJNI`


### Creating TUN Interface

This is to send persistent data to our .java program. (using ping)

(You might need to have run as root)
1. `openvpn --mktun --dev tun77`
2. `ip link set tun77 up`
3. `ip addr add 10.0.0.1/24 dev tun77`



Run `ping 10.0.0.2` which will send though tun77 and if you have the java program running,
you will see the ping data being read and outputted.



Perhaps the best next steps would be sending bytes back to the network. For that we need an address
and sending data back throught that address?


