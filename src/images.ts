// import * as rough from "../img/rough.png";
// import * as left from "../img/left.png";

export class Images {
	// 背景画像
// 	rough = document.createElement("img");
// 	left = document.createElement("img");

	// canvasの幅と高さと拡大率
	width = window.innerWidth;
	height = this.width * 1.5;
	scale = this.width / 480;

	// debug 左手のアニメーション時間
	leftTime = 59;

	constructor() {
		// 画像読み込み
// 		this.rough.src = rough;
// 		this.left.src = left;
		// canvasの幅と高さと拡大率
		if (window.innerWidth > window.innerHeight) {
			this.height = window.innerHeight;
			this.width = this.height / 1.5;
			this.scale = this.width / 480;
		}
	}

	// 左手の状態を更新して位置を返す
	updateLeft(): number[] {
		// 左手のアニメーション時間を更新
		this.leftTime += (this.leftTime > 0) ? -1 : 59;

		// ソースのx,y,w,h,canvasのx,y,w,h
		// debug: 30frameで切り替わるようにしてみた
		let a = new Array();
		a.push(Math.floor(this.leftTime / 30) * 53);
		a.push(0);
		a.push(53);
		a.push(53);
		a.push(111 * this.scale);
		a.push(453 * this.scale);
		a.push(53 * this.scale);
		a.push(53 * this.scale);
		return a;
	}
}