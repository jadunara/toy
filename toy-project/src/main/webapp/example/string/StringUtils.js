var _pfx = {};
_pfx.len = function( s ){
	if ( typeof s == "undefined" ) {
		return 0;
	}
	if ( typeof s == "string") {
		return s.length;
	}
}
_pfx.nvl = function( s , r) {
	console.log( 's == undefined = ' , s == undefined );
	console.log( 's == null = ' , s == null );
	if ( s == undefined || s == null )
		return r
	else
		s;
}