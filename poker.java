import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

class Card {
	// 定数の定義
	public final static int HEART = 0;
	public final static int DIAMOND = 1;
	public final static int SPADE = 2;
	public final static int CLUB = 3;
	// スーツの数
	public final static int NSUITS = 4;
	// スーツあたりのカードの数
	public final static int NCARDS_PER_SUIT = 13;

	// インスタンス変数
	int suit; // スーツ(0-3)
	int number; // 番号(1-13)

	Card cards[] = new Card[5];

	// コンストラクタ
	Card() {
		this.suit = 4;
		this.number = 0;
	}

	public Card(int suit, int number) {
		this.number = number;
		this.suit = suit;
	}

	// このカードの数を取得する
	public int getNumber() {
		return number;
	}

	// このカードの種類を取得する
	public int getSuit() {
		return suit;
	}

	public void arrangeOrder() {
		int buf;
		for (int i = 0; i < cards.length; i++) {
			for (int j = i + 1; j < cards.length; j++) {
				if (cards[i].number > cards[j].number) {
					buf = cards[i].number;
					cards[i].number = cards[j].number;
					cards[j].number = buf;
					buf = cards[i].suit;
					cards[i].suit = cards[j].suit;
					cards[j].suit = buf;
				}
			}
		}
	}

}

class CardStack extends Card {

	// カードの残枚数
	private int nCard;
	// 各カードが抜かれていれば true になる2次元配列
	// 最初の次元はスーツ，次の次元は番号(0-12)
	private boolean[][] taken = new boolean[Card.NSUITS][Card.NCARDS_PER_SUIT];

	// コンストラクタ
	public CardStack() {
		// 残枚数を初期化する
		nCard = Card.NSUITS * Card.NCARDS_PER_SUIT;
	}

	// カードを１枚引き，引いたカードを Card 型のインスタンスとして返す．
	// カードがないならば null を返す．
	public Card draw() {
		if (nCard == 0) {
			return null;
		}

		// 乱数でカードを選ぶ
		// カードの残り枚数が少なくなってくると効率が悪いが...
		while (true) {
			int s = (int) (Math.random() * Card.NSUITS);
			int n = (int) (Math.random() * Card.NCARDS_PER_SUIT);
			// まだ抜き取られていないカードを選んだかチェック
			if (!taken[s][n]) {
				taken[s][n] = true;
				nCard--;
				// Cardクラスのインスタンスを作って返す
				// + 1 しているのはカードの番号が 1 から始まっているため
				return new Card(s, n + 1);
			}
		}
	}

}

class Player extends Combination {
	private static int point = 0;
	static String result = null;

	public Player() {
		for (int i = 0; i < 5; i++) {
			cards[i] = draw();
		}
	}

	public static int getPoint() {
		return point;
	}

	public void exchange(int index) {
		cards[index] = draw();
	}

	public void addPoint() {
		if (RSFlush() == 100) {
			result = "ロイヤルストレートフラッシュ です";
			point += RSFlush();
		} else if (SFlush() == 90) {
			result = "ストレートフラッシュ です";
			point += SFlush();
		} else if (Flush() == 60 || Strait() == 50) {
			if (Flush() == 60)
				result = "フラッシュ です";
			else
				result = "ストレート です";
			point += Flush() + Strait();
		} else if (FullHouse() == 70 || FourCard() == 80) {
			if (FullHouse() == 70)
				result = "フルハウス です";
			else
				result = "フォーカード です";
			point += FullHouse() + FourCard();
		} else if (ThreeCard() == 30) {
			result = "スリーカード です";
			point += ThreeCard();
		} else if (Pair() == 10 || Pair() == 20) {
			if (Pair() == 10)
				result = "ワンペア です";
			else
				result = "ツーペア です";
			point += Pair();
		} else {
			result = "ノーペア です";
		}
	}

	void displayPCards() {
		System.out.print("カード番号:\t");
		for (int j = 0; j < cards.length; j++) {
			System.out.print((j + 1) + "\t");
		}
		System.out.print("\n");
		for (int k = 0; k < 100; k++) {
			System.out.print("-");
		}
		System.out.print("\nマーク\t:\t");
		for (int k = 0; k < cards.length; k++) {
			System.out.print(suitReturn(cards[k].suit) + "\t");
		}
		System.out.print("\n数字\t:\t");
		for (int l = 0; l < cards.length; l++) {
			System.out.print(cards[l].number + "\t");
		}
	}

	String suitReturn(int n) {
		switch (n) {
		case 0:
			return "HEART";
		case 1:
			return "DIAMOND";
		case 2:
			return "SPADE";
		case 3:
			return "CLUB";
		default:
			return "Error";
		}
	}
}

