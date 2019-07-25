//视频上传
function uploadVideo() {
  var me = this;
  wx.chooseVideo({
    sourceType: ['album', 'camera'],
    maxDuration: 15,
    camera: 'back',
    success(res) {
      console.log(res);
      var duration = res.duration;
      var height = res.height;
      var width = res.width;
      var size = res.size;
      var tempFilePath = res.tempFilePath;
      var thumbTempFilePath = res.thumbTempFilePath;
      if (duration > 25) {
        wx.showToast({
          title: '视频超过25秒，请重新上传',
          icon: "none",
          duration: 2000
        })
      } else if (duration < 1) {
        wx.showToast({
          title: '视频过段，请重新上传',
          icon: "none",
          duration: 2000
        })
      } else {
        //打开选择bgm的页面
        wx.navigateTo({
          url: '../chooseBgm/chooseBgm?duration=' + duration +
            "&height=" + height +
            "&width=" + width +
            "&size=" + size +
            "&tempFilePath=" + tempFilePath +
            "&thumbTempFilePath=" + thumbTempFilePath,
        })
      }

    }
  })
}
module.exports = {
  uploadVideo: uploadVideo
}