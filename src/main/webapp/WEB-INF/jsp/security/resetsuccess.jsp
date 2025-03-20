<%@ include file="loginHeader.jsp" %>
</br>
</br>
</br>
</br>
  <body>
	<div class="container-fluid">
        <div class="row">
            <div class="col-xs-12 col-md-12 col-sm-12" style="text-align:center;font-weight:bold;color:${color};">
                    ${message}
            </div>
        </div>
        </br>
        <h4 class="text-center"><a href="${contextPath}/login">Login Now</a></h4>
		<div class="clear"> </div>
	</div>
	<div class="text-center" style="margin-bottom:0">
        <%@ include file="../footer.jsp" %>
    </div>
</body>
</html>
