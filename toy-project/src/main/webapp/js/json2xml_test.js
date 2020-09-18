var xmlData = '<?xml version="1.0" encoding="UTF-8"?>'
+"\n<hd h='xx'>   "
+"\n	<dt a='1'  b='1'/>"
+"\n	<dt a='2'  b='2'/>"
+"\n	<dt a='3'  b='3'/>"
+"\n	<dt a='4'  b='4'/>"
+"\n</hd>         "
;
;
function test(){
	var x2js = new X2JS();
	var jsonObj = x2js.xml_str2json( xmlData );
    console.log(jsonObj);
    var xmlAsStr = x2js.json2xml_str( jsonObj );
    console.log(xmlAsStr);
}