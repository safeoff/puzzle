//import { Images } from "./images";
import { Board } from "./board"

export class GameScene {
	// 各画像と内部バッファ
	private board: Board
	private buff = document.createElement("canvas")

	// canvasの幅と高さ
	width = window.innerWidth
	height = this.width * 1.5
	scale = this.width / 480

	constructor() {
		// 画像読み込み
		this.board= new Board()

		// 横長ディスプレイの場合はサイズを変える
		if (window.innerWidth > window.innerHeight) {
			this.height = window.innerHeight
			this.width = this.height / 1.5
			this.scale = this.width / 480
		}

		// 内部バッファの幅と高さ
		this.buff.width = this.width
		this.buff.height= this.height
	}

	// ゲームの状態を更新する
	update(): void {
		this.board.update()
	}

	// 内部バッファに画像を描画する
	draw(): HTMLCanvasElement {
		const ctx = this.buff.getContext('2d')
		ctx.clearRect(0, 0, this.buff.width, this.buff.height)

		// 盤面を描画
		ctx.drawImage(this.board.canvas, 0, 0, this.board.width * this.scale, this.board.height * this.scale);
		// ctx.drawImage(this.board.canvas, 0, 0)
		// ctx.drawImage(this.images.rough, 0, 0, this.images.width, this.images.height);

		// 戦闘画面を描画

		// debug: 左手をアニメーションさせてみる
		// const leftPos = this.images.updateLeft();
		// ctx.drawImage(this.images.left, leftPos[0], leftPos[1], leftPos[2], leftPos[3],
		// 	leftPos[4], leftPos[5], leftPos[6], leftPos[7]);

		return this.buff
	}
}