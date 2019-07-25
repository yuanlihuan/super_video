//index.js
//获取应用实例
const app = getApp()

Page({

  data: {
    
  },
  doRegist: function (e) {
    var forObject = e.detail.value;
    var username = forObject.username;
    var password = forObject.password;

    //验证
    if (username.length == 0 || password.length == 0) {
      wx.showToast({
        title: '用户名密码不能为空',
        icon:"none",
        duration:3000
      })
    } else {
      wx.showLoading({
        title: '请等待...',
      })
      var severUrl = app.severUrl;
      wx.request({
        url: severUrl +'/regist',
        method:"POST",
        data:{
          username:username,
          password:password
        },
        header: {
          'Content-type': 'application/json'
        },
        success: function(res) {
          console.log(res.data);
          var status = res.data.status;
          if (status == 200) {
            wx.hideLoading();
            wx.showToast({
              title: '用户注册成功',
              icon:"none",
              duration:3000
            }),
              //app.globalData.userInfo = res.data.data;
            //将用户信息缓存到微信本地
            app.setRedisUserInfo(res.data.data);
          } else if (status == 500) {
            wx.showToast({
              title:  res.data.msg,
              icon: "none",
              duration: 3000
            })
          }
        }
      })
    }

  },
  goLoginPage: function () {
    wx.navigateTo({
      url: '../userLogin/login',
    })
  }
  
})
