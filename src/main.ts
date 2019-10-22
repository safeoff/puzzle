import { GameScene } from "./gameScene"

class Main {
	private readonly canvas: HTMLCanvasElement
	private readonly ctx: CanvasRenderingContext2D
	private GameScene: GameScene

	// 触り中かどうか
	private touched = false

	// タイマーイベント開始
	constructor() {
		// canvasを取得・設定
		this.canvas = <HTMLCanvasElement>document.getElementById('canvas')
		this.canvas.width = window.innerWidth
		this.canvas.height = window.innerHeight
		this.ctx = this.canvas.getContext('2d')

		// ゲーム画面を初期化
		this.GameScene = new GameScene()
		window.requestAnimationFrame(() => this.draw())

		// リスナーを入れる
		this.canvas.addEventListener("touchstart", this.down)
		this.canvas.addEventListener("mousedown", this.down)
		this.canvas.addEventListener("touchmove", this.move)
		this.canvas.addEventListener("mousemove", this.move)
		this.canvas.addEventListener("touchend", this.up)
		this.canvas.addEventListener("mouseup", this.up)
	}

	// タップイベント達
	down(e) {
		this.touched = true
	}

	move(e) {
		if (!this.touched) return
	}

	up(e) {
		this.touched = false
	}

	// 更新と描画
	draw() {
		// TODO: TitleかGameかResultか、みたいな分岐を入れるといいのかも

		// 状態を更新して、内部バッファをcanvasに反映させる。
		this.GameScene.update()
		this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height)
		this.ctx.drawImage(this.GameScene.draw(), 0, 0)

		// ループ
		window.requestAnimationFrame(() => this.draw())
	}
}

window.onload = function() {
	new Main()
}