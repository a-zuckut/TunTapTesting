import java.io.File;
import java.io.FileDescriptor;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.util.Arrays;

public class TestingJNI {

	native void ioctl(String dev, int descriptor);
	
	static {
		System.loadLibrary("ctest");
	}
	
    public static void main(String[] arg){
        try {
            File f = new File("/dev/net/tun");
            RandomAccessFile file = new RandomAccessFile(f, "rws");
            FileDescriptor fd = file.getFD();
            
            Field field = fd.getClass().getDeclaredField("fd");
            field.setAccessible(true);
            int descriptor = field.getInt(fd);


            //use of Java Native Interface
            new TestingJNI().ioctl("tun77", descriptor);

            while(true){
                System.out.println("reading");
                byte[] bytes = new byte[2000];
                int l = 0;
                l = file.read(bytes);
                
                for(int i = 0; i < l; i++) {
                	
                	System.out.print((bytes[i] & 0xFF) + " ");
                }
                
                System.out.println();
                
                // How could I read this byte[]
                
                // see if I can write back to the file
                
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static int unsignedToBytes(byte b) {
        return b & 0xFF;
      }
    
    
}