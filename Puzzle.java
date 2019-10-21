import static java.awt.RenderingHints.*;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

/*--- �i�S�ʁj���C���t���[�� ---*/
public class Puzzle extends JFrame {
	public static void main(String[] args) {
		Puzzle frame = new Puzzle();
		frame.setTitle("�p�Y�h��");
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setResizable(false);
		// �t���[���Ƀp�l����ݒu���ăT�C�Y����
		MainPanel panel = new MainPanel();
		frame.getContentPane().add(panel);
		frame.pack();
	}
}

/*--- �i�o�g���j���C���p�l�� ---*/
class MainPanel extends JPanel
implements Runnable, MouseMotionListener, MouseListener {
	// �A�v���̈ʒu
	static final int APP_WIDTH = 480;
	static final int APP_HEIGHT = APP_WIDTH * 3 / 2;
	static final int BOARD_X = APP_WIDTH / 120;
	static final int BOARD_Y = APP_HEIGHT * 449 / 1000;
	static final int GRID_WIDTH = (APP_WIDTH - BOARD_X) / 6;
	static final int GRID_HEIGHT = (APP_HEIGHT - BOARD_Y) / 5;
	static final int MOVE_DROP_WIDTH = GRID_WIDTH * 2 / 3;
	static final int MOVE_DROP_HEIGHT = GRID_HEIGHT * 2 / 3;
	int cureDropAddWidth;
	int cureDropAddHeight;
	int x, y;
	// ���C�����[�v
	Thread thread;
	final int FPS = 30;
	// �Ֆ�
	static int preX, preY;
	static int nowX, nowY;
	static int[][] dropMap = new int[6][5];
	// �h���b�v����
	static ArrayList<Place> swapNum = new ArrayList<>();
	static boolean swapDrop = false;
	boolean touchDrop = false;
	double canMoveSecond = 4.0 * 2;
	long startMoveTime;
	long nowTime;
	// �R���{����
	int[][] eraseMap = new int[8][7];
	ArrayList<ComboInfo> comboInfo = new ArrayList<>();
	int head;
	String temp;
	ArrayList<Place> place = new ArrayList<>();
	static boolean eraseDrop = false;
	int eraseCount;
	double comboStringColorNum;
	static int tempI, tempJ, tempFall;
	static int setCount;
	boolean eraseFlag = false;
	// �摜
	Image dropImg;
	Image UIPAT1;
	Graphics2D g2;
	URL url;
	Graphics2D dbg2;
	Image dbImage;
	float alpha;
	// �L�����N�^�[�i�o�����X���߃I�V���X�p�j
	Character character;
	// �_���W�����i�_�X�̉��j
	Dungeon dungeon;
	int enemyConcatWidth;
	int enemyMaxHeight;
	// �o�g���i�s
	int nowSkillTurn;
	int nowTurn;
	int preTurn = -1;
	int nowFloor;
	int enemyTurn;
	double turnStringColorNum;
	int[] enemyNowHp = new int[4];
	int[] characterAttackNum = new int[6];
	int[] characterAttackSubNum = new int[6];
	int nowRcu;
	boolean enemyAlive = false;
	// �f�o�b�O
	double fpsNum;
	long beforeTime = System.nanoTime();
	long afterTime = System.nanoTime();
	int errorNum;

	public MainPanel() {
		// ��{�ݒ�
		setPreferredSize(new Dimension(APP_WIDTH, APP_HEIGHT));
		setSize(APP_WIDTH, APP_HEIGHT);
		setBackground(Color.black);
		addMouseMotionListener(this);
		addMouseListener(this);
		// �摜�擾
		url = this.getClass().getClassLoader().getResource("img/BLOCK2.PNG");
		dropImg = new ImageIcon(url).getImage();
		dropImg = dropImg.getScaledInstance(
				dropImg.getWidth(this) * (GRID_WIDTH - BOARD_X * 5 / 6) / 100, -1, Image.SCALE_SMOOTH);
		url = this.getClass().getClassLoader().getResource("img/UIPAT1.PNG");
		UIPAT1 = new ImageIcon(url).getImage();
		// �L�����N�^�[�ƃ_���W�����ǂݍ���
		character = new Character();
		dungeon = new Dungeon();
		// �X���b�h�N��
		thread = new Thread(this);
		thread.start();
	}

	// �}�E�X�h���b�O.�Ֆʂ𑀍�.swap�Ȃ�swap
	@Override
	public void mouseDragged(MouseEvent e) {
		if(touchDrop == true && eraseDrop == false && character.nowHp > 0) {
			x = e.getX();
			y = e.getY();
			nowX = (x - BOARD_X) / GRID_WIDTH;
			nowY = (y - BOARD_Y) / GRID_HEIGHT;
			// �J�[�\������ʓ��ɗ��߂�
			if(nowY < 0) {
				nowY = 0;
			}
			if(y < 0) {
				y = 0;
				nowY = 0;
			} else if(nowY > 4) {
				y = APP_HEIGHT;
				nowY = 4;
			}
			if(x < 0) {
				x = 0;
				nowX = 0;
			} else if(nowX > 5) {
				x = APP_WIDTH;
				nowX = 5;
			}
			if(nowX != preX || nowY != preY) {
				eraseFlag = true;
				swapDrop = true;
				swapDrop();
			}
		}
	}

	// �}�E�X�v���X.�ՖʂȂ�h���b�v����
	@Override
	public void mousePressed(MouseEvent e) {
		// �ՖʂȂ�h���b�v����
		if(e.getX() > BOARD_X && e.getX() < BOARD_X + GRID_WIDTH * 6
				&& e.getY() > BOARD_Y && e.getY() < BOARD_Y + GRID_HEIGHT * 5 && eraseDrop == false && character.nowHp > 0) {
			// x��y�ɍ��W��o�^
			x = e.getX();
			y = e.getY();
			// �Ֆʂ̈ʒu�����W����v�Z
			nowX = (x - BOARD_X) / GRID_WIDTH;
			nowY = (y - BOARD_Y) / GRID_HEIGHT;
			preX = nowX;
			preY = nowY;
			touchDrop = true;
			alpha = 1f;
		}
	}

	// �}�E�X�����[�X.�R���{�𔻒�
	@Override
	public void mouseReleased(MouseEvent e) {
		// swap�}���`�X���b�h�̂��݂��c���Ă�����j��
		if(swapNum.size() != 0) {
			swapNum.clear();
		}
		if(touchDrop == true && eraseDrop == false){
			touchDrop = false;
			startMoveTime = 0;
		}
		if(eraseDrop == false) {
			if(eraseFlag == true) {
				preTurn = nowTurn;
				eraseFlag = false;
				checkErase();
			}
		}
	}

	// �R���{����
	void checkErase() {
		int eraseNum = 0;
		int concatNum = 1;
		int temp = 0;
		boolean pass = false;
		int dropColor = 0;
		double coefficient = 0;
		boolean special = false;
		eraseDrop = true;

		// �����\�ȍ��W��������
		for(int j = 6; j >= 0; j--) {
			for(int i = 0; i < 8; i++) {
				eraseMap[i][j] = 0;
			}
		}

		// ��������3�������W���L�^
		for(int j = 4; j >= 0; j--) {
			for(int i = 0; i < 4; i++) {
				if(dropMap[i][j] == dropMap[i + 1][j] &&
						dropMap[i + 1][j] == dropMap[i + 2][j]) {
					eraseNum++;
					eraseMap[i + 1][j + 1] = eraseNum;
					eraseMap[i + 2][j + 1] = eraseNum;
					eraseMap[i + 3][j + 1] = eraseNum;
				}
			}
		}
		// �c������3�������W���L�^
		for(int j = 4; j >= 2; j--) {
			for(int i = 0; i < 6; i++) {
				if(dropMap[i][j] == dropMap[i][j - 1] &&
						dropMap[i][j - 1] == dropMap[i][j - 2]) {
					eraseNum++;
					eraseMap[i + 1][j + 1] = eraseNum;
					eraseMap[i + 1][j] = eraseNum;
					eraseMap[i + 1][j - 1] = eraseNum;
				}
			}
		}

		// �����\�Ȃ��̂������Ȃ甲����
		if(eraseNum == 0) {
			// �����̍U��.�^�[���ƃX�L���^�[���̌o��
			if(comboInfo.size() > 0) {
				playerAttack();
				nowSkillTurn++;
				// ���񎞂̃A�j���[�V�����X�L�b�v���I��
			} else if(nowSkillTurn == 0) {
				nowSkillTurn++;
			}
			// �G�̍U��
			if(preTurn == nowTurn) {
				nowTurn++;
				enemyAttack();
			}
			// �O��̃R���{���������Ŕj��
			comboInfo.clear();
			eraseCount = 0;
			head = 0;
			comboStringColorNum = 0;
			eraseDrop = false;
			return;
		}

		// 3�����̈�̌���
		// ���ォ��c���ŉE�֌������Ĕ���B�����\�ȍ��W�̉��ƉE�������\�����ׁA
		// ���F�Ȃ瑊��������̐��l�ɕς���B���̍ۑ���̐��l���L�^���Ă����A
		// ����̐��l��Ֆʂ���T���A�����̐��l�ɕς���B
		for(int k = 0; k < eraseNum; k++) {
			for(int i = 1; i < 7; i++) {
				for(int j = 1; j < 6; j++) {
					if(eraseMap[i][j] == concatNum) {
						// ��
						if(eraseMap[i][j + 1] > 0) {
							if(dropMap[i - 1][j - 1] == dropMap[i - 1][j]) {
								temp = eraseMap[i][j + 1];
								eraseMap[i][j + 1] = eraseMap[i][j];
								for(int concatI = 1; concatI < 7; concatI++) {
									for(int concatJ = 1; concatJ < 6; concatJ++) {
										if(eraseMap[concatI][concatJ] == temp) {
											eraseMap[concatI][concatJ] = concatNum;
										}
									}
								}
							}
						}
						// �E
						if(eraseMap[i + 1][j] > 0) {
							if(dropMap[i - 1][j - 1] == dropMap[i][j - 1]) {
								temp = eraseMap[i + 1][j];
								eraseMap[i + 1][j] = eraseMap[i][j];
								for(int concatI = 1; concatI < 7; concatI++) {
									for(int concatJ = 1; concatJ < 6; concatJ++) {
										if(eraseMap[concatI][concatJ] == temp) {
											eraseMap[concatI][concatJ] = concatNum;
										}
									}
								}
							}
						}
					}
				}
			}
			concatNum++;
		}

		// ���l���l�߂�
		for(int k = 1; k < concatNum; k++) {
			for(int i = 1; i < 7; i++) {
				for(int j = 1; j < 6; j++) {
					if(eraseMap[i][j] == k) {
						pass = true;
					}
				}
			}
			if(pass == false) {
				temp = k;
				while(pass == false) {
					for(int fillI = 1; fillI < 7; fillI++) {
						for(int fillJ = 1; fillJ < 6; fillJ++) {
							if(eraseMap[fillI][fillJ] == temp + 1) {
								eraseMap[fillI][fillJ] = k;
								pass = true;
							}
							if(temp > concatNum) {
								pass = true;
							}
						}
					}
					temp++;
				}
			}
			pass = false;
		}

		// �R���{����o�^
		for(int k = 0; k < 10; k++) {
			for(int i = 1; i < 7; i++) {
				for(int j = 1; j < 6; j++) {
					if(eraseMap[i][j] == k + 1) {
						place.add(new Place(i - 1, j - 1));
					}
					if(eraseMap[i][j] == k + 1 && pass == false) {
						pass = true;
						dropColor = dropMap[i - 1][j - 1];
					}
				}
			}
			if(pass == true) {
				// �{���Ɨ�or�l�����̔���͂܂�
				comboInfo.add(new ComboInfo(dropColor, coefficient, special, place));
				place.clear();
			}
			pass = false;
		}
		if(nowSkillTurn != 0) {
			eraseAnime();
		} else {
			eraseDrop();
		}
	}

	// �h���b�v����������A�j��
	void eraseAnime() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new LimitTimerTask(timer, 13) {
			@Override
			public void run() {
				synchronized (this) {
				cureDropAddWidth = 0;
				cureDropAddHeight = 0;
				eraseAlpha -= 0.07f;
				for(int i = 0; i < comboInfo.get(eraseCount).place.size(); i++) {
					// �񕜂̓h���b�v��������Ə�����
					if(dropMap[comboInfo.get(eraseCount).place.get(0).x][comboInfo.get(eraseCount).place.get(0).y] == 12) {
						cureDropAddWidth = GRID_WIDTH * 2 / 25;
						cureDropAddHeight = GRID_HEIGHT * 2 / 25;
					}
					// �h���b�v��`��
					dbg2.setComposite(AlphaComposite.getInstance(
							AlphaComposite.SRC_OVER, eraseAlpha));
					dbg2.drawImage(dropImg, comboInfo.get(eraseCount).place.get(i).x * GRID_WIDTH + BOARD_X + cureDropAddWidth,
							comboInfo.get(eraseCount).place.get(i).y * GRID_HEIGHT + BOARD_Y + cureDropAddWidth,
							comboInfo.get(eraseCount).place.get(i).x * GRID_WIDTH + BOARD_X + GRID_WIDTH - cureDropAddWidth,
							comboInfo.get(eraseCount).place.get(i).y * GRID_HEIGHT + BOARD_Y + GRID_HEIGHT - cureDropAddWidth,
							(dropMap[comboInfo.get(eraseCount).place.get(i).x][comboInfo.get(eraseCount).place.get(i).y] % 4) * GRID_WIDTH,
							(dropMap[comboInfo.get(eraseCount).place.get(i).x][comboInfo.get(eraseCount).place.get(i).y] / 4) * GRID_WIDTH,
							(dropMap[comboInfo.get(eraseCount).place.get(i).x][comboInfo.get(eraseCount).place.get(i).y] % 4) * GRID_WIDTH + GRID_WIDTH - cureDropAddWidth * 2,
							(dropMap[comboInfo.get(eraseCount).place.get(i).x][comboInfo.get(eraseCount).place.get(i).y] / 4) * GRID_WIDTH + GRID_WIDTH - cureDropAddWidth * 2, null);
				}
				cureDropAddWidth = 0;
				cureDropAddHeight = 0;
				// �R���{��`��
				int comboStartX = 1000, comboStartY = 1000;
				int comboEndX = 0, comboEndY = 0;
				int comboX = 0, comboY = 0;
				for(int comboI = 0; comboI < comboInfo.get(eraseCount).place.size(); comboI++) {
					if(comboStartX > comboInfo.get(eraseCount).place.get(comboI).x * GRID_WIDTH + BOARD_X) {
						comboStartX = comboInfo.get(eraseCount).place.get(comboI).x * GRID_WIDTH + BOARD_X;
					}
					if(comboStartY > comboInfo.get(eraseCount).place.get(comboI).y * GRID_HEIGHT + BOARD_Y) {
						comboStartY = comboInfo.get(eraseCount).place.get(comboI).y * GRID_HEIGHT + BOARD_Y;
					}
					if(comboEndX < comboInfo.get(eraseCount).place.get(comboI).x * GRID_WIDTH + BOARD_X) {
						comboEndX = comboInfo.get(eraseCount).place.get(comboI).x * GRID_WIDTH + BOARD_X;
					}
					if(comboEndY < comboInfo.get(eraseCount).place.get(comboI).y * GRID_HEIGHT + BOARD_Y) {
						comboEndY = comboInfo.get(eraseCount).place.get(comboI).y * GRID_HEIGHT + BOARD_Y;
					}
				}
				comboX = (comboStartX + comboEndX) / 2;
				comboY = (int) ((comboStartY + comboEndY) / 2 + radius * Math.sin(Math.toRadians(-13 * count)) + GRID_HEIGHT * 2 / 9);
				if(eraseCount > 0) {
					dbg2.setComposite(AlphaComposite.getInstance(
							AlphaComposite.SRC_OVER, 1f));
					dbg2.setColor(Color.getHSBColor(0 + (float)(comboStringColorNum % 1.02), 1f, 0.6f));
					comboStringColorNum += 0.02;
					dbg2.setFont(new Font("SansSerif", Font.BOLD, APP_WIDTH / 32 + GRID_WIDTH / 2 - count * GRID_WIDTH / 24));
					try {
						dbg2.drawString("Combo", comboX + GRID_WIDTH / 9 - GRID_WIDTH + count * GRID_WIDTH / 12, comboY + GRID_HEIGHT / 2);
						dbg2.setFont(new Font("SansSerif", Font.BOLD, APP_WIDTH / 22 + GRID_WIDTH / 2 - count * GRID_WIDTH / 24));
						temp = Integer.toString(eraseCount + 1);
						dbg2.drawString(temp, comboX + GRID_WIDTH * 31 / 40 + GRID_WIDTH - count * GRID_WIDTH / 12, comboY + GRID_HEIGHT / 2);
					} catch (Exception e) {
						errorNum++;
					}
				}
				// �񕜂����Z
				if(dropMap[comboInfo.get(eraseCount).place.get(0).x][comboInfo.get(eraseCount).place.get(0).y] == 12 && count == 0) {
					nowRcu += character.maxRcu;
				}
				// �񕜂�`��
				if(nowRcu > 0) {
					temp = "+" + nowRcu;
					dbg2.setColor(Color.getHSBColor(0.32f, 0.9f, 0.9f));
					dbg2.setFont(new Font("SansSerif", Font.BOLD, APP_WIDTH / 20));
					dbg2.setComposite(AlphaComposite.getInstance(
							AlphaComposite.SRC_OVER, 1f));
					try {
						dbg2.drawString(temp, APP_WIDTH / 2,
								BOARD_Y);
					} catch (Exception e) {
						errorNum++;
					}
				}

				// �_���[�W��`��.�呮��
				for(int i = 0; i < 6; i++) {
					character.getCharacterStatus(i);
					switch (character.mainColor) {
					case 0:
						dbg2.setColor(Color.getHSBColor(0f, 0.9f, 0.9f));
						break;
					case 1:
						dbg2.setColor(Color.getHSBColor(0.6f, 0.9f, 0.9f));
						break;
					case 2:
						dbg2.setColor(Color.getHSBColor(0.32f, 0.9f, 0.9f));
						break;
					case 3:
						dbg2.setColor(Color.getHSBColor(0.16f, 0.9f, 0.9f));
						break;
					case 4:
						dbg2.setColor(Color.getHSBColor(0.78f, 0.9f, 0.9f));
						break;
					}
					if(comboInfo.get(eraseCount).dropColor == character.mainColor) {
						if(count == 0) {
							characterAttackNum[i] += character.atk;
						}
						temp = "" + characterAttackNum[i];
						dbg2.setFont(new Font("SansSerif", Font.BOLD, APP_WIDTH / 20));
						dbg2.setComposite(AlphaComposite.getInstance(
								AlphaComposite.SRC_OVER, 1f));
						try {
							dbg2.drawString(temp, i * GRID_WIDTH + BOARD_X,
									(int) (BOARD_Y - GRID_HEIGHT / 3 - GRID_HEIGHT * 50 / 51 + radius * Math.sin(Math.toRadians(-13 * count)) + GRID_HEIGHT * 4 / 9));
						} catch (Exception e) {
							errorNum++;
						}
					} else if(characterAttackNum[i] > 0) {
						temp = "" + characterAttackNum[i];
						dbg2.setFont(new Font("SansSerif", Font.BOLD, APP_WIDTH / 20));
						dbg2.setComposite(AlphaComposite.getInstance(
								AlphaComposite.SRC_OVER, 1f));
						try {
							dbg2.drawString(temp, i * GRID_WIDTH + BOARD_X,
									(int) (BOARD_Y - GRID_HEIGHT / 3 - GRID_HEIGHT * 50 / 51 + GRID_HEIGHT * 4 / 9));
						} catch (Exception e) {
							errorNum++;
						}
					}
					// ������
					switch (character.subColor) {
					case 0:
						dbg2.setColor(Color.getHSBColor(0f, 0.9f, 0.9f));
						break;
					case 1:
						dbg2.setColor(Color.getHSBColor(0.6f, 0.9f, 0.9f));
						break;
					case 2:
						dbg2.setColor(Color.getHSBColor(0.32f, 0.9f, 0.9f));
						break;
					case 3:
						dbg2.setColor(Color.getHSBColor(0.16f, 0.9f, 0.9f));
						break;
					case 4:
						dbg2.setColor(Color.getHSBColor(0.78f, 0.9f, 0.9f));
						break;
					}
					if(comboInfo.get(eraseCount).dropColor == character.subColor) {
						if(count == 0) {
							if(character.mainColor != character.subColor) {
								characterAttackSubNum[i] += character.atk * 3 / 10;
							} else {
								characterAttackSubNum[i] += character.atk / 10;
							}
						}
						temp = "" + characterAttackSubNum[i];
						dbg2.setFont(new Font("SansSerif", Font.BOLD, APP_WIDTH / 20));
						dbg2.setComposite(AlphaComposite.getInstance(
								AlphaComposite.SRC_OVER, 1f));
						try {
							dbg2.drawString(temp, i * GRID_WIDTH + BOARD_X * 8,
									(int) (BOARD_Y - GRID_HEIGHT / 3 - GRID_HEIGHT * 50 / 51 + radius * Math.sin(Math.toRadians(-13 * count)) + GRID_HEIGHT));
						} catch (Exception e) {
							errorNum++;
						}
					} else if(characterAttackSubNum[i] > 0) {
						temp = "" + characterAttackSubNum[i];
						dbg2.setFont(new Font("SansSerif", Font.BOLD, APP_WIDTH / 20));
						dbg2.setComposite(AlphaComposite.getInstance(
								AlphaComposite.SRC_OVER, 1f));
						try {
							dbg2.drawString(temp, i * GRID_WIDTH + BOARD_X * 8,
									(int) (BOARD_Y - GRID_HEIGHT / 3 - GRID_HEIGHT * 50 / 51 + GRID_HEIGHT));
						} catch (Exception e) {
							errorNum++;
						}
					}
				}

				// �I������
					if(++count == procNum) {
						this.cancel();
						timer.cancel();
						// �ċA�T��
						eraseCount++;
						if(eraseCount < comboInfo.size()) {
							eraseAnime();
						} else {
							eraseDrop();
						}
					}
				}
			}
		}, 1000 / FPS, 1000 / FPS);
	}

	// ���ۂɃh���b�v����������
	void eraseDrop() {
		boolean pass = false;
		int temp;
		// �h���b�v������
		temp = comboInfo.size() - head;;
		for(int k = 0; k < temp; k++) {
			for(int i = 1; i < 7; i++) {
				for(int j = 1; j < 6; j++) {
					if(eraseMap[i][j] == k + 1) {
						dropMap[i - 1][j - 1] = -1;
						eraseMap[i][j] = -1;
					}
				}
			}
			head++;
		}

		// �h���b�v�����֋l�߂�
		for(int i = 0; i < 6; i++) {
			for(int j = 4; j > 0; j--) {
				pass = false;
				if(dropMap[i][j] == -1) {
					int fall = j - 1;
					while(pass == false) {
						if(dropMap[i][fall] != -1) {
							dropMap[i][j] = dropMap[i][fall];
							dropMap[i][fall] = -1;
							eraseMap[i + 1][fall + 1] = -1;
							pass = true;
							tempI = i;
							tempJ = j;
							tempFall = fall;

							if(nowSkillTurn != 0) {
								Timer timer = new Timer();
								timer.scheduleAtFixedRate(new LimitTimerTask(timer, 10){
									@Override
									public void run() {
										synchronized (this) {

											cureDropAddWidth = 0;
											cureDropAddHeight = 0;
											// �񕜂̓h���b�v��������Ə�����
											if(dropMap[tempI][tempJ] == 12) {
												cureDropAddWidth = GRID_WIDTH * 2 / 25;
												cureDropAddHeight = GRID_HEIGHT * 2 / 25;
											}
												dbg2.drawImage(dropImg, tempI * GRID_WIDTH + BOARD_X + cureDropAddWidth,
														tempFall * GRID_HEIGHT + BOARD_Y + cureDropAddHeight + count * GRID_HEIGHT / 9 * (tempJ - tempFall),
														tempI * GRID_WIDTH + GRID_WIDTH + BOARD_X - cureDropAddWidth,
														tempFall * GRID_HEIGHT + GRID_HEIGHT + BOARD_Y - cureDropAddHeight + count * GRID_HEIGHT / 9 * (tempJ - tempFall),
														(dropMap[tempI][tempJ] % 4) * GRID_WIDTH, (dropMap[tempI][tempJ] / 4) * GRID_WIDTH,
														(dropMap[tempI][tempJ] % 4) * GRID_WIDTH + GRID_WIDTH - cureDropAddWidth * 2,
														(dropMap[tempI][tempJ] / 4) * GRID_WIDTH + GRID_WIDTH - cureDropAddWidth * 2, null);

											// �I������
											if(++count == procNum) {
												this.cancel();
												timer.cancel();
											}
										}
									}
								}, 1000 / FPS, 1000 / FPS);
							}

						}
						if(fall == 0) {
							pass = true;
						}
						fall--;
					}
				}
			}
		}

		// �h���b�v���~�点��
		for(int i = 0; i < 6; i++) {
			for(int j = 4; j >= 0; j--) {
				if(dropMap[i][j] == -1) {
					temp = (int)(Math.random() * 6);
					if(temp == 5) {
						temp = 12;
					}
					dropMap[i][j] = temp;
					setCount++;
				}
			}
			tempI = i;
			if(setCount != 0) {
				if(nowSkillTurn != 0) {
					Timer timer = new Timer();
					timer.scheduleAtFixedRate(new LimitTimerTask(timer, 10){
						@Override
						public void run() {
							synchronized (this) {
								cureDropAddWidth = 0;
								cureDropAddHeight = 0;
								// �񕜂̓h���b�v��������Ə�����
								for(int j = 0; j < setCount; j++) {
									if(dropMap[tempI][j] == 12) {
										cureDropAddWidth = GRID_WIDTH * 2 / 25;
										cureDropAddHeight = GRID_HEIGHT * 2 / 25;
									}
									dbg2.drawImage(dropImg, tempI * GRID_WIDTH + BOARD_X + cureDropAddWidth,
											j * GRID_HEIGHT + BOARD_Y + cureDropAddHeight + count * GRID_HEIGHT / 9 * setCount - GRID_HEIGHT * setCount,
											tempI * GRID_WIDTH + GRID_WIDTH + BOARD_X - cureDropAddWidth,
											j * GRID_HEIGHT + GRID_HEIGHT + BOARD_Y - cureDropAddHeight + count * GRID_HEIGHT / 9 * setCount - GRID_HEIGHT * setCount,
											(dropMap[tempI][j] % 4) * GRID_WIDTH, (dropMap[tempI][j] / 4) * GRID_WIDTH,
											(dropMap[tempI][j] % 4) * GRID_WIDTH + GRID_WIDTH - cureDropAddWidth * 2,
											(dropMap[tempI][j] / 4) * GRID_WIDTH + GRID_WIDTH - cureDropAddWidth * 2, null);
								}
								// �I������
								if(++count == procNum) {
									this.cancel();
									timer.cancel();
								}
							}
						}
					}, 1000 / FPS, 1000 / FPS);
				}
			}
			setCount = 0;
		}
		if(nowSkillTurn != 0) {
			try { Thread.sleep(1000 / FPS * 10); } catch (InterruptedException e) {}
		}
		// �ċA�T��
		checkErase();
	}

	void playerAttack() {
		int damage;
		// 6�R���{�ȏ��12.25�{
		if(comboInfo.size() >= 6) {
			for(int i = 0; i < 6; i++) {
				characterAttackNum[i] *= 12.25;
				characterAttackSubNum[i] *= 12.25;
			}
		}
		// �R���{���Ŕ{��������
		for(int i = 0; i < 6; i++) {
			characterAttackNum[i] *= (1 + (comboInfo.size() - 1) * 0.25);
			characterAttackSubNum[i] *= (1 + (comboInfo.size() - 1) * 0.25);
		}
		nowRcu *= (1 + (comboInfo.size() - 1) * 0.25);
		// i�͓G
		for(int i = 0; i < 4; i++) {
			// �呮���U��.�G���܂������Ȃ���s
			if(enemyNowHp[i] > 0) {
				// j�̓p�[�e�B
				for(int j = 0; j < 6; j++) {
					damage = characterAttackNum[j] - dungeon.def;
					if(damage > 0) {
						enemyNowHp[i] -= damage;
					} else {
						enemyNowHp[i]--;
					}
					characterAttackNum[i] = 0;
					// �|�ꂽ�����
					if(enemyNowHp[i] <= 0) {
						dungeon.enemyData[i] = -1;
					}
				}
			}
			// �������U��.�G���܂������Ȃ���s
			if(enemyNowHp[i] > 0) {
				// j�̓p�[�e�B
				for(int j = 0; j < 6; j++) {
					damage = characterAttackSubNum[j] - dungeon.def;
					if(damage > 0) {
						enemyNowHp[i] -= damage;
					} else {
						enemyNowHp[i]--;
					}
					characterAttackSubNum[i] = 0;
					// �|�ꂽ�����
					if(enemyNowHp[i] <= 0) {
						dungeon.enemyData[i] = -1;
					}
				}
			}
		}
		// ��
		character.nowHp += nowRcu;
		if(character.nowHp > character.maxHp) {
			character.nowHp = character.maxHp;
		}

		// �S���|������
		if(enemyNowHp[0] <= 0 &&enemyNowHp[1] <= 0 &&enemyNowHp[2] <= 0 &&enemyNowHp[3] <= 0) {
			enemyAlive = false;
			enemyConcatWidth = 0;
			enemyMaxHeight = 0;
		}

		// �U���͂�������
		for(int i = 0; i < 6; i++) {
			characterAttackNum[i] = 0;
			characterAttackSubNum[i] = 0;
			nowRcu = 0;
		}
	}

	// �R���{�����o�b�t�@�����O
	void paintcombo() {
		temp = "1";
		for(int i = 1; i < eraseCount; i++) {
			temp = Integer.toString(i + 1);
			dbg2.setFont(new Font("SansSerif", Font.BOLD, APP_WIDTH / 32));
			int comboStartX = 1000, comboStartY = 1000;
			int comboEndX = 0, comboEndY = 0;
			int comboX = 0, comboY = 0;
			for(int comboI = 0; comboI < comboInfo.get(i).place.size(); comboI++) {
				if(comboStartX > comboInfo.get(i).place.get(comboI).x * GRID_WIDTH + BOARD_X) {
					comboStartX = comboInfo.get(i).place.get(comboI).x * GRID_WIDTH + BOARD_X;
				}
				if(comboStartY > comboInfo.get(i).place.get(comboI).y * GRID_HEIGHT + BOARD_Y) {
					comboStartY = comboInfo.get(i).place.get(comboI).y * GRID_HEIGHT + BOARD_Y;
				}
				if(comboEndX < comboInfo.get(i).place.get(comboI).x * GRID_WIDTH + BOARD_X) {
					comboEndX = comboInfo.get(i).place.get(comboI).x * GRID_WIDTH + BOARD_X;
				}
				if(comboEndY < comboInfo.get(i).place.get(comboI).y * GRID_HEIGHT + BOARD_Y) {
					comboEndY = comboInfo.get(i).place.get(comboI).y * GRID_HEIGHT + BOARD_Y;
				}
			}
			comboX = (comboStartX + comboEndX) / 2;
			comboY = (comboStartY + comboEndY) / 2;
			dbg2.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 1f));
			dbg2.setColor(Color.getHSBColor(0 + (float)(comboStringColorNum % 1.02), 1f, 0.6f));
			comboStringColorNum += 0.02;

			dbg2.drawString("Combo", comboX + GRID_WIDTH / 9, comboY + GRID_HEIGHT / 2);
			dbg2.setFont(new Font("SansSerif", Font.BOLD, APP_WIDTH / 22));
			dbg2.drawString(temp, comboX + GRID_WIDTH * 31 / 40, comboY + GRID_HEIGHT / 2);
		}
	}

	// �ׂ荇���h���b�v����].��]����Ԃ���util�̃^�C�}�[�Ń}���`�X���b�h���N��
	void swapDrop() {
		int temp;
		// ���邭���.�񐔐����̂���TimerTask�i����j���N��
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new LimitTimerTask(timer, 5) {
			@Override
			public void run() {
				synchronized (this) {
				// �h���b�v�̓��������ɂ���ĉ�]�������w��
				if(nowX > preX || nowY < preY) {
					velocity = 100;
				} else {
					velocity = -100;
				}
				if(nowY - preY != 0) {
					t = t - 0.9;
				}
				if(nowX < preX) {
					t = t - 1.8;
				}
				// ��ɂ������h���b�v
				dbg2.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, 1f));
				x = cx + radius * Math.cos(Math.toRadians(velocity * t));
				y = cy + radius * Math.sin(Math.toRadians(velocity * t));
				dbg2.drawImage(dropImg, (int)x + preCureDropAddWidth,
						(int)y + preCureDropAddWidth,
						(int)x + GRID_WIDTH - preCureDropAddWidth,
						(int)y + GRID_HEIGHT - preCureDropAddWidth,
						(dropMap[preX][preY] % 4) * GRID_WIDTH, (dropMap[preX][preY] / 4) * GRID_WIDTH,
						(dropMap[preX][preY] % 4) * GRID_WIDTH + GRID_WIDTH - preCureDropAddWidth * 2,
						(dropMap[preX][preY] / 4) * GRID_WIDTH + GRID_WIDTH - preCureDropAddWidth * 2, null);

				// �������Ă���h���b�v
				dbg2.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, 0.3f));
				x = cx + radius * -Math.cos(Math.toRadians(velocity * t));
				y = cy + radius * -Math.sin(Math.toRadians(velocity * t));
				dbg2.drawImage(dropImg, (int)x + nowCureDropAddWidth,
						(int)y + nowCureDropAddWidth,
						(int)x + GRID_WIDTH - nowCureDropAddWidth,
						(int)y + GRID_HEIGHT - nowCureDropAddWidth,
						(dropMap[nowX][nowY] % 4) * GRID_WIDTH, (dropMap[nowX][nowY] / 4) * GRID_WIDTH,
						(dropMap[nowX][nowY] % 4) * GRID_WIDTH + GRID_WIDTH - nowCureDropAddWidth * 2,
						(dropMap[nowX][nowY] / 4) * GRID_WIDTH + GRID_WIDTH - nowCureDropAddWidth * 2, null);

				if(nowY - preY != 0) {
					t = t + 0.9;
				}
				if(nowX < preX) {
					t = t + 1.8;
				}
				t += 0.36;
				dbg2.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, 1f));
				// �I������
					if(++count == procNum) {
						if(swapNum.size() != 0) {
							swapNum.remove(0);
						}
						swapDrop = false;
						this.cancel();
						timer.cancel();
					}
				}
			}
		}, 1000 / FPS, 1000 / FPS);

		// �Ֆʂ̓�������swap
		temp = dropMap[preX][preY];
		dropMap[preX][preY] = dropMap[nowX][nowY];
		dropMap[nowX][nowY] = temp;
		preX = nowX;
		preY = nowY;
	}

	// �����Ă���h���b�v���o�b�t�@�����O
	void moveDrop() {
		dbg2.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 0.8f));
		cureDropAddWidth = 0;
		cureDropAddHeight = 0;
		// �񕜂̓h���b�v��������Ə�����
		if(dropMap[nowX][nowY] == 12) {
			cureDropAddWidth = GRID_WIDTH * 2 / 25;
			cureDropAddHeight = GRID_HEIGHT * 2 / 25;
		}
		dbg2.drawImage(dropImg, x - MOVE_DROP_WIDTH + cureDropAddWidth, y - MOVE_DROP_HEIGHT + cureDropAddHeight,
		x + MOVE_DROP_WIDTH - cureDropAddWidth, y + MOVE_DROP_HEIGHT - cureDropAddHeight,
		(dropMap[nowX][nowY] % 4) * GRID_WIDTH, (dropMap[nowX][nowY] / 4) * GRID_WIDTH,
		(dropMap[nowX][nowY] % 4) * GRID_WIDTH + GRID_WIDTH - cureDropAddWidth * 2,
		(dropMap[nowX][nowY] / 4) * GRID_WIDTH + GRID_WIDTH - cureDropAddWidth * 2, this);

		// ���쎞�Ԏc��3�b����x����\��
		dbg2.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 1f));
		if(canMoveSecond - (nowTime - startMoveTime) / 1000L <= 3 && startMoveTime != 0) {
			BigDecimal bd = new BigDecimal(canMoveSecond - (nowTime - startMoveTime) / 1000.0);
			BigDecimal bd1 = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
			if(bd1.doubleValue() * 10 % 6 == 0 || bd1.doubleValue() * 10 % 6 == 1 || bd1.doubleValue() * 10 % 6 == 2) {
			dbg2.drawImage(UIPAT1, x - GRID_WIDTH / 2, y - GRID_HEIGHT / 2 - GRID_HEIGHT * 4 / 3,
					x + GRID_WIDTH / 2, y + GRID_HEIGHT / 2 - GRID_HEIGHT * 4 / 3,
					1, 49, 46, 93, this);
			} else {
				dbg2.drawImage(UIPAT1, x - GRID_WIDTH / 2, y - GRID_HEIGHT / 2 - GRID_HEIGHT * 4 / 3,
						x + GRID_WIDTH / 2, y + GRID_HEIGHT / 2 - GRID_HEIGHT * 4 / 3,
						49, 49, 94, 93, this);
			}
			// �σo�[
			dbg2.setColor(Color.getHSBColor(0 + (float)(bd1.doubleValue() / 3 % 0.13), 0.8f, 0.8f));
			dbg2.fillRect(x - GRID_WIDTH * 10 / 23, y - GRID_HEIGHT * 150 / 151,
					(GRID_WIDTH * 6 / 21) * (int)(bd1.doubleValue() * 10) / 10, GRID_HEIGHT / 9);
		}
	}

	// �Ֆʂ̃h���b�v���o�b�t�@�����O
	void paintDrop() {
		boolean pass = false;
		for(int i = 0; i < 6; i++) {
			for(int j = 0; j < 5; j++) {
				// �I�񂾃h���b�v�𓧖���
				if(nowX == i && nowY == j && touchDrop == true) {
					dbg2.setComposite(AlphaComposite.getInstance(
							AlphaComposite.SRC_OVER, alpha));
				} else {
					dbg2.setComposite(AlphaComposite.getInstance(
							AlphaComposite.SRC_OVER, 1f));
				}
				cureDropAddWidth = 0;
				cureDropAddHeight = 0;
				// �񕜂̓h���b�v��������Ə�����
				if(dropMap[i][j] == 12) {
					cureDropAddWidth = GRID_WIDTH * 2 / 25;
					cureDropAddHeight = GRID_HEIGHT * 2 / 25;
				}
				// swap���Ă���h���b�v�͕\�����Ȃ�
				if(swapDrop == true) {
					if(nowX == i && nowY == j) {
						pass = true;
					}
				}
				if(pass == false) {
					for(int swapI = 0; swapI < swapNum.size(); swapI++) {
						if(swapNum.get(swapI).x== i && swapNum.get(swapI).y == j) {
							pass = true;
						}
					}
				}
				// �����A�j�������Q�[���I�[�o�[�Ȃ�\�����Ȃ�
				if(eraseMap[i + 1][j + 1] <= eraseCount - head + 1 && eraseMap[i + 1][j + 1] != 0 || character.nowHp <= 0) {
					pass = true;
				}
				// �\�����Ă����Ȃ�h���b�v��\��
				if(pass == false) {
					dbg2.drawImage(dropImg, i * GRID_WIDTH + BOARD_X + cureDropAddWidth,
							j * GRID_HEIGHT + BOARD_Y + cureDropAddHeight,
							i * GRID_WIDTH + GRID_WIDTH + BOARD_X - cureDropAddWidth,
							j * GRID_HEIGHT + GRID_HEIGHT + BOARD_Y - cureDropAddHeight,
							(dropMap[i][j] % 4) * GRID_WIDTH, (dropMap[i][j] / 4) * GRID_WIDTH,
							(dropMap[i][j] % 4) * GRID_WIDTH + GRID_WIDTH - cureDropAddWidth * 2,
							(dropMap[i][j] / 4) * GRID_WIDTH + GRID_WIDTH - cureDropAddWidth * 2, this);
				}
				dbg2.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, 1f));
				pass = false;
			}
		}
	}

	// �w�i���o�b�t�@�����O
	void paintBg() {
		// �w�i�����h��
		dbg2.setColor(Color.darkGray);
		dbg2.fillRect(0, 0, APP_WIDTH, APP_HEIGHT);
		// �Ֆ�
		dbg2.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 1f));
		for(int i = 0; i < 6; i++) {
			for(int j = 0; j < 5; j++) {
				if((i % 2 == 1 && j % 2 == 0) ||
						(i % 2 == 0 && j % 2 == 1)) {
					dbg2.setColor(Color.getHSBColor(0.08f, 1f, 0.29f));
				} else {
					dbg2.setColor(Color.getHSBColor(0.06f, 1f, 0.22f));
				}
				dbg2.fillRect(i * GRID_WIDTH + BOARD_X, j * GRID_HEIGHT + BOARD_Y,
						GRID_WIDTH, GRID_HEIGHT);
			}
		}
		// �񕜃Q�[�W
		dbg2.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 1f));
		dbg2.drawImage(UIPAT1, GRID_WIDTH / 12 + BOARD_X, BOARD_Y - GRID_HEIGHT / 3,
				GRID_WIDTH * 2 / 5 + BOARD_X, BOARD_Y - GRID_HEIGHT / 30,
				176, 48, 212, 82, this);
		dbg2.drawImage(UIPAT1, GRID_WIDTH * 2 / 5 + BOARD_X, BOARD_Y - GRID_HEIGHT / 3,
				APP_WIDTH - (GRID_WIDTH / 6 + BOARD_X), BOARD_Y - GRID_HEIGHT / 30,
				213, 48, 242, 82, this);
		dbg2.drawImage(UIPAT1, APP_WIDTH - (GRID_WIDTH / 6 + BOARD_X), BOARD_Y - GRID_HEIGHT / 3,
				APP_WIDTH - (GRID_WIDTH / 24 + BOARD_X), BOARD_Y - GRID_HEIGHT / 30,
				243, 48, 255, 82, this);
		// �񕜃o�[.HP�̊����Œ�������
		dbg2.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 1f));
		BigDecimal bd = new BigDecimal((double)character.nowHp / (double)character.maxHp);
		BigDecimal bd1 = bd.setScale(3, BigDecimal.ROUND_UP);
		dbg2.drawImage(UIPAT1, GRID_WIDTH * 2 / 5 + BOARD_X, BOARD_Y - GRID_HEIGHT / 4,
				GRID_WIDTH * 2 / 5 + BOARD_X + (int)((APP_WIDTH - (double)(GRID_WIDTH * 16 / 27 + BOARD_X)) * bd1.doubleValue()), BOARD_Y - GRID_HEIGHT / 8,
				268, 66, 283, 77, this);
		// HP���l
		temp = character.nowHp + "/" + character.maxHp;
		dbg2.setFont(new Font("SansSerif", Font.BOLD, APP_WIDTH / 23));
		// �F����
		if( bd1.doubleValue() == 1) {
			dbg2.setColor(Color.getHSBColor(0.3f, 0.9f, 0.9f));
		} else if( bd1.doubleValue() >= 0.8) {
			dbg2.setColor(Color.getHSBColor(0.55f, 0.9f, 0.9f));
		} else if( bd1.doubleValue() >= 0.5) {
			dbg2.setColor(Color.getHSBColor(0.17f, 0.9f, 0.9f));
		} else if( bd1.doubleValue() >= 0.2) {
			dbg2.setColor(Color.getHSBColor(0.11f, 0.9f, 0.9f));
		} else {
			dbg2.setColor(Color.getHSBColor(0f, 0.9f, 0.9f));
		}
		dbg2.drawString(temp, GRID_WIDTH * 71 / 16, GRID_HEIGHT * 139 / 34);
}

	// �L�����N�^�[���o�b�t�@�����O
	void paintCharacter() {
		for(int i = 0; i < 6; i++) {
			character.getCharacterImage(i);
			// ��
			dbg2.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 1f));
			dbg2.drawImage(character.faceCard, i * GRID_WIDTH + BOARD_X, BOARD_Y - GRID_HEIGHT / 3 - GRID_HEIGHT * 50 / 51,
					i * GRID_WIDTH + GRID_WIDTH * 50 / 51 + BOARD_X, BOARD_Y - GRID_HEIGHT / 3,
					character.faceX, character.faceY, character.faceX + 95, character.faceY + 95, this);
			// �呮���ƕ�����
			dbg2.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 1f));
			dbg2.drawImage(character.CARDFRAME2, i * GRID_WIDTH + BOARD_X, BOARD_Y - GRID_HEIGHT / 3 - GRID_HEIGHT,
					i * GRID_WIDTH + GRID_WIDTH + BOARD_X, BOARD_Y - GRID_HEIGHT / 3,
					character.mainColor * 102 + 1, 1, character.mainColor * 102 + 102, 99, this);
			dbg2.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 1f));
			dbg2.drawImage(character.CARDFRAME2, i * GRID_WIDTH + BOARD_X, BOARD_Y - GRID_HEIGHT / 3 - GRID_HEIGHT,
					i * GRID_WIDTH + GRID_WIDTH + BOARD_X, BOARD_Y - GRID_HEIGHT / 3,
					character.subColor * 102 + 1, 103, character.subColor * 102 + 102, 203, this);
		}
	}

	void setEnemy() {
		dungeon.setEnemyFloorData(nowFloor);
		for(int i = 0; i < dungeon.enemyData.length; i++) {
			dungeon.getEnemyImage(dungeon.enemyData[i]);
			dungeon.getEnemyStatus(dungeon.enemyData[i]);
			if(dungeon.mainColor != -1) {
				enemyConcatWidth += dungeon.enemyWidth;
				if(enemyMaxHeight < dungeon.enemyImg.getHeight(this) * GRID_HEIGHT / 140.0) {
					enemyMaxHeight = (int)(dungeon.enemyImg.getHeight(this) * GRID_HEIGHT / 140.0);
				}
				enemyNowHp[i] = dungeon.hp;
			}
		}
		enemyConcatWidth = (int)(enemyConcatWidth * GRID_WIDTH / 140.0);
	}

	void paintEnemy() {
		// �ݒ�
		if(enemyAlive == false) {
			nowFloor++;
			enemyAlive = true;
			setEnemy();
		}
		// �`��
		int enemyPosiX = 0;
		for(int i = 0; i < dungeon.enemyData.length; i++) {
			dungeon.getEnemyImage(dungeon.enemyData[i]);
			if(dungeon.mainColor != -1) {
				// �G�摜
				dbg2.drawImage(dungeon.enemyImg, (APP_WIDTH - enemyConcatWidth) / 2 + enemyPosiX,
						(int)(dungeon.enemyY * GRID_HEIGHT / 140.0) + GRID_HEIGHT / 3,
						(int)((dungeon.enemyWidth * GRID_WIDTH / 140.0) + (APP_WIDTH - enemyConcatWidth) / 2) + enemyPosiX,
						(int)(dungeon.enemyImg.getHeight(this) * GRID_HEIGHT / 140.0) + (int)(dungeon.enemyY * GRID_HEIGHT / 140.0) + GRID_HEIGHT * 2 / 3,
						0, 0, dungeon.enemyWidth, dungeon.enemyImg.getHeight(this), this);
				// HP�Q�[�W
				dbg2.drawImage(UIPAT1, (APP_WIDTH - enemyConcatWidth) / 2 + enemyPosiX,
						APP_HEIGHT * 10 / 38,
						(int)((dungeon.enemyWidth + (APP_WIDTH - enemyConcatWidth) / 2) * GRID_WIDTH / 140.0) + enemyPosiX,
						APP_HEIGHT * 100 / 356,
						176, 103, 239, 120, this);
				switch (dungeon.mainColor) {
				case 0:
					dbg2.setColor(Color.getHSBColor(0f, 0.9f, 0.9f));
					break;
				case 1:
					dbg2.setColor(Color.getHSBColor(0.6f, 0.9f, 0.9f));
					break;
				case 2:
					dbg2.setColor(Color.getHSBColor(0.32f, 0.9f, 0.9f));
					break;
				case 3:
					dbg2.setColor(Color.getHSBColor(0.16f, 0.9f, 0.9f));
					break;
				case 4:
					dbg2.setColor(Color.getHSBColor(0.78f, 0.9f, 0.9f));
					break;
				}
				// HP�o�[.��
				dbg2.fillRect((APP_WIDTH - enemyConcatWidth) / 2 + enemyPosiX + GRID_WIDTH / 6,
						 APP_HEIGHT * 10 / 38 + GRID_HEIGHT / 16,
						(int)(dungeon.enemyWidth * GRID_WIDTH / 140.0) - GRID_WIDTH  * 10/ 18,
						GRID_HEIGHT / 15);
				// �^�[����
				enemyTurn = dungeon.turn - nowTurn % dungeon.turn;
				temp = "����" + enemyTurn;
				dbg2.setFont(new Font("SansSerif", Font.BOLD, APP_WIDTH / 22));
				// �Ȃ�ƂȂ��F����
				dbg2.setColor(Color.getHSBColor(0f, (float)(turnStringColorNum % 0.92), 0.9f));
				turnStringColorNum += 0.02;
				dbg2.drawString(temp, (APP_WIDTH - enemyConcatWidth) / 2 + enemyPosiX, GRID_HEIGHT / 3);
				enemyPosiX = enemyPosiX + (int)(dungeon.enemyWidth * GRID_WIDTH / 140.0);
			}
		}
	}

	void enemyAttack() {
		for(int i = 0; i < dungeon.enemyData.length; i++) {
			dungeon.getEnemyStatus(dungeon.enemyData[i]);
			if(dungeon.mainColor != -1) {
				enemyTurn = dungeon.turn - nowTurn % dungeon.turn;
				if(enemyTurn == dungeon.turn) {
					character.nowHp -= dungeon.atk;
					if(character.nowHp <= 0) {
						character.nowHp = 0;
					}
				}
			}
		}
	}

	void gameOver() {
		dbg2.setFont(new Font("SansSerif", Font.BOLD, GRID_WIDTH));
		dbg2.setColor(Color.getHSBColor(0f, 0.9f, 0.9f));
		dbg2.drawString("GAME OVER", BOARD_X, BOARD_Y * 3 / 2);
	}

	// �f�o�b�O�����o�b�t�@�����O
	void paintDebugMenu() {
		dbg2.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 1f));
		dbg2.setColor(Color.white);
		dbg2.fillRect(APP_WIDTH * 3 / 4, APP_HEIGHT / 50, APP_WIDTH / 5, APP_HEIGHT / 7);
		dbg2.setFont(new Font("SansSerif", Font.PLAIN, APP_WIDTH / 40));
		dbg2.setColor(Color.black);
		BigDecimal bd = new BigDecimal(fpsNum);
		BigDecimal bd1 = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
		temp = "fps " + bd1.doubleValue();
			dbg2.drawString(temp, APP_WIDTH * 4 / 5, APP_HEIGHT / 25);
		temp = "error " + errorNum;
		dbg2.setColor(Color.black);
		dbg2.drawString(temp, APP_WIDTH * 4 / 5, APP_HEIGHT / 17);
		temp = "swap " + swapNum.size();
		dbg2.setColor(Color.black);
		dbg2.drawString(temp, APP_WIDTH * 4 / 5, APP_HEIGHT / 13);
		temp = "turn " + nowTurn;
		dbg2.setColor(Color.black);
		dbg2.drawString(temp, APP_WIDTH * 4 / 5, APP_HEIGHT * 2 / 21);
		temp = "skill " + nowSkillTurn;
		dbg2.setColor(Color.black);
		dbg2.drawString(temp, APP_WIDTH * 4 / 5, APP_HEIGHT / 9);
		temp = "floor " + nowFloor;
		dbg2.setColor(Color.black);
		dbg2.drawString(temp, APP_WIDTH * 4 / 5, APP_HEIGHT * 5 / 39);
	}

	// �o�b�t�@�����O
	void gameRender() {
		dbg2 = (Graphics2D) dbImage.getGraphics();
		dbg2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
		dbg2.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC);
		paintBg();
		paintDrop();
		paintEnemy();
		paintCharacter();
		paintDebugMenu();
	}

	// �o�b�t�@����ʂɕ`��
	void paintScreen() {
		g2 = (Graphics2D) getGraphics();
		g2.drawImage(dbImage, 0, 0, null);
		Toolkit.getDefaultToolkit().sync();
		g2.dispose();
		dbg2.dispose();
	}

	// �����_���ȃh���b�v��Ֆʂɔz�u
	void setDrop() {
		int temp;
		for(int j = 0; j < 5; j++) {
			for(int i = 0; i < 6; i++) {
				temp = (int)(Math.random() * 6);
				if(temp == 5) {
					temp = 12;
				}
				dropMap[i][j] = temp;
			}
		}
	}

	// �o�b�t�@�̈�����
	void setBuffer() {
		dbImage = createImage(APP_WIDTH, APP_HEIGHT);
	}

	// ���C�����[�v.FPS�̊Ԋu�ŃA�N�e�B�u�����_�����O
	@Override
	public void run() {
		// timer1��timer2�ŏ����ݒ�
		Timer timer1 = new Timer();
		timer1.schedule(new TimerTask() {
			@Override
			public void run() {
				setBuffer();
				setDrop();
				gameRender();
			}
		}, 100);
		Timer timer2 = new Timer();
		timer2.schedule(new TimerTask() {
			@Override
			public void run() {
				gameRender();
				checkErase();
			}
		}, 200);
		try { Thread.sleep(1000 / FPS * 10 - 2); } catch (InterruptedException e) {}

		while(true) {
			try {
				beforeTime = afterTime;
				// �h���b�v�ړ����Ȃ�`��
				if(touchDrop == true) {
					moveDrop();
					if(alpha > 0.4f) {
						alpha -= 0.06f;
					}
				}
				// �R���{�Ȃ�`��
				if(comboInfo.size() > 1) {
					paintcombo();
				}
				// �Q�[���I�[�o�[�Ȃ�`��
				if(character.nowHp <= 0) {
					gameOver();
				}
				paintScreen();
				gameRender();
				nowTime = System.currentTimeMillis();
				if(startMoveTime == 0 && touchDrop == true && swapDrop == true) {
					startMoveTime = System.currentTimeMillis();
				}
				// �h���b�v���쎞�Ԃ𒴂����痣��
				if((nowTime - startMoveTime) / 1000L >= canMoveSecond && touchDrop == true && startMoveTime != 0){
					touchDrop = false;
					startMoveTime = 0;
				}
				// 30FPS�𖞂����܂őҋ@
				while((afterTime - beforeTime) / 1000000.0 < 1000 / (double)FPS) {
					afterTime = System.nanoTime();
				}
				fpsNum = ((afterTime - beforeTime) / 1000000.0);
				fpsNum = 1000.0 / fpsNum;
			} catch(Exception e) {
				errorNum++;
			}
		}
	}

	// �g��Ȃ����ǕK�v�ȃ��\�b�h
	@Override
	public void mouseMoved(MouseEvent e) {}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	@Override
	public void mouseExited(MouseEvent e) {}
}

