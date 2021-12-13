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


function initDiv() {
	let div = document.createElement("div");
	div.id = "div";
	document.body.appendChild(div);
	return div;
}

function makeTag(div , tagName , tagId , innerText ) {
	let tagElement = document.createElement(tagName);
	tagElement.id = tagId ;
	tagElement.innerText = innerText;
	div.appendChild(tagElement);
}
function test () {
	return new Promise(function (resolve, reject) {
	  for(let i=0; i < 10; i++) {
		setTimeout(function(){
		  console.log(i);
		  resolve('result');
		}, i*1000);
	  }
	})
  }

  function delay(item) {
	return new Promise(function(resolve, reject){
	  setTimeout(function(){
		//console.log(item);
		//resolve('item - ' + item);
		resolve({'item - ' : item , time : new Date().toISOString() } );
	  },1000)
	})
  }
  
  async function test2(array) {
	let elem = document.getElementById("myBar");
	for(let i=0; i< array.length; i++){
	  let itx = await delay(array[i]);
	  console.log('itx' , itx);
	  elem.style.width = i + "%";
	  //if ( i == 3)
	  //throw 'xxxx';
	}
	console.log('Done 24');
  }
  

$(document).ready(function(){

	let div = initDiv();
	makeTag(div , "P" , "id_00" , "HASH");
	makeTag(div , "P" , "id_01" , "RANGE_01");
	makeTag(div , "P" , "id_02" , "RANGE_02");
	makeTag(div , "P" , "id_03" , "RANGE_03");
	//test().then(function(x){
	//	console.log('Done' , x)
	// });
	try {
		let arrayList = [];
		for ( let i = 0 ; i < 100 ; i++ ) {
			arrayList.push(i);
		}
		//test2([11,12,13,14,15]);
		//test2(arrayList);
	} catch (error) {
		console.error("xxxxxxxxxxxxxxxx");
		console.error(error);
	}
});


/* $("div[id^=cnt]").hide();
c1 = $("#xcnt1" ) , c2 = $("#xcnt2");
c1.text( "0%");
c2.text( "0%");
//	setTimeout(test , 100);
 */