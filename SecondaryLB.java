import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class SecondaryLB {
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

	public static int findOpenPort(int port) {
		try {
			new ServerSocket(port);
			return port;
		} catch (IOException e) {
			findOpenPort(++port);
		}
		return port;

	}

	public static ConcurrentHashMap<InetAddress, Integer> map = new ConcurrentHashMap<InetAddress, Integer>();
	public static ConcurrentHashMap<InetAddress, ArrayList<Double>> averageLoad = new ConcurrentHashMap<InetAddress, ArrayList<Double>>();
	public static ConcurrentHashMap<InetAddress, Integer> requestCount = new ConcurrentHashMap<InetAddress, Integer>();

	public static void main(String[] args) throws UnknownHostException,
			IOException {
		int pport;
		pport = findOpenPort(2000);
		Socket s = new Socket("Enter Your Primary Load Balancer's IP Here", 1500);
		PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
		pw.println("join " + pport);
		s.close();

		// Server join request -- start
		new Thread() {
			public void run() {
				int savePort = 6767;
				while (true) {
					try {
						ServerSocket ss = new ServerSocket(1700);
						Socket cs = ss.accept();
						BufferedReader br = new BufferedReader(
								new InputStreamReader(cs.getInputStream()));
						String input = br.readLine();
						int i = 0;
						System.out.println("I got the join request");
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
		// Server join request -- end

		new Thread() {
			public void run() {
				int nextServer = 0;
				while (true) {
					try {
						ServerSocket ss = new ServerSocket(1999);
						Socket cs = ss.accept();
						BufferedReader br = new BufferedReader(
								new InputStreamReader(cs.getInputStream()));
						String input = br.readLine();
						System.out.println("Received from primary" + input);
						if (input.charAt(input.length() - 4) == 'E'
								&& input.charAt(input.length() - 3) == 'X'
								&& input.charAt(input.length() - 2) == 'I'
								&& input.charAt(input.length() - 1) == 'T') {
							Thread.interrupted();
						}
						InetAddress minLoadIp = null;
						double minLoad = 99999;
						double minMemory = 999999999;
						for (Map.Entry<InetAddress, Integer> j : map.entrySet()) {
							Socket tmpSoc = new Socket();
							try {
								Vector<String> minIPVector;
								tmpSoc.connect(new InetSocketAddress(
										j.getKey(), 2001), 5000);
								PrintWriter pw = new PrintWriter(
										tmpSoc.getOutputStream(), true);
								pw.println("Send load");
								BufferedReader br1 = new BufferedReader(
										new InputStreamReader(
												tmpSoc.getInputStream()));
								String input2 = br1.readLine();
								double load = Double.parseDouble(input2);
								double memoryAvailable = Double.parseDouble(br1
										.readLine());

								System.out.println("Load on " + j.getKey()
										+ " is: " + load);
								ArrayList<Double> l = averageLoad.get(j
										.getKey());
								if (l == null)
									averageLoad.put(j.getKey(),
											l = new ArrayList<Double>());
								l.add(load);
								tmpSoc.close();
							} catch (Exception ste) {
								map.remove(j.getKey());
							}
						}
						int temp = 0;
						for (Map.Entry<InetAddress, Integer> j : map.entrySet()) {
							if ((temp) == nextServer) {
								minLoadIp = j.getKey();
								nextServer++;
								nextServer = nextServer % (map.size());
								break;
							} else {
								temp++;
							}
						}
						System.out.println("We selected " + minLoadIp);
						Integer l = requestCount.get(minLoadIp);
						if (l != null) {
							l++;
							requestCount.put(minLoadIp, l);
						} else {
							requestCount.put(minLoadIp, 1);
						}
						Socket ss1 = new Socket(minLoadIp, 2020);
						PrintWriter pw = new PrintWriter(ss1.getOutputStream(),
								true);
						pw.println(input);
						ss1.close();
						cs.close();
						ss.close();
					} catch (Exception e) {

					}
				}
			}
		}.start();

		// PrimaryLB request -- start
		new Thread() {
			public void run() {
				int nextServer = 0;
				while (true) {
					try {
						ServerSocket ss = new ServerSocket(1998);
						Socket cs = ss.accept();
						BufferedReader br = new BufferedReader(
								new InputStreamReader(cs.getInputStream()));
						String input = br.readLine();
						System.out.println("String received: " + input);
						InetAddress minLoadIp = null;
						double minLoad = 99999;
						double minMemory = 999999999;
						for (Map.Entry<InetAddress, Integer> j : map.entrySet()) {
							Socket tmpSoc = new Socket();
							try {
								tmpSoc.connect(new InetSocketAddress(
										j.getKey(), 2001), 5000);
								PrintWriter pw = new PrintWriter(
										tmpSoc.getOutputStream(), true);
								pw.println("Send load");
								BufferedReader br1 = new BufferedReader(
										new InputStreamReader(
												tmpSoc.getInputStream()));
								String input2 = br1.readLine();
								double load = Double.parseDouble(input2);
								double memoryAvailable = Double.parseDouble(br1
										.readLine());

								System.out.println("Load on " + j.getKey()
										+ " is: " + load);
								ArrayList<Double> l = averageLoad.get(j
										.getKey());
								if (l == null)
									averageLoad.put(j.getKey(),
											l = new ArrayList<Double>());
								l.add(load);
								tmpSoc.close();
							} catch (Exception ste) {
								map.remove(j.getKey());
							}
						}
						int temp = 0;
						for (Map.Entry<InetAddress, Integer> j : map.entrySet()) {
							if ((temp) == nextServer) {
								minLoadIp = j.getKey();
								nextServer++;
								nextServer = nextServer % (map.size());
								break;
							} else {
								temp++;
							}
						}
						System.out.println("We selected " + minLoadIp);
						Integer l = requestCount.get(minLoadIp);
						if (l != null) {
							l++;
							requestCount.put(minLoadIp, l);
						} else {
							requestCount.put(minLoadIp, 1);
						}

						Socket ss1 = new Socket(minLoadIp, whichPort());
						PrintWriter pw = new PrintWriter(ss1.getOutputStream(),
								true);
						pw.println(input);
						ss1.close();
						cs.close();
						ss.close();

					} catch (IOException e) {

					}

				}
			}
		}.start();
		// PrimaryLB join request -- end

		while (true) {
			System.out.println("Do you want to print? (1/0)");
			Scanner scan = new Scanner(System.in);
			int i = scan.nextInt();
			if (i == 1) {
				System.out
						.println("-------------LOAD BALANCING USING ROUND ROBIN-------------");
				System.out
						.println("server\t\t\tAverage Load\t\tnumberOfRequests");

				for (Map.Entry<InetAddress, Integer> j : map.entrySet()) {
					ArrayList<Double> al = averageLoad.get(j.getKey());
					Double sum = 0.0;
					if (al != null) {
						for (int k = 0; k < al.size(); k++) {
							sum = sum + al.get(k);
						}

						if (requestCount.get(j.getKey()) != null) {
							System.out.println(j.getKey() + "\t \t" + sum
									/ al.size() + "\t  "
									+ requestCount.get(j.getKey()));
						} else {
							System.out.println(j.getKey() + "\t \t" + sum
									/ al.size() + "\t\t  " + "0");
						}
					} else {
						System.out.println(j.getKey() + "\t \t" + "NA"
								+ "\t\t  " + "0");

					}
				}
			}
		}
	}
}