/*--- �i�o�g���j�R���{�̏����L�^����N���X ---*/
class ComboInfo{
	int dropColor;
	double coefficient;
	boolean special;
	ArrayList<Place> place = new ArrayList<>();

	@SuppressWarnings("unchecked")
	ComboInfo(int dropColor, double coefficient, boolean special, ArrayList<Place> place) {
		this.dropColor = dropColor;
		this.coefficient = coefficient;
		this.special = special;
		// �N���[����n��.�ʕ������ɂȂ�
		this.place = (ArrayList<Place>) place.clone();
	}
}

/*--- �i�o�g���j�񐔐����̂���TimerTask ---*/
class LimitTimerTask extends TimerTask {
	Timer timer;
	int procNum;
	int count = 0;
	int nowX, nowY;
	int preX, preY;
	int preCureDropAddWidth;
	int preCureDropAddHeight;
	int nowCureDropAddWidth;
	int nowCureDropAddHeight;
	double t = 0.18;
	double x, y;
	double cx, cy;
	double radius;
	double velocity;
	// �h���b�v�����p
	float eraseAlpha = 0.98f;
	// �h���b�v�����p
	int tempI, tempJ, tempFall;
	int setCount;

	// ��]�̂��߂̕������Z
	public LimitTimerTask(Timer timer, int procNum) {
		this.timer = timer;
		this.procNum = procNum;
		this.nowX = MainPanel.nowX;
		this.nowY = MainPanel.nowY;
		this.preX = MainPanel.preX;
		this.preY = MainPanel.preY;
		cx = (double)((nowX + preX) / 2.0) * MainPanel.GRID_WIDTH + MainPanel.BOARD_X;
		cy = (double)((nowY + preY) / 2.0) * MainPanel.GRID_HEIGHT + MainPanel.BOARD_Y;
		radius = MainPanel.GRID_WIDTH / 2.0;
		if(MainPanel.swapDrop == true) {
			MainPanel.swapNum.add(new Place(preX, preY));
		}
		if(MainPanel.dropMap[preX][preY] == 12) {
			nowCureDropAddWidth = MainPanel.GRID_WIDTH * 2 / 25;
			nowCureDropAddHeight = MainPanel.GRID_HEIGHT * 2 / 25;
		}
		if(MainPanel.dropMap[nowX][nowY] == 12) {
			preCureDropAddWidth = MainPanel.GRID_WIDTH * 2 / 25;
			preCureDropAddHeight = MainPanel.GRID_HEIGHT * 2 / 25;
		}
		// �h���b�v�����p
		if(MainPanel.eraseDrop == true) {
			tempI = MainPanel.tempI;
			tempJ = MainPanel.tempJ;
			tempFall = MainPanel.tempFall;
			setCount = MainPanel.setCount;
		}
	}

