var eventBus = new Vue();

var ChildComponent = new Vue ({
	// template: '<div>{{value}}</div>',
	component :{
		template : "#child",
		data : function() {
			return {
				  value : 0
				, list : []
			};
		}
		,
		created : function(){
			var l = [
					  'a0',  'b0' , 'c0' , 'd0' , 'e0' , 'f0' , 'g0' , 'h0' , 'j0' , 'k0'
					, 'a1' , 'b1' , 'c1' , 'd1' , 'e1' , 'f1' , 'g1' , 'h1' , 'j1' , 'k1'
					, 'a2' , 'b2' , 'c2' , 'd2' , 'e2' , 'f2' , 'g2' , 'h2' , 'j2' , 'k2'
					, 'a3' , 'b3' , 'c3' , 'd3' , 'e3' , 'f3' , 'g3' , 'h3' , 'j3' , 'k3'
					, 'a4' , 'b4' , 'c4' , 'd4' , 'e4' , 'f4' , 'g4' , 'h4' , 'j4' , 'k4'
					, 'a5' , 'b5' , 'c5' , 'd5' , 'e5' , 'f5' , 'g5' , 'h5' , 'j5' , 'k5'
					, 'a6' , 'b6' , 'c6' , 'd6' , 'e6' , 'f6' , 'g6' , 'h6' , 'j6' , 'k6'
				]
			;
			console.log('l length ' , l.length);
			this.list = l;
		}
		,
		methods : {
			setValue : function(value) {
				console.log('child setValue value ', value);
				this.value = value.y + 1;
			},
			callParent : function() {
				alert('xxx');
				console.log('child callParent value ', this.value);
				this.$emit("update" , "xxxx");
			}
		}

	}
});
var foo = {
	template : "#foo"
		, data : function() {
			return {
				  f1 : "x11"
				, f2 : "x21"
				, list : []
			}
		}
, 	methods : {
		click1 : function() {
			var obj = {
				x : 100,
				y : 102
			};
//			console.log(this.$refs.ChildComponent);
//			ChildComponent.setValue(obj);
			console.log("click 1");
//			this.$on('sistersaid', 'Mom said do your homework!');
			var data = obj;
//			eventBus.$on('update_eventBus',eventBus);
//			eventBus.$on('update_eventBus', data => {
//				console.log("data");
//			});
			this.$root.$emit('eventing_child', data);
		}
		,
		sizezz : function (obj) {
			console.log("app sizezz : " + obj)
		}
		, update : function(obj) {
			console.log("app obj" , obj);
		}
		, sistersaidFoo : function (obj ) {
			console.log("sistersaidFoo obj : " , obj);
		}
	}
	, mounted : function() {
		console.log("app cmp mounted");
	    this.$root.$on('eventing_foo', data => {
	        console.log("app cmp mounted on ", data);
	    });
	}
	, created : function() {
		console.log("app cmp created");
//		eventBus.$on('triggerEventBus',function(data){
//        	console.log("app  created eventBus eventName" , data); //abc cr
//        });
		var l = [
			  	  'a0',  'b0' , 'c0' , 'd0' , 'e0' , 'f0' , 'g0' , 'h0' , 'j0' , 'k0'
//				, 'a1' , 'b1' , 'c1' , 'd1' , 'e1' , 'f1' , 'g1' , 'h1' , 'j1' , 'k1'
//				, 'a2' , 'b2' , 'c2' , 'd2' , 'e2' , 'f2' , 'g2' , 'h2' , 'j2' , 'k2'
//				, 'a3' , 'b3' , 'c3' , 'd3' , 'e3' , 'f3' , 'g3' , 'h3' , 'j3' , 'k3'
//				, 'a4' , 'b4' , 'c4' , 'd4' , 'e4' , 'f4' , 'g4' , 'h4' , 'j4' , 'k4'
//				, 'a5' , 'b5' , 'c5' , 'd5' , 'e5' , 'f5' , 'g5' , 'h5' , 'j5' , 'k5'
//				, 'a6' , 'b6' , 'c6' , 'd6' , 'e6' , 'f6' , 'g6' , 'h6' , 'j6' , 'k6'
				, 'a7' , 'b7'
			]
		;
		console.log('l lenght' , l.length);
		this.list = l;
	}
	, updated: function() {
		console.log("app cmp updated");
	}
};
var child = {
	template : "#child"
	// el : "#child"
	, data : function() {
		return {
			  f1 : "x1"
			, f2 : "x2"
		}
	}
	, methods : {
		click1 : function() {
			var obj = {
				x : 100,
				y : 10211
			};
			console.log("child click1");
//			console.log(this.$refs.ChildComponent);
//			ChildComponent.setValue(obj);
//			this.$on('sistersaidFoo', 'Mom said do your homework!');
			var data1 = obj;
			this.$root.$emit('eventing_foo', data1);
//			eventBus.$emit('triggerEventBus',function(data1){
//	        	console.log("child click1 app eventBus eventName" , data1); //abc222
//			});

		}
		,
		sizezz : function (obj) {
			console.log("child sizezz : " + obj)
		}
		, update : function(obj) {
			console.log("child obj" , obj);
		}
		, sistersaid : function (obj) {
			console.log("child sistersaid : " , obj);
		}
		, update_eventBus : function (obj) {
			console.log(" child update_eventBus " , obj);
		}
	}
	, mounted : function() {
		console.log("child cmp mounted");
	    this.$root.$on('eventing_child', data => {
	        console.log("child mounted eventBus on ", data);
	    });
	}
	, created : function() {
		console.log("child cmp created");
	}
	, updated: function() {
		console.log("child cmp updated");
	}
};

var routes = [
	  { path: '/foo', components: { default : foo , 'sub' : child } },
	  { path: '/child', component: child }
	];

var router = new VueRouter({
	 routes
});

var v = new Vue({
	el : "#app" ,
	router ,
//	components : {foo} ,
//	template : "#app1"
});
$(document).ready(function(){
	try {

		router.push('/foo');
	} catch (e) {

	}
});
