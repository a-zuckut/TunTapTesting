
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.unix.LibC;

public class Sample {
    static Linux_C_lib libC = new Linux_C_lib_DirectMapping();

    public static int O_RDWR = 0x00000002;
    
    public static final int TUNSETIFF = 1074025674;
    public static final int IFF_TUN = 1;
    public static final int IFF_TAP = 2;
    public static final int IFF_NO_PI = 4096;
    
    static {
    	Native.loadLibrary(LibC.class);
    }

    public static void main(String[] args) {
        String fileName = "/dev/net/tun";

        int file = libC.open(fileName, O_RDWR);
        if(file<0){
            System.out.println("Error opening file");
            return;
        }else{
            System.out.println("File open for reading and writing");
        }
        
        //Print out returned value from file open
        System.out.println(file);

        int[] flags = {IFF_TUN};

        int iocntl = libC.ioctl(file, TUNSETIFF, flags);
        //Print returned value after ioctl action
        System.out.println("fcntl = " + iocntl);

        if(iocntl<0){
            System.out.println("error on libC.ioctl");
            libC.close(file);
            return;
        }else{
            System.out.println("ioctl complete");
        }

    }
    
    public interface Linux_C_lib extends Library {
    	Linux_C_lib INSTANCE = (Linux_C_lib) Native.loadLibrary("c", Linux_C_lib.class);
        public long memcpy(int[] dst, short[] src, long n);
        public int memcpy(int[] dst, short[] src, int n);
        public int pipe(int[] fds);
        public int tcdrain(int fd);
        public int fcntl(int fd, int cmd, int arg);
        public int ioctl(int fd, int cmd, int[] arg);
        public int open(String path, int flags);
        public int close(int fd);
        public int write(int fd, byte[] buffer, int count);
        public int read(int fd, byte[] buffer, int count);
        public long write(int fd, byte[] buffer, long count);
        public long read(int fd, byte[] buffer, long count);
        public int poll(int[] fds, int nfds, int timeout);
        public int tcflush(int fd, int qs);
        public void perror(String msg);
        public int tcsendbreak(int fd, int duration);
    }
    
    public static class Linux_C_lib_DirectMapping implements Linux_C_lib {
        native public long memcpy(int[] dst, short[] src, long n);
        native public int memcpy(int[] dst, short[] src, int n);
        native public int pipe(int[] fds);
        native public int tcdrain(int fd);
        native public int fcntl(int fd, int cmd, int arg);
        native public int ioctl(int fd, int cmd, int[] arg);
        native public int open(String path, int flags);
        native public int close(int fd);
        native public int write(int fd, byte[] buffer, int count);
        native public int read(int fd, byte[] buffer, int count);
        native public long write(int fd, byte[] buffer, long count);
        native public long read(int fd, byte[] buffer, long count);
        native public int poll(int[] fds, int nfds, int timeout);
        native public int tcflush(int fd, int qs);
        native public void perror(String msg);
        native public int tcsendbreak(int fd, int duration);
        static {
            try {
                Native.register("c");
                System.out.println("registered to c library");
            } catch (Exception e) {
                System.out.println("Failed registering to c");
                e.printStackTrace();
            }
        }
    }
}