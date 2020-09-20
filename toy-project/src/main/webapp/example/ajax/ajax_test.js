'use strict';
//ajax.getDataType(params);
let ajax = new Ajax();
let c1  , c2 , fsm;
let params = {
		data : ""
			, dataType : "json"
				, url : 'https://www.daum.net'
};


let gv_cnt = 0; /** 값 갱신을 위한 변수**/

function rnd() {
	console.log('rnd : ' + gv_cnt);
//	c1.html( (gv_cnt ) + "%");
//	c2.html( (gv_cnt ) + "%");
}
function test(){
//	let id = setInterval(function(){
//		rnd();
//	}, 100);
//	console.log('id : ' + id);
	let r ;
	let i = 0;

	for ( i = 0; i <= 100 ; i++) {
		console.log(i);
		gv_cnt++;
		//setTimeout(rnd, 100);
		rnd();
		$("#cnt_"+ i).show();
		util.sleep(100);
	}
}

function move(i) {
	console.log('move ' + i);
	if (i == 0) {
		i = 1;
		let elem = document.getElementById("myBar");
		let width = 1;
		let id = setInterval(frame, 10);
		function frame() {
			if (width >= 100) {
				clearInterval(id);
				i = 0;
			} else {
				width++;
				elem.style.width = width + "%";
			}
		}
	}
}
$(document).ready(function(){
	$("div[id^=cnt]").hide();
	c1 = $("#xcnt1" ) , c2 = $("#xcnt2");
	c1.text( "0%");
	c2.text( "0%");
//	setTimeout(test , 100);
});
