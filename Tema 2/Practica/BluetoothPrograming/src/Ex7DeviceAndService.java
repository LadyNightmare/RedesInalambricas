import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

public class Ex7DeviceAndService {

	public static final Vector/*<String>*/ serviceFound = new Vector();
	private static final int SERVICE_NAME_ATTRID = 0x0100;

	public static void main(String[] args) throws IOException, InterruptedException {

		// First run RemoteDeviceDiscovery and use discoved device
		Ex3DiscoverFriendDevices.main(args);

		serviceFound.clear();

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
					if (serviceName != null  && ((String)serviceName.getValue()).equals(args[1])) {
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

		UUID uuids[] = new UUID[1];
		uuids[0] = new UUID(0x1002); //PublicBrowseGroup
		
		int attridset[] = new int[1];
		attridset[0] = SERVICE_NAME_ATTRID;

		for(Enumeration en = Ex3DiscoverFriendDevices.devicesDiscovered.elements(); en.hasMoreElements(); ) {
			RemoteDevice btDevice = (RemoteDevice)en.nextElement();

			synchronized(serviceSearchCompletedEvent) {
				System.out.println("search services on " + btDevice.getBluetoothAddress() + " " + btDevice.getFriendlyName(false));
				LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attridset, uuids, btDevice, listener);
				serviceSearchCompletedEvent.wait();
			}
		}

	}

}
