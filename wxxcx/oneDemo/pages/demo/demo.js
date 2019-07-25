const common = require('../utils/common.js')
 Page({
  data: {
    motto: 'Hello World',
    name : 'liulai',
    a:1,
    b:2,
    c:3,
    user:[
      { name: 'zhangsan' },
      { name: 'lisi' },
      { name: 'wangwu' }
    ]
  },
  onLoad: function() {
    const appInstance = getApp()
    console.log(appInstance.courseName); 
    this.setData({
      motto: appInstance.courseName
    });
   },
   onReady() {
     console.log('触发--onReady'); 
   },
   onShow() {
     console.log('触发--onShow'); 
   },
   onHide() {
     console.log('触发--onHide'); 

   },
   onUnload() {
     console.log('触发--onUnload'); 
   },
   clickMe:function() {
     debugger;
    wx.redirectTo({
      url: '../index/index'
    })
   },
   clickTo:function(e) {
     console.log(e);
     var a = e.currentTarget.dataset.name;
     console.log(a);
     common.sayHello('zhangsan');
     common.sayGoodbye('lisi');
   }

})
