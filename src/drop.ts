import * as img from "../img/block.png";
export class Drop {
	// 盤面
	map: number[][]
	width = 6
	height = 5

	// 持っているドロップ
	moveP = {x: 0, y: 0}
	move = document.createElement("canvas")
	mctx = this.move.getContext('2d')

	// 画像
	img   = document.createElement("img")
	fire  = document.createElement("canvas")
	water = document.createElement("canvas")
	tree  = document.createElement("canvas")
	shine = document.createElement("canvas")
	dark  = document.createElement("canvas")
	cure  = document.createElement("canvas")
	colorNum = 6

	// 管理番号と画像の対応
	colorMap = {0:this.fire,
				1:this.water,
				2:this.tree,
				3:this.shine,
				4:this.dark,
				5:this.cure}

	constructor() {
		// 画像読み込み
		this.setIMG()

		// ランダムなドロップを配置
		this.setDrop()
	}

	// 画像読み込み
	private setIMG() {
		this.img.src = img
		const drops = [this.fire, this.water, this.tree, this.shine, this.dark, this.cure]
		for (let i = 0; i < drops.length; i++) {
			let ctx = drops[i].getContext('2d')
			drops[i].width = 100
			drops[i].height = 100
			// 画像ソースのx,y,w,h,canvasのx,y,w,h
			ctx.drawImage(this.img, i*100 + 10, 0, 100, 100, 0, 0, 100, 100)
		}
	}

	// ランダムなドロップを配置
	private setDrop() {
		this.map = new Array()
		for (let i = 0; i < this.width; i++) {
			this.map[i] = new Array()
			for (let j = 0; j < this.height; j++) {
				this.map[i][j] = Math.floor(Math.random() * this.colorNum)
			}
		}
	}
}