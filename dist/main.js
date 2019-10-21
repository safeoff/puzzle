/******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, { enumerable: true, get: getter });
/******/ 		}
/******/ 	};
/******/
/******/ 	// define __esModule on exports
/******/ 	__webpack_require__.r = function(exports) {
/******/ 		if(typeof Symbol !== 'undefined' && Symbol.toStringTag) {
/******/ 			Object.defineProperty(exports, Symbol.toStringTag, { value: 'Module' });
/******/ 		}
/******/ 		Object.defineProperty(exports, '__esModule', { value: true });
/******/ 	};
/******/
/******/ 	// create a fake namespace object
/******/ 	// mode & 1: value is a module id, require it
/******/ 	// mode & 2: merge all properties of value into the ns
/******/ 	// mode & 4: return value when already ns object
/******/ 	// mode & 8|1: behave like require
/******/ 	__webpack_require__.t = function(value, mode) {
/******/ 		if(mode & 1) value = __webpack_require__(value);
/******/ 		if(mode & 8) return value;
/******/ 		if((mode & 4) && typeof value === 'object' && value && value.__esModule) return value;
/******/ 		var ns = Object.create(null);
/******/ 		__webpack_require__.r(ns);
/******/ 		Object.defineProperty(ns, 'default', { enumerable: true, value: value });
/******/ 		if(mode & 2 && typeof value != 'string') for(var key in value) __webpack_require__.d(ns, key, function(key) { return value[key]; }.bind(null, key));
/******/ 		return ns;
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = "./src/main.ts");
/******/ })
/************************************************************************/
/******/ ({

/***/ "./img/block.png":
/*!***********************!*\
  !*** ./img/block.png ***!
  \***********************/
/*! no static exports found */
/***/ (function(module, exports) {

eval("module.exports = \"data:image/png;base64,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\"\n\n//# sourceURL=webpack:///./img/block.png?");

/***/ }),

/***/ "./src/board.ts":
/*!**********************!*\
  !*** ./src/board.ts ***!
  \**********************/
/*! exports provided: Board */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
eval("__webpack_require__.r(__webpack_exports__);\n/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, \"Board\", function() { return Board; });\n/* harmony import */ var _drop__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./drop */ \"./src/drop.ts\");\n\nvar Board = /** @class */ (function () {\n    function Board() {\n        // 画像読み込み\n        // this.left.src = left\n        // 盤面\n        this.canvas = document.createElement(\"canvas\");\n        this.ctx = this.canvas.getContext('2d');\n        // \tleft = document.createElement(\"img\")\n        // canvasの幅と高さ\n        this.width = 480;\n        this.height = 400;\n        // debug 左手のアニメーション時間\n        this.leftTime = 59;\n        // ドロップ\n        this.drop = new _drop__WEBPACK_IMPORTED_MODULE_0__[\"Drop\"]();\n        // サイズ\n        this.canvas.width = this.width;\n        this.canvas.height = this.height;\n        this.gridWidth = this.width / this.drop.width;\n        this.gridHeight = this.height / this.drop.height;\n    }\n    Board.prototype.update = function () {\n        this.drawBG();\n        this.drawDrop();\n    };\n    // 盤面のドロップを描画\n    Board.prototype.drawDrop = function () {\n        for (var i = 0; i < this.drop.width; i++) {\n            for (var j = 0; j < this.drop.height; j++) {\n                this.ctx.drawImage(this.drop.colorMap[this.drop.map[i][j]], i * this.gridWidth, j * this.gridHeight, this.gridWidth, this.gridHeight);\n            }\n        }\n    };\n    // 背景を描画\n    Board.prototype.drawBG = function () {\n        // 市松模様\n        for (var i = 0; i < this.drop.width; i++) {\n            for (var j = 0; j < this.drop.height; j++) {\n                this.ctx.fillStyle = this.calcCheckeredCode(i, j);\n                this.ctx.fillRect(i * this.gridWidth, j * this.gridHeight, this.gridWidth, this.gridHeight);\n            }\n        }\n    };\n    // 市松模様のカラーコードを計算\n    Board.prototype.calcCheckeredCode = function (i, j) {\n        if ((i % 2 == 1 && j % 2 == 0) ||\n            (i % 2 == 0 && j % 2 == 1)) {\n            return \"#492300\";\n        }\n        return \"#381400\";\n    };\n    // 左手の状態を更新して位置を返す\n    Board.prototype.updateLeft = function () {\n        // 左手のアニメーション時間を更新\n        this.leftTime += (this.leftTime > 0) ? -1 : 59;\n        // ソースのx,y,w,h,canvasのx,y,w,h\n        // debug: 30frameで切り替わるようにしてみた\n        var a = new Array();\n        // a.push(Math.floor(this.leftTime / 30) * 53)\n        // a.push(0)\n        // a.push(53)\n        // a.push(53)\n        // a.push(111 * this.scale)\n        // a.push(453 * this.scale)\n        // a.push(53 * this.scale)\n        // a.push(53 * this.scale)\n        return a;\n    };\n    return Board;\n}());\n\n\n\n//# sourceURL=webpack:///./src/board.ts?");

/***/ }),

/***/ "./src/drop.ts":
/*!*********************!*\
  !*** ./src/drop.ts ***!
  \*********************/
