<%@ include file="../head.jsp" %>
<body>
	<div class="container-fluid">
		<header>
		<%@ include file="../mainMenu.jsp" %>
		</header>
		<div class="container-fluid">
			<div class="row">
				<div class="col-xs-12 col-md-3 col-sm-12">
					<%@ include file="../sideMenu.jsp" %>
				</div>
				<div class="col-xs-12 col-md-9 col-sm-12">
					<section class="main-content-section">
						<%@ include file="loginContent.jsp" %>
					</section>
				</div>
			</div>
		</div>
		<div class="clear"> </div>
	</div>
	<div class="jumbotron text-center" style="margin-bottom:0">
		<%@ include file="../footer.jsp" %>
	</div>
</body>
</html>