	@Override
	public void run() {
	}
}

/*--- �i�o�g���j���W�擾�N���X ---*/
class Place {
	int x;
	int y;

	public Place(int x, int y) {
		this.x = x;
		this.y = y;
	}
}

/*--- �i�o�g���j�p�[�e�B�Ґ��N���X ---*/
class Character {
	// �摜
	Image CARDS_017;
	Image CARDS_020;
	Image CARDFRAME2;
	URL url;
	int faceX, faceY;
	Image faceCard;
	// �X�e�[�^�X
	int mainColor, subColor;
	int hp;
	int atk;
	int rcu;
	int maxHp;
	int maxRcu;
	int nowHp;
	double coefficientHp = 1;
	double coefficientAtk = 1;
	double coefficientRcu = 1;

	Character() {
		url = this.getClass().getClassLoader().getResource("img/CARDFRAME2.PNG");
		CARDFRAME2 = new ImageIcon(url).getImage();
		url = this.getClass().getClassLoader().getResource("img/CARDS_017.PNG");
		CARDS_017 = new ImageIcon(url).getImage();
		url = this.getClass().getClassLoader().getResource("img/CARDS_020.PNG");
		CARDS_020 = new ImageIcon(url).getImage();
		for(int i = 0; i < 6; i++) {
			getCharacterStatus(i);
			maxHp += hp;
			maxRcu += rcu;
		}
		maxHp = (int)(maxHp * coefficientHp);
		nowHp = maxHp;
		maxRcu = (int)(maxRcu * coefficientRcu);
	}

