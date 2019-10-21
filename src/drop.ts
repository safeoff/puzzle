export class Drop {
	map: number[][]

	constructor() {
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