"use strict";
$( document ).ready(function() {
    vueInit();
});

var app;
function vueInit(){
    // 1. 라우트 컴포넌트를 정의하세요.
    // 아래 내용들은 다른 파일로부터 가져올 수 있습니다.
    var Foo = { template: '<div>foo</div>' };
    var Bar = { template: '<div>bar</div>' };
    var Na = { template: '<div>name route view</div>' };
    var User = {
        template: '<div>User - 1 </div>'
      }
    var routes = [
          { path: '/foo', component: Foo }
        , { path: '/bar', component: Bar }
        , { path: '/user', name : 'user' , components: { default : User  , b : Na }  }
    ];
    var router = new VueRouter({routes});
    app = new Vue({router});
    app.$mount('#app');
    console.log('path : '  + app.$route.fullPath );
    if ( app.$route.fullPath != '/' && app.$route.fullPath != '') {
        app.$router.push('/')
    }
    //app.$router.push({name : 'user'});
}
function getComponent() {
    var cmp = {
        
    }
}