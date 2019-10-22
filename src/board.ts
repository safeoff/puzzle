import { Drop } from "./drop"

export class Board {
	// 盤面
	buff= document.createElement("canvas")
	ctx = this.buff.getContext('2d')

	// 	left = document.createElement("img")

	// canvasの幅と高さ
	width = 480
	height = 400
	gridWidth
	gridHeight

	// ドロップ
	drop: Drop

	// debug 左手のアニメーション時間
	leftTime = 59

	constructor() {
		// 画像読み込み
		// this.left.src = left

		// ドロップ
		this.drop = new Drop()

		// サイズ
		this.buff.width = this.width
		this.buff.height = this.height
		this.gridWidth = this.width / this.drop.width
		this.gridHeight = this.height / this.drop.height

		// 持っているドロップ
		this.drop.move.width = this.gridWidth* 1.3
		this.drop.move.height= this.gridHeight * 1.3
		this.drop.mctx.globalAlpha = 0.8
	}

	// 座標設定
	setPoint(point) {
		this.drop.preP.x = this.drop.moveP.x
		this.drop.preP.y = this.drop.moveP.y
		this.drop.moveP.x = Math.floor(point.x / this.gridWidth)
		this.drop.moveP.y = Math.floor(point.y / this.gridHeight)
	}

	// 盤面の更新
	// point: 触っている座標
	update(point) {
		// 座標設定
		this.setPoint(point)

		const p = JSON.stringify(this.drop.preP)
		const m = JSON.stringify(this.drop.moveP)
		if (p != m) {
			this.drop.swap()
		}
	}

	// 盤面の描画
	// touched: 触り中かどうか
	// point: 触っている座標
	draw(touched, point) {
		this.drawBG()
		this.drawDrop(touched)
		if (touched) this.drawOperation(point)
	}

	// 操作ドロップを描画
	private drawOperation(point) {
		// 座標計算
		const dx = point.x - this.drop.move.width / 2
		const dy = point.y - this.drop.move.height / 2

		// 半透明のcanvasをbuffに重ねる
		this.drop.mctx.clearRect(0, 0, this.drop.move.width, this.drop.move.height)
		// this.drop.mctx.drawImage(this.drop.colorMap[this.drop.map[i][j]],
		this.drop.mctx.drawImage(this.drop.colorMap[this.drop.map[this.drop.moveP.x][this.drop.moveP.y]],
			0, 0, this.drop.move.width, this.drop.move.height)
		this.ctx.drawImage(this.drop.move, dx, dy, this.drop.move.width, this.drop.move.height)
	}

	// 盤面のドロップを描画
	private drawDrop(touched) {
		for (let i = 0; i < this.drop.width; i++) {
			for (let j = 0; j < this.drop.height; j++) {
				if (touched && i == this.drop.moveP.x && j == this.drop.moveP.y) continue
				this.ctx.drawImage(this.drop.colorMap[this.drop.map[i][j]],
				i * this.gridWidth, j * this.gridHeight, this.gridWidth, this.gridHeight)
			}
		}
	}

	// 背景を描画
	private drawBG() {
		// 市松模様
		for (let i = 0; i < this.drop.width; i++) {
			for (let j = 0; j < this.drop.height; j++) {
				this.ctx.fillStyle = this.calcCheckeredCode(i, j)
				this.ctx.fillRect(i * this.gridWidth, j * this.gridHeight, this.gridWidth, this.gridHeight)
			}
		}
	}

	// 市松模様のカラーコードを計算
	private calcCheckeredCode(i:number, j:number): string {
		if((i % 2 == 1 && j % 2 == 0) ||
		(i % 2 == 0 && j % 2 == 1)) {
			return "#492300"
		}
		return "#381400"
	}

	// 左手の状態を更新して位置を返す
	updateLeft(): number[] {
		// 左手のアニメーション時間を更新
		this.leftTime += (this.leftTime > 0) ? -1 : 59

		// ソースのx,y,w,h,canvasのx,y,w,h
		// debug: 30frameで切り替わるようにしてみた
		let a = new Array()
		// a.push(Math.floor(this.leftTime / 30) * 53)
		// a.push(0)
		// a.push(53)
		// a.push(53)
		// a.push(111 * this.scale)
		// a.push(453 * this.scale)
		// a.push(53 * this.scale)
		// a.push(53 * this.scale)
		return a
	}
}