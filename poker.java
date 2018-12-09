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
	// �萔�̒�`
	public final static int HEART = 0;
	public final static int DIAMOND = 1;
	public final static int SPADE = 2;
	public final static int CLUB = 3;
	// �X�[�c�̐�
	public final static int NSUITS = 4;
	// �X�[�c������̃J�[�h�̐�
	public final static int NCARDS_PER_SUIT = 13;

	// �C���X�^���X�ϐ�
	int suit; // �X�[�c(0-3)
	int number; // �ԍ�(1-13)

	Card cards[] = new Card[5];

	// �R���X�g���N�^
	Card() {
		this.suit = 4;
		this.number = 0;
	}

	public Card(int suit, int number) {
		this.number = number;
		this.suit = suit;
	}

	// ���̃J�[�h�̐����擾����
	public int getNumber() {
		return number;
	}

	// ���̃J�[�h�̎�ނ��擾����
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

	// �J�[�h�̎c����
	private int nCard;
	// �e�J�[�h��������Ă���� true �ɂȂ�2�����z��
	// �ŏ��̎����̓X�[�c�C���̎����͔ԍ�(0-12)
	private boolean[][] taken = new boolean[Card.NSUITS][Card.NCARDS_PER_SUIT];

	// �R���X�g���N�^
	public CardStack() {
		// �c����������������
		nCard = Card.NSUITS * Card.NCARDS_PER_SUIT;
	}

	// �J�[�h���P�������C�������J�[�h�� Card �^�̃C���X�^���X�Ƃ��ĕԂ��D
	// �J�[�h���Ȃ��Ȃ�� null ��Ԃ��D
	public Card draw() {
		if (nCard == 0) {
			return null;
		}

		// �����ŃJ�[�h��I��
		// �J�[�h�̎c�薇�������Ȃ��Ȃ��Ă���ƌ�����������...
		while (true) {
			int s = (int) (Math.random() * Card.NSUITS);
			int n = (int) (Math.random() * Card.NCARDS_PER_SUIT);
			// �܂���������Ă��Ȃ��J�[�h��I�񂾂��`�F�b�N
			if (!taken[s][n]) {
				taken[s][n] = true;
				nCard--;
				// Card�N���X�̃C���X�^���X������ĕԂ�
				// + 1 ���Ă���̂̓J�[�h�̔ԍ��� 1 ����n�܂��Ă��邽��
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
			result = "���C�����X�g���[�g�t���b�V�� �ł�";
			point += RSFlush();
		} else if (SFlush() == 90) {
			result = "�X�g���[�g�t���b�V�� �ł�";
			point += SFlush();
		} else if (Flush() == 60 || Strait() == 50) {
			if (Flush() == 60)
				result = "�t���b�V�� �ł�";
			else
				result = "�X�g���[�g �ł�";
			point += Flush() + Strait();
		} else if (FullHouse() == 70 || FourCard() == 80) {
			if (FullHouse() == 70)
				result = "�t���n�E�X �ł�";
			else
				result = "�t�H�[�J�[�h �ł�";
			point += FullHouse() + FourCard();
		} else if (ThreeCard() == 30) {
			result = "�X���[�J�[�h �ł�";
			point += ThreeCard();
		} else if (Pair() == 10 || Pair() == 20) {
			if (Pair() == 10)
				result = "�����y�A �ł�";
			else
				result = "�c�[�y�A �ł�";
			point += Pair();
		} else {
			result = "�m�[�y�A �ł�";
		}
	}

	void displayPCards() {
		System.out.print("�J�[�h�ԍ�:\t");
		for (int j = 0; j < cards.length; j++) {
			System.out.print((j + 1) + "\t");
		}
		System.out.print("\n");
		for (int k = 0; k < 100; k++) {
			System.out.print("-");
		}
		System.out.print("\n�}�[�N\t:\t");
		for (int k = 0; k < cards.length; k++) {
			System.out.print(suitReturn(cards[k].suit) + "\t");
		}
		System.out.print("\n����\t:\t");
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
	static String msg = "��������J�[�h�̖����ɍ����{�^���������Ă�������";

	public static void main(String[] args) {

		int n = 5;// ���s��

		Player p1 = new Player();

		JFrame frame = new JFrame("Poker");
		frame.setSize(1100, 700);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		JPanel panelOfCard = new JPanel();
		panelOfCard.setLayout(new FlowLayout());
		JLabel lbl[] = new JLabel[p1.cards.length];

		for (int k = 0; k < p1.cards.length; k++) { // ��D�̃J�[�h(��)���E�B���h�E�ɕ\��
			lbl[k] = new JLabel(getCardIcon(panelOfCard, "./png/unknown.png"));
			panelOfCard.add(lbl[k], BorderLayout.CENTER);
		}

		frame.add(panelOfCard, BorderLayout.NORTH);
		frame.setVisible(true);

		JPanel panelOfButton = new JPanel(); // �{�^���̒ǉ�
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

			msg = "��������J�[�h������I��ł�������";

			waitby(); // ���͂�����܂őҋ@

			setCardsIcon(p1, lbl, panelOfCard); // ��D�̃J�[�h(�\)���E�B���h�E�ɕ\��

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

			msg = "�ԖڂɌ�������J�[�h�ԍ���I�����Ă�������";

			for (int j = 0; j < exNum; j++) {
				JOptionPane
						.showMessageDialog(null, String.valueOf(j + 1) + msg);

				waitby();

				lbl[judgeNum()].setIcon(new ImageIcon("./png/unknown.png")); // �I�������J�[�h�𗠕Ԃ�
				p1.exchange(judgeNum()); // �I�������J�[�h������
				lbl[judgeNum()].setVisible(true);
				frame.setVisible(true);
				resetJudge();
			}

			for (int k = 0; k < 5; k++) {
				// �J�[�h�A�C�R���̍X�V
				lbl[k].setIcon(new ImageIcon(getURL(p1.cards[k])));
			}

			p1.arrangeOrder();

			p1.addPoint();

			JOptionPane.showMessageDialog(null, Player.result);

			JOptionPane.showMessageDialog(null,
					"���݂̍��v�|�C���g : " + String.valueOf(Player.getPoint()));

			resetJudge();

			for (int k = 0; k < 5; k++) {
				btn[k].setVisible(false);
			}

			btn[2].setText("����");
			btn[2].setVisible(true);

			waitby();

		}

		JOptionPane.showMessageDialog(null,
				"[�ŏI����] " + String.valueOf(Player.getPoint()) + "�|�C���g");

		System.exit(0);
	}

	static ImageIcon getCardIcon(JPanel panel, String url) { // �摜��\��t��
		MediaTracker tracker = new MediaTracker(panel);
		ImageIcon icon = new ImageIcon(url); // url�̎w���摜��ݒ�
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

	static String getURL(Card card) {// �J�[�h�摜�t�@�C���̏ꏊ(���΃p�X)
		return "./png/" + card.suit + card.number + ".png";
	}

	static void setTextLabel(JFrame frame, String text) {
		JLabel label = new JLabel(text);
		Container contentPane = frame.getContentPane();
		contentPane.add(label, BorderLayout.SOUTH);
	}

	static void frameDisplay() {// �E�B���h�E��\��
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
		while (!jg) { // jg��false�̊ԑ�����
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
			return "�uGAME START�v�������ƃQ�[�����J�n";
		else if (x < 4)
			return (x + 1) + "�Q�[���ڂɓ���܂�";
		else if (x == 4)
			return "�ŏI�Q�[���ɓ���܂�";
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
		} else if (cmd.equals("����")) {
			jg = true;
		}
	}
