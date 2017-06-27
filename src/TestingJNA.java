import java.nio.ByteBuffer;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class TestingJNA {

    public interface CLibrary extends Library {
        CLibrary INSTANCE = (CLibrary) Native.loadLibrary("c", CLibrary.class);
//        void printf(String format, Object... args);
    }
    
    public static void main(String[] args) {
        String tun_name = "tun77";
        int tun_fd;
        ByteBuffer buffer; // for getting in data
        
        int nread, nwrite, plength;
        
//        tun_fd = tun_alloc(tun_name, IFF_TUN | IFF_NO_PI);
        CLibrary lib = CLibrary.INSTANCE;
//        lib.printf("TESTING %d", 54);
        
    }

}