	void getCharacterImage(int num) {
		// ���i�g���Ă�p�[�e�B
		switch (num) {
		case 0: // �I�V���X
			mainColor = 2;
			subColor = -1;
			faceX = 308;
			faceY = 614;
			faceCard = CARDS_017;
			break;
		case 1: // ���F���_���f�B
			mainColor = 2;
			subColor = 0;
			faceX = 104;
			faceY = 716;
			faceCard = CARDS_017;
			break;
		case 2: // ���F���_���f�B
			mainColor = 2;
			subColor = 0;
			faceX = 104;
			faceY = 716;
			faceCard = CARDS_017;
			break;
		case 3: // �f���K�h
			mainColor = 2;
			subColor = 0;
			faceX = 308;
			faceY = 512;
			faceCard = CARDS_017;
			break;
		case 4: // �V�[�t
			mainColor = 2;
			subColor = 4;
			faceX = 104;
			faceY = 2;
			faceCard = CARDS_020;
			break;
		case 5: // �I�V���X
			mainColor = 2;
			subColor = -1;
			faceX = 308;
			faceY = 614;
			faceCard = CARDS_017;
			break;
		}
	}
	void getCharacterStatus(int num) {
		switch (num) {
		case 0: // �I�V���X
			mainColor = 2;
			subColor = -1;
			hp = 2990;
			atk = 1417;
			rcu = 308;
			coefficientHp = 1.35;
			coefficientRcu = 1.35;
			break;
		case 1: // ���F���_���f�B
			mainColor = 2;
			subColor = 0;
			hp = 3155;
			atk = 1417;
			rcu = 452;
			break;
		case 2: // ���F���_���f�B
			mainColor = 2;
			subColor = 0;
			hp = 3155;
			atk = 1417;
			rcu = 452;
			break;
		case 3: // �f���K�h
			mainColor = 2;
			subColor = 0;
			hp = 2655;
			atk = 1418;
			rcu = 225;
			break;
		case 4: // �V�[�t
			mainColor = 2;
			subColor = 4;
			hp = 2620;
			atk = 1518;
			rcu = 483;
			break;
		case 5: // �I�V���X
			mainColor = 2;
			subColor = -1;
			hp = 2990;
			atk = 1417;
			rcu = 308;
			coefficientHp *= 1.35;
			coefficientRcu *= 1.35;
			break;
		}
	}
}

