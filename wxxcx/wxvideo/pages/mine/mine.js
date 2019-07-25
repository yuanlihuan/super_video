var videoUtils = require('../../utils/videoUtils/uploadVideo.js');
const app = getApp()

Page({
  data: {
    faceUrl: "../resource/images/noneface.png",
    isMe: true,
    isFollow: true,

    videoSelClass:"video-info",
    isSelectedWork: "video-info-selected",
    isSelectedLike: "",
    isSelectedFollow: "",

    myWorkFalg:true,
    myLikesFalg:false,
    myFollowFalg:false,

    myVideoList:[],
    myVideoPage:1,
    myVideoCounts:1,

    likeVideoList:[],
    likeVideoPage: 1,
    likeVideoCounts: 1,

    followVideoList:[],
    followVideoPage:1,
    followVideoCounts:1,



  },
  //获取用户基本信息
  onLoad: function(pream) {
    var me = this;
    var publishUserId = pream.publishUserId;
    var severUrl = app.severUrl;
    var userInfo = app.getRedisUserInfo();
    var userId = userInfo.id;
    if (publishUserId != null && publishUserId != undefined && publishUserId != '') {
      userId = publishUserId;
      me.setData({
        isMe: false,
        publishUserId: publishUserId
      })
    }
    //console.log(app.globalData.userInfo);
    wx.showLoading({
      title: '请等待...',
    })
    wx.request({
      url: severUrl + '/user/query?userId=' + userId + "&fanId=" + userInfo.id,
      method: "POST",
      header: {
        "contextType": "application/json",
        "userId": userInfo.id,
        "userToken": userInfo.userToken
      },
      success: function(res) {
        wx.hideLoading();
        var data = res.data;
        console.log(data.data.isNotFollow);
        if (data.status == 200) {
          var faceUrl = me.data.faceUrl;
          if (data.data.faceImage != null && data.data.faceImage != '' &&
            data.data.faceImage != undefined) {
            var faceUrl = severUrl + data.data.faceImage;
          }
          me.setData({
            faceUrl: faceUrl,
            nickname: data.data.nickname,
            fansCounts: data.data.fansCounts,
            followCounts: data.data.followCounts,
            receiveLikeCounts: data.data.receiveLikeCounts,
            isFollow: data.data.isNotFollow
          })
        } else if (data.status == 502) {
          wx.showToast({
            title: data.msg,
            icon: "none",
            duration: 2000,
            success: function() {
              wx.navigateTo({
                url: '../userLogin/login'
              })
            }
          })
        }
      }
    })
  },
  logout: function() {
    wx.showLoading({
      title: '请等待...',
    });
    var severUrl = app.severUrl;
    var userInfo = app.getRedisUserInfo();
    wx.request({
      url: severUrl + '/logout?userId=' + userInfo.id,
      method: "POST",
      header: {
        "contextType": "application/json"
      },
      success: function(res) {
        wx.hideLoading();
        var status = res.data.status;
        if (status == 200) {
          wx.showToast({
            title: '注销成功',
            icon: "success",
            duration: 3000
          });
          //app.globalData.userInfo= null;
          wx.removeStorageSync("userInfo");
          wx.navigateTo({
            url: '../userLogin/login',
          })
        } else {
          wx.showToast({
            title: '注销失败',
            icon: "none",
            duration: 3000
          })
        }
      }
    })
  },
  changeFace: function() {
    var me = this;
    wx.showLoading({
      title: '上传中...',
    });
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album'],
      success(res) {
        // tempFilePath可以作为img标签的src属性显示图片
        const tempFilePaths = res.tempFilePaths
        console.log(tempFilePaths);
        var severUrl = app.severUrl;
        var userInfo = app.getRedisUserInfo();
        console.log(userInfo);
        wx.uploadFile({
          url: severUrl + '/user/uploadFace?userId=' + userInfo.id, // 仅为示例，非真实的接口地址
          filePath: tempFilePaths[0],
          name: 'file',
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
                title: '上传头像成功',
                icon: 'success',
                duration: 3000
              });
              var imageUrl = data.data;
              me.setData({
                faceUrl: app.severUrl + imageUrl
              });
            } else if (data.status == 502) {
              wx.showToast({
                title: data.msg,
                icon: "none",
                duration: 2000,
                success: function() {
                  wx.navigateTo({
                    url: '../userLogin/login'
                  })
                }
              })
            } else {
              wx.showToast({
                title: '上传头像失败',
                icon: 'none',
                duration: 3000
              })
            }

          }
        })
      }
    })
  },
  //视频上传
  uploadVideo: function() {
    //引入jsutils视频上传
    //videoUtis.uploadVideo();
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
  },
  //关注
  fanFollow: function(e) {
    var me = this;
    console.log(e);
    wx.showLoading({
      title: '请等待...',
    });
    var severUrl = app.severUrl;
    var userInfo = app.getRedisUserInfo();
    var publishUserId = me.data.publishUserId;
    var isNotFollow = e.currentTarget.dataset.isnotfollow;
    wx.request({
      url: severUrl + '/user/fanFollow?userId=' + publishUserId + "&fanId=" + userInfo.id + "&isNotFollow=" + isNotFollow,
      method: "POST",
      header: {
        "contextType": "application/json",
        "userId": userInfo.id,
        "userToken": userInfo.userToken
      },
      success: function(res) {
        var data = res.data;
        wx.hideLoading();
        if (data.status == 200) {
          if (isNotFollow == 1) {
            me.setData({
              isFollow: true,
              fansCounts: ++me.data.fansCounts
            })
          } else {
            me.setData({
              isFollow: false,
              fansCounts: --me.data.fansCounts
            })
          }
        } else if (data.status == 502) {
          wx.showToast({
            title: data.msg,
            icon: "none",
            duration: 2000,
            success: function() {
              wx.navigateTo({
                url: '../userLogin/login'
              })
            }
          })
        } else {
          wx.showToast({
            title: '关注失败',
            icon: 'none',
            duration: 3000
          })
        }
      }
    })
  },
  //作品
  doSelectWork:function() {
    var me = this;
    me.setData({
      isSelectedWork:"video-info-selected",
      isSelectedLike:"",
      isSelectedFollow:"",

      myWorkFalg: false,
      myLikesFalg: true,
      myFollowFalg: true,

      myVideoList: [],
      myVideoPage: 1,
      myVideoTotal: 1,

      likeVideoList: [],
      likeVideoPage: 1,
      likeVideoTotal: 1,

      followVideoList: [],
      followVideoPage: 1,
      followVideoTotal: 1
    });
    me.getDoSelectWork(1);
  },
  getDoSelectWork : function (pream) {
    var me = this;
    console.log(pream);
    var user = app.getRedisUserInfo();
    var serverUrl = app.severUrl;
    var searchValue = '';
    var isSaveSearch = 0;
    var page = me.data.myVideoPage;
    var pageNew = pream.page;
    if (pageNew != null && pageNew != undefined && pageNew != '') {
      page = pageNew;
    }
    wx.request({
      url: serverUrl + '/video/queryAllVideo?page=' + page + "&userId=" + user.id + "&isSaveSearch=" + isSaveSearch,
      method: "POST",
      data: {
        videoDesc: searchValue
      },
      success: function (res) {
        var data = res.data;
        console.log(data);
        wx.hideLoading();
        wx.hideNavigationBarLoading();
        wx.stopPullDownRefresh();
        if (data.status == 200) {
          wx.hideLoading();
          if (page == 1) {
            me.setData({
              myVideoList: [],
            });
          }
          var pageListOld = me.data.myVideoList;
          var pageListNew = data.data.rows;
          me.setData({
            myVideoList: pageListOld.concat(pageListNew),
            myVideoPage: data.data.page,
            myVideoCounts: data.data.total,
            serverUrl: serverUrl
          });
          console.log(me.data.myVideoList);
        }
      }
    })
  },
  //上拉
  onReachBottom: function () {
    debugger;
    var me = this;
    var myWorkFalg = me.data.myWorkFalg;
    var myLikesFalg = me.data.myLikesFalg;
    var myFollowFalg = me.data.myFollowFalg;
    console.log(myWorkFalg + myLikesFalg);
    if (!myWorkFalg) {
      var pages = me.data.myVideoPage;
      var pageSize = me.data.myVideoCounts;
      if (pages === pageSize) {
        wx.showToast({
          title: '已经是视频最后了...',
          icon: 'none',
        });
        return;
      }
      var page = pages + 1;
      console.log(page);
      me.getDoSelectWork(page);
    }
    if (!myLikesFalg) {
      var pages = me.data.likeVideoPage;
      var pageSize = me.data.likeVideoCounts;
      if (pages === pageSize) {
        wx.showToast({
          title: '已经是视频最后了...',
          icon: 'none',
        });
        return;
      }
      var page = pages + 1;
      console.log(page);
      me.getDoSelectLike(page);
    }
    if (!myFollowFalg) {
      var pages = me.data.followVideoPage;
      var pageSize = me.data.followVideoCounts;
      if (pages === pageSize) {
        wx.showToast({
          title: '已经是视频最后了...',
          icon: 'none',
        });
        return;
      }
      var page = pages + 1;
      console.log(page);
      me.getDoSelectFollow(page);
    }
    
  },
  //跳转视频页面
  showVideo:function(e) {
    var me = this;
    var myVideoList = me.data.myVideoList;
    var arrindex = e.target.dataset.arrindex;
    var videoInfo = JSON.stringify(myVideoList[arrindex]);
    wx.navigateTo({
      url: '../videoinfo/videoinfo?videoInfo=' + videoInfo,
    })
  },
  showVideoLike: function (e) {
    var me = this;
    var likeVideoList = me.data.likeVideoList;
    var arrindex = e.target.dataset.arrindex;
    var videoInfo = JSON.stringify(likeVideoList[arrindex]);
    wx.navigateTo({
      url: '../videoinfo/videoinfo?videoInfo=' + videoInfo,
    })
  },
  showVideoFollow: function (e) {
    var me = this;
    var followVideoList = me.data.followVideoList;
    var arrindex = e.target.dataset.arrindex;
    var videoInfo = JSON.stringify(followVideoList[arrindex]);
    wx.navigateTo({
      url: '../videoinfo/videoinfo?videoInfo=' + videoInfo,
    })
  },
  doSelectLike:function() {
    var me = this;
    me.setData({
      isSelectedWork: "",
      isSelectedLike: "video-info-selected",
      isSelectedFollow: "",

      myWorkFalg: true,
      myLikesFalg: false,
      myFollowFalg: true,

      likeVideoList: [],
      likeVideoPage: 1,
      likeVideoTotal: 1,
    });
    me.getDoSelectLike(1);
  },
  getDoSelectLike: function (pream) {
    var me = this;
    console.log(pream);
    var user = app.getRedisUserInfo();
    var serverUrl = app.severUrl;
    var page = me.data.likeVideoPage;
    var pageNew = pream.page;
    if (pageNew != null && pageNew != undefined && pageNew != '') {
      page = pageNew;
    }
    wx.request({
      url: serverUrl + '/video/collectionAllVideo?page=' + page + "&userId=" + user.id,
      method: "POST",
      success: function (res) {
        var data = res.data;
        console.log(data);
        wx.hideLoading();
        wx.hideNavigationBarLoading();
        wx.stopPullDownRefresh();
        if (data.status == 200) {
          wx.hideLoading();
          if (page == 1) {
            me.setData({
              likeVideoList: [],
            });
          }
          var pageListOld = me.data.likeVideoList;
          var pageListNew = data.data.rows;
          me.setData({
            likeVideoList: pageListOld.concat(pageListNew),
            likeVideoPage: data.data.page,
            likeVideoCounts: data.data.total,
            serverUrl: serverUrl
          });
          console.log(me.data.likeVideoList);
        }
      }
    })
  },
  doSelectFollow:function() {
    var me = this;
    me.setData({
      isSelectedWork: "",
      isSelectedLike: "",
      isSelectedFollow: "video-info-selected",

      myWorkFalg: true,
      myLikesFalg: true,
      myFollowFalg: false,

      followVideoList: [],
      followVideoPage: 1,
      followVideoTotal: 1,

    });
    me.getDoSelectFollow(1);
  },
  getDoSelectFollow: function (pream) {
    var me = this;
    console.log(pream);
    var user = app.getRedisUserInfo();
    var serverUrl = app.severUrl;
    var page = me.data.likeVideoPage;
    var pageNew = pream.page;
    if (pageNew != null && pageNew != undefined && pageNew != '') {
      page = pageNew;
    }
    wx.request({
      url: serverUrl + '/video/followAllVideo?page=' + page + "&userId=" + user.id,
      method: "POST",
      success: function (res) {
        var data = res.data;
        console.log(data);
        wx.hideLoading();
        wx.hideNavigationBarLoading();
        wx.stopPullDownRefresh();
        if (data.status == 200) {
          wx.hideLoading();
          if (page == 1) {
            me.setData({
              followVideoList: [],
            });
          }
          var pageListOld = me.data.followVideoList;
          var pageListNew = data.data.rows;
          me.setData({
            followVideoList: pageListOld.concat(pageListNew),
            followVideoPage: data.data.page,
            followVideoCounts: data.data.total,
            serverUrl: serverUrl
          });
          console.log(me.data.followVideoList);
        }
      }
    })
  },
})