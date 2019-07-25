Page({
  data:{
     
  },
  changeTap:function() {
    wx.request({
      url: 'http://238815v74m.zicp.vip:18805/demo/getUser', 
      data: {
         id:"10001",
         name:"liulai"
      },
      header: {
        'content-type': 'application/json'
      },
      success(res) {
        console.log(res.data)
      }
    })
  }
})