/*--- �i�o�g���j�_���W�����f�[�^ ---*/
class Dungeon {
	Image MONS_192;
	Image MONS_194;
	Image MONS_067;
	Image MONS_197;
	Image MONS_075;
	Image MONS_199;
	Image MONS_181;
	Image MONS_251;
	Image MONS_121;
	Image MONS_215;
	Image MONS_187;
	URL url;
	int enemyWidth, enemyY;
	Image enemyImg;
	int mainColor, subColor;
	int hp;
	int def;
	int atk;
	int rcu;
	int turn;
	int nowHp;
	int[] enemyData = new int[4];

	Dungeon() {
		url = this.getClass().getClassLoader().getResource("img/MONS_192.PNG");
		MONS_192 = new ImageIcon(url).getImage();
		url = this.getClass().getClassLoader().getResource("img/MONS_194.PNG");
		MONS_194 = new ImageIcon(url).getImage();
		url = this.getClass().getClassLoader().getResource("img/MONS_067.PNG");
		MONS_067 = new ImageIcon(url).getImage();
		url = this.getClass().getClassLoader().getResource("img/MONS_197.PNG");
		MONS_197 = new ImageIcon(url).getImage();
		url = this.getClass().getClassLoader().getResource("img/MONS_075.PNG");
		MONS_075 = new ImageIcon(url).getImage();
		url = this.getClass().getClassLoader().getResource("img/MONS_199.PNG");
		MONS_199 = new ImageIcon(url).getImage();
		url = this.getClass().getClassLoader().getResource("img/MONS_181.PNG");
		MONS_181 = new ImageIcon(url).getImage();
		url = this.getClass().getClassLoader().getResource("img/MONS_251.PNG");
		MONS_251 = new ImageIcon(url).getImage();
		url = this.getClass().getClassLoader().getResource("img/MONS_121.PNG");
		MONS_121 = new ImageIcon(url).getImage();
		url = this.getClass().getClassLoader().getResource("img/MONS_215.PNG");
		MONS_215 = new ImageIcon(url).getImage();
		url = this.getClass().getClassLoader().getResource("img/MONS_187.PNG");
		MONS_187 = new ImageIcon(url).getImage();
	}

