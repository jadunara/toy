var eventBus = new Vue();
Vue.component('child', {
  props: ['value'],
  // el : '#child' ,

  watch :
  {
	  'value' : function() {
		  console.log('child watch value');
		  this.callMe();
	  }
  }

  , created:function(){
  	eventBus.$on('triggerEventBus',function(data){
      	console.log("child eventBus eventName" , data); //abc
      });
  }
  ,
   data : function(){
      return  {
    	  valueChild : "X"
    	, values : "Y"
     }
  },

  template : "#child",
  mounted () {
// setInterval(() => {
// console.log("setInterval " , this.value);
// this.$emit('update', this.value + 1)
// }, 5000)
	  console.log("mounted valueChild" , this.valueChild);
	  console.log("mounted values" , this.values);
  },
  updated : function ()  {
	  console.log("updated valueChild" , this.valueChild);
	  console.log("updated values" , this.values);

  },
  methods :  {
	  childClick : function() {
		  console.clear();
		  console.log("child button click ");

		  var obj = {
				   x : this.value
				  ,y : this.value *12

		  }
//		  this.$emit('update', this.value + 1);
		  this.$emit('update', obj);
	  }
  	,
  	callMe : function() {
  		console.log('call this function from parent'
  				+ "\n valueChild : " + this.valueChild
  				+ "\n values : " + this.values
  				);
  	}
  	,
  	reciveData : function (data) {
  		console.log("child reciveData : " , data)
  	}
  	,
    showLog:function(){
    	console.log('child showLog' );
        eventBus.$emit('triggerEventBus',100);
      }
  }

// render (h, ctx) {
// return h('h2', this.value)
// }
});

var vue = new Vue({
  el: '#app',
  data: {
	    value: 15
	  },
	created: function ()
	{
		console.log("main created");
	},
  methods : {
     onChildUpdate : function(newValue) {
      console.log('newValue' , newValue);
      this.value = newValue ;
      // this.$on("valueChild" , this.value *100 );
    }
    , created:function(){
    	eventBus.$on('triggerEventBus',function(data){
        	console.log("app eventBus eventName" , data); //abc
        });
    }
	, updated : function (){
		console.log("app update  value : " + this.value);
	}
    ,
	  parentClick : function() {
		  console.log("parent button click ");
		  //this.$commit("valueChild" , this.value *100 );//없는 function
		  //this.$commit("values" , this.value *100 ); //없는 function

//		  this.$emit('values', this.value + 1);
//		  this.$emit("valueChild" , this.value *100 );

//		  eventBus.$on('child', (this.value + 1) + "");
//          eventBus.$on('triggerEventBus',function(value){
//              console.log("app 13123 이벤트를 전달받음. 전달받은 값:"+value);
//            });

	  }
     , testxxx : function (name, value) {
    	 try {
    		 this.$emit(name, value);
		} catch (e) {
			console.error(e);
		}
     }
     , callMeApp : function () {
    	 console.log('app call this function from child ??');
     }
  }// end methods
 ,

 watch :
 {
	  'value' : function() {
		  console.log('app watch value');
		  this.callMeApp();
	  }
 },//end watch

})
;
