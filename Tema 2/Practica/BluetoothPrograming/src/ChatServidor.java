import java.io.*;
import java.util.Scanner;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class ChatServidor {
	private static String mensajeServer = "";
	private static String response = "";

	public void startServer() throws IOException {
		String url = "btspp://localhost:" + new UUID(0x1101).toString() + ";name=chat";
		StreamConnectionNotifier service = (StreamConnectionNotifier) Connector.open(url);
		
		LocalDevice ld = LocalDevice.getLocalDevice();
		System.out.println("Datos del servidor: " + ld.getBluetoothAddress() + " - " + ld.getFriendlyName());
		System.out.println("\nServidor Iniciado. Esperando clientes...");
		
		StreamConnection con = (StreamConnection) service.acceptAndOpen();
		RemoteDevice dev = RemoteDevice.getRemoteDevice(con);
		System.out.println("Direcci�nn del dispositivo remoto: " + dev.getBluetoothAddress());
		System.out.println("Nombre del dispositivo remoto: " + dev.getFriendlyName(true));

		InputStream is = con.openInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		OutputStream os	= con.openOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
		
		Scanner sc = new Scanner(System.in);
		String response = "";

		while (!mensajeServer.equals("FIN.")) {
			System.out.println("Esperano respuesta del cliente...");
			mensajeServer = br.readLine();
			System.out.println(dev.getBluetoothAddress() + " - " + dev.getFriendlyName(true) + ": " + mensajeServer);
			System.out.print(ld.getFriendlyName() + ": ");
			response = sc.nextLine();
			bw.write(response);
			bw.newLine();
			bw.flush();
		}
		System.out.println("Dispositivo " + dev.getBluetoothAddress() + " - " + dev.getFriendlyName(true) + " desconectado correctamente");
		br.close();
		bw.close();
		sc.close();
		mensajeServer = "";
		con.close();
	}
	
	public void startClient() throws IOException {
		
		String url = "btspp://localhost:" + new UUID(0x1101).toString() + ";name=chat";
		StreamConnectionNotifier service = (StreamConnectionNotifier) Connector.open(url);
		
		LocalDevice ld = LocalDevice.getLocalDevice();
		System.out.println("Datos del cliente: " + ld.getBluetoothAddress() + " - " + ld.getFriendlyName());
		System.out.println("\nCliente Iniciado.");
		
		StreamConnection con = (StreamConnection) service.acceptAndOpen();
		RemoteDevice dev = RemoteDevice.getRemoteDevice(con);
		System.out.println("Direcci�nn del dispositivo remoto: " + dev.getBluetoothAddress());
		System.out.println("Nombre del dispositivo remoto: " + dev.getFriendlyName(true));

		InputStream is = con.openInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		OutputStream os	= con.openOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
		
		Scanner sc = new Scanner(System.in);
		String mensajeCliente = "";

		while (!mensajeServer.equals("FIN.")) {
			System.out.println("Esperano respuesta del servidor...");
			response = br.readLine();
			System.out.println(dev.getBluetoothAddress() + " - " + dev.getFriendlyName(true) + ": " + mensajeServer);
			System.out.print(ld.getFriendlyName() + ": ");
			mensajeCliente = sc.nextLine();
			bw.write(mensajeCliente);
			bw.newLine();
			bw.flush();
		}
		System.out.println("Dispositivo " + dev.getBluetoothAddress() + " - " + dev.getFriendlyName(true) + " desconectado correctamente");
		br.close();
		bw.close();
		sc.close();
		mensajeServer = "";
		con.close();
	}

	public static void main(String args[]) throws IOException {
		ChatServidor servidor = new ChatServidor();
		servidor.startServer();
		servidor.startClient();
	}
}
