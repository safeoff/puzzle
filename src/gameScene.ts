import { Board } from "./board"

export class GameScene {
	// 各画像と内部バッファ
	board: Board
	private buff = document.createElement("canvas")

	// canvasの幅と高さ
	width = window.innerWidth
	height = this.width * 1.5
	scale = this.width / 480

	// 要素の位置
	boardHeight

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

		// 要素の位置
		this.boardHeight = this.height - this.board.height * this.scale
	}

	// 盤面の位置にy座標を調整
	getBoardPoint(point) {
		let p = {x: point.x, y: point.y}
		// 盤面の位置にy座標を調整
		p.y = p.y - this.boardHeight

		// 座標をscale
		p.x /= this.scale
		p.y /= this.scale

		// 盤面内に制限
		const maxX = (this.board.drop.width - 0.01) * this.board.gridWidth
		const maxY = (this.board.drop.height - 0.01) * this.board.gridHeight
		if (p.x < 0) p.x = 0
		if (p.x > maxX) p.x = maxX
		if (p.y < 0) p.y = 0
		if (p.y > maxY) p.y = maxY

		return p
	}

	// ゲームの状態を更新する
	// touched: 触り中かどうか
	// point: 触っている座標
	update(touched, point): void {
		// 盤面の位置にy座標を調整
		let p = {x: point.x, y: point.y}
		if (touched) {
			p = this.getBoardPoint(p)
		}

		if (touched) this.board.update(p)
		this.board.draw(touched, p)
	}

	// 内部バッファに画像を描画する
	draw(): HTMLCanvasElement {
		const ctx = this.buff.getContext('2d')
		ctx.clearRect(0, 0, this.buff.width, this.buff.height)

		// 盤面を描画
		ctx.drawImage(this.board.buff, 0, this.boardHeight,
			this.board.width * this.scale, this.board.height * this.scale);

		// 戦闘画面を描画

		// debug: 左手をアニメーションさせてみる
		// const leftPos = this.images.updateLeft();
		// ctx.drawImage(this.images.left, leftPos[0], leftPos[1], leftPos[2], leftPos[3],
		// 	leftPos[4], leftPos[5], leftPos[6], leftPos[7]);

		return this.buff
	}
}