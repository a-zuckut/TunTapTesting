import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;

public class TestingJNI {

	native void ioctl(String dev, int descriptor);
	native int socket(String port);
	
	static {
		System.loadLibrary("ctest");
	}
	
    public static void main(String[] arg){
    	
    	TestingJNI nat = new TestingJNI();
    	
        try {
        	// Getting the file of the tun interface
            File f = new File("/dev/net/tun");
            RandomAccessFile file = new RandomAccessFile(f, "rws");
            FileDescriptor fd = file.getFD();
            
            Field field = fd.getClass().getDeclaredField("fd");
            field.setAccessible(true);
            int descriptor = field.getInt(fd);

            FileOutputStream output = new FileOutputStream(fd);
            FileInputStream input = new FileInputStream(fd);

            //use of Java Native Interface
            nat.ioctl("tun77", descriptor); // default interface set as tun77
            
            while(true){
                System.out.println("reading");
                byte[] bytes = new byte[2000];
                int l = 0;
                l = input.read(bytes); // this is reading from tun interface
                
                print(bytes, l);
                
                output.write(bytes); // This is writing back to tun interface
                
                
            }
            
//            output.close();
//            input.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static int unsignedToBytes(byte b) {
        return b & 0xFF;
    }
    
    public static void print(byte[] bytes, int length) {
    	for(int i = 0; i < length; i++) {
        	System.out.print(unsignedToBytes(bytes[i]) + " ");
        }
        
        System.out.println();
    }
    
}