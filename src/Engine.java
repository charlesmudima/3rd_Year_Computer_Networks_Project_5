import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;

public class Engine {
	public static String name;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {

		int choice = Integer
				.parseInt(JOptionPane.showInputDialog("Please enter you choice (1) Run Server or (2) Run Client"));

		if (choice == 1) {
			Server server = new Server();
		} else if (choice == 2) {

			Client client = new Client();

		}

	}

}
