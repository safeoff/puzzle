import * as img from "../img/block.png";
export class Drop {
	map: number[][]
 	img = document.createElement("img")

	constructor() {
		// 画像読み込み
 		this.img.src = img

		this.map = new Array()
		// ランダムなドロップを配置
		for (let i = 0; i < 6; i++) {
			this.map[i] = new Array()
			for (let j = 0; j < 5; j++) {
				this.map[i][j] = Math.floor(Math.random() * 6)
			}
		}
	}
}