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
	private static final String SALE_FILE_SERIAL_NUMBER = "売上ファイル名が連番になっていません";
	private static final String TOTAL_AMOUNT_EXCEEDE_TEN_FIGURES = "合計金額が10桁を超えました";
	private static final String CODE_INVALID_FORMAT = "の支店コードが不正です";
	private static final String FILE_LINE_INVALID_FORMAT = "のフォーマットが不正です";

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
		for(int a = 0; a < rcdFiles.size() -1; a++) {
			String currentFiles = files[a].getName();
			int former = Integer.parseInt(currentFiles.substring(0, 8));
			String nextfiles = files[a + 1].getName();
			int latter = Integer.parseInt(nextfiles.substring(0, 8));
			if((latter - former) != 1) {
				System.out.println(SALE_FILE_SERIAL_NUMBER);
			}
		}
		BufferedReader br = null;
		for(int i = 0; i < rcdFiles.size(); i++) {
			try {
				File file = rcdFiles.get(i); //rcdFilesのリストの中から、i番目を取得する。
				FileReader fr = new FileReader(file);
				br = new BufferedReader(fr);
				String line;
				ArrayList<String> elementsList = new ArrayList<String>();
				while((line = br.readLine()) != null) {
					elementsList.add(line);
					System.out.println(elementsList.size());
				}
				if(elementsList.size() != 2) {
					System.out.println(elementsList + FILE_LINE_INVALID_FORMAT);
				}
				String storeCode = elementsList.get(0);
				String streSale = elementsList.get(1);
				long fileSale = Long.parseLong(streSale);
				System.out.println(fileSale);
				long saleAmount = branchSales.get(storeCode) + fileSale;
				if(saleAmount >= 10000000000L) {
					System.out.println(TOTAL_AMOUNT_EXCEEDE_TEN_FIGURES);
				}
				if(!branchNames.containsKey(storeCode)) {
					System.out.println(storeCode + CODE_INVALID_FORMAT);
				}
				branchSales.put(storeCode, saleAmount);
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
			if(!file.exists()) {
				System.out.println(FILE_NOT_EXIST);
			}
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line;
			// 一行ずつ読み込む
			while((line = br.readLine()) != null) {
				// ※ここの読み込み処理を変更してください。(処理内容1-2)
				String[] items = line.split(",");
				//Mapに追加する２つの情報を、putの引数として指定します。
				branchNames.put(items[0], items[1]);
				branchSales.put(items[0], 0L);
                if ((items.length != 2) || (!items[0].matches("[0-9]{3}"))) {
					System.out.println(FILE_INVALID_FORMAT);
				}
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
		try {
			File file = new File(path, fileName);
			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			for (String key : branchNames.keySet()) {
			  bw.write(key + "," + branchNames.get(key) + "," + branchSales.get(key));
			  bw.newLine();
			}
		} catch(IOException e) {
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
		return true;
	}

}