'use strict';
var x = 1;

function foo() {
  var x = 10;
  console.log(`foo x is ${x}`);
  bar();
}

function bar() {
  console.log(x);
}

foo(); // ?
bar(); // ?
/**
 *
 */
function test() {
	//console.clear();
	var s = "  x한글테스트z " ;
//	console.log("s String.prototype.unbracketize " + String.unbracketize(s) );
//	console.log("s String.prototype.unbracketize " + String.prototype.unbracketize(s) );
//	console.log(String.validBrackets(s));
//	console.log(s.unbrace(""));;
//	console.log(s.unbracketize(""));;
//	console.log("s.rtrim() : "+ s.rtrim()+"#");
//	console.log("s.ltrim() : "+ s.ltrim()+"#");
//	console.log("s. trim() : "+ s. trim()+"#");
//	console.log(" format test : " + ("%-20s".sprintf("a") )) ;
//	var a1 = s.replace(/[\0-\x7f]|([0-\u07ff]|(.))/g,"$&$1$2").length;
//	console.log(a1);
//	console.log(s.length);
//	console.log("bytelength : " + s.bytelength());

	var s1 = { ResultCode : "0" , Data:  null , msg : "zz" } ;
	console.log('hello world');
	s1 = "{\"ResultCode\":\"0\",\"msg\":\"\",\"Data\":{\"a\":\"xxx\" } a}";
//	try {
//		console.log( JSON.parse(s1) );
//	} catch (e) {
//		console.log(e);
//	}
	var a1 = s1.split(",");
	for ( var i = 0 ; i < a1.length ; i++ ) {
		var b = a1[i];
		console.log(b);
		b = regex(b);
	}
	var rcrx = /\"ResultCode\"(.*)\d\"/g;
	var msgrx = /\"msg\"(.*?)\"/g
//	var p = /\{(.*?)\"(.*?)\"(.*?)\}/g ;
	var a1 = s1.match(rcrx);
	var a2 = s1.match(msgrx);
	console.log("ResultCode :" + a1)
	console.log("msg :" + a2)
	console.log("ResultCode :" + a1[0].trim())
	
	//document.querySelector("#val").value = "xxxx1 " + new Date();
	//util.pasteTxt( document.querySelector("#val").value );
	//util.copyTxt( document.querySelector("#val").value );
}

var STRING_DASHERIZE_REGEXP = (/[ _]/g);
var STRING_DASHERIZE_CACHE = {};
var STRING_DECAMELIZE_REGEXP = (/([a-z\d])([A-Z])/g);
var STRING_CAMELIZE_REGEXP = (/(\-|_|\.|\s)+(.)?/g);
var STRING_UNDERSCORE_REGEXP_1 = (/([a-z\d])([A-Z]+)/g);
var STRING_UNDERSCORE_REGEXP_2 = (/\-|\s+/g);

function camelize(str) {
	  return str.replace(STRING_CAMELIZE_REGEXP, function(match, separator, chr) {
	    return chr ? chr.toUpperCase() : '';
	  }).replace(/^([A-Z])/, function(match) {
	    return match.toLowerCase();
	  });
	}
function regex( s) {
	
}
function sleepTest(ms) {
	console.log('sleep start' , ms);
	util.sleep(ms);
	console.log('sleep end' , ms);
}
function cutByteLength(s , len) {
	if (s == null || s.length == 0) {
		return 0;
	}
	var size = 0;
	var rIndex = s.length;

	for ( var i = 0; i < s.length; i++) {
		var x = charByteSize(s.charAt(i));
		if ( x > 1) {
			x = 2;
		}
		size += x ;
		if( size == len ) {
			rIndex = i + 1;
			break;
		} else if( size > len ) {
			rIndex = i;
			break;
		}
	}
	return s.substring(0, rIndex);
}

function charByteSize ( ch ) {
	if (ch == null || ch.length == 0) {
		return 0;
	}

	var charCode = ch.charCodeAt(0);

	if (charCode <= 0x00007F) {
		return 1;
	} else if (charCode <= 0x0007FF) {
		return 2;
	} else if (charCode <= 0x00FFFF) {
		return 3;
	} else {
		return 4;
	}
}