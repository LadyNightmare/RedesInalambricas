import java.io.IOException;
import java.util.Vector;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

public class Ex3DiscoverFriendDevices {

	public static final Vector/*<RemoteDevice>*/ devicesDiscovered = new Vector();

	public static void main(String[] args) throws IOException, InterruptedException {

		final Object inquiryCompletedEvent = new Object();

		devicesDiscovered.clear();

		DiscoveryListener listener = new DiscoveryListener() {

			public void serviceSearchCompleted(int transID, int respCode) {
				
				//Method implemented because of DiscoveryListener
				
			}

			public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
				
				//Method implemented because of DiscoveryListener
				
			}
			
			public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
				
				try {
					
					if(btDevice.getFriendlyName(false).equals(args[0]) || btDevice.getBluetoothAddress().equals(args[0])) {
						
						System.out.println("Device " + btDevice.getBluetoothAddress() + " found with " + btDevice.getFriendlyName(false) + " as name");
						
						devicesDiscovered.addElement(btDevice);
						
					}
				} catch (IOException cantGetDeviceName) {
				}
			}

			public void inquiryCompleted(int discType) {
				System.out.println("Device Inquiry completed!");
				synchronized(inquiryCompletedEvent){
					inquiryCompletedEvent.notifyAll();
				}
			}
		};

		synchronized(inquiryCompletedEvent) {
			boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
			if (started) {
				System.out.println("Wait for device inquiry to complete...");
				inquiryCompletedEvent.wait();
				System.out.println(devicesDiscovered.size() +  " device(s) found");
			}
		}
	}
	
}
