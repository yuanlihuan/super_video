const app = getApp()
Page({
  data: {
    bgmList: [],
    serverUrl: "",
    videoParams: {}
  },
  getLocation: function (e) {
    wx.getLocation({
      type: 'wgs84',
      success: function (res) {
       console.log(res);
       alert();
        var latitude = res.latitude
        var longitude = res.longitude
        //弹框
        wx.showModal({
          title: '当前位置',
          content: "纬度:" + latitude + ",经度:" + longitude,
        })
      }
    })
  },
  getChooseLocation :function() {
    wx.getLocation({
      type: 'gcj02', // 返回可以用于wx.openLocation的经纬度
      success(res) {
        const latitude = res.latitude
        const longitude = res.longitude
        wx.openLocation({
          latitude: res.latitude,
          longitude: res.longitude,
          scale: 18,
          success:function(res) {

            wx.chooseLocation({
              success: function(res) {
                console.log(res)
                alert(res);
              },
            })
          }
        })
      }
    })
  }

})
