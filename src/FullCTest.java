
public class FullCTest {

	native void run(String dev);
	
	static {
		System.loadLibrary("FullCTest");
	}
	
	public static void main(String[] args) {
		FullCTest nat = new FullCTest();
		nat.run("tun77");
	}
}
