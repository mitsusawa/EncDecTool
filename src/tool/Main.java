package tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main extends Application {
	TextField textField;
	Button openKey;
	Button enc;
	Button dec;
	List<List<Button>> buy;
	ImageView result;
	KeyGen keyGen;
	Key key;
	FileChooser fileChooser = new FileChooser();
	
	public void start(Stage stage) throws Exception {
		stage.setWidth(300);
		stage.setHeight(225);
		
		textField = new TextField("");
		openKey = new Button("鍵の読み込み");
		enc = new Button("暗号化");
		dec = new Button("復号");
		
		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("ファイル");
		MenuItem menuOpen = new MenuItem("開く");
		MenuItem menuExit = new MenuItem("終了");
		menuExit.setOnAction(event -> System.exit(0));
		fileMenu.getItems().add(menuOpen);
		fileMenu.getItems().add(menuExit);
		menuBar.getMenus().add(fileMenu);
		
		Button createKey = new Button("鍵生成");
		VBox box2 = new VBox(3);
		box2.getChildren().add(textField);
		box2.setPadding(new Insets(20, 25, 25, 25));
		box2.getChildren().add(createKey);
		createKey.setOnAction(event -> {
			try {
				gen();
				textField.setText("鍵を生成しました。");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		box2.getChildren().add(openKey);
		openKey.setOnAction(event -> {
			try {
				openKey();
				textField.setText("鍵を読み込みました。");
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		HBox box3 = new HBox(2);
		box2.getChildren().add(box3);
		box3.getChildren().addAll(enc, dec);
		
		enc.setOnAction(event -> {
			try {
				if (!Objects.isNull(key)) {
					File file = fileChooser.showOpenDialog(stage);
					if (!Objects.isNull(file)) {
						String filePath = file.getPath();
						encrypt(Files.readAllBytes(file.toPath()), filePath);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		dec.setOnAction(event -> {
			try {
				if (!Objects.isNull(key)) {
					File file = fileChooser.showOpenDialog(stage);
					if (!Objects.isNull(file)) {
						String filePath = file.getPath();
						decrypt(Files.readAllBytes(file.toPath()), filePath);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		BorderPane root = new BorderPane();
		// root.setTop(menuBar);
		root.setCenter(box2);
		
		stage.setScene(new Scene(root));
		stage.show();
	}
	
	public void openKey() throws IOException {
		FileInputStream n = new FileInputStream("./n");
		FileInputStream e = new FileInputStream("./e");
		FileInputStream d = new FileInputStream("./d");
		this.key = new Key(new BigInteger(n.readAllBytes()), new BigInteger(e.readAllBytes()), new BigInteger(d.readAllBytes()));
	}
	
	public void encrypt(byte[] file, String path) throws Exception {
		if (!Objects.isNull(key)) {
			Controller controller = new Controller(key);
			List<byte[]> list = new ArrayList<>();
			for (int i = 0; i < file.length; i++) {
				if (i % 31 == 0) {
					list.add(new byte[Math.min(32, file.length - i + 1)]);
					list.get(list.size() - 1)[0] = 0;
				}
				list.get(list.size() - 1)[i % 31 + 1] = file[i];
			}
			// List<BigInteger> encryptedList = new ArrayList<>();
			List<EncObject> encObjectList = Collections.synchronizedList(new ArrayList<>());
			ExecutorService executor = Executors.newCachedThreadPool();
			List<Future<?>> futureList = new ArrayList<Future<?>>();
			for (int i = 0; i < list.size(); i++) {
				futureList.add(executor.submit(new ExecEnc(controller, i, list.get(i), encObjectList)));
				// encryptedList.add(controller.encrypt(list.get(i)));
			}
			for (Future<?> future : futureList) {
				future.get();
			}
			executor.shutdown();
			Collections.sort(encObjectList);
			List<BigInteger> encryptedList = new ArrayList<>();
			for (EncObject encObject : encObjectList) {
				encryptedList.add(encObject.encrypted);
			}
			// BigInteger encrypted = controller.encrypt(file);
			FileOutputStream encryptedFile = new FileOutputStream(path + ".enc");
			PrintWriter csv = new PrintWriter(path + ".enc.csv");
			// encryptedFile.write(encrypted.toByteArray());
			for (BigInteger encrypted : encryptedList) {
				byte[] array = encrypted.toByteArray();
				encryptedFile.write(array);
				csv.print(array.length + ",");
			}
			encryptedFile.close();
			csv.close();
			textField.setText("暗号化しました。");
		}
	}
	
	public void decrypt(byte[] file, String path) throws Exception {
		if (!Objects.isNull(key)) {
			BufferedReader csv = new BufferedReader(new FileReader(path + ".csv"));
			String str = csv.readLine();
			String[] strArray = str.split(",");
			Controller controller = new Controller(key);
			List<byte[]> list = new ArrayList<>();
			int count = 0;
			int index = 0;
			int len = Integer.parseInt(strArray[index]);
			list.add(new byte[len]);
			for (int i = 0; i < file.length; i++) {
				if (count >= len) {
					len = Integer.parseInt(strArray[++index]);
					list.add(new byte[len]);
					count = 0;
				}
				list.get(list.size() - 1)[count++] = file[i];
			}
			// List<byte[]> decryptedList = new ArrayList<>();
			
			List<DecObject> decObjectList = Collections.synchronizedList(new ArrayList<>());
			ExecutorService executor = Executors.newCachedThreadPool();
			List<Future<?>> futureList = new ArrayList<Future<?>>();
			
			for (int i = 0; i < list.size(); i++) {
				// decryptedList.add(controller.decrypt(new BigInteger(list.get(i))));
				futureList.add(executor.submit(new ExecDec(controller, i, list.get(i), decObjectList)));
			}
			for (Future<?> future : futureList) {
				future.get();
			}
			executor.shutdown();
			Collections.sort(decObjectList);
			List<byte[]> decryptedList = new ArrayList<>();
			for (DecObject decObject : decObjectList) {
				decryptedList.add(decObject.decrypted);
			}
			// byte[] decrypted = controller.decrypt(new BigInteger(file));
			FileOutputStream decryptedFile = new FileOutputStream(path + ".dec");
			for (byte[] decrypted : decryptedList) {
				if (decrypted.length >= 32) {
					byte[] array = new byte[decrypted.length - 1];
					for (int j = 0; j < array.length; j++) {
						array[j] = decrypted[j + 1];
					}
					decryptedFile.write(array);
					System.out.println(array.length);
				} else if (decrypted.length < 31 && decrypted != decryptedList.get(decryptedList.size() - 1)) {
					byte[] array = new byte[31];
					int diff = 31 - decrypted.length;
					for (int i = 0; i < array.length; i++) {
						if (i < diff) {
							array[i] = 0;
						} else {
							array[i] = decrypted[i - diff];
						}
					}
					decryptedFile.write(array);
					System.out.println(array.length);
				} else if (decrypted == decryptedList.get(decryptedList.size() - 1)) {
					byte[] array = new byte[decrypted.length - 1];
					for (int j = 0; j < array.length; j++) {
						array[j] = decrypted[j + 1];
					}
					decryptedFile.write(array);
					System.out.println(array.length);
				} else {
					decryptedFile.write(decrypted);
					System.out.println(decrypted.length);
				}
			}
			decryptedFile.close();
			csv.close();
			textField.setText("復号しました。");
		}
	}
	
	public void gen() throws Exception {
		Key key = this.keyGen.gen();
		FileOutputStream n = new FileOutputStream("./n");
		FileOutputStream e = new FileOutputStream("./e");
		FileOutputStream d = new FileOutputStream("./d");
		n.write(key.n.toByteArray());
		e.write(key.e.toByteArray());
		d.write(key.d.toByteArray());
		n.close();
		e.close();
		d.close();
	}
	
	public Main() {
		this.keyGen = new KeyGen();
	}
}
