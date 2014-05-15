import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PrimaryLB {

	public static ConcurrentHashMap<InetAddress, Integer> map = new ConcurrentHashMap<InetAddress, Integer>();

	public static int whichPort() {
		return 2000;
	}

	public static InetAddress whichSecondaryLB() {
		InetAddress returnAddress = null;
		for (Map.Entry<InetAddress, Integer> j : map.entrySet()) {
			returnAddress = j.getKey();
		}
		return returnAddress;
	}

	public static void main(String[] args) throws IOException {
		new Thread() {
			public void run() {
				while (true) {
					try {
						ServerSocket ss = new ServerSocket(2050);
						Socket cs = ss.accept();
						System.out.println("waiting for string");
						BufferedReader br = new BufferedReader(
								new InputStreamReader(cs.getInputStream()));
						final String input = br.readLine();
						final String inet = cs.getInetAddress() + "";
						System.out.println("String received: " + input);
						new Thread() {
							final String newInput = input;

							public void run() {
								try {
									String newNewInput = newInput;
									System.out.println("String received: "
											+ newNewInput);
									if (newNewInput.equals("EXIT")) {
										Socket ss1 = new Socket(
												whichSecondaryLB(), 1999);
										PrintWriter pw = new PrintWriter(
												ss1.getOutputStream(), true);
										newNewInput = inet + "|" + newNewInput;
										pw.println(newNewInput);
										ss1.close();
									}
									Socket ss1 = new Socket(whichSecondaryLB(),
											1999);
									PrintWriter pw = new PrintWriter(
											ss1.getOutputStream(), true);
									newNewInput = inet + "|" + newNewInput;
									pw.println(newNewInput);
									System.out.println("Sent");

									ss1.close();
								} catch (Exception e) {

								}
							}
						}.start();
						cs.close();
						ss.close();
						// }

					} catch (Exception e) {
					}
				}
			}
		}.start();
		// SecondaryLB join request -- start
		new Thread() {
			public void run() {
				int savePort = 6767;
				while (true) {
					try {
						ServerSocket ss = new ServerSocket(1500);
						Socket cs = ss.accept();
						BufferedReader br = new BufferedReader(
								new InputStreamReader(cs.getInputStream()));
						String input = br.readLine();
						int i = 0;
						if (input.charAt(i) == 'j') {
							String portPartA = input.substring(5);
							savePort = Integer.parseInt(portPartA);
							InetAddress ipAd = cs.getInetAddress();
							map.put(ipAd, savePort);
							cs.close();
							ss.close();

						}
					} catch (IOException e) {

					}

				}
			}
		}.start();
		// SecondaryLB join request -- end

		// Client request -- start
		new Thread() {
			public void run() {
				while (true) {
					try {
						ServerSocket ss = new ServerSocket(2000);
						Socket cs = ss.accept();
						BufferedReader br = new BufferedReader(
								new InputStreamReader(cs.getInputStream()));
						String input = br.readLine();
						System.out.println("Join request received");
						Socket ss1 = new Socket(whichSecondaryLB(), 1998);
						PrintWriter pw = new PrintWriter(ss1.getOutputStream(),
								true);
						System.out.println("Request sent to secondary on ip: "
								+ cs.getInetAddress() + "on port: " + 1998);
						input = cs.getInetAddress() + "|" + input;
						pw.println(input);
						ss1.close();
						cs.close();
						ss.close();

					} catch (IOException e) {

					}

				}
			}
		}.start();

		// Client request -- end

		while (true) {

		}

	}
}