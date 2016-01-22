<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="model.CourseWare" %>
<%@ page import="java.util.ArrayList" %>
<!-- saved from url=(0031)http://192.168.199.189:3000/lj/ -->
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:set var="BASE" value="${pageContext.request.contextPath}"/>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>超级画板检索</title>
    <link href="static/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="static/bootstrap.css">
    <link rel="stylesheet" href="static/client.css">
    <script src="static/jquery.min.js"></script>
    <script src="static/bootstrap.min.js"></script>
    <script src="../../assets/js/ie10-viewport-bug-workaround.js"></script>

</head>

<body youdao="bind">
    <div class="container">
        <div class="row head">
            <div class="col-md-12"><img src="image/5.jpg" class="img-rounded img-responsive"></div>
        </div>
        <div class="row title">
            <div class="col-md-12">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h3>超级画板 | 检索演示系统</h3></div>
                    <div class="panel-body">
                        <p>这里是介绍这里是介绍这里是介绍这里是介绍这里是介绍这里是介绍</p>
                    </div>
                </div>
            </div>
        </div>
        <div class="row search-btn">
            <div class="col-md-12">
                <div class="input-group">
                    <input type="file" placeholder="搜索" class="form-control"><span class="input-group-btn"><button class="btn btn-warning">检索</button></span></div>
            </div>
        </div>
        <%--检索结果展示--%>
        <div class="row img-list">
            <c:forEach var="ware" items="${wareList}">
            <div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
                <div class="panel panel-default">
                    <div class="panel-body">
                        <img src="image/${ware.imgName}" class="img-responsive" style="width: 100%">
                    </div>
                    <div class="panel-footer">
                        <span class="text-primary pull-left">${ware.fileName}</span><a href="${BASE}/query?zjz=${ware.fileName}" class="pull-right">检索</a>
                        <div class="clearfix"></div>
                    </div>
                </div>
            </div>
            </c:forEach>
        </div>
        <div class="page text-center">
            <ul class="pagination">
                <li><a href="#">«</a></li>
                <li><a href="#">1</a></li>
                <li class="active"><a href="#">2</a></li>
                <li><a href="#">3</a></li>
                <li><a href="#">4</a></li>
                <li><a href="#">»</a></li>
            </ul>
        </div>
        <div class="modal fade">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-md-7"><img src="" class="img-responsive"></div>
                            <div class="col-md-5">
                                <div class="panel panel-default">
                                    <div class="panel-heading">简介</div>
                                    <div class="panel-body info">
                                        <p>这里是图片简介这里是图片简介这里是图片简介这里是图片简介这里是图片简介这里是图片简介这里是图片简介这里是图片简介这里是图片简介这里是图片简介这里是图片简介这里是图片简介这里是图片简介这里是图片简介这里是图片简介这里是图片简介这里是图片简介这里是图片简介这里是图片简介这里是图片简介这里是图片简介这里是图片简介这里是图片简介这里是图片简介这里是图片简介这里是图片简介这里是图片简介</p>
                                    </div>
                                    <div class="panel-footer">
                                        <button class="btn btn-primary pull-right btn-download">下载</button>
                                        <div class="clearfix"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <button type="button" data-dismiss="modal" aria-label="Close" class="close"><span aria-hidden="true">  ×</span></button>
            </div>
        </div>
        <script tyoe="text/javascript">
            var modal = $('.modal')
            $('.img-list').delegate('img', 'click', function(event) {
                modal.find('img').attr('src', $(this).attr('src'));
                modal.modal();
            });
            $('.btn-download').click(function(e){
                alert('aaaaaa')
            })
        </script>
    </div>


    <!-- Bootstrap core JavaScript
          ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->

    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
</body>

</html>