class Combination extends CardStack {

	int pairNum = 0;
	int ThreeCardNum = 0;

	public int Flush() {
		if (cards[0].suit == cards[1].suit && cards[1].suit == cards[2].suit
				&& cards[2].suit == cards[3].suit
				&& cards[3].suit == cards[4].suit
				&& cards[4].suit == cards[5].suit)
			return 60;
		return 0;
	}

	public int Strait() {
		arrangeOrder();
		if ((cards[0].number + 1) == cards[1].number
				&& cards[1].number + 1 == cards[2].number
				&& cards[2].number + 1 == cards[3].number
				&& cards[3].number + 1 == cards[4].number
				&& cards[4].number + 1 == cards[5].number) {
			return 50;
		} else if ((cards[1].number - cards[0].number) == 9
				&& cards[1].number + 1 == cards[2].number
				&& cards[2].number + 1 == cards[3].number
				&& cards[3].number + 1 == cards[4].number
				&& cards[4].number + 1 == cards[5].number)
			return 50;
		return 0;
	}

	public int SFlush() {
		if ((Flush() == 60) && (Strait() == 50)) {
			return 90;
		}
		return 0;
	}

	public int FourCard() {
		for (int i = 0; i < cards.length; i++) {
			int judge = 0;
			for (int j = i + 1; j < cards.length; j++) {
				if (cards[i].number == cards[j].number)
					judge++;
			}
			if (judge == 3) {
				return 80;
			}
		}
		return 0;
	}

	public int ThreeCard() {
		for (int i = 0; i < cards.length; i++) {
			int judge = 0;
			for (int j = i + 1; j < cards.length; j++) {
				if (cards[i].number == cards[j].number) {
					ThreeCardNum = cards[i].number;
					judge++;
				}
			}
			if (judge == 2) {
				return 30;
			}
		}
		return 0;
	}

	public int Pair() {
		int judge = 0;
		int cnt = 0;
		for (int i = 0; i < cards.length; i++) {
			judge = 0;
			for (int j = i + 1; j < cards.length; j++) {
				if (cards[i].number == cards[j].number) {
					pairNum = cards[i].number;
					judge++;
				}
			}
			if (judge == 1)
				cnt++;
		}
		if (cnt == 1) {
			return 10;
		} else if (cnt == 2) {
			return 20;
		}
		return 0;
	}

	public int FullHouse() {
		if (ThreeCard() == 30 && Pair() == 10 && pairNum != ThreeCardNum) {
			return 70;
		}
		return 0;
	}

	public int RSFlush() {
		if (SFlush() == 90 && cards[0].number == 1) {
			return 100;
		}
		return 0;
	}
}

class Poker implements ActionListener {

	static boolean jg = false;
	static boolean judge[] = new boolean[5];
	static String msg = "交換するカードの枚数に合うボタンを押してください";

