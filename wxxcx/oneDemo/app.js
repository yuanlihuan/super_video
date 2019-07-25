App({
  onLaunch(options) {
    console.log("触发--onLaunch");
  },
  onShow(options) {
    console.log("触发--onShow");
  },
  onHide() {
    console.log();
  },
  onError(msg) {
    console.log(msg)
  },
  globalData: 'I am global data',
  courseName: '小程序'
})