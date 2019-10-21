import { Drop } from "./drop"

export class Board {
	// 盤面
	canvas = document.createElement("canvas")
	ctx = this.canvas.getContext('2d')
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
		this.canvas.width = this.width
		this.canvas.height = this.height
		this.gridWidth = this.width / this.drop.width
		this.gridHeight = this.height / this.drop.height
	}

	update() {
		this.drawBG()
		this.drawDrop()
	}

	// 盤面のドロップを描画
	private drawDrop() {
		for (let i = 0; i < this.drop.width; i++) {
			for (let j = 0; j < this.drop.height; j++) {
				this.ctx.drawImage(this.drop.colorMap[this.drop.map[i][j]], i * this.gridWidth, j * this.gridHeight, this.gridWidth, this.gridHeight)
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