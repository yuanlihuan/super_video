var videoUtis = require('../../utils/videoUtils/uploadVideo.js');
const app = getApp()

Page({
  data: {
    serverUrl: app.severUrl,
    cover: "cover",
    loop: false, //循环播放
    src: "",
    videoInfo: {},
    videoId: "",
    userLikeVideo: false,
    userFace: "",

    placeholder:'评论',
    commentFocus:false,
    replyFatherCommentId:'',
    replyToUserId:'',
    hiddenComment:true,

    page:1,
    pageSize:1,
    commentList:[],

    fathercommentid: '',
    tonickname: '',
    touserid: '', 
    placeholder:'你想要评论...'
  },
  videoContext: {},
  //获取当前视频的上下文
  onLoad: function(pream) {
    var me = this;
    me.videoContext = wx.createVideoContext("myVideo", me)
    var videoInfo = JSON.parse(pream.videoInfo);
    var videoWidth = videoInfo.videoWIdth;
    var videoHeight = videoInfo.videoHeight;
    var cover = "cover";
    if (videoWidth >= videoHeight) {
      cover = "";
    }
    me.setData({
      src: app.severUrl + videoInfo.videoPath,
      videoInfo: videoInfo,
      videoId: videoInfo.id,
      cover: cover,
    });
    var severUrl = app.severUrl;
    var user = app.getRedisUserInfo();
    console.log(user.id + ":" + user.userToken);
    wx.request({
      url: severUrl + "/user/videoAndUserDetail?userId=" + user.id + "&videoId=" + videoInfo.id + "&publishUserId=" + videoInfo.userId,
      method: "POST",
      header: {
        "contextType": "application/json",
        "userId": user.id,
        "userToken": user.userToken
      },
      success: function(res) {
        var data = res.data;
        console.log(res.data);
        if (data.status == 200) {
          me.setData({
            userLikeVideo: data.data.bol,
            userFace: severUrl + data.data.usersVo.faceImage
          })
        }
      }
    });
    me.getCommentsAll(1);
  },
  onShow: function() {
    var me = this;
    me.videoContext.play();
  },
  onHide: function() {
    var me = this;
    me.videoContext.pause();
  },

  showSearch: function() {
    wx.navigateTo({
      url: '../searchVideo/searchVideo',
    });
  },
  //上传视频
  upload: function() {
    var me = this;
    var user = app.getRedisUserInfo();
    //获取videoInfo
    var videoInfo = JSON.stringify(me.data.videoInfo);
    var videoUrl = '../videoinfo/videoinfo#videoInfo@' + videoInfo;
    console.log(videoUrl);
    if (user == null || user == undefined || user == '') {
      wx.navigateTo({
        url: '../userLogin/login?videoUrls=' + videoUrl,
      })
    } else {
      videoUtis.uploadVideo();
    }
  },
  //首页按钮
  showIndex: function() {
    wx.navigateTo({
      url: '../index/index',
    })
  },
  //我的个人信息
  showMine: function() {
    var user = app.getRedisUserInfo();
    if (user == null || user == undefined || user == '') {
      wx.navigateTo({
        url: '../userLogin/login',
      })
    } else {
      wx.navigateTo({
        url: '../mine/mine',
      })
    }
  },
  likeVideoOrNot: function() {
    var me = this;
    var user = app.getRedisUserInfo();
    var videoInfo = me.data.videoInfo;

    if (user == null || user == undefined || user == '') {
      wx.navigateTo({
        url: '../userLogin/login',
      })
    } else {
      wx.showLoading({
        title: '...',
      })
      var userLikeVideo = me.data.userLikeVideo;
      var url = "/video/likeVideo?userId=" + user.id + "&videoId=" + videoInfo.id + "&videoCreateId=" + videoInfo.userId;
      console.log(url);
      if (userLikeVideo) {
        url = "/video/unLikeVideo?userId=" + user.id + "&videoId=" + videoInfo.id + "&videoCreateId=" + videoInfo.userId;
      }
      var severUrl = app.severUrl;
      wx.request({
        url: severUrl + url,
        method: "POST",
        header: {
          "contextType": "application/json",
          "userId": user.id,
          "userToken": user.userToken
        },
        success: function(res) {
          wx.hideLoading();
          var data = res.data;
          if (data.status) {
            me.setData({
              userLikeVideo: !userLikeVideo
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
          } else {
            wx.showToast({
              title: '点赞失败',
              icon: 'none',
              duration: 1000
            })
          }
        }
      })
    }
  },
  showPublisher: function() {
    var me = this;
    var user = app.getRedisUserInfo();
    //获取videoInfo
    var videoInfo = me.data.videoInfo;
    var videoUrl = '../mine/mine?publishUserId=' + videoInfo.userId;
    console.log(videoUrl);
    if (user == null || user == undefined || user == '') {
      wx.navigateTo({
        url: '../userLogin/login?videoUrls=' + videoUrl,
      })
    } else {
      wx.navigateTo({
        url: '../mine/mine?publishUserId=' + videoInfo.userId,
      })
    }
  },
  //分享
  shareMe: function() {
    var me = this;
    var videoInfo = me.data.videoInfo;
    var server = app.severUrl;
    wx.showActionSheet({
      itemList: ['举报用户', '下载本地', '转发到微信好友'],
      success(res) {
        console.log(res.tapIndex)
        var userId = me.data.videoInfo.userId;
        var videoId = me.data.videoInfo.id;
        //举报用户
        if (res.tapIndex == 0) {
          wx.navigateTo({
            url: '../report/report?videoId=' + videoId +
              "&userId=" + userId,
          })
        }
        if (res.tapIndex == 1) {
          wx.showLoading({
            title: '下载中...',
          })
          wx.downloadFile({
            url: server + videoInfo, // 仅为示例，并非真实的资源
            success(res) {
              if (res.statusCode === 200) {
                wx.saveVideoToPhotosAlbum({
                  filePath: res.tempFilePath,
                  success(res) {
                    console.log(res);
                    wx.hideLoading();
                    wx.showToast({
                      title: '下载成功',
                    })
                  }
                })
              }
            }
          })
        }
      },
      fail(res) {
        console.log(res.errMsg)
      }
    })
  },
  //分享我喜欢的视频
  onShareAppMessage(res) {
    var me = this;
    var videoInfo = me.data.videoInfo;
    return {
      title: '我喜欢的视频',
      path: "pages/videoinfo/videoinfo?videoInfo=" + JSON.stringify(videoInfo)
    }
  },
  //评论获取评论的焦点
  leaveComment:function() {
    var me = this;
    me.setData({
      commentFocus:true,
      hiddenComment:false
    });    
  },
  //提交评论
  saveComment:function(e) {
    wx.showLoading({
      title: '提交评论...',
    })
    var me = this;
    var severUrl =  app.severUrl;
    var user=app.getRedisUserInfo();
    var videoInfo = me.data.videoInfo;
    var fathercommentid = 1;
    if (fathercommentid != '' && fathercommentid != null && fathercommentid != undefined) {
      fathercommentid = me.data.fathercommentid;
    }
    var touserid = 1;
    if (touserid != '' && touserid != null && touserid != undefined) {
      touserid = me.data.touserid;
    }
    console.log()
    wx.request({
      url: severUrl +'/comment/userComment',
      method:"POST",
      data:{
        fatherCommentId: fathercommentid,
        toUserId: touserid,
        videoId: videoInfo.id,
        fromUserId: user.id,
        comment:e.detail.value,
      },
      header:{
        "contextType": "application/json",
        "userId": user.id,
        "userToken": user.userToken
      },
      success:function(res) {
        var data = res.data;
        if (data.status == 200) {
          wx.hideLoading();
          me.setData({
            hiddenComment:true
          });
          wx.showToast({
            title: '评论成功',
            icon: 'success',
            duration: 3000
          });
          me.getCommentsAll(1);
        }
      }
    })
  },
  //分页查询评论数据
  getCommentsAll:function(page) {
    var me = this;
    var severUrl = app.severUrl;
    var videoId = me.data.videoInfo.id;
    var pageSize = me.data.pageSize;
    console.log(pageSize + ":" + page);
    if (page == undefined || page == null || page == '') {
      page = 1
    }
    wx.request({
      url: severUrl + '/comment/queryAllComment?page='+page+"&videoId="+videoId+"&pageSize=5",
      method: "POST",
      success: function (res) {
        var data = res.data;
        var oldPageList = me.data.commentsList;
        var newPageList = data.data.rows;
        if (page == 1) {
          me.setData({
            pageList: [],
          });
        };
        console.log(data);
        me.setData({
          commentsList: newPageList.concat(oldPageList), 
          page:data.data.page,
          pageSize: data.data.total
        })
      }
    })
  },
  //上拉刷新
  onReachBottom: function () {
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
    me.getCommentsAll(page);
  },
  //点击回复视频
  replyFocus:function(e) {
    var me = this;
    console.log(e);
    var data = e.currentTarget.dataset;
    me.setData({
      fathercommentid: data.fathercommentid,
      tonickname: data.tonickname,
      touserid: data.touserid, 
      placeholder: "回复  " + data.tonickname,
      commentFocus: true,
      hiddenComment: false
    });

  },
})