/*! exports provided: Drop */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
eval("__webpack_require__.r(__webpack_exports__);\n/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, \"Drop\", function() { return Drop; });\n/* harmony import */ var _img_block_png__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../img/block.png */ \"./img/block.png\");\n/* harmony import */ var _img_block_png__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(_img_block_png__WEBPACK_IMPORTED_MODULE_0__);\n\nvar Drop = /** @class */ (function () {\n    function Drop() {\n        this.width = 6;\n        this.height = 5;\n        // 画像\n        this.img = document.createElement(\"img\");\n        this.fire = document.createElement(\"canvas\");\n        this.water = document.createElement(\"canvas\");\n        this.tree = document.createElement(\"canvas\");\n        this.shine = document.createElement(\"canvas\");\n        this.dark = document.createElement(\"canvas\");\n        this.cure = document.createElement(\"canvas\");\n        this.colorNum = 6;\n        // 管理番号と画像の対応\n        this.colorMap = { 0: this.fire,\n            1: this.water,\n            2: this.tree,\n            3: this.shine,\n            4: this.dark,\n            5: this.cure };\n        // 画像読み込み\n        this.setIMG();\n        // ランダムなドロップを配置\n        this.setDrop();\n    }\n    // 画像読み込み\n    Drop.prototype.setIMG = function () {\n        this.img.src = _img_block_png__WEBPACK_IMPORTED_MODULE_0__;\n        var drops = [this.fire, this.water, this.tree, this.shine, this.dark, this.cure];\n        for (var i = 0; i < drops.length; i++) {\n            var ctx = drops[i].getContext('2d');\n            drops[i].width = 100;\n            drops[i].height = 100;\n            // 画像ソースのx,y,w,h,canvasのx,y,w,h\n            ctx.drawImage(this.img, i * 100 + 10, 0, 100, 100, 0, 0, 100, 100);\n        }\n    };\n    // ランダムなドロップを配置\n    Drop.prototype.setDrop = function () {\n        this.map = new Array();\n        for (var i = 0; i < this.width; i++) {\n            this.map[i] = new Array();\n            for (var j = 0; j < this.height; j++) {\n                this.map[i][j] = Math.floor(Math.random() * this.colorNum);\n            }\n        }\n    };\n    return Drop;\n}());\n\n\n\n//# sourceURL=webpack:///./src/drop.ts?");

/***/ }),

/***/ "./src/gameScene.ts":
/*!**************************!*\
  !*** ./src/gameScene.ts ***!
  \**************************/
/*! exports provided: GameScene */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
eval("__webpack_require__.r(__webpack_exports__);\n/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, \"GameScene\", function() { return GameScene; });\n/* harmony import */ var _board__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./board */ \"./src/board.ts\");\n\nvar GameScene = /** @class */ (function () {\n    function GameScene() {\n        this.buff = document.createElement(\"canvas\");\n        // canvasの幅と高さ\n        this.width = window.innerWidth;\n        this.height = this.width * 1.5;\n        this.scale = this.width / 480;\n        // 画像読み込み\n        this.board = new _board__WEBPACK_IMPORTED_MODULE_0__[\"Board\"]();\n        // 横長ディスプレイの場合はサイズを変える\n        if (window.innerWidth > window.innerHeight) {\n            this.height = window.innerHeight;\n            this.width = this.height / 1.5;\n            this.scale = this.width / 480;\n        }\n        // 内部バッファの幅と高さ\n        this.buff.width = this.width;\n        this.buff.height = this.height;\n        // 要素の位置\n        this.boardHeight = this.height - this.board.height * this.scale;\n    }\n    // ゲームの状態を更新する\n    GameScene.prototype.update = function () {\n        this.board.update();\n    };\n    // 内部バッファに画像を描画する\n    GameScene.prototype.draw = function () {\n        var ctx = this.buff.getContext('2d');\n        ctx.clearRect(0, 0, this.buff.width, this.buff.height);\n        // 盤面を描画\n        ctx.drawImage(this.board.canvas, 0, this.boardHeight, this.board.width * this.scale, this.board.height * this.scale);\n        // 戦闘画面を描画\n        // debug: 左手をアニメーションさせてみる\n        // const leftPos = this.images.updateLeft();\n        // ctx.drawImage(this.images.left, leftPos[0], leftPos[1], leftPos[2], leftPos[3],\n        // \tleftPos[4], leftPos[5], leftPos[6], leftPos[7]);\n        return this.buff;\n    };\n    return GameScene;\n}());\n\n\n\n//# sourceURL=webpack:///./src/gameScene.ts?");

/***/ }),

/***/ "./src/main.ts":
/*!*********************!*\
  !*** ./src/main.ts ***!
  \*********************/
/*! no exports provided */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
eval("__webpack_require__.r(__webpack_exports__);\n/* harmony import */ var _gameScene__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./gameScene */ \"./src/gameScene.ts\");\n\nvar Main = /** @class */ (function () {\n    // タイマーイベント開始\n    function Main() {\n        var _this = this;\n        // canvasを取得・設定\n        this.canvas = document.getElementById('canvas');\n        this.canvas.width = window.innerWidth;\n        this.canvas.height = window.innerHeight;\n        this.ctx = this.canvas.getContext('2d');\n        // ゲーム画面を初期化\n        this.GameScene = new _gameScene__WEBPACK_IMPORTED_MODULE_0__[\"GameScene\"]();\n        window.requestAnimationFrame(function () { return _this.draw(); });\n    }\n    // TODO: タップイベントのリスナーを入れる？\n    // 更新と描画\n    Main.prototype.draw = function () {\n        // TODO: TitleかGameかResultか、みたいな分岐を入れるといいのかも\n        var _this = this;\n        // 状態を更新して、内部バッファをcanvasに反映させる。\n        this.GameScene.update();\n        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);\n        this.ctx.drawImage(this.GameScene.draw(), 0, 0);\n        // ループ\n        window.requestAnimationFrame(function () { return _this.draw(); });\n    };\n    return Main;\n}());\nwindow.onload = function () {\n    new Main();\n};\n\n\n//# sourceURL=webpack:///./src/main.ts?");

/***/ })

/******/ });