	void getEnemyImage(int num) {
		switch (num) {
		case 0: // �n�[�s�B�f�r��
			mainColor = 3;
			subColor = -1;
			enemyImg = MONS_197;
			enemyWidth = 128;
			enemyY = 128;
			turn = 1;
			break;
		case 1: // �u���b�h�f�r��
			mainColor = 4;
			subColor = -1;
			enemyImg = MONS_199;
			enemyWidth = 128;
			enemyY = 128;
			turn = 1;
			break;
		case 2: // �z���C�g�i�C�g
			mainColor = 3;
			subColor = -1;
			enemyImg = MONS_075;
			enemyWidth = 157;
			enemyY = 40;
			turn = 2;
			break;
		case 3: // �p�C���f�[����
			mainColor = 0;
			subColor = -1;
			enemyImg = MONS_192;
			enemyWidth = 210;
			enemyY = 0;
			turn = 2;
			break;
		case 4: // �t���X�g�f�[����
			mainColor = 1;
			subColor = -1;
			enemyImg = MONS_194;
			enemyWidth = 210;
			enemyY = 0;
			turn = 2;
			break;
		case 5: // �L���O�S�[���h�h���S��
			mainColor = 3;
			subColor = -1;
			enemyImg = MONS_181;
			enemyWidth = 256;
			enemyY = 0;
			turn = 1;
			break;
		case 6: // �_�u�~�X���b�g
			mainColor = 4;
			subColor = -1;
			enemyImg = MONS_251;
			enemyWidth = 210;
			enemyY = 0;
			turn = 5;
			break;
		case 7: // �A�[�}�[�I�[�K
			mainColor = 2;
			subColor = -1;
			enemyImg = MONS_067;
			enemyWidth = 256;
			enemyY = 0;
			turn = 4;
			break;
		case 8: // �e�B�A�}�b�g
			mainColor = 4;
			subColor = -1;
			enemyImg = MONS_121;
			enemyWidth = 512;
			enemyY = 0;
			break;
		case 9: // �J�I�X�f�r���h���S��
			mainColor = 4;
			subColor = -1;
			enemyImg = MONS_215;
			enemyWidth = 512;
			enemyY = 0;
			break;
		case 10: // �[�E�X
			mainColor = 3;
			subColor = -1;
			enemyImg = MONS_187;
			enemyWidth = 512;
			enemyY = 0;
			break;
		case -1: // ����
			mainColor = -1;
			break;
		}
	}

