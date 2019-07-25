const app = getApp()

Page({
  onReady(e) {
    // 使用 wx.createAudioContext 获取 audio 上下文 context
    this.audioCtx = wx.createInnerAudioContext('myAudio')
  },
  data: { 
    bgmList : [],
    severUrl:"",
    videoParams:{}
  },
  onLoad:function(params) {
    var me = this;
    console.log(params);
    me.setData({
      videoParams: params
    });
    wx.showLoading({
      title: '请等待...',
    });
    var severUrl = app.severUrl;
    wx.request({
      url: severUrl + '/bgm/query',
      method: "POST",
      header: {
        'Content-type': 'application/json'
      },
      success: function (res) {
        console.log(res.data);
        var status = res.data.status;
        if (status == 200) {
          wx.hideLoading();
          var bgmList = res.data.data;
          me.setData({
            bgmList: bgmList,
            severUrl: severUrl
          })
         
        }  
      }
    })
  },
  upload: function(e) {
    wx.showLoading({
      title: '正在上传请等待...',
    })
    var me = this;
    var bgmId = e.detail.value.bgmId;
    var desc = e.detail.value.desc;
    var duration = me.data.videoParams.duration;
    var height = me.data.videoParams.height;
    var width = me.data.videoParams.width;
    var size = me.data.videoParams.size;
    var tempFilePath = me.data.videoParams.tempFilePath;
    var thumbTempFilePath = me.data.videoParams.thumbTempFilePath;
    var severUrl = app.severUrl;
    var userId = app.getRedisUserInfo().id;
    var userInfo = app.getRedisUserInfo();

    //var userId = app.globalData.userInfo.id;
    //上传视频
    wx.uploadFile({
      url: severUrl + '/video/uploadVideo',
      filePath: tempFilePath,
      formData:{
        userId: userId,
        videoSeconds: duration,
        videoHeight: height,
        videoWidth: width,
        videoSize: size,
        thumbTempFilePath: thumbTempFilePath,
        bgmId: bgmId,
        desc: desc
      },
      name: 'files',
      header: {
        'Content-type': 'application/json',
        "userId": userInfo.id,
        "userToken": userInfo.userToken
      },
      success(res) {
        wx.hideLoading();
        var data = JSON.parse(res.data);
        var status = data.status
        console.log(status);
        if (status == 200) {
          wx.showToast({
            title: '上传视频',
            icon: 'success',
            duration: 3000
          });
          wx.navigateBack({
             delta:1,
          })
          // var videoId = data.data;
          // console.log(videoId + ":" + userId);
          // //上传视频封面
          // wx.uploadFile({
          //   url: severUrl + '/video/uploadVideoCover',
          //   filePath: thumbTempFilePath,
          //   formData: {
          //     userId: userId,
          //     videoId: videoId
          //   },
          //   name: 'files',
          //   header: {
          //     'Content-type': 'application/json'
          //   },
          //   success(res) {
          //     wx.hideLoading();
          //     var data = JSON.parse(res.data);
          //     var status = data.status
          //     console.log(status);
          //     if (status == 200) {
          //       wx.showToast({
          //         title: '上传视频成功',
          //         icon: 'success',
          //         duration: 3000
          //       });
          //       wx.navigateBack({
          //         delta:1,
          //       })
          //     }  
          //   }
          //})
        } else if (status == 502) {
          wx.showToast({
            title: data.msg,
            icon: 'none',
            duration: 2000,
            success:function() {
              wx.navigateTo({
                url: '../userLogin/login',
              })
            }
          });
        } else {
          wx.showToast({
            title: '上传视频失败',
            icon: 'none',
            duration: 3000
          });
          wx.navigateTo({
            url: '../mine/mine',
          })
        }
      }
    })
  }
})

