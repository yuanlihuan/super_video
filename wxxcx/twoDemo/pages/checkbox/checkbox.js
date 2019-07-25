Page({
   data:{
     arrays:[
       { name: "a中国", id:'1001', value: "1", disable:false,checked:false} ,
       { name: "b中国", id:'1002', value: "2", disable: false, checked: false},
       { name: "c中国", id:'1003', value: "3", disable: false, checked: false } ,
       { name: "d中国", id:'1004', value: "4", disable: false, checked: false } 
     ]
   },
  changed : function(e) {
    debugger;
  },
  mySubmit:function(e) {
    debugger
  },
  myReset: function (e) {
    return "设置重置。。s"
  }

})
