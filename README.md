<p align="center">    

**人工检测图像质量平台的具体设计说明**  

</p>   

<br>/src/main/resources/static 中存放前端静态资源。  

  

<br>**index.html**为平台初始界面，主要包含受试者信息注册与登陆以及管理者登陆等;  

  

<br>**userPrompt.html**为受试者提示界面，告知受试者如何进行后续实验，并提供部分高视觉质量图像与低视觉质量图像供参考；  

  

<br>**user.html**为受试者实验界面，主要包含图像的随机选择、展示，倒计时功能以及部分交互功能等。  

  

<br>**result.html**用于提示受试者成功完成实验，进行数据的保存；  

<br>管理者可以通过登陆管理账号"admin"，查看数据可视化结果。  

<br>具体界面如下图所示，链接：[http://ttyc.3s.tunnelfrp.com](http://ttyc.3s.tunnelfrp.com)  

<br>(打开网页登陆，需联系管理员email:tangyingchun_131@163.com)  
<br>
<p align="center">  
客户端界面
  xxxxx
</p> 
<p align="center">  
移动端界面
  xxxxx
</p> 
<br>/img文件夹中存放预先设置的中毒图像。
<br>
<br>/src/main//java/com/example中主要存放后端逻辑代码，主要实现功能由/controller/UserController.java,/service/UserSevrivice.java以及/service/impl/UserSevriviceImpl.java三部分耦合实现，通过解耦的方式设计。/mapper/UserMapper主要用于受试者数据的写入，保存至数据库中。
