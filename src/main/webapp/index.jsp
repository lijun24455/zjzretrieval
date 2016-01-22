<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.GregorianCalendar" %><%--
  Created by IntelliJ IDEA.
  User: lijun
  Date: 15/12/28
  Time: 下午4:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="../../favicon.ico">

    <title>超级画板·检索系统</title>

    <!-- Bootstrap core CSS -->
    <link href="http://apps.bdimg.com/libs/bootstrap/3.3.0/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="starter-template.css" rel="stylesheet">

    <!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
    <!--[if lt IE 9]><script src="../../assets/js/ie8-responsive-file-warning.js"></script><![endif]-->
    <script src="../../assets/js/ie-emulation-modes-warning.js"></script>

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="//cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="//cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    <style>
    body{
      padding-top: 70px;
    }
    </style>
  </head>

  <body>

  <%!
      private int initVar = 0;
      private int serviceVar = 0;
      private int destroyVar = 0;
  %>

  <%!
      public void jspInit(){
          initVar++;
          System.out.println("jspInit():JSP初始化了"+ initVar + "times");
      }

      public void jspDestroy(){
          destroyVar++;
          System.out.println("jspDestroy():JSP销毁了" + destroyVar + "times");
      }
  %>
  <%
      serviceVar++;
      System.out.println("_jspService(): JSP共响应了"+serviceVar+"次请求");

      String content1="初始化次数 : "+initVar;
      String content2="响应客户请求次数 : "+serviceVar;
      String content3="销毁次数 : "+destroyVar;
  %>

    <nav class="navbar navbar-inverse navbar-fixed-top">
        <div class="container">
          <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed"
              data-toggle="collapse"
              data-target="#navbar"
              aria-expanded="false"
              aria-controls="navbar">
              <span class="sr-only">Toggle navigation</span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">检索系统</a>
          </div>
          <div id="navbar" class="collapse navbar-collapse">
            <ul class="nav navbar-nav">
              <li class="active"><a href="#">Home</a></li>
              <li><a href="#about">About</a></li>
              <li><a href="#contact">Contact</a></li>
            </ul>
          </div><!--/.nav-collapse -->
        </div>
      </nav>

      
      <div class="container">

            <div style="width:100%; text-align:right; background-color: #FFF">
                <h1>超级画板检索演示系统</h1>
                <p class="lead">Use this document as a way to quickly start any new project.<br> All you get is this text and a mostly barebones HTML document.</p>
            </div>

          <div>
              <h2><%=content1 %></h2>
              <h2><%=content2 %></h2>
              <h2><%=content3 %></h2>
              <table width="100%" border="1" align="center">
                  <tr bgcolor="#949494">
                      <th>Header Name</th><th>Header Values</th>
                  </tr>
                  <%
                      Enumeration headerNames = request.getHeaderNames();
                      while(headerNames.hasMoreElements()) {
                          String paramName = (String) headerNames.nextElement();
                          out.print("<tr><td>" + paramName + "</td>\n");
                          String paramValue = request.getHeader(paramName);
                          out.println("<td> " + paramValue + "</td></tr>\n");
                      }
                  %>

              </table>

              <%
                  // 设置每隔5秒自动刷新
                  response.setIntHeader("Refresh", 5);
                  // 获取当前时间
                  Calendar calendar = new GregorianCalendar();
                  String am_pm;
                  int hour = calendar.get(Calendar.HOUR);
                  int minute = calendar.get(Calendar.MINUTE);
                  int second = calendar.get(Calendar.SECOND);
                  if(calendar.get(Calendar.AM_PM) == 0)
                      am_pm = "AM";
                  else
                      am_pm = "PM";
                  String CT = hour+":"+ minute +":"+ second +" "+ am_pm;
                  out.println("Current Time is: " + CT + "\n");
              %>


          </div>




      </div><!-- /.container -->


      <!-- Bootstrap core JavaScript
      ================================================== -->
      <!-- Placed at the end of the document so the pages load faster -->
      <script src="http://apps.bdimg.com/libs/jquery/2.0.0/jquery.min.js"></script>
      <script src="http://apps.bdimg.com/libs/bootstrap/3.3.0/js/bootstrap.min.js"></script>
      <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
      <script src="../../assets/js/ie10-viewport-bug-workaround.js"></script>

  </body>
</html>