	void getEnemyStatus(int num) {
		switch (num) {
		case 0: // �n�[�s�B�f�r��
			mainColor = 3;
			subColor = -1;
			hp = 17539;
			def = 1260;
			atk = 3213;
			turn = 1;
			break;
		case 1: // �u���b�h�f�r��
			mainColor = 4;
			subColor = -1;
			hp = 17673;
			def = 1260;
			atk = 3294;
			turn = 1;
			break;
		case 2: // �z���C�g�i�C�g
			mainColor = 3;
			subColor = -1;
			hp = 26100;
			def = 270;
			atk = 5280;
			turn = 2;
			break;
		case 3: // �p�C���f�[����
			mainColor = 0;
			subColor = -1;
			hp = 41639;
			def = 1980;
			atk = 4874;
			turn = 2;
			break;
		case 4: // �t���X�g�f�[����
			mainColor = 1;
			subColor = -1;
			hp = 42978;
			def = 1980;
			atk = 4900;
			turn = 2;
			break;
		case 5: // �L���O�S�[���h�h���S��
			mainColor = 3;
			subColor = -1;
			hp = 15;
			def = 60000;
			atk = 384;
			turn = 1;
			break;
		case 6: // �_�u�~�X���b�g
			mainColor = 4;
			subColor = -1;
			hp = 15;
			def = 100000;
			atk = 7777;
			turn = 5;
			break;
		case 7: // �A�[�}�[�I�[�K
			mainColor = 2;
			subColor = -1;
			hp = 63000;
			def = 0;
			atk = 13950;
			turn = 4;
			break;
		case 8: // �e�B�A�}�b�g
			mainColor = 4;
			subColor = -1;
			hp = 560895;
			def = 2380;
			atk = 14254;
			turn = 2;
			break;
		case 9: // �J�I�X�f�r���h���S��
			mainColor = 4;
			subColor = -1;
			hp = 1018700;
			def = 7000;
			atk = 24319;
			turn = 2;
			break;
		case 10: // �[�E�X
			mainColor = 3;
			subColor = -1;
			hp = 5305418;
			def = 7360;
			atk = 25487;
			turn = 1;
			break;
		case -1: // ����
			mainColor = -1;
			break;
		}
	}

