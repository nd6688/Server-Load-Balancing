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
import java.util.concurrent.ConcurrentHashMap;
import java.lang.management.*;
import java.lang.Runtime;

public class Server {

	public static ConcurrentHashMap<String, String> userData = new ConcurrentHashMap<String, String>();
	public static ConcurrentHashMap<String, String> userIP = new ConcurrentHashMap<String, String>();
	public static ConcurrentHashMap<String, String> itemCategory = new ConcurrentHashMap<String, String>();

	public static ConcurrentHashMap<String, String> veggiesCategoryList = new ConcurrentHashMap<String, String>();
	public static ConcurrentHashMap<String, String> breadCategoryList = new ConcurrentHashMap<String, String>();
	public static ConcurrentHashMap<String, String> pulsesCategoryList = new ConcurrentHashMap<String, String>();
	public static ConcurrentHashMap<String, String> softDrinksCategoryList = new ConcurrentHashMap<String, String>();

	public static ConcurrentHashMap<String, ArrayList<String>> shoppingCart = new ConcurrentHashMap<String, ArrayList<String>>();
	public static ConcurrentHashMap<String, Integer> priceList = new ConcurrentHashMap<String, Integer>();

	public static int findOpenPort(int port) {
		try {
			new ServerSocket(port);
			return port;
		} catch (IOException e) {
			findOpenPort(++port);
		}
		return port;
	}

	public static void main(String[] args) throws UnknownHostException,
			IOException {

		itemCategory.put("01", "Vegetables");
		itemCategory.put("02", "Bread");
		itemCategory.put("03", "Pulses");
		itemCategory.put("04", "Soft Drinks");

		veggiesCategoryList.put("11", "Carrot");
		veggiesCategoryList.put("12", "Brocolli");
		veggiesCategoryList.put("13", "Cabbage");
		veggiesCategoryList.put("14", "Peppers");

		breadCategoryList.put("21", "Brown");
		breadCategoryList.put("22", "Fiber");
		breadCategoryList.put("23", "Wheat");
		breadCategoryList.put("24", "Italian");

		pulsesCategoryList.put("31", "Black Eyed Peas");
		pulsesCategoryList.put("32", "Black Beans");
		pulsesCategoryList.put("33", "Chick Peas");
		pulsesCategoryList.put("34", "Green Peas");

		softDrinksCategoryList.put("41", "Coca Cola");
		softDrinksCategoryList.put("42", "Ginger Ale");
		softDrinksCategoryList.put("43", "Pepsi");
		softDrinksCategoryList.put("44", "Mountain Dew");

		priceList.put("11", 2);
		priceList.put("12", 1);
		priceList.put("13", 6);
		priceList.put("14", 8);

		priceList.put("21", 8);
		priceList.put("22", 5);
		priceList.put("23", 11);
		priceList.put("24", 10);

		priceList.put("31", 9);
		priceList.put("32", 7);
		priceList.put("33", 5);
		priceList.put("34", 3);

		priceList.put("41", 4);
		priceList.put("42", 6);
		priceList.put("43", 8);
		priceList.put("44", 1);

		int pport;
		pport = findOpenPort(2000);
		Socket s = new Socket("Enter Your Secondary Load Balancer's IP Here", 1700);

		PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
		pw.println("join " + pport);
		System.out.println("Join request sent");
		s.close();

		Socket replicatorSocket = new Socket("Enter Your Replicator's IP Here", 1500);

		PrintWriter pw1 = new PrintWriter(replicatorSocket.getOutputStream(),
				true);
		pw1.println("join " + 3001);
		replicatorSocket.close();

		new Thread() {
			public void run() {
				while (true) {
					try {
						ServerSocket ss = new ServerSocket(1600);
						Socket soc = ss.accept();
						BufferedReader br = new BufferedReader(
								new InputStreamReader(soc.getInputStream()));
						final String str = br.readLine();
						final String str1 = br.readLine();
						soc.close();
						ss.close();
						new Thread() {
							final String in = str;
							final String inIP = str1;

							public void run() {
								String temp = "";
								String clientID = "";
								String clientName = "";

								for (int i = 2; i < in.length(); i++) {
									if (in.charAt(i) == '|') {
										clientID = temp;
										temp = "";
									} else {
										temp = temp + in.charAt(i);
									}
								}
								clientName = temp;
								userData.put(clientID, clientName);
								temp = "";
								clientID = "";
								clientName = "";

								for (int i = 2; i < inIP.length(); i++) {
									if (inIP.charAt(i) == '|') {
										clientID = temp;
										temp = "";
									} else {
										temp = temp + inIP.charAt(i);
									}
								}
								clientName = temp;
								userIP.put(clientID, clientName);
							}
						}.start();

					} catch (Exception e) {

					}
				}
			}
		}.start();

		new Thread() {
			public void run() {

				while (true) {
					try {
						ServerSocket ss = new ServerSocket(2020);
						Socket cs = ss.accept();
						BufferedReader br = new BufferedReader(
								new InputStreamReader(cs.getInputStream()));
						final String input = br.readLine();
						cs.close();
						ss.close();
						new Thread() {
							final String in = input;

							public void run() {
								System.out
										.println("Request received from client is: "
												+ in);
								String clientAdd = "";
								String clientID = "";
								int clientPort = 2100;
								int count = 0;
								String tmp = "";
								for (int i = 1; i < in.length(); i++) {
									if (count == 0 && in.charAt(i) == '|') {
										clientAdd = tmp;
										count++;
										tmp = "";
									} else if (count == 1
											&& in.charAt(i) == '|') {
										clientID = tmp;
										count++;
										tmp = "";
									} else
										tmp = tmp + in.charAt(i);
								}
								try {
									Socket reply = new Socket();
									reply.connect(new InetSocketAddress(
											clientAdd, clientPort), 5000);
									PrintWriter pwReply = new PrintWriter(
											reply.getOutputStream(), true);
									if (tmp.equals("SC")) {
										String sendReply = "";
										for (Map.Entry<String, String> i : itemCategory
												.entrySet())
											sendReply = sendReply + i.getKey()
													+ "|" + i.getValue() + "!";
										pwReply.println(sendReply);

									} else if (tmp.equals("SI")) {
										String sendReply = "";
										ArrayList<String> al = shoppingCart
												.get(clientID);
										if (al == null || al.size() == 0)
											sendReply = "Cart Empty!";
										else {
											for (int i = 0; i < al.size(); i++) {
												if (al.get(i).charAt(0) == '1')
													sendReply = sendReply
															+ veggiesCategoryList
																	.get(al.get(i))
															+ "!";
												else if (al.get(i).charAt(0) == '2')
													sendReply = sendReply
															+ breadCategoryList
																	.get(al.get(i))
															+ "!";
												else if (al.get(i).charAt(0) == '3')
													sendReply = sendReply
															+ pulsesCategoryList
																	.get(al.get(i))
															+ "!";
												else if (al.get(i).charAt(0) == '4')
													sendReply = sendReply
															+ softDrinksCategoryList
																	.get(al.get(i))
															+ "!";
											}
										}
										pwReply.println(sendReply);

									} else if (tmp.charAt(0) == 'R') {
										String tmp1 = "";
										for (int i = 0; i < tmp.length(); i++) {
											if (tmp.charAt(i) == '|') {
												tmp1 = "";
											} else
												tmp1 = tmp1 + tmp.charAt(i);
										}
										ArrayList<String> al = shoppingCart
												.get(clientID);
										if (al != null) {
											al.remove(tmp1);
											Socket soc = new Socket(
													"Enter Your Replicator's IP Here", 3011);
											PrintWriter pRplictr = new PrintWriter(
													soc.getOutputStream(), true);
											pRplictr.println(clientID + "|"
													+ tmp1);
											soc.close();
										}
										pwReply.println("Item removed!");
									} else if (tmp.equals("TB")) {
										ArrayList<String> al = shoppingCart
												.get(clientID);
										String sendReply = "";
										int total = 0;
										if (al != null) {
											for (int k = 0; k < al.size(); k++) {
												if (al.get(k).charAt(0) == '1') {
													total = total
															+ priceList.get(al
																	.get(k));
												} else if (al.get(k).charAt(0) == '2') {
													total = total
															+ priceList.get(al
																	.get(k));
												} else if (al.get(k).charAt(0) == '3') {
													total = total
															+ priceList.get(al
																	.get(k));
												} else if (al.get(k).charAt(0) == '4') {
													total = total
															+ priceList.get(al
																	.get(k));
												}
											}
											sendReply = "Total = " + total
													+ "!";
										}
										pwReply.println(sendReply);

									} else if (tmp.equals("01")) {
										String sendReply = "";
										for (Map.Entry<String, String> i : veggiesCategoryList
												.entrySet())
											sendReply = sendReply + i.getKey()
													+ "|" + i.getValue() + "!";
										pwReply.println(sendReply);
									} else if (tmp.equals("02")) {
										System.out.println("I am here");
										String sendReply = "";
										for (Map.Entry<String, String> i : breadCategoryList
												.entrySet())
											sendReply = sendReply + i.getKey()
													+ "|" + i.getValue() + "!";
										System.out.println("Also here");
										pwReply.println("" + sendReply);
										System.out.println("Replied");
									} else if (tmp.equals("03")) {
										String sendReply = "";
										for (Map.Entry<String, String> i : pulsesCategoryList
												.entrySet())
											sendReply = sendReply + i.getKey()
													+ "|" + i.getValue() + "!";
										pwReply.println(sendReply);

									} else if (tmp.equals("04")) {
										String sendReply = "";
										for (Map.Entry<String, String> i : softDrinksCategoryList
												.entrySet())
											sendReply = sendReply + i.getKey()
													+ "|" + i.getValue() + "!";
										pwReply.println(sendReply);

									} else if (tmp.equals("11")) {
										ArrayList<String> l = shoppingCart
												.get(clientID);
										if (l == null)
											shoppingCart
													.put(clientID,
															l = new ArrayList<String>());
										l.add(tmp);
										pwReply.println("Item added in cart!");
										Socket soc = new Socket("Enter Your Replicator's IP Here",
												3010);
										PrintWriter pRplictr = new PrintWriter(
												soc.getOutputStream(), true);
										pRplictr.println(clientID + "|" + tmp);
										soc.close();
									} else if (tmp.equals("12")) {
										ArrayList<String> l = shoppingCart
												.get(clientID);
										if (l == null)
											shoppingCart
													.put(clientID,
															l = new ArrayList<String>());
										l.add(tmp);
										pwReply.println("Item added in cart!");
										Socket soc = new Socket("Enter Your Replicator's IP Here",
												3010);
										PrintWriter pRplictr = new PrintWriter(
												soc.getOutputStream(), true);
										pRplictr.println(clientID + "|" + tmp);
										soc.close();
									} else if (tmp.equals("13")) {
										ArrayList<String> l = shoppingCart
												.get(clientID);
										if (l == null)
											shoppingCart
													.put(clientID,
															l = new ArrayList<String>());
										l.add(tmp);
										pwReply.println("Item added in cart!");
										Socket soc = new Socket("Enter Your Replicator's IP Here",
												3010);
										PrintWriter pRplictr = new PrintWriter(
												soc.getOutputStream(), true);
										pRplictr.println(clientID + "|" + tmp);
										soc.close();
									} else if (tmp.equals("14")) {
										ArrayList<String> l = shoppingCart
												.get(clientID);
										if (l == null)
											shoppingCart
													.put(clientID,
															l = new ArrayList<String>());
										l.add(tmp);
										pwReply.println("Item added in cart!");
										Socket soc = new Socket("Enter Your Replicator's IP Here",
												3010);
										PrintWriter pRplictr = new PrintWriter(
												soc.getOutputStream(), true);
										pRplictr.println(clientID + "|" + tmp);
										soc.close();

									} else if (tmp.equals("21")) {
										ArrayList<String> l = shoppingCart
												.get(clientID);
										if (l == null)
											shoppingCart
													.put(clientID,
															l = new ArrayList<String>());
										l.add(tmp);
										pwReply.println("Item added in cart!");
										Socket soc = new Socket("Enter Your Replicator's IP Here",
												3010);
										PrintWriter pRplictr = new PrintWriter(
												soc.getOutputStream(), true);
										pRplictr.println(clientID + "|" + tmp);
										soc.close();

									} else if (tmp.equals("22")) {
										ArrayList<String> l = shoppingCart
												.get(clientID);
										if (l == null)
											shoppingCart
													.put(clientID,
															l = new ArrayList<String>());
										l.add(tmp);
										pwReply.println("Item added in cart!");
										Socket soc = new Socket("Enter Your Replicator's IP Here",
												3010);
										PrintWriter pRplictr = new PrintWriter(
												soc.getOutputStream(), true);
										pRplictr.println(clientID + "|" + tmp);
										soc.close();
									} else if (tmp.equals("23")) {
										ArrayList<String> l = shoppingCart
												.get(clientID);
										if (l == null)
											shoppingCart
													.put(clientID,
															l = new ArrayList<String>());
										l.add(tmp);
										pwReply.println("Item added in cart!");
										Socket soc = new Socket("Enter Your Replicator's IP Here",
												3010);
										PrintWriter pRplictr = new PrintWriter(
												soc.getOutputStream(), true);
										pRplictr.println(clientID + "|" + tmp);
										soc.close();
									} else if (tmp.equals("24")) {
										ArrayList<String> l = shoppingCart
												.get(clientID);
										if (l == null)
											shoppingCart
													.put(clientID,
															l = new ArrayList<String>());
										l.add(tmp);
										Socket soc = new Socket("Enter Your Replicator's IP Here",
												3010);
										PrintWriter pRplictr = new PrintWriter(
												soc.getOutputStream(), true);
										pRplictr.println(clientID + "|" + tmp);
										soc.close();
									} else if (tmp.equals("31")) {
										ArrayList<String> l = shoppingCart
												.get(clientID);
										if (l == null)
											shoppingCart
													.put(clientID,
															l = new ArrayList<String>());
										l.add(tmp);
										pwReply.println("Item added in cart!");
										Socket soc = new Socket("Enter Your Replicator's IP Here",
												3010);
										PrintWriter pRplictr = new PrintWriter(
												soc.getOutputStream(), true);
										pRplictr.println(clientID + "|" + tmp);
										soc.close();
									} else if (tmp.equals("32")) {
										ArrayList<String> l = shoppingCart
												.get(clientID);
										if (l == null)
											shoppingCart
													.put(clientID,
															l = new ArrayList<String>());
										l.add(tmp);
										pwReply.println("Item added in cart!");
										Socket soc = new Socket("Enter Your Replicator's IP Here",
												3010);
										PrintWriter pRplictr = new PrintWriter(
												soc.getOutputStream(), true);
										pRplictr.println(clientID + "|" + tmp);
										soc.close();
									} else if (tmp.equals("33")) {
										ArrayList<String> l = shoppingCart
												.get(clientID);
										if (l == null)
											shoppingCart
													.put(clientID,
															l = new ArrayList<String>());
										l.add(tmp);
										pwReply.println("Item added in cart!");
										Socket soc = new Socket("Enter Your Replicator's IP Here",
												3010);
										PrintWriter pRplictr = new PrintWriter(
												soc.getOutputStream(), true);
										pRplictr.println(clientID + "|" + tmp);
										soc.close();
									} else if (tmp.equals("34")) {
										ArrayList<String> l = shoppingCart
												.get(clientID);
										if (l == null)
											shoppingCart
													.put(clientID,
															l = new ArrayList<String>());
										l.add(tmp);
										pwReply.println("Item added in cart!");
										Socket soc = new Socket("Enter Your Replicator's IP Here",
												3010);
										PrintWriter pRplictr = new PrintWriter(
												soc.getOutputStream(), true);
										pRplictr.println(clientID + "|" + tmp);
										soc.close();
									} else if (tmp.equals("41")) {
										ArrayList<String> l = shoppingCart
												.get(clientID);
										if (l == null)
											shoppingCart
													.put(clientID,
															l = new ArrayList<String>());
										l.add(tmp);
										pwReply.println("Item added in cart!");
										Socket soc = new Socket("Enter Your Replicator's IP Here",
												3010);
										PrintWriter pRplictr = new PrintWriter(
												soc.getOutputStream(), true);
										pRplictr.println(clientID + "|" + tmp);
										soc.close();
									} else if (tmp.equals("42")) {
										ArrayList<String> l = shoppingCart
												.get(clientID);
										if (l == null)
											shoppingCart
													.put(clientID,
															l = new ArrayList<String>());
										l.add(tmp);
										pwReply.println("Item added in cart!");
										Socket soc = new Socket("Enter Your Replicator's IP Here",
												3010);
										PrintWriter pRplictr = new PrintWriter(
												soc.getOutputStream(), true);
										pRplictr.println(clientID + "|" + tmp);
										soc.close();
									} else if (tmp.equals("43")) {
										ArrayList<String> l = shoppingCart
												.get(clientID);
										if (l == null)
											shoppingCart
													.put(clientID,
															l = new ArrayList<String>());
										l.add(tmp);
										pwReply.println("Item added in cart!");
										Socket soc = new Socket("Enter Your Replicator's IP Here",
												3010);
										PrintWriter pRplictr = new PrintWriter(
												soc.getOutputStream(), true);
										pRplictr.println(clientID + "|" + tmp);
										soc.close();
									} else if (tmp.equals("44")) {
										ArrayList<String> l = shoppingCart
												.get(clientID);
										if (l == null)
											shoppingCart
													.put(clientID,
															l = new ArrayList<String>());
										l.add(tmp);
										pwReply.println("Item added in cart!");
										Socket soc = new Socket("Enter Your Replicator's IP Here",
												3010);
										PrintWriter pRplictr = new PrintWriter(
												soc.getOutputStream(), true);
										pRplictr.println(clientID + "|" + tmp);
										soc.close();
									} else if (tmp.equals("EXIT")) {
										pwReply.println("BYE :-)");
									}
									reply.close();

									long startTimer = System
											.currentTimeMillis();
									long endTimer = 0;
									for (int i = 0; i < 9999999
											&& System.currentTimeMillis()
													- startTimer < 100; i++) {
										for (int j = 0; j < 9999999
												&& System.currentTimeMillis()
														- startTimer < 100; j++) {
											for (int k = 0; k < 9999999
													&& System
															.currentTimeMillis()
															- startTimer < 100; k++) {
												int sum = j + j;
												sum = i + i;
												sum = i + j;
											}
										}
									}

								} catch (Exception e) {
								}
							}
						}.start();
					} catch (Exception e) {

					}
				}
			}
		}.start();

		// SecondaryLB request -- start
		new Thread() {
			public void run() {
				while (true) {
					try {
						ServerSocket ss = new ServerSocket(2000);
						Socket cs = ss.accept();
						BufferedReader br = new BufferedReader(
								new InputStreamReader(cs.getInputStream()));
						final String input = br.readLine();
						cs.close();
						ss.close();
						new Thread() {
							final String in = input;

							public void run() {
								String clientAdd = null;
								String clientID = null;
								String clientName = null;
								int clientPort = 2000;
								int count = 0;
								String tmp = "";
								for (int i = 1; i < in.length(); i++) {
									if (count == 0 && in.charAt(i) == '|') {
										clientAdd = tmp;
										count++;
										tmp = "";
									} else if (count == 1
											&& in.charAt(i) == '|') {
										clientID = tmp;
										count++;
										tmp = "";
									}

									else
										tmp = tmp + in.charAt(i);
								}

								clientName = tmp;
								userData.put(clientID, clientName);
								try {
									Socket s = new Socket("129.21.37.36", 3000);
									PrintWriter pw = new PrintWriter(
											s.getOutputStream(), true);
									userIP.put(clientID, clientAdd);
									s.close();
								} catch (Exception e) {

								}
								String reply = in;
								reply = "";
								try {
									Socket clientOut = new Socket(clientAdd,
											2000);

									PrintWriter pw = new PrintWriter(
											clientOut.getOutputStream(), true);

									pw.println(reply + "");

									clientOut.close();
								} catch (Exception e) {

								}
							}

						}.start();

					} catch (IOException e) {

					}

				}
			}
		}.start();
		// SecondaryLB request -- end

		// Health request and handling -- start
		new Thread() {
			public void run() {
				while (true) {
					try {
						ServerSocket ss = new ServerSocket(2001);
						Socket cs = ss.accept();
						BufferedReader br = new BufferedReader(
								new InputStreamReader(cs.getInputStream()));
						String input = br.readLine();
						OperatingSystemMXBean osm = ManagementFactory
								.getPlatformMXBean(OperatingSystemMXBean.class);
						PrintWriter pw = new PrintWriter(cs.getOutputStream(),
								true);
						Runtime r = Runtime.getRuntime();
						int proc = r.availableProcessors();
						long availableMemory = r.freeMemory();
						pw.println(osm.getSystemLoadAverage()
								/ osm.getAvailableProcessors());
						pw.println(availableMemory);
						cs.close();
						ss.close();
					} catch (IOException e) {
					}
				}
			}
		}.start();
		// Health request and handling -- end
		new Thread() {
			public void run() {
				try {
					while (true) {
						ServerSocket ss = new ServerSocket(3080);
						Socket cs = ss.accept();
						BufferedReader br = new BufferedReader(
								new InputStreamReader(cs.getInputStream()));
						String fromReplicator = br.readLine();
						String tmp = "";
						String clientID = "";
						for (int i = 0; i < fromReplicator.length(); i++) {
							if (fromReplicator.charAt(i) == '|') {
								clientID = tmp;
								tmp = "";
							} else {
								tmp = tmp + fromReplicator.charAt(i);
							}
						}
						if (tmp.charAt(0) == '1' || tmp.charAt(0) == '2'
								|| tmp.charAt(0) == '3' || tmp.charAt(0) == '4') {
							ArrayList<String> l = shoppingCart.get(clientID);
							if (l == null)
								shoppingCart.put(clientID,
										l = new ArrayList<String>());
							l.add(tmp);
						}
					}
				} catch (Exception e) {

				}
			}
		}.start();

		new Thread() {
			public void run() {
				try {
					while (true) {
						ServerSocket ss = new ServerSocket(3081);
						Socket s = ss.accept();
						BufferedReader br = new BufferedReader(
								new InputStreamReader(s.getInputStream()));
						String input = br.readLine();
						s.close();
						ss.close();
						String tmp = "";
						String clientID = "";
						for (int i = 0; i < input.length(); i++) {
							if (input.charAt(i) == '|') {
								clientID = tmp;
								tmp = "";
							} else {
								tmp = tmp + input.charAt(i);
							}
						}

						ArrayList<String> al = shoppingCart.get(clientID);
						if (al != null) {
							al.remove(tmp);
						}

					}
				} catch (Exception e) {

				}
			}
		}.start();

		while (true) {
			System.out.println("Press 1 to see all the data of users");
			System.out
					.println("Press 2 and userID to see shopping cart the data of a user");
			Scanner scan = new Scanner(System.in);
			int option = scan.nextInt();
			if (option == 1) {
				System.out.println("Client ID\tClient Name\tClient IP");
				for (Map.Entry<String, String> i : userData.entrySet())
					System.out.println(i.getKey() + "\t" + i.getValue() + "\t"
							+ userIP.get(i.getKey()));
			} else if (option == 2) {
				String sendReply = "";
				Scanner scanID = new Scanner(System.in);
				String clientID = scanID.next();
				ArrayList<String> al = shoppingCart.get(clientID);
				for (int i = 0; i < al.size(); i++) {
					if (al.get(i).charAt(0) == '1')
						sendReply = sendReply
								+ veggiesCategoryList.get(al.get(i)) + "!";
					else if (al.get(i).charAt(0) == '2')
						sendReply = sendReply
								+ breadCategoryList.get(al.get(i)) + "!";
					else if (al.get(i).charAt(0) == '3')
						sendReply = sendReply
								+ pulsesCategoryList.get(al.get(i)) + "!";
					else if (al.get(i).charAt(0) == '4')
						sendReply = sendReply
								+ softDrinksCategoryList.get(al.get(i)) + "!";
				}
				System.out.println(sendReply);
			}

		}
	}
}