	public static void main(String[] args) {

		int n = 5;// 試行回数

		Player p1 = new Player();

		JFrame frame = new JFrame("Poker");
		frame.setSize(1100, 700);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		JPanel panelOfCard = new JPanel();
		panelOfCard.setLayout(new FlowLayout());
		JLabel lbl[] = new JLabel[p1.cards.length];

		for (int k = 0; k < p1.cards.length; k++) { // 手札のカード(裏)をウィンドウに表示
			lbl[k] = new JLabel(getCardIcon(panelOfCard, "./png/unknown.png"));
			panelOfCard.add(lbl[k], BorderLayout.CENTER);
		}

		frame.add(panelOfCard, BorderLayout.NORTH);
		frame.setVisible(true);

		JPanel panelOfButton = new JPanel(); // ボタンの追加
		panelOfButton.setLayout(new FlowLayout());

		JButton btn[] = new JButton[5];
		for (int k = 0; k < 5; k++) {
			btn[k] = new JButton();
			btn[k].setPreferredSize(new Dimension(200, 100));
			btn[k].addActionListener(new Poker());
			panelOfButton.add(btn[k]);
			btn[k].setVisible(false);
		}

		frame.add(panelOfButton, BorderLayout.SOUTH);
		frame.setVisible(true);

		for (int i = 0; i < n; i++) {

			p1 = new Player();

			JOptionPane.showMessageDialog(null, getText(i));

			btn[2].setText("GAME START");
			btn[2].setVisible(true);

			for (int k = 0; k < 5; k++) {
				lbl[k].setIcon(new ImageIcon("./png/unknown.png"));
			}
			frame.setVisible(true);

			msg = "交換するカード枚数を選んでください";

			waitby(); // 入力があるまで待機

			setCardsIcon(p1, lbl, panelOfCard); // 手札のカード(表)をウィンドウに表示

			for (int k = 0; k < 5; k++) {
				btn[k].setText(String.valueOf(k + 1));
				btn[k].setVisible(true);
			}

			frame.setVisible(true);

			resetJudge();
			int exNum = 0;

			waitby();

			for (int k = 0; k < 5; k++) {
				if (judge[k]) {
					exNum = k + 1;
				}
			}

			resetJudge();

			msg = "番目に交換するカード番号を選択してください";

			for (int j = 0; j < exNum; j++) {
				JOptionPane
						.showMessageDialog(null, String.valueOf(j + 1) + msg);

				waitby();

				lbl[judgeNum()].setIcon(new ImageIcon("./png/unknown.png")); // 選択したカードを裏返す
				p1.exchange(judgeNum()); // 選択したカードを交換
				lbl[judgeNum()].setVisible(true);
				frame.setVisible(true);
				resetJudge();
			}

			for (int k = 0; k < 5; k++) {
				// カードアイコンの更新
				lbl[k].setIcon(new ImageIcon(getURL(p1.cards[k])));
			}

			p1.arrangeOrder();

			p1.addPoint();

			JOptionPane.showMessageDialog(null, Player.result);

			JOptionPane.showMessageDialog(null,
					"現在の合計ポイント : " + String.valueOf(Player.getPoint()));

			resetJudge();

			for (int k = 0; k < 5; k++) {
				btn[k].setVisible(false);
			}

			btn[2].setText("次へ");
			btn[2].setVisible(true);

			waitby();

		}

		JOptionPane.showMessageDialog(null,
				"[最終結果] " + String.valueOf(Player.getPoint()) + "ポイント");

		System.exit(0);
	}

	static ImageIcon getCardIcon(JPanel panel, String url) { // 画像を貼り付け
		MediaTracker tracker = new MediaTracker(panel);
		ImageIcon icon = new ImageIcon(url); // urlの指す画像を設定
		tracker.addImage(icon.getImage(), 0);
		try {
			tracker.waitForAll();
		} catch (InterruptedException e) {
			System.out.println(e);
		}

		return icon;
	}

	static void setCardsIcon(Player p, JLabel[] label, JPanel panel) {
		for (int m = 0; m < p.cards.length; m++) {
			label[m].setIcon(getCardIcon(panel, getURL(p.cards[m])));
		}
	}

	static String getURL(Card card) {// カード画像ファイルの場所(相対パス)
		return "./png/" + card.suit + card.number + ".png";
	}

	static void setTextLabel(JFrame frame, String text) {
		JLabel label = new JLabel(text);
		Container contentPane = frame.getContentPane();
		contentPane.add(label, BorderLayout.SOUTH);
	}

	static void frameDisplay() {// ウィンドウを表示
		JFrame frame = new JFrame("Poker");
		frame.setSize(1100, 700);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new FlowLayout());

		frame.setVisible(true);
	}

	static int exNum(String s) {
		switch (s) {
		case "0":
			return 0;
		case "1":
			return 1;
		case "2":
			return 2;
		case "3":
			return 3;
		case "4":
			return 4;
		default:
			return 5;
		}
	}

	static void waitby() {
		while (!jg) { // jgがfalseの間続ける
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		jg = false;
	}

	static void resetJudge() {
		for (int k = 0; k < 5; k++) {
			judge[k] = false;
		}
		jg = false;
	}

	static int judgeNum() {
		for (int l = 0; l < 5; l++) {
			if (judge[l])
				return l;
		}
		return 0;
	}

	static String getText(int x) {
		if (x == 0)
			return "「GAME START」を押すとゲームを開始";
		else if (x < 4)
			return (x + 1) + "ゲーム目に入ります";
		else if (x == 4)
			return "最終ゲームに入ります";
		else
			return null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		if (cmd.equals("1")) {
			judge[0] = true;
			jg = true;
		} else if (cmd.equals("2")) {
			judge[1] = true;
			jg = true;
		} else if (cmd.equals("3")) {
			judge[2] = true;
			jg = true;
		} else if (cmd.equals("4")) {
			judge[3] = true;
			jg = true;
		} else if (cmd.equals("5")) {
			judge[4] = true;
			jg = true;
		} else if (cmd.equals("GAME START")) {
			jg = true;
			JOptionPane.showMessageDialog(null, msg);
		} else if (cmd.equals("次へ")) {
			jg = true;
		}
	}
