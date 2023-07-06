
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class Client {
	@SuppressWarnings({ "unchecked", "rawtypes", "resource", "unused" })

	public static ArrayList<String> name = new ArrayList<String>();

	/**
	 * Client functionality
	 */
	public Client() {
		Socket socket;

		ArrayList al;
		ArrayList<FileInfo> arrList = new ArrayList<FileInfo>();
		Scanner scanner = new Scanner(System.in);
		ObjectInputStream ois;
		ObjectOutputStream oos;
		String string;
		Object o, b;
		String directoryPath = null;
		int peerServerPort = 0;
		DataOutputStream dataoutput;
		DataInputStream datainput;

		String Client_name = JOptionPane.showInputDialog("Please Eneter Your Username");
		if (Client_name.equals("")) {
			JOptionPane.showMessageDialog(null, "Enter username please");
			Client_name = JOptionPane.showInputDialog("Please Eneter Your Username Aagin");
			name.add(Client_name);

		} else {

			name.add(Client_name);
		}

		for (int i = 0; i < name.size(); i++) {
			System.out.println(name.get(i));
		}

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			JOptionPane.showMessageDialog(null, "Welcome " + Client_name + ", you have joined as a client!");

			System.out.println(" ");
			directoryPath = JOptionPane.showInputDialog("Upload directory to share files");
			if (directoryPath.equals("exit")) {
				System.exit(0);
			}

			JFrame f = new JFrame("Uploading files");

			JPanel p = new JPanel();

			JProgressBar c = new JProgressBar();

			c.setValue(0);

			c.setStringPainted(true);

			p.add(c);

			f.add(p);

			f.setSize(150, 150);
			f.setVisible(true);

			fillUpload(c);
			f.setVisible(false);

			peerServerPort = Integer
					.parseInt(JOptionPane.showInputDialog("Enter the port on which you want to share your files on"));

			ServerDownload objServerDownload = new ServerDownload(peerServerPort, directoryPath);
			objServerDownload.start();

			Socket clientThread = new Socket("localhost", 7799);

			ObjectOutputStream objOutStream = new ObjectOutputStream(clientThread.getOutputStream());
			ObjectInputStream objInStream = new ObjectInputStream(clientThread.getInputStream());

			al = new ArrayList();

			socket = new Socket("localhost", 7799);

			JOptionPane.showMessageDialog(null, "Connection established");

			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());

			int readpid = Integer.parseInt(JOptionPane.showInputDialog("Enter client id"));

			File folder = new File(directoryPath);
			File[] listofFiles = folder.listFiles();
			FileInfo currentFile;
			File file;

			for (int i = 0; i < listofFiles.length; i++) {
				currentFile = new FileInfo();
				file = listofFiles[i];
				currentFile.fileName = file.getName();
				currentFile.peerid = readpid;
				currentFile.portNumber = peerServerPort;
				arrList.add(currentFile);
			}
			oos.writeObject(arrList);

			ArrayList<FileInfo> totalfiles = new ArrayList<FileInfo>();

			totalfiles = (ArrayList<FileInfo>) ois.readObject();

			String searchFile = JOptionPane.showInputDialog("Search for your files to download");
			String[] SFsplit = searchFile.split("");
			int SFsize = SFsplit.length;

			int k = 0;
			while (k < totalfiles.size()) {
				String[] TFsplit = totalfiles.get(k).fileName.split("");
				int TFsize = TFsplit.length;

				int smallestlength = Math.min(SFsize, TFsize);

				int matchsore = 0;
				for (int j = 0; j < smallestlength; j++) {

					if (SFsplit[j].equals(TFsplit[j])) {
						matchsore = matchsore + 1;

						if (matchsore > 3) {

							System.out.println("Filenames with matching names: " + totalfiles.get(k).fileName);

						} else {
							System.out.println("No files with similar name found");
						}
					}

				}
				k++;

			}

			String fileNameToDownload = JOptionPane.showInputDialog("Name of file to Download");

			if (fileNameToDownload.equals("exit")) {
				JOptionPane.showMessageDialog(null, "You have exited!");
				System.exit(0);
			}
			oos.writeObject(fileNameToDownload);

			JOptionPane.showMessageDialog(null, "Waiting for Server to reply!");

			ArrayList<FileInfo> peers = new ArrayList<FileInfo>();
			peers = (ArrayList<FileInfo>) ois.readObject();

			for (int i = 0; i < peers.size(); i++) {
				int result = peers.get(i).peerid;
				int port = peers.get(i).portNumber;

				System.out.println("The file is stored on Port: " + port + " client id: " + result);
				JOptionPane.showMessageDialog(null, "The file is stored on Port: " + port + " client id: " + result);
			}

			int clientAsServerPortNumber = Integer.parseInt(JOptionPane.showInputDialog("Enter port number"));

			int clientAsServerPeerid = Integer.parseInt(JOptionPane.showInputDialog("Enter client id"));

			clientAsServer(clientAsServerPeerid, clientAsServerPortNumber, fileNameToDownload, directoryPath);
		} catch (Exception e) {

			JOptionPane.showMessageDialog(null,
					"Error in establishing the Connection between the Client and the Server!! ");

			JOptionPane.showMessageDialog(null, "Please cross-check the host address and the port number..");
			System.exit(0);
		}
	}

	/**
	 * @param clientAsServerPeerid
	 * @param clientAsServerPortNumber
	 * @param fileNamedwld
	 * @param directoryPath
	 * @throws ClassNotFoundException
	 */
	public static void clientAsServer(int clientAsServerPeerid, int clientAsServerPortNumber, String fileNamedwld,
			String directoryPath) throws ClassNotFoundException {
		try {
			@SuppressWarnings("resource")
			Socket clientAsServersocket = new Socket("localhost", clientAsServerPortNumber);

			ObjectOutputStream clientAsServerOOS = new ObjectOutputStream(clientAsServersocket.getOutputStream());
			ObjectInputStream clientAsServerOIS = new ObjectInputStream(clientAsServersocket.getInputStream());

			clientAsServerOOS.writeObject(fileNamedwld);
			int readBytes = (int) clientAsServerOIS.readObject();

			byte[] arr_byte = new byte[readBytes];
			clientAsServerOIS.readFully(arr_byte);
			OutputStream fileOPstream = new FileOutputStream(directoryPath + "//" + fileNamedwld);

			@SuppressWarnings("resource")

			BufferedOutputStream BOS = new BufferedOutputStream(fileOPstream);
			BOS.write(arr_byte, 0, (int) readBytes);

			JFrame f = new JFrame("ProgressBar demo");

			JPanel p = new JPanel();

			JProgressBar b = new JProgressBar();

			b.setValue(0);

			b.setStringPainted(true);

			p.add(b);

			f.add(p);

			f.setSize(150, 150);
			f.setVisible(true);

			fill(b);
			f.setVisible(false);

			System.out.println("Requested file - " + fileNamedwld + ", has been downloaded to your desired directory "
					+ directoryPath);
			System.out.println(" ");
			System.out.println("Display file " + fileNamedwld);

			BOS.flush();
		} catch (IOException ex) {
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String status;
		try {
			status = br.readLine();
			if (status.equals("exit")) {
				System.exit(0);
			}
		} catch (IOException e) {

		}

	}

	/**
	 * @param b
	 */
	public static void fill(JProgressBar b) {
		int i = 0;
		try {
			while (i <= 100) {

				if (i > 30 && i < 70)
					b.setString("wait for sometime");
				else if (50 < i && i <= 99)
					b.setString("almost done ..");
				else if (i == 100) {
					b.setString("complete");
				} else
					b.setString("loading started");

				b.setValue(i + 10);

				Thread.sleep(1000);
				i += 30;
			}
		} catch (Exception e) {
		}
	}

	/**
	 * @param b
	 */
	public static void fillUpload(JProgressBar b) {

		int i = 0;
		try {
			while (i <= 100) {

				if (i > 30 && i < 70)
					b.setString("wait for sometime");
				else if (50 < i && i <= 99)
					b.setString("almost done ..");
				else if (i == 100) {
					b.setString("complete");
				} else
					b.setString("loading started");

				b.setValue(i + 10);

				Thread.sleep(1500);
				i += 30;
			}
		} catch (Exception e) {
		}
	}
}
