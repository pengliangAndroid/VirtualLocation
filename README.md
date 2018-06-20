# VirtualLocation 
虚拟定位，免root安装后可修改其他应用定位，可修改钉钉打卡位置，微信/QQ朋友圈位置，附近的人位置等

**gps定位转wifi接口功能暂时无法使用，目前没有时间修改代码，请参考http://www.cellocation.com/**

概述
---
由于前断时间朋友使用其他模拟定位App后（使用年费居然要几百···），对其背后的实现非常好奇，思考后进行开发实现的项目。
实现原理是让第三方应用在已经Hook过Android FrameWork的容器中运行，在框架内可以拦截GPS定位、Wifi、基站进行模拟返回，
达到偷梁换柱的效果，第三方应用获取后的数据就是模拟位置的真实GPS、WIFI、基站数据。


**由于该项目其使用的虚拟化容器框架VirtualApp受使用授权限制，其VirtualApp框架的代码仅供个人研究**  

[VirtualApp](https://github.com/asLody/VirtualApp) 该框架的可扩展功能有双开/多开、应用市场、模拟定位、一键改机、隐私保护、游戏修改、自动化测试、无感知热更新等技术

apk文件
---
[VirtualLocation.apk](https://raw.githubusercontent.com/pengliangAndroid/VirtualLocation/master/app/VirtualLocation.apk)

使用效果截图
---
**添加第三方应用**
![image](https://github.com/pengliangAndroid/VirtualLocation/blob/master/screenshot/1.png "使用效果截图")



**选择第三方应用的模拟位置**
![image](https://github.com/pengliangAndroid/VirtualLocation/blob/master/screenshot/2.png "使用效果截图")


**启动后效果**
  
  
![image](https://github.com/pengliangAndroid/VirtualLocation/blob/master/screenshot/3.png "使用效果截图")

