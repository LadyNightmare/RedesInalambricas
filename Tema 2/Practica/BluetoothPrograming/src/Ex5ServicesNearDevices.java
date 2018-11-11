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

public class Ex5ServicesNearDevices {

	static final UUID OBEX_FILE_TRANSFER = new UUID(0x1106);
    static final UUID OBEX_OBJECT_PUSH = new UUID(0x1105);

	public static final Vector/*<String>*/ serviceFound = new Vector();

	public static void main(String[] args) throws IOException, InterruptedException {

		// First run RemoteDeviceDiscovery and use discoved device
		Ex2DiscoverNearDevices.main(args);

		serviceFound.clear();

		UUID serviceUUID = OBEX_OBJECT_PUSH;
		if ((args != null) && (args.length > 0)) {
			serviceUUID = new UUID(args[0], false);
		}

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
					serviceFound.add(url);
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

		for(Enumeration en = Ex2DiscoverNearDevices.devicesDiscovered.elements(); en.hasMoreElements(); ) {
			RemoteDevice btDevice = (RemoteDevice)en.nextElement();

			synchronized(serviceSearchCompletedEvent) {
				System.out.print("search services on " + btDevice.getBluetoothAddress() + " ");
				try {
					System.out.print("name " + btDevice.getFriendlyName(false));
				} catch (IOException cantGetDeviceName) {
				}
				System.out.print("\n");
				LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attrIDs, searchUuidSet, btDevice, listener);
				serviceSearchCompletedEvent.wait();
			}
		}

	}
	
}
