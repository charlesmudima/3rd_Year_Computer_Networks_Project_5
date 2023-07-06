
import java.io.*;
import java.net.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.Vector;
import java.lang.Runnable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import java.lang.Integer;

@SuppressWarnings("unused")

public class Server {
	public static ArrayList<FileInfo> globalArray = new ArrayList<FileInfo>();
	private static int port = 4454;
	private static ServerSocket serverSocket;
	private static DataInputStream input;
	private static DataOutputStream output;
	static int clientsConnected = 0;

	@SuppressWarnings("resource")
	public Server() throws NumberFormatException, IOException {

		ServerSocket serverSocket = null;
		Socket socket = null;
		try {
			serverSocket = new ServerSocket(7799);
			System.out.println("Server started!! ");
			System.out.println(" ");
			JOptionPane.showMessageDialog(null, "Waiting for the Client to be connected ..");
		} catch (IOException e) {
			System.out.println("Error with clients connecting to the Server: " + e);

		}
		while (true) {
			try {
				socket = serverSocket.accept();

			} catch (IOException e) {

				System.out.println("Client disconnected");
			}
			new ServerTestClass(socket, globalArray).start();
		}
	}

}

class ServerTestClass extends Thread {
	protected Socket socket;
	ArrayList<FileInfo> globalArray;

	public ServerTestClass(Socket clientSocket, ArrayList<FileInfo> globalArray) {
		this.socket = clientSocket;
		this.globalArray = globalArray;
	}

	ArrayList<FileInfo> filesList = new ArrayList<FileInfo>();
	ArrayList<FileInfo> clientList = new ArrayList<FileInfo>();
	ObjectOutputStream oos;
	ObjectInputStream ois;
	String str;
	int index;

	@SuppressWarnings("unchecked")
	public void run() {
		try {
			InputStream is = socket.getInputStream();
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(is);

			filesList = (ArrayList<FileInfo>) ois.readObject();
			for (int i = 0; i < filesList.size(); i++) {
				globalArray.add(filesList.get(i));

			}

			oos.writeObject(globalArray);
			System.out.println(
					"Total number of files available in clients connected to the Server "
							+ globalArray.size());
		}

		catch (IndexOutOfBoundsException e) {
			System.out.println("Index out of bounds exception");
		} catch (IOException e) {
			System.out.println("I/O exception");
		} catch (ClassNotFoundException e) {
			System.out.println("Class not found exception");
		}

		try {
			str = (String) ois.readObject();
		} catch (IOException | ClassNotFoundException ex) {

		}

		ArrayList<FileInfo> sendingPeers = new ArrayList<FileInfo>();
		System.out.println("Searching for the file name.");

		for (int j = 0; j < globalArray.size(); j++) {
			FileInfo fileInfo = globalArray.get(j);
			Boolean tf = fileInfo.fileName.equals(str);
			if (tf) {
				index = j;
				sendingPeers.add(fileInfo);
			}
		}

		try {
			oos.writeObject(sendingPeers);
		} catch (IOException ex) {

		}
	}
}
