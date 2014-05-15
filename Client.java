import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) throws UnknownHostException,
			IOException, Exception {
		int j = 0;
		Socket socket = new Socket("Enter Your Primary Load Balancer's IP Here", 2000);
		PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
		pw.println(args[0] + "|" + args[1]);
		System.out.println("Join request sent");
		socket.close();
		ServerSocket ss = new ServerSocket(2000);
		Socket cs = ss.accept();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				cs.getInputStream()));
		String input = br.readLine();
		System.out.println(input);
		cs.close();
		ss.close();

		String option = "";
		Scanner scan = new Scanner(System.in);
		System.out.println("--------------------");
		while (!option.equals("4")) {
			System.out.println("1. Show Categories");
			System.out.println("2. Show items in Cart");
			System.out.println("3. Total Bill");
			System.out.println("4. Exit");
			System.out.print("Select Your Option: ");
			option = scan.next();
			if (option.equals("1")) {
				Socket socketForTalking = new Socket("Enter Your Primary Load Balancer's IP Here", 2050);
				PrintWriter pwForTalking = new PrintWriter(
						socketForTalking.getOutputStream(), true);
				pwForTalking.println(args[0] + "|SC");
				socketForTalking.close();
				ServerSocket ssForTalking = new ServerSocket(2100);
				Socket csForTalking = ssForTalking.accept();
				BufferedReader brForTalking = new BufferedReader(
						new InputStreamReader(csForTalking.getInputStream()));
				String caught = brForTalking.readLine();
				for (int i = 0; i < caught.length(); i++) {
					if (caught.charAt(i) == '|')
						System.out.print('\t');
					else if (caught.charAt(i) == '!')
						System.out.print('\n');
					else
						System.out.print(caught.charAt(i));
				}
				csForTalking.close();
				ssForTalking.close();
				System.out.print("Please select the subCategory code: ");
				option = scan.next();
				Socket socketForTalking1 = new Socket("Enter Your Primary Load Balancer's IP Here", 2050);
				PrintWriter pwForTalking1 = new PrintWriter(
						socketForTalking1.getOutputStream(), true);
				pwForTalking1.println(args[0] + "|" + option);
				socketForTalking1.close();
				System.out.println("Sending this at line 77: " + args[0] + "|"
						+ option);
				ServerSocket ssForTalking1 = new ServerSocket(2100);
				Socket csForTalking1 = ssForTalking1.accept();
				BufferedReader brForTalking1 = new BufferedReader(
						new InputStreamReader(csForTalking1.getInputStream()));
				String caught1 = brForTalking1.readLine();
				csForTalking1.close();
				ssForTalking1.close();
				for (int i = 0; i < caught1.length(); i++) {
					if (caught1.charAt(i) == '|')
						System.out.print('\t');
					else if (caught1.charAt(i) == '!')
						System.out.print('\n');
					else
						System.out.print(caught1.charAt(i));
				}
				System.out
						.print("Please select the item you want to purchase or 0: ");
				option = scan.next();
				if (option.equals("0")) {
					break;
				} else {
					Socket socketForTalking2 = new Socket("Enter Your Primary Load Balancer's IP Here", 2050);
					PrintWriter pwForTalking2 = new PrintWriter(
							socketForTalking2.getOutputStream(), true);
					pwForTalking2.println(args[0] + "|" + option + "");
					socketForTalking2.close();
					ServerSocket ssForTalking2 = new ServerSocket(2100);
					Socket csForTalking2 = ssForTalking2.accept();
					BufferedReader brForTalking2 = new BufferedReader(
							new InputStreamReader(
									csForTalking2.getInputStream()));

					String caught2 = brForTalking2.readLine();
					for (int i = 0; i < caught2.length(); i++) {
						if (caught2.charAt(i) == '|')
							System.out.print('\t');
						else if (caught2.charAt(i) == '!')
							System.out.print('\n');
						else
							System.out.print(caught2.charAt(i));
					}
					csForTalking2.close();
					ssForTalking2.close();
				}
			} else if (option.equals("2")) {
				Socket socketForTalking3 = new Socket("Enter Your Primary Load Balancer's IP Here", 2050);
				PrintWriter pwForTalking3 = new PrintWriter(
						socketForTalking3.getOutputStream(), true);
				pwForTalking3.println(args[0] + "|" + "SI");
				socketForTalking3.close();
				ServerSocket ssForTalking = new ServerSocket(2100);
				Socket csForTalking = ssForTalking.accept();
				BufferedReader brForTalking = new BufferedReader(
						new InputStreamReader(csForTalking.getInputStream()));
				String caught = brForTalking.readLine();
				for (int i = 0; i < caught.length(); i++) {
					if (caught.charAt(i) == '|')
						System.out.print('\t');
					else if (caught.charAt(i) == '!')
						System.out.print('\n');
					else
						System.out.print(caught.charAt(i));
				}

				csForTalking.close();
				ssForTalking.close();
				System.out
						.print("Do you want to remove any item from from cart?(1(YES)/0(NO)): ");
				option = scan.next();
				if (option.equals("1")) {
					System.out.print("Please enter item number: ");
					option = scan.next();
					Socket socketForTalking4 = new Socket("Enter Your Primary Load Balancer's IP Here", 2050);
					PrintWriter pwForTalking4 = new PrintWriter(
							socketForTalking4.getOutputStream(), true);
					pwForTalking4.println(args[0] + "|" + "R|" + option);
					socketForTalking4.close();
					ServerSocket ssForTalking1 = new ServerSocket(2100);
					Socket csForTalking1 = ssForTalking1.accept();
					BufferedReader brForTalking1 = new BufferedReader(
							new InputStreamReader(
									csForTalking1.getInputStream()));
					String caught1 = brForTalking1.readLine();
					for (int i = 0; i < caught1.length(); i++) {
						if (caught1.charAt(i) == '|')
							System.out.print('\t');
						else if (caught1.charAt(i) == '!')
							System.out.print('\n');
						else
							System.out.print(caught1.charAt(i));
					}
					csForTalking1.close();
					ssForTalking1.close();
				}
			} else if (option.equals("3")) {
				Socket socketForTalking5 = new Socket("Enter Your Primary Load Balancer's IP Here", 2050);
				PrintWriter pwForTalking5 = new PrintWriter(
						socketForTalking5.getOutputStream(), true);
				pwForTalking5.println(args[0] + "|" + "TB");
				socketForTalking5.close();
				ServerSocket ssForTalking = new ServerSocket(2100);
				Socket csForTalking = ssForTalking.accept();
				BufferedReader brForTalking = new BufferedReader(
						new InputStreamReader(csForTalking.getInputStream()));
				String caught = brForTalking.readLine();
				for (int i = 0; i < caught.length(); i++) {
					if (caught.charAt(i) == '|')
						System.out.print('\t');
					else if (caught.charAt(i) == '!')
						System.out.print('\n');
					else
						System.out.print(caught.charAt(i));
				}
				System.out.println();
				csForTalking.close();
				ssForTalking.close();
			} else {
				Socket socketForTalking6 = new Socket("Enter Your Primary Load Balancer's IP Here", 2050);
				PrintWriter pwForTalking6 = new PrintWriter(
						socketForTalking6.getOutputStream(), true);
				pwForTalking6.println("EXIT");
				socketForTalking6.close();
				ServerSocket ssForTalking = new ServerSocket(2100);
				Socket csForTalking = ssForTalking.accept();
				BufferedReader brForTalking = new BufferedReader(
						new InputStreamReader(csForTalking.getInputStream()));
				String caught = brForTalking.readLine();
				System.out.println(caught);
				csForTalking.close();
				ssForTalking.close();
				System.exit(0);
			}
		}
	}
}
