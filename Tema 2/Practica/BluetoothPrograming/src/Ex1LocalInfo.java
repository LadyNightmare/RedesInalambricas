import javax.bluetooth.*;

public class Ex1LocalInfo {
	
	public static void main (String [] args) throws BluetoothStateException {

		LocalDevice ld = LocalDevice.getLocalDevice();
		
		String properties = LocalDevice.getProperty("bluetooth.stack");
		
		System.out.println(ld.getBluetoothAddress());
		System.out.println(ld.getFriendlyName());
		
		System.out.println(properties);		

}

}
