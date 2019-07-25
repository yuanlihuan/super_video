Page({
  data:{
    iconSize: [20, 30, 40, 50, 60, 70],
    iconColor: [
      'red', 'orange', 'yellow', 'green', 'rgb(0,255,255)', 'blue', 'purple'
    ],
    iconType: [
      'success', 'success_no_circle', 'info', 'warn', 'waiting', 'cancel', 'download', 'search', 'clear'
    ],
    nodess: '<img class="course-banner" src="//img1.mukewang.com/szimg/5c6bdb3e08e4674a06000338.jpg">',

    node2:[{
      name:"img",
      attrs:{
      class:'course-banner',
      src:'//img1.mukewang.com/szimg/5c6bdb3e08e4674a06000338.jpg'
      }
    }],
    mypercent:15,
    name:"liulai"
    
  },
  donghua: function () {
    var numbers = this.data.mypercent + 10;
    this.setData({
      mypercent: numbers
    })
  }
})
