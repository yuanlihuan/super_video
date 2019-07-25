Page({
  data:{
    time:"00:00",
    region: ['广东省', '广州市', '海珠区'],
    showname:"选择我",
    ranges:[1,2,3,4],
    object:[
      { id: "11", name:"lilai"},
      { id: "22", name:"lisi" },
      { id: "33", name:"wangwu" },
      { id: "44", name:"zhaoliu" },
      
      ]
  },
  mychange:function(e) {
    var index = e.detail.value;
    var id = this.data.object[index].id;
    var name = this.data.object[index].name;
    console.log(id+name);
    this.setData({
      showname:id+name
    })
   
  },
  mycancel:function(e) {

  },
  changetime:function(e){
    var index = e.detail.value;
    this.setData({
      time: index
    })
  }
})
