import javax.bluetooth.UUID;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import javax.bluetooth.*;
import java.lang.Object.*;
import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

public class Ex4ServicesKnownDevice {

	public static final Vector/*<String>*/ serviceFound = new Vector();

	public static void main(String[] args) throws IOException, InterruptedException {

		// First run RemoteDeviceDiscovery and use discoved device
		Ex3DiscoverFriendDevices.main(args);

		serviceFound.clear();

		UUID serviceUUID = new UUID(0x1002);

		final Object serviceSearchCompletedEvent = new Object();

		DiscoveryListener listener = new DiscoveryListener() {

			public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
			}

			public void inquiryCompleted(int discType) {
			}

			public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
				for (int i = 0; i < servRecord.length; i++) {
					String url = servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
					if (url == null) {
						continue;
					}
					DataElement serviceName = servRecord[i].getAttributeValue(0x0100);
					if (serviceName != null) {
						System.out.println("service " + serviceName.getValue() + " found " + url);
					} else {
						System.out.println("service found " + url);
					}
				}
			}

			public void serviceSearchCompleted(int transID, int respCode) {
				System.out.println("service search completed!");
				synchronized(serviceSearchCompletedEvent){
					serviceSearchCompletedEvent.notifyAll();
				}
			}

		};

		UUID[] searchUuidSet = new UUID[] { serviceUUID };
		int[] attrIDs =  new int[] {
				0x0100 // Service name
		};

		for(Enumeration en = Ex3DiscoverFriendDevices.devicesDiscovered.elements(); en.hasMoreElements(); ) {
			RemoteDevice btDevice = (RemoteDevice)en.nextElement();

			synchronized(serviceSearchCompletedEvent) {
				System.out.println("search services on " + btDevice.getBluetoothAddress() + " " + btDevice.getFriendlyName(false));
				LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attrIDs, searchUuidSet, btDevice, listener);
				serviceSearchCompletedEvent.wait();
			}
		}

	}

}
