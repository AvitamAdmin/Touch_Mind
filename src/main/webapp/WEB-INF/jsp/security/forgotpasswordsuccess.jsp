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
            </br>
            </br>
            <h4 style="text-align: center;"><a href="/login">Click here to Login</a></h4>
        </div>
		<div class="clear"> </div>
	</div>
	<div class="text-center" style="margin-bottom:0">
        <%@ include file="../footer.jsp" %>
    </div>
</body>
</html>
