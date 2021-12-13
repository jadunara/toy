class User {
     constructor(firstName , lastName , age ) {
         this.firstName = firstName;
         this.lastName = lastName;
         this.age = age;
     }
     get age(){
         return this._age;
     }

     set age(val ) {
         this._age = val;
     }
}

const user1 = new User('Steve' , 'job' , -1);
console.log(user1.age  ) ;