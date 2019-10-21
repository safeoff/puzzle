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

/*--- （全般）メインフレーム ---*/
public class Puzzle extends JFrame {
	public static void main(String[] args) {
		Puzzle frame = new Puzzle();
		frame.setTitle("パズドラ");
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setResizable(false);
		// フレームにパネルを設置してサイズ調整
		MainPanel panel = new MainPanel();
		frame.getContentPane().add(panel);
		frame.pack();
	}
}

/*--- （バトル）メインパネル ---*/
class MainPanel extends JPanel
implements Runnable, MouseMotionListener, MouseListener {
	// アプリの位置
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
	// メインループ
	Thread thread;
	final int FPS = 30;
	// 盤面
	static int preX, preY;
	static int nowX, nowY;
	static int[][] dropMap = new int[6][5];
	// ドロップ操作
	static ArrayList<Place> swapNum = new ArrayList<>();
	static boolean swapDrop = false;
	boolean touchDrop = false;
	double canMoveSecond = 4.0 * 2;
	long startMoveTime;
	long nowTime;
	// コンボ判定
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
	// 画像
	Image dropImg;
	Image UIPAT1;
	Graphics2D g2;
	URL url;
	Graphics2D dbg2;
	Image dbImage;
	float alpha;
	// キャラクター（バランス染めオシリスパ）
	Character character;
	// ダンジョン（神々の王）
	Dungeon dungeon;
	int enemyConcatWidth;
	int enemyMaxHeight;
	// バトル進行
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
	// デバッグ
	double fpsNum;
	long beforeTime = System.nanoTime();
	long afterTime = System.nanoTime();
	int errorNum;

	public MainPanel() {
		// 基本設定
		setPreferredSize(new Dimension(APP_WIDTH, APP_HEIGHT));
		setSize(APP_WIDTH, APP_HEIGHT);
		setBackground(Color.black);
		addMouseMotionListener(this);
		addMouseListener(this);
		// 画像取得
		url = this.getClass().getClassLoader().getResource("img/BLOCK2.PNG");
		dropImg = new ImageIcon(url).getImage();
		dropImg = dropImg.getScaledInstance(
				dropImg.getWidth(this) * (GRID_WIDTH - BOARD_X * 5 / 6) / 100, -1, Image.SCALE_SMOOTH);
		url = this.getClass().getClassLoader().getResource("img/UIPAT1.PNG");
		UIPAT1 = new ImageIcon(url).getImage();
		// キャラクターとダンジョン読み込み
		character = new Character();
		dungeon = new Dungeon();
		// スレッド起動
		thread = new Thread(this);
		thread.start();
	}

	// マウスドラッグ.盤面を操作.swapならswap
	@Override
	public void mouseDragged(MouseEvent e) {
		if(touchDrop == true && eraseDrop == false && character.nowHp > 0) {
			x = e.getX();
			y = e.getY();
			nowX = (x - BOARD_X) / GRID_WIDTH;
			nowY = (y - BOARD_Y) / GRID_HEIGHT;
			// カーソルを画面内に留める
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

	// マウスプレス.盤面ならドロップ操作
	@Override
	public void mousePressed(MouseEvent e) {
		// 盤面ならドロップ操作
		if(e.getX() > BOARD_X && e.getX() < BOARD_X + GRID_WIDTH * 6
				&& e.getY() > BOARD_Y && e.getY() < BOARD_Y + GRID_HEIGHT * 5 && eraseDrop == false && character.nowHp > 0) {
			// xとyに座標を登録
			x = e.getX();
			y = e.getY();
			// 盤面の位置を座標から計算
			nowX = (x - BOARD_X) / GRID_WIDTH;
			nowY = (y - BOARD_Y) / GRID_HEIGHT;
			preX = nowX;
			preY = nowY;
			touchDrop = true;
			alpha = 1f;
		}
	}

	// マウスリリース.コンボを判定
	@Override
	public void mouseReleased(MouseEvent e) {
		// swapマルチスレッドのごみが残っていたら破棄
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

	// コンボ判定
	void checkErase() {
		int eraseNum = 0;
		int concatNum = 1;
		int temp = 0;
		boolean pass = false;
		int dropColor = 0;
		double coefficient = 0;
		boolean special = false;
		eraseDrop = true;

		// 消去可能な座標を初期化
		for(int j = 6; j >= 0; j--) {
			for(int i = 0; i < 8; i++) {
				eraseMap[i][j] = 0;
			}
		}

		// 横方向の3つ揃い座標を記録
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
		// 縦方向の3つ揃い座標を記録
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

		// 消去可能なものが無いなら抜ける
		if(eraseNum == 0) {
			// 自分の攻撃.ターンとスキルターンの経過
			if(comboInfo.size() > 0) {
				playerAttack();
				nowSkillTurn++;
				// 初回時のアニメーションスキップを終了
			} else if(nowSkillTurn == 0) {
				nowSkillTurn++;
			}
			// 敵の攻撃
			if(preTurn == nowTurn) {
				nowTurn++;
				enemyAttack();
			}
			// 前回のコンボ情報をここで破棄
			comboInfo.clear();
			eraseCount = 0;
			head = 0;
			comboStringColorNum = 0;
			eraseDrop = false;
			return;
		}

		// 3つ揃い領域の結合
		// 左上から縦順で右へ向かって判定。消去可能な座標の下と右が消去可能か調べ、
		// 同色なら相手を自分の数値に変える。その際相手の数値を記録しておき、
		// 相手の数値を盤面から探し、自分の数値に変える。
		for(int k = 0; k < eraseNum; k++) {
			for(int i = 1; i < 7; i++) {
				for(int j = 1; j < 6; j++) {
					if(eraseMap[i][j] == concatNum) {
						// 下
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
						// 右
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

		// 数値を詰める
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

		// コンボ情報を登録
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
				// 倍率と列or四つ消しの判定はまだ
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

	// ドロップを消去するアニメ
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
					// 回復はドロップがちょっと小さめ
					if(dropMap[comboInfo.get(eraseCount).place.get(0).x][comboInfo.get(eraseCount).place.get(0).y] == 12) {
						cureDropAddWidth = GRID_WIDTH * 2 / 25;
						cureDropAddHeight = GRID_HEIGHT * 2 / 25;
					}
					// ドロップを描く
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
				// コンボを描く
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
				// 回復を加算
				if(dropMap[comboInfo.get(eraseCount).place.get(0).x][comboInfo.get(eraseCount).place.get(0).y] == 12 && count == 0) {
					nowRcu += character.maxRcu;
				}
				// 回復を描く
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

				// ダメージを描く.主属性
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
					// 副属性
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

				// 終了条件
					if(++count == procNum) {
						this.cancel();
						timer.cancel();
						// 再帰探索
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

	// 実際にドロップを消去する
	void eraseDrop() {
		boolean pass = false;
		int temp;
		// ドロップを消去
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

		// ドロップを下へ詰める
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
											// 回復はドロップがちょっと小さめ
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

											// 終了条件
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

		// ドロップを降らせる
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
								// 回復はドロップがちょっと小さめ
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
								// 終了条件
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
		// 再帰探索
		checkErase();
	}

	void playerAttack() {
		int damage;
		// 6コンボ以上で12.25倍
		if(comboInfo.size() >= 6) {
			for(int i = 0; i < 6; i++) {
				characterAttackNum[i] *= 12.25;
				characterAttackSubNum[i] *= 12.25;
			}
		}
		// コンボ数で倍率が発生
		for(int i = 0; i < 6; i++) {
			characterAttackNum[i] *= (1 + (comboInfo.size() - 1) * 0.25);
			characterAttackSubNum[i] *= (1 + (comboInfo.size() - 1) * 0.25);
		}
		nowRcu *= (1 + (comboInfo.size() - 1) * 0.25);
		// iは敵
		for(int i = 0; i < 4; i++) {
			// 主属性攻撃.敵がまだ存命なら実行
			if(enemyNowHp[i] > 0) {
				// jはパーティ
				for(int j = 0; j < 6; j++) {
					damage = characterAttackNum[j] - dungeon.def;
					if(damage > 0) {
						enemyNowHp[i] -= damage;
					} else {
						enemyNowHp[i]--;
					}
					characterAttackNum[i] = 0;
					// 倒れたら消去
					if(enemyNowHp[i] <= 0) {
						dungeon.enemyData[i] = -1;
					}
				}
			}
			// 副属性攻撃.敵がまだ存命なら実行
			if(enemyNowHp[i] > 0) {
				// jはパーティ
				for(int j = 0; j < 6; j++) {
					damage = characterAttackSubNum[j] - dungeon.def;
					if(damage > 0) {
						enemyNowHp[i] -= damage;
					} else {
						enemyNowHp[i]--;
					}
					characterAttackSubNum[i] = 0;
					// 倒れたら消去
					if(enemyNowHp[i] <= 0) {
						dungeon.enemyData[i] = -1;
					}
				}
			}
		}
		// 回復
		character.nowHp += nowRcu;
		if(character.nowHp > character.maxHp) {
			character.nowHp = character.maxHp;
		}

		// 全員倒したら
		if(enemyNowHp[0] <= 0 &&enemyNowHp[1] <= 0 &&enemyNowHp[2] <= 0 &&enemyNowHp[3] <= 0) {
			enemyAlive = false;
			enemyConcatWidth = 0;
			enemyMaxHeight = 0;
		}

		// 攻撃力を初期化
		for(int i = 0; i < 6; i++) {
			characterAttackNum[i] = 0;
			characterAttackSubNum[i] = 0;
			nowRcu = 0;
		}
	}

	// コンボ情報をバッファリング
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

	// 隣り合うドロップを回転.回転する間だけutilのタイマーでマルチスレッドを起動
	void swapDrop() {
		int temp;
		// くるくる回す.回数制限のあるTimerTask（自作）を起動
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new LimitTimerTask(timer, 5) {
			@Override
			public void run() {
				synchronized (this) {
				// ドロップの動く方向によって回転方向を指定
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
				// 先にあったドロップ
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

				// 動かしているドロップ
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
				// 終了条件
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

		// 盤面の内部情報をswap
		temp = dropMap[preX][preY];
		dropMap[preX][preY] = dropMap[nowX][nowY];
		dropMap[nowX][nowY] = temp;
		preX = nowX;
		preY = nowY;
	}

	// 持っているドロップをバッファリング
	void moveDrop() {
		dbg2.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 0.8f));
		cureDropAddWidth = 0;
		cureDropAddHeight = 0;
		// 回復はドロップがちょっと小さめ
		if(dropMap[nowX][nowY] == 12) {
			cureDropAddWidth = GRID_WIDTH * 2 / 25;
			cureDropAddHeight = GRID_HEIGHT * 2 / 25;
		}
		dbg2.drawImage(dropImg, x - MOVE_DROP_WIDTH + cureDropAddWidth, y - MOVE_DROP_HEIGHT + cureDropAddHeight,
		x + MOVE_DROP_WIDTH - cureDropAddWidth, y + MOVE_DROP_HEIGHT - cureDropAddHeight,
		(dropMap[nowX][nowY] % 4) * GRID_WIDTH, (dropMap[nowX][nowY] / 4) * GRID_WIDTH,
		(dropMap[nowX][nowY] % 4) * GRID_WIDTH + GRID_WIDTH - cureDropAddWidth * 2,
		(dropMap[nowX][nowY] / 4) * GRID_WIDTH + GRID_WIDTH - cureDropAddWidth * 2, this);

		// 操作時間残り3秒から警告を表示
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
			// 可変バー
			dbg2.setColor(Color.getHSBColor(0 + (float)(bd1.doubleValue() / 3 % 0.13), 0.8f, 0.8f));
			dbg2.fillRect(x - GRID_WIDTH * 10 / 23, y - GRID_HEIGHT * 150 / 151,
					(GRID_WIDTH * 6 / 21) * (int)(bd1.doubleValue() * 10) / 10, GRID_HEIGHT / 9);
		}
	}

	// 盤面のドロップをバッファリング
	void paintDrop() {
		boolean pass = false;
		for(int i = 0; i < 6; i++) {
			for(int j = 0; j < 5; j++) {
				// 選んだドロップを透明に
				if(nowX == i && nowY == j && touchDrop == true) {
					dbg2.setComposite(AlphaComposite.getInstance(
							AlphaComposite.SRC_OVER, alpha));
				} else {
					dbg2.setComposite(AlphaComposite.getInstance(
							AlphaComposite.SRC_OVER, 1f));
				}
				cureDropAddWidth = 0;
				cureDropAddHeight = 0;
				// 回復はドロップがちょっと小さめ
				if(dropMap[i][j] == 12) {
					cureDropAddWidth = GRID_WIDTH * 2 / 25;
					cureDropAddHeight = GRID_HEIGHT * 2 / 25;
				}
				// swapしているドロップは表示しない
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
				// 消去アニメ中かゲームオーバーなら表示しない
				if(eraseMap[i + 1][j + 1] <= eraseCount - head + 1 && eraseMap[i + 1][j + 1] != 0 || character.nowHp <= 0) {
					pass = true;
				}
				// 表示していいならドロップを表示
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

	// 背景をバッファリング
	void paintBg() {
		// 背景を黒塗り
		dbg2.setColor(Color.darkGray);
		dbg2.fillRect(0, 0, APP_WIDTH, APP_HEIGHT);
		// 盤面
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
		// 回復ゲージ
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
		// 回復バー.HPの割合で長さを可変
		dbg2.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 1f));
		BigDecimal bd = new BigDecimal((double)character.nowHp / (double)character.maxHp);
		BigDecimal bd1 = bd.setScale(3, BigDecimal.ROUND_UP);
		dbg2.drawImage(UIPAT1, GRID_WIDTH * 2 / 5 + BOARD_X, BOARD_Y - GRID_HEIGHT / 4,
				GRID_WIDTH * 2 / 5 + BOARD_X + (int)((APP_WIDTH - (double)(GRID_WIDTH * 16 / 27 + BOARD_X)) * bd1.doubleValue()), BOARD_Y - GRID_HEIGHT / 8,
				268, 66, 283, 77, this);
		// HP数値
		temp = character.nowHp + "/" + character.maxHp;
		dbg2.setFont(new Font("SansSerif", Font.BOLD, APP_WIDTH / 23));
		// 色を可変
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

	// キャラクターをバッファリング
	void paintCharacter() {
		for(int i = 0; i < 6; i++) {
			character.getCharacterImage(i);
			// 顔
			dbg2.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 1f));
			dbg2.drawImage(character.faceCard, i * GRID_WIDTH + BOARD_X, BOARD_Y - GRID_HEIGHT / 3 - GRID_HEIGHT * 50 / 51,
					i * GRID_WIDTH + GRID_WIDTH * 50 / 51 + BOARD_X, BOARD_Y - GRID_HEIGHT / 3,
					character.faceX, character.faceY, character.faceX + 95, character.faceY + 95, this);
			// 主属性と副属性
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
		// 設定
		if(enemyAlive == false) {
			nowFloor++;
			enemyAlive = true;
			setEnemy();
		}
		// 描画
		int enemyPosiX = 0;
		for(int i = 0; i < dungeon.enemyData.length; i++) {
			dungeon.getEnemyImage(dungeon.enemyData[i]);
			if(dungeon.mainColor != -1) {
				// 敵画像
				dbg2.drawImage(dungeon.enemyImg, (APP_WIDTH - enemyConcatWidth) / 2 + enemyPosiX,
						(int)(dungeon.enemyY * GRID_HEIGHT / 140.0) + GRID_HEIGHT / 3,
						(int)((dungeon.enemyWidth * GRID_WIDTH / 140.0) + (APP_WIDTH - enemyConcatWidth) / 2) + enemyPosiX,
						(int)(dungeon.enemyImg.getHeight(this) * GRID_HEIGHT / 140.0) + (int)(dungeon.enemyY * GRID_HEIGHT / 140.0) + GRID_HEIGHT * 2 / 3,
						0, 0, dungeon.enemyWidth, dungeon.enemyImg.getHeight(this), this);
				// HPゲージ
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
				// HPバー.可変
				dbg2.fillRect((APP_WIDTH - enemyConcatWidth) / 2 + enemyPosiX + GRID_WIDTH / 6,
						 APP_HEIGHT * 10 / 38 + GRID_HEIGHT / 16,
						(int)(dungeon.enemyWidth * GRID_WIDTH / 140.0) - GRID_WIDTH  * 10/ 18,
						GRID_HEIGHT / 15);
				// ターン数
				enemyTurn = dungeon.turn - nowTurn % dungeon.turn;
				temp = "あと" + enemyTurn;
				dbg2.setFont(new Font("SansSerif", Font.BOLD, APP_WIDTH / 22));
				// なんとなく色を可変
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

	// デバッグ情報をバッファリング
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

	// バッファリング
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

	// バッファを画面に描画
	void paintScreen() {
		g2 = (Graphics2D) getGraphics();
		g2.drawImage(dbImage, 0, 0, null);
		Toolkit.getDefaultToolkit().sync();
		g2.dispose();
		dbg2.dispose();
	}

	// ランダムなドロップを盤面に配置
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

	// バッファ領域を作る
	void setBuffer() {
		dbImage = createImage(APP_WIDTH, APP_HEIGHT);
	}

	// メインループ.FPSの間隔でアクティブレンダリング
	@Override
	public void run() {
		// timer1とtimer2で初期設定
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
				// ドロップ移動中なら描画
				if(touchDrop == true) {
					moveDrop();
					if(alpha > 0.4f) {
						alpha -= 0.06f;
					}
				}
				// コンボなら描画
				if(comboInfo.size() > 1) {
					paintcombo();
				}
				// ゲームオーバーなら描画
				if(character.nowHp <= 0) {
					gameOver();
				}
				paintScreen();
				gameRender();
				nowTime = System.currentTimeMillis();
				if(startMoveTime == 0 && touchDrop == true && swapDrop == true) {
					startMoveTime = System.currentTimeMillis();
				}
				// ドロップ操作時間を超えたら離す
				if((nowTime - startMoveTime) / 1000L >= canMoveSecond && touchDrop == true && startMoveTime != 0){
					touchDrop = false;
					startMoveTime = 0;
				}
				// 30FPSを満たすまで待機
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

	// 使わないけど必要なメソッド
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

/*--- （バトル）コンボの情報を記録するクラス ---*/
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
		// クローンを渡す.別物扱いになる
		this.place = (ArrayList<Place>) place.clone();
	}
}

/*--- （バトル）回数制限のあるTimerTask ---*/
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
	// ドロップ消去用
	float eraseAlpha = 0.98f;
	// ドロップ落下用
	int tempI, tempJ, tempFall;
	int setCount;

	// 回転のための物理演算
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
		// ドロップ落下用
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

/*--- （バトル）座標取得クラス ---*/
class Place {
	int x;
	int y;

	public Place(int x, int y) {
		this.x = x;
		this.y = y;
	}
}

/*--- （バトル）パーティ編成クラス ---*/
class Character {
	// 画像
	Image CARDS_017;
	Image CARDS_020;
	Image CARDFRAME2;
	URL url;
	int faceX, faceY;
	Image faceCard;
	// ステータス
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
		// 普段使ってるパーティ
		switch (num) {
		case 0: // オシリス
			mainColor = 2;
			subColor = -1;
			faceX = 308;
			faceY = 614;
			faceCard = CARDS_017;
			break;
		case 1: // ヴェルダンディ
			mainColor = 2;
			subColor = 0;
			faceX = 104;
			faceY = 716;
			faceCard = CARDS_017;
			break;
		case 2: // ヴェルダンディ
			mainColor = 2;
			subColor = 0;
			faceX = 104;
			faceY = 716;
			faceCard = CARDS_017;
			break;
		case 3: // デルガド
			mainColor = 2;
			subColor = 0;
			faceX = 308;
			faceY = 512;
			faceCard = CARDS_017;
			break;
		case 4: // シーフ
			mainColor = 2;
			subColor = 4;
			faceX = 104;
			faceY = 2;
			faceCard = CARDS_020;
			break;
		case 5: // オシリス
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
		case 0: // オシリス
			mainColor = 2;
			subColor = -1;
			hp = 2990;
			atk = 1417;
			rcu = 308;
			coefficientHp = 1.35;
			coefficientRcu = 1.35;
			break;
		case 1: // ヴェルダンディ
			mainColor = 2;
			subColor = 0;
			hp = 3155;
			atk = 1417;
			rcu = 452;
			break;
		case 2: // ヴェルダンディ
			mainColor = 2;
			subColor = 0;
			hp = 3155;
			atk = 1417;
			rcu = 452;
			break;
		case 3: // デルガド
			mainColor = 2;
			subColor = 0;
			hp = 2655;
			atk = 1418;
			rcu = 225;
			break;
		case 4: // シーフ
			mainColor = 2;
			subColor = 4;
			hp = 2620;
			atk = 1518;
			rcu = 483;
			break;
		case 5: // オシリス
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

/*--- （バトル）ダンジョンデータ ---*/
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
		case 0: // ハーピィデビル
			mainColor = 3;
			subColor = -1;
			enemyImg = MONS_197;
			enemyWidth = 128;
			enemyY = 128;
			turn = 1;
			break;
		case 1: // ブラッドデビル
			mainColor = 4;
			subColor = -1;
			enemyImg = MONS_199;
			enemyWidth = 128;
			enemyY = 128;
			turn = 1;
			break;
		case 2: // ホワイトナイト
			mainColor = 3;
			subColor = -1;
			enemyImg = MONS_075;
			enemyWidth = 157;
			enemyY = 40;
			turn = 2;
			break;
		case 3: // パイルデーモン
			mainColor = 0;
			subColor = -1;
			enemyImg = MONS_192;
			enemyWidth = 210;
			enemyY = 0;
			turn = 2;
			break;
		case 4: // フロストデーモン
			mainColor = 1;
			subColor = -1;
			enemyImg = MONS_194;
			enemyWidth = 210;
			enemyY = 0;
			turn = 2;
			break;
		case 5: // キングゴールドドラゴン
			mainColor = 3;
			subColor = -1;
			enemyImg = MONS_181;
			enemyWidth = 256;
			enemyY = 0;
			turn = 1;
			break;
		case 6: // ダブミスリット
			mainColor = 4;
			subColor = -1;
			enemyImg = MONS_251;
			enemyWidth = 210;
			enemyY = 0;
			turn = 5;
			break;
		case 7: // アーマーオーガ
			mainColor = 2;
			subColor = -1;
			enemyImg = MONS_067;
			enemyWidth = 256;
			enemyY = 0;
			turn = 4;
			break;
		case 8: // ティアマット
			mainColor = 4;
			subColor = -1;
			enemyImg = MONS_121;
			enemyWidth = 512;
			enemyY = 0;
			break;
		case 9: // カオスデビルドラゴン
			mainColor = 4;
			subColor = -1;
			enemyImg = MONS_215;
			enemyWidth = 512;
			enemyY = 0;
			break;
		case 10: // ゼウス
			mainColor = 3;
			subColor = -1;
			enemyImg = MONS_187;
			enemyWidth = 512;
			enemyY = 0;
			break;
		case -1: // 無し
			mainColor = -1;
			break;
		}
	}

	void getEnemyStatus(int num) {
		switch (num) {
		case 0: // ハーピィデビル
			mainColor = 3;
			subColor = -1;
			hp = 17539;
			def = 1260;
			atk = 3213;
			turn = 1;
			break;
		case 1: // ブラッドデビル
			mainColor = 4;
			subColor = -1;
			hp = 17673;
			def = 1260;
			atk = 3294;
			turn = 1;
			break;
		case 2: // ホワイトナイト
			mainColor = 3;
			subColor = -1;
			hp = 26100;
			def = 270;
			atk = 5280;
			turn = 2;
			break;
		case 3: // パイルデーモン
			mainColor = 0;
			subColor = -1;
			hp = 41639;
			def = 1980;
			atk = 4874;
			turn = 2;
			break;
		case 4: // フロストデーモン
			mainColor = 1;
			subColor = -1;
			hp = 42978;
			def = 1980;
			atk = 4900;
			turn = 2;
			break;
		case 5: // キングゴールドドラゴン
			mainColor = 3;
			subColor = -1;
			hp = 15;
			def = 60000;
			atk = 384;
			turn = 1;
			break;
		case 6: // ダブミスリット
			mainColor = 4;
			subColor = -1;
			hp = 15;
			def = 100000;
			atk = 7777;
			turn = 5;
			break;
		case 7: // アーマーオーガ
			mainColor = 2;
			subColor = -1;
			hp = 63000;
			def = 0;
			atk = 13950;
			turn = 4;
			break;
		case 8: // ティアマット
			mainColor = 4;
			subColor = -1;
			hp = 560895;
			def = 2380;
			atk = 14254;
			turn = 2;
			break;
		case 9: // カオスデビルドラゴン
			mainColor = 4;
			subColor = -1;
			hp = 1018700;
			def = 7000;
			atk = 24319;
			turn = 2;
			break;
		case 10: // ゼウス
			mainColor = 3;
			subColor = -1;
			hp = 5305418;
			def = 7360;
			atk = 25487;
			turn = 1;
			break;
		case -1: // 無し
			mainColor = -1;
			break;
		}
	}

	void setEnemyFloorData(int floor) {
		switch (floor) {
		// 1階は2体のみ
		case 1:
			enemyData[0] =(int)(Math.random() * 8);
			enemyData[1] = (int)(Math.random() * 8);
			enemyData[2] = -1;
			enemyData[3] = -1;
			break;
		// 2~7階はランダム
		case 2: case 3: case 4: case 5: case 6: case 7:
			switch ((int)(Math.random() * 3)) {
			case 0:
				// 大きい敵のみ出現
				enemyData[0] =(int)(Math.random() * 5 + 3);
				enemyData[1] = (int)(Math.random() * 5 + 3);
				enemyData[2] = -1;
				enemyData[3] = -1;
				break;
			case 1:
				enemyData[0] =(int)(Math.random() * 8);
				// 大きい敵の出現を制限
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
				// 大きい敵の出現を制限
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
		// 8~10階は固定
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