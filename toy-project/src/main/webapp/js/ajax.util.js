'use strict';
let Ajax = function() {
	this.post = function(obj) {
		let r ;
		try {
			$.ajax({
				type:"POST"
				, url : getUrl(obj)
				, data : obj.data
				, async : false
				, dataType : getDataType(obj) /** 서버에서 받을 데이터 형식 **/
				, contentType : getContentType(obj) /*** 헤더의 content type 지정.(서버에서 받을 데이터 형식) ***/
				, success : function(xhr) {
					r = xhr;
				}
				, beforeSend : function(xhr){
		        }
				, fail : function(xhr) {
					console.error(xhr);
				}
			});//end ajax
		} catch (e) {
			console.error(e);
		}
		return r;
	}
	this.get = function(obj) {
		let r ;
		try {
			$.ajax({
				type:"GET"
				, url : getUrl(obj)
				, data : obj.data
				//, async : false
				, dataType : getDataType(obj) /** 서버에서 받을 데이터 형식 **/
				, contentType : getContentType(obj) /*** 헤더의 content type 지정.(서버에서 받을 데이터 형식) ***/
				, beforeSend : function(xhr){
		        }
				, success : function(xhr) {
					r = xhr;
				}
				, fail : function(xhr) {
					console.error(xhr);
				}
			});//end ajax
		} catch (e) {
			console.error(e);
		}
		return r;
	}
	function getDataType(obj) {
		if ( util.isEmpty(obj) || util.isEmpty(obj.dataType) ){
			return "json";
		}
		return obj.dataType;
	}
	function getUrl (obj){
		if ( util.isEmpty(obj) || util.isEmpty(obj.url) )
			throw "ajax request URL empty!!\n require parameter attribute [url]";
		return obj.url;
	}
	function getContentType( obj ){
		if ( util.isEmpty(obj) || util.isEmpty(obj.contentType) )
			return "application/json; charset=utf-8";
		return obj.contentType;
	}
}