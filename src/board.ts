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

	// debug 左手のアニメーション時間
	leftTime = 59

	constructor() {
		// 画像読み込み
		// this.rough.src = rough
		// this.left.src = left

		this.canvas.width = this.width
		this.canvas.height = this.height

		this.gridWidth = this.width / 6
		this.gridHeight = this.height / 5
	}

	update() {
		this.drawBG()
		this.ctx.fillStyle = "black"
		this.ctx.fillRect(10,10,10,10)
	}

	// 背景を描画
	drawBG() {
		this.ctx.fillStyle = "darkgray"
		this.ctx.fillRect(0, 0, this.width, this.height)
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