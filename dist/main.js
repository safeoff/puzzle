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

/***/ "./src/board.ts":
/*!**********************!*\
  !*** ./src/board.ts ***!
  \**********************/
/*! exports provided: Board */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
eval("__webpack_require__.r(__webpack_exports__);\n/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, \"Board\", function() { return Board; });\nvar Board = /** @class */ (function () {\n    function Board() {\n        // 画像読み込み\n        // this.rough.src = rough\n        // this.left.src = left\n        // 盤面\n        this.canvas = document.createElement(\"canvas\");\n        this.ctx = this.canvas.getContext('2d');\n        // \tleft = document.createElement(\"img\")\n        // canvasの幅と高さ\n        this.width = 480;\n        this.height = 400;\n        // debug 左手のアニメーション時間\n        this.leftTime = 59;\n        this.canvas.width = this.width;\n        this.canvas.height = this.height;\n        this.gridWidth = this.width / 6;\n        this.gridHeight = this.height / 5;\n    }\n    Board.prototype.update = function () {\n        this.drawBG();\n        this.ctx.fillStyle = \"black\";\n        this.ctx.fillRect(10, 10, 10, 10);\n    };\n    // 背景を描画\n    Board.prototype.drawBG = function () {\n        this.ctx.fillStyle = \"darkgray\";\n        this.ctx.fillRect(0, 0, this.width, this.height);\n    };\n    // 左手の状態を更新して位置を返す\n    Board.prototype.updateLeft = function () {\n        // 左手のアニメーション時間を更新\n        this.leftTime += (this.leftTime > 0) ? -1 : 59;\n        // ソースのx,y,w,h,canvasのx,y,w,h\n        // debug: 30frameで切り替わるようにしてみた\n        var a = new Array();\n        // a.push(Math.floor(this.leftTime / 30) * 53)\n        // a.push(0)\n        // a.push(53)\n        // a.push(53)\n        // a.push(111 * this.scale)\n        // a.push(453 * this.scale)\n        // a.push(53 * this.scale)\n        // a.push(53 * this.scale)\n        return a;\n    };\n    return Board;\n}());\n\n\n\n//# sourceURL=webpack:///./src/board.ts?");

/***/ }),

/***/ "./src/gameScene.ts":
/*!**************************!*\
  !*** ./src/gameScene.ts ***!
  \**************************/
/*! exports provided: GameScene */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
eval("__webpack_require__.r(__webpack_exports__);\n/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, \"GameScene\", function() { return GameScene; });\n/* harmony import */ var _board__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./board */ \"./src/board.ts\");\n//import { Images } from \"./images\";\n\nvar GameScene = /** @class */ (function () {\n    function GameScene() {\n        this.buff = document.createElement(\"canvas\");\n        // canvasの幅と高さ\n        this.width = window.innerWidth;\n        this.height = this.width * 1.5;\n        this.scale = this.width / 480;\n        // 画像読み込み\n        this.board = new _board__WEBPACK_IMPORTED_MODULE_0__[\"Board\"]();\n        // 横長ディスプレイの場合はサイズを変える\n        if (window.innerWidth > window.innerHeight) {\n            this.height = window.innerHeight;\n            this.width = this.height / 1.5;\n            this.scale = this.width / 480;\n        }\n        // 内部バッファの幅と高さ\n        this.buff.width = this.width;\n        this.buff.height = this.height;\n    }\n    // ゲームの状態を更新する\n    GameScene.prototype.update = function () {\n        this.board.update();\n    };\n    // 内部バッファに画像を描画する\n    GameScene.prototype.draw = function () {\n        var ctx = this.buff.getContext('2d');\n        ctx.clearRect(0, 0, this.buff.width, this.buff.height);\n        // 盤面を描画\n        ctx.drawImage(this.board.canvas, 0, 0, this.board.width * this.scale, this.board.height * this.scale);\n        // ctx.drawImage(this.board.canvas, 0, 0)\n        // ctx.drawImage(this.images.rough, 0, 0, this.images.width, this.images.height);\n        // 戦闘画面を描画\n        // debug: 左手をアニメーションさせてみる\n        // const leftPos = this.images.updateLeft();\n        // ctx.drawImage(this.images.left, leftPos[0], leftPos[1], leftPos[2], leftPos[3],\n        // \tleftPos[4], leftPos[5], leftPos[6], leftPos[7]);\n        return this.buff;\n    };\n    return GameScene;\n}());\n\n\n\n//# sourceURL=webpack:///./src/gameScene.ts?");

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