	void setEnemyFloorData(int floor) {
		switch (floor) {
		// 1�K��2�̂̂�
		case 1:
			enemyData[0] =(int)(Math.random() * 8);
			enemyData[1] = (int)(Math.random() * 8);
			enemyData[2] = -1;
			enemyData[3] = -1;
			break;
		// 2~7�K�̓����_��
		case 2: case 3: case 4: case 5: case 6: case 7:
			switch ((int)(Math.random() * 3)) {
			case 0:
				// �傫���G�̂ݏo��
				enemyData[0] =(int)(Math.random() * 5 + 3);
				enemyData[1] = (int)(Math.random() * 5 + 3);
				enemyData[2] = -1;
				enemyData[3] = -1;
				break;
			case 1:
				enemyData[0] =(int)(Math.random() * 8);
				// �傫���G�̏o���𐧌�
				if(enemyData[0] >= 7) {
					enemyData[1] = (int)(Math.random() * 7);
					enemyData[2] = (int)(Math.random() * 7);
				} else {
					enemyData[1] = (int)(Math.random() * 8);
					if(enemyData[1] >= 7) {
						enemyData[2] = (int)(Math.random() * 7);
					} else {
						enemyData[2] = (int)(Math.random() * 8);
					}
				}
				enemyData[3] = -1;
				break;
			case 2:
				enemyData[0] =(int)(Math.random() * 8);
				// �傫���G�̏o���𐧌�
				if(enemyData[0] >= 7) {
					enemyData[1] = (int)(Math.random() * 7);
					enemyData[2] = (int)(Math.random() * 7);
					enemyData[3] = (int)(Math.random() * 7);
				} else {
					enemyData[1] = (int)(Math.random() * 8);
					if(enemyData[1] >= 7) {
						enemyData[2] = (int)(Math.random() * 7);
						enemyData[3] = (int)(Math.random() * 7);
					} else {
						enemyData[2] = (int)(Math.random() * 8);
						if(enemyData[2] >= 7) {
							enemyData[3] = (int)(Math.random() * 7);
						} else {
							enemyData[3] = (int)(Math.random() * 8);
						}
					}
				}
				break;
			}
			break;
		// 8~10�K�͌Œ�
		case 8:
			enemyData[0] = 8;
			enemyData[1] = -1;
			enemyData[2] = -1;
			enemyData[3] = -1;
			break;
		case 9:
			enemyData[0] = 9;
			enemyData[1] = -1;
			enemyData[2] = -1;
			enemyData[3] = -1;
			break;
		case 10:
			enemyData[0] = 10;
			enemyData[1] = -1;
			enemyData[2] = -1;
			enemyData[3] = -1;
			break;
		}
	}
}