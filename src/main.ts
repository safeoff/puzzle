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
		// 関数ではなくe => {}の書式で書く理由は、thisの参照がcanvasに変わっちゃうから
		let events = ["touchstart","mousedown"]
		for (const event of events) {
			this.canvas.addEventListener(event, e => {
				this.touched = true
			})
		}

		events = ["touchmove","mousemove"]
		for (const event of events) {
			this.canvas.addEventListener(event, e => {
				this.touched = true
				const p = this.getPoint(e)
			})
		}

		events = ["touchend","mouseup"]
		for (const event of events) {
			this.canvas.addEventListener(event, e => {
				this.touched = false
			})
		}
	}

	// 座標を取得
	getPoint(e) {
		// タッチイベントとマウスイベントの差を吸収
		let te = e.type.match(/mouse/) ? e : e.changedTouches[0]
		return {x: 2, y: 2}
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