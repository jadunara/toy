'use strict';
let util =  {
	isEmpty : function(o){
		if ( o == null || o == undefined ) return true;
		if (typeof o ==  "undefined" ) return true;
		if (typeof o == 'array' ) {
			if ( o.length == 0 ) return true;
			else return false;
		} else if ( typeof o == 'object') {
			if ( Object.keys(o).length == 0 ) return true;
			else return false;
		} else if ( typeof o == 'string') {
			if ( o.length == 0 ) return true;
			else return false;
		}
		return false;
	}
	,
	isNotEmpty : function( o ) {
		if( this.isEmpty (o)) return false;
		else return true;
	}
	,
	toCamelCase : function(s) {
		if ( this.isEmpty( s )) return "";
	    return s.toLowerCase().replace(/[^a-zA-Z0-9]+(.)/g, function(match, chr) { return chr.toUpperCase(); });
	}
	,
	replaceAll : function(s, r) {
		if ( this.isEmpty (s)) return "";
		if ( this.isEmpty (r)) return s;
		if ( s.indexOf(r) == -1 ) return s;
		return s.split(s).join(r);
	}
	,
	pasteTxt : function(s) {
		navigator.clipboard.writeText(s).then(function(){
				console.log("paste success");
			}, function(e) {
				console.error("paste fail" , e);
			}
		)
	}
	,
	copyTxt : function(s) {
		navigator.clipboard.readText().then (clipTxt =>{
			clipText = s;//TODO ???
			console.log("copy success" , clipText);
		});
	}
	/********************************************************
	 * Blocking.(Sync)
	 ********************************************************/
	, sleep : function(ms) {
		if ( !Number.isInteger(ms) )
			throw "정수형 숫자만 입력가능합니다.[sleep parameter]";

	    var date = new Date();
	    var curDate = null;
	    do {curDate = new Date();}
	    while(curDate-date<ms);
	}
	, isInteger : function( n ){
		if (Number.isInteger(n)) return true;
		if ( this.isEmpty( n )) return false;
		if( n.startsWith("+") || n.startsWith("-")) return /^\d+$/.test(n.substring(1));
		else return /^\d+$/.test(n);
	}
	, nvl : function(a , b) {
		return this.isNotEmpty(a)?a:b;
	}
	, clone : function(o) {
		if( this.isEmpty(o)) return o;
		if ( typeof o == "object" || typeof o == "array") return JSON.parse(JSON.stringify(o));
		return o;
	}
	/*******************************************
	 * 호출위치 찾기.
	 *******************************************/
	, getStackTrace : function(){
		let stack = new Error().stack || '';
		stack = stack.split('\n').map(function (line) {
				return line.trim();
			});
		return stack.splice(stack[0] == 'Error' ? 2 : 1);
	}
}
