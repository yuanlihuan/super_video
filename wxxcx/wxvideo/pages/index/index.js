const app = getApp()

Page({
  data: {
    page:1,//当前页数
    pageSize:1,//总计数
    pageList:[],//每页的数据
    screenWidth: 350,
    serverUrl:"",
    searchValue:""
  },

  onLoad: function (params) {
    var me = this;
    var isSaveSearch = params.isSaveSearch;
    var searchValue = params.searchValue;
    console.log(params+"999");
    if (searchValue == null || searchValue == "" || searchValue==undefined) {
      isSaveSearch=0;
    }
    me.setData({
      searchValue: searchValue,
      
    });
    var screenWidth = wx.getSystemInfoSync().screenWidth;
    me.setData({
      screenWidth: screenWidth,
    });
    wx.showLoading({
      title: '请等待...',
    });
    var page = me.data.page;
    me.getAllVideo(page, isSaveSearch);
  },
  //查询video公用方法
  getAllVideo: function (page, isSaveSearch) {
    var me = this;
    var serverUrl = app.severUrl;
    var searchValue ='';
    searchValue = me.data.searchValue;
    console.log(searchValue+"888") 
    
    wx.request({
      url: serverUrl + '/video/queryAllVideo?page=' + page + "&isSaveSearch=" + isSaveSearch,
      method: "POST",
      data:{
        videoDesc: searchValue
      },
      success: function (res) {
        var data = res.data;
         wx.hideLoading();
         wx.hideNavigationBarLoading();
         wx.stopPullDownRefresh();
        if (data.status == 200) {
          wx.hideLoading();
          if (page == 1) {
            me.setData({
              pageList: [],
            });
          }
          var pageListOld = me.data.pageList;
          var pageListNew = data.data.rows;
          me.setData({
            pageList: pageListOld.concat(pageListNew),
            page: data.data.page,
            pageSize: data.data.total,
            serverUrl: serverUrl
          })
        }
      }
    })
  },
  //上拉查询视频
  onReachBottom : function () {
    var me = this;
    var pages = me.data.page;
    var pageSize = me.data.pageSize;
    if (pages === pageSize) {
      wx.showToast({
        title: '已经是视频最后了...',
        icon: 'none',
      });
      return;
    }
    var page = pages + 1;
    console.log(page);
    me.getAllVideo(page,0);
  },
  //下拉触发查询video
  onPullDownRefresh:function() {
    var me = this;
    wx.showNavigationBarLoading();
    me.getAllVideo(1,0);
  },
  //点击视频列表进行跳转并且携带参数
  showVideoInfo:function(e) {
    var me = this;
    var pageList = me.data.pageList;
    var arrindex = e.target.dataset.arrindex;
    var videoInfo = JSON.stringify(pageList[arrindex]);
    wx.navigateTo({
      url: '../videoinfo/videoinfo?videoInfo=' + videoInfo,
    })
  }

})
