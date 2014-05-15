import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Replicator {

	public static ConcurrentHashMap<InetAddress, Integer> map = new ConcurrentHashMap<InetAddress, Integer>();

	public static void main(String[] args) {
		new Thread() {
			public void run() {
				while (true) {
					try {
						ServerSocket ss = new ServerSocket(3000);
						Socket soc = ss.accept();
						BufferedReader br = new BufferedReader(
								new InputStreamReader(soc.getInputStream()));
						final String str = br.readLine();
						final String strIP = br.readLine();
						soc.close();
						ss.close();

						new Thread() {
							final String in = str;
							final String inIP = strIP;

							public void run() {
								try {
									for (Map.Entry<InetAddress, Integer> j : map
											.entrySet()) {
										Socket s = new Socket();
										s.connect(
												new InetSocketAddress(j
														.getKey(), 1600), 5000);
										PrintWriter pw = new PrintWriter(
												s.getOutputStream(), true);
										pw.println(in);
										pw.println(inIP);
										s.close();
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

		new Thread() {
			public void run() {
				while (true) {
					try {
						int savePort = 0;
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
		new Thread() {
			public void run() {
				try {
					while (true) {
						ServerSocket ss = new ServerSocket(3010);
						Socket cs = ss.accept();
						BufferedReader br = new BufferedReader(
								new InputStreamReader(cs.getInputStream()));
						final String str = br.readLine();
						final String csInetAdd = "" + cs.getInetAddress();
						cs.close();
						ss.close();
						new Thread() {
							final String fromClient = str;

							public void run() {
								try {
									for (Map.Entry<InetAddress, Integer> j : map
											.entrySet()) {
										if (!csInetAdd.equals("" + j.getKey())) {
											Socket s = new Socket(j.getKey(),
													3080);
											PrintWriter pw = new PrintWriter(
													s.getOutputStream(), true);
											pw.println(fromClient);
											s.close();
										}
									}
								} catch (Exception e) {

								}
							}
						}.start();
					}
				} catch (Exception e) {

				}
			}
		}.start();

		new Thread() {
			public void run() {
				try {
					while (true) {
						ServerSocket ss = new ServerSocket(3011);
						Socket cs = ss.accept();
						BufferedReader br = new BufferedReader(
								new InputStreamReader(cs.getInputStream()));
						final String str = br.readLine();
						final String csInetAdd = "" + cs.getInetAddress();
						cs.close();
						ss.close();
						new Thread() {
							final String fromClient = str;

							public void run() {
								try {
									for (Map.Entry<InetAddress, Integer> j : map
											.entrySet()) {
										if (!csInetAdd.equals("" + j.getKey())) {
											Socket s = new Socket(j.getKey(),
													3081);
											PrintWriter pw = new PrintWriter(
													s.getOutputStream(), true);
											pw.println(fromClient);
											s.close();
										}
									}
								} catch (Exception e) {

								}
							}
						}.start();
					}
				} catch (Exception e) {

				}
			}
		}.start();
	}
}
