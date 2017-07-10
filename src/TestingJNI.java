import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class TestingJNI {

    native void ioctl(String dev, int descriptor);
    
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
        	
//            Constructor<FileDescriptor> ctor = FileDescriptor.class.getDeclaredConstructor(Integer.TYPE);
//            ctor.setAccessible(true);
//            FileDescriptor fd2 = ctor.newInstance(fileDesc);
//            ctor.setAccessible(false);
            
            
            while(true){
            	
                System.out.print("reading");
                byte[] bytes = new byte[2000];
                int l = 0;
                l = input.read(bytes); // this is reading from tun interface
                System.out.println("\nRead " + l + " byte(s) of data.");
                print(bytes, l);
                
                byte[] reply = computeResponse(bytes, l);
                output.write(reply); // This is writing back to tun interface
                
                System.out.println("Replied");

            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final byte IPV4_FLAGS_MOREFRAG = 0x1;
    public static final byte IPV4_FLAGS_DONTFRAG = 0x2;
    public static final byte IPV4_FLAGS_MASK = 0x7;
    public static final byte IPV4_FLAGS_SHIFT = 13;
    public static final short IPV4_OFFSET_MASK = (1 << IPV4_FLAGS_SHIFT) - 1;

    public static byte[] computeResponse(byte[] pingRequest, int l) throws Exception {
        ByteBuffer bb = ByteBuffer.wrap(pingRequest);
        
        short discard = bb.getShort();
        short ipv4 = bb.getShort();

        byte version = bb.get();
        byte diffServ = bb.get();
        short totalLength = bb.getShort();
        short identification = bb.getShort();
        byte flagByte = bb.get();
        byte finalFrag = bb.get();
        byte timeToLive = bb.get();
        byte protocol = bb.get();
        short checksumIPv4 = bb.getShort();
        int sourceAddress = bb.getInt();
        int destinationAddress = bb.getInt();
        
        // Done with parsing IPv4 Header
        
        // Start InternetControlMessageProtocol (Request)
        
        byte icmpType = bb.get();
        byte icmpCode = bb.get();
        short checksumICMP = bb.getShort();

        short padding = 0; // Because request/reply - no padding

        bb.position(bb.position() + padding);
        byte[] payload = Arrays.copyOfRange(pingRequest, bb.position(), l);
        
        byte[] ret = new byte[l];
        ByteBuffer buffer = ByteBuffer.wrap(ret);
        
        buffer.putShort(discard);
        buffer.putShort(ipv4);
        
        // Added IPv4 Header
        buffer.put(version);
        buffer.put(diffServ);
        buffer.putShort(totalLength);
        buffer.putShort((short)(identification + 1)); // new identification
        buffer.put((byte) 0x00); // instead of flag byte
        buffer.put(finalFrag);
        buffer.put(timeToLive);
        buffer.put(protocol);
        buffer.putShort(ipc4_checksum(version, diffServ, totalLength, identification, flagByte, finalFrag, timeToLive, protocol, intToBytes(sourceAddress), intToBytes(destinationAddress)));
        buffer.putInt(destinationAddress); // Switch
        buffer.putInt(sourceAddress); // Switch (reply)
        
        buffer.put((byte) 0x00); // Echo ping reply (ICMP type)
        buffer.put(icmpCode);
        buffer.putShort(checksum(payload));
        buffer.put(payload);
        
        System.out.println("\nComputing IPv4 and ICMP");
        
        print(ret);
        
        return ret;
    }
    
    private static byte[] intToBytes(int sourceAddress) {
        return ByteBuffer.allocate(4).putInt(1695609641).array();
    }

    public static int unsignedToBytes(byte b) {
        return b & 0xFF;
    }
    
    public static void print(byte[] bytes) {
        print(bytes, bytes.length);
    }
    
    public static void print(byte[] bytes, int length) {
        for(int i = 0; i < length; i++) {
            System.out.print(unsignedToBytes(bytes[i]) + " ");
        }
        
        System.out.println();
    }
    
    static short ipc4_checksum(byte version, byte service, short length, short identification, byte flag, byte frag, byte timetolive,
        byte protocol, byte[] source, byte[] dest) {
        byte[] buf = new byte[] {version, service, (byte) (length >> 8 % 0xff), (byte) (length & 0xff), 
                (byte) (identification >> 8 & 0xff), (byte) (identification & 0xff), flag, frag, timetolive, protocol,
                source[0], source[1], source[2], source[3], dest[0], dest[1], dest[2], dest[3]
        };
        return checksum(buf);
    }
        
    static short checksum(byte[] buf) {
        int length = buf.length;
        int i = 0;
        long sum = 0;
        while (length > 0) {
            sum += (buf[i++]&0xff) << 8;
            if ((--length)==0) break;
            sum += (buf[i++]&0xff);
            --length;
        }
        return (short) (~((sum & 0xFFFF)+(sum >> 16))&0xFFFF);
    }

    public long calculateChecksum(byte[] buf) {
        int length = buf.length;
        int i = 0;
        long sum = 0;
        long data;
        while (length > 1) {
          data = (((buf[i] << 8) & 0xFF00) | ((buf[i + 1]) & 0xFF));
          sum += data;
          if ((sum & 0xFFFF0000) > 0) {
            sum = sum & 0xFFFF;
            sum += 1;
          }
          i += 2;
          length -= 2;
        }
        if (length > 0) {
          sum += (buf[i] << 8 & 0xFF00);
          if ((sum & 0xFFFF0000) > 0) {
            sum = sum & 0xFFFF;
            sum += 1;
          }
        }
        sum = ~sum;
        sum = sum & 0xFFFF;
        return sum;

      }
    
}