package jp.alhinc.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculateSales {

	// 支店定義ファイル名
	private static final String FILE_NAME_BRANCH_LST = "branch.lst";

	// 支店別集計ファイル名
	private static final String FILE_NAME_BRANCH_OUT = "branch.out";

	// エラーメッセージ
	private static final String UNKNOWN_ERROR = "予期せぬエラーが発生しました";
	private static final String FILE_NOT_EXIST = "支店定義ファイルが存在しません";
	private static final String FILE_INVALID_FORMAT = "支店定義ファイルのフォーマットが不正です";

	/**
	 * メインメソッド
	 *
	 * @param コマンドライン引数
	 */
	public static void main(String[] args) {
		// 支店コードと支店名を保持するMap
		Map<String, String> branchNames = new HashMap<>();
		// 支店コードと売上金額を保持するMap
		Map<String, Long> branchSales = new HashMap<>();

		// 支店定義ファイル読み込み処理
		if(!readFile(args[0], FILE_NAME_BRANCH_LST, branchNames, branchSales)) {
			return;
		}

		// ※ここから集計処理を作成してください。(処理内容2-1、2-2)

		//listFilesを使⽤してfilesという配列に、
		//指定したパスに存在する全てのファイル(または、ディレクトリ)の情報を格納します
		File[] files = new File(args[0]).listFiles();
		//先にファイルの情報を格納する List(ArrayList) を宣言します。
		List<File> rcdFiles = new ArrayList<>();

		//filesの数だけ繰り返すことで、
		//指定したパスに存在する全てのファイル(または、ディレクトリ)の数だけ繰り返されます。
		for(int i = 0; i < files.length ; i++) {
			//files[i].getName() でファイル名を取得,変数に代入してあげる事で、次の行で使える材料になる。
			String fileName = files[i].getName();

			//matches を使用してファイル名(fileNameに入っている文字列)が「数字8桁.rcd」なのか判定します。
			if (fileName.matches("\\d{8}\\.rcd")) {
				//okだったら、rcdfilesっていうファイル型のオブジェクトを入れる専用のリストに、files[i]っていうFile型オブジェクトを入れる
				rcdFiles.add(files[i]);
			}
		}
		String line1 = null;
		String line2 = null;
		BufferedReader br = null;
		for(int i = 0; i < rcdFiles.size(); i++) {
			try {
				File file = rcdFiles.get(i); //rcdFilesのリストの中から、i番目を取得する。
				FileReader fr = new FileReader(file);
				br = new BufferedReader(fr);
				line1 = br.readLine(); //一行目の内容を「line1」に代入
				line2 = br.readLine(); //二行目の内容を「line2」に代入
				long fileSale = Long.parseLong(line2);
				long saleAmount = branchSales.get(line1) + fileSale;
				branchSales.put(line1, saleAmount);
			} catch(IOException e) {
				System.out.println(UNKNOWN_ERROR);
				return;
			} finally {
				// ファイルを開いている場合
				if(br != null) {
					try {
						// ファイルを閉じる
						br.close();
					} catch(IOException e) {
						System.out.println(UNKNOWN_ERROR);
						return;
					}
				}
			}
	    }

		// writeFileメソッドを呼び出すぜ！その時に、この4つの引数を渡すぜ！　と言っている行　支店別集計ファイル書き込み処理
		if(!writeFile(args[0], FILE_NAME_BRANCH_OUT, branchNames, branchSales)) {
			return;
		}

	}

	/**
	 * 支店定義ファイル読み込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 読み込み可否
	 */
	private static boolean readFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		BufferedReader br = null;

		try {
			File file = new File(path, fileName);
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line;
			// 一行ずつ読み込む
			while((line = br.readLine()) != null) {
				// ※ここの読み込み処理を変更してください。(処理内容1-2)
				String[] items = line.split(",");

				//Mapに追加する２つの情報を、putの引数として指定します。
				branchNames.put(items[0],items[1]);
				branchSales.put(items[0],(long)0);
				System.out.println(line);
			}

		} catch(IOException e) {
			System.out.println(UNKNOWN_ERROR);
			return false;
		} finally {
			// ファイルを開いている場合
			if(br != null) {
				try {
					// ファイルを閉じる
					br.close();
				} catch(IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 支店別集計ファイル書き込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 書き込み可否
	 */
	private static boolean writeFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		// ※ここに書き込み処理を作成してください。(処理内容3-1)
		BufferedWriter bw = null;
		String storeNames = null;
		Long storeSales = null;
		try {
			File file = new File(path, fileName);
			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			for (String key : branchNames.keySet()) {
			  storeNames = branchNames.get(key);
			  storeSales = branchSales.get(key);
			  bw.write(key + "," + storeNames + "," + storeSales + "\r\n");
			}
			bw.close();
		} catch(IOException e) {
			System.out.println("なんか失敗");
			return false;
		} finally {
			// ファイルを開いている場合
			if(bw != null) {
				try {
					// ファイルを閉じる
					bw.close();
				} catch(IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		  }





//		try {
//			File file = new File(path, fileName);
//			FileWriter fw = new FileWriter(file);
//			bw = new BufferedWriter(fw);
//
//		//支店コードの数分（例えば、 branchNames　を入れたなら、 branchNamesのkeyの数分繰り返すぜ　と言っている）
//		for (String key : branchNames.keySet()) {
////			書き出すぜ！なメソッド(key);
//			Map<String, String> storeNames = new HashMap<>();
//			Map<String, String> storeSales = new HashMap<>();
//			String storename = storeNames.get(key);
//			String storesale = storeSales.get(key);
//			fw.write(key + storename + storesale);
//			bw.newLine();
//		}
//		} catch(IOException e) {
//			System.out.println(UNKNOWN_ERROR);
//			return false;
//		} finally {
//			// ファイルを開いている場合
//			if(bw != null) {
//				try {
//					// ファイルを閉じる
//					bw.close();
//				} catch(IOException e) {
//					System.out.println(UNKNOWN_ERROR);
//					return false;
//				}
//			}
//		}
		return true;
	}

}
