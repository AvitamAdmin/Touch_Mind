<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en">

<head>
	<title>${pageTitle}</title>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<link rel="shortcut icon" href="images/Favicon.png" />

	<link rel="stylesheet" media="all" href="${contextPath}/css/custom.css" />
	<link rel="stylesheet" href="${contextPath}/css/cron-picker.css" />


	<!-- plugins:css -->
	<link rel="stylesheet" href="${contextPath}/vendors/feather/feather.css">
	<link rel="stylesheet" href="${contextPath}/vendors/ti-icons/css/themify-icons.css">
	<link rel="stylesheet" href="${contextPath}/vendors/css/vendor.bundle.base.css">
	<link rel="stylesheet" href="${contextPath}/vendors/datatables.net-bs4/dataTables.bootstrap4.css">
	<link rel="stylesheet" href="${contextPath}/vendors/ti-icons/css/themify-icons.css">
	<link rel="stylesheet" type="text/css" href="DataTables/datatables.min.css"/>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
	<link rel="stylesheet" media="all" href="${contextPath}/css/style.css" />
    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
    <script src="https://code.jquery.com/jquery-3.4.1.slim.min.js" integrity="sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n" crossorigin="anonymous"></script>
    <link href="https://cdn.jsdelivr.net/npm/summernote@0.8.18/dist/summernote-lite.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/summernote@0.8.18/dist/summernote-lite.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.18.1/moment.min.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.13.0/css/all.min.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.0.1/dist/chart.umd.min.js"></script>
    </head>
<body>
	<div class="container-scroller">
		<c:set var="home" value="/"/>
		<!-- Top_navbar -->
			<nav class="navbar col-lg-12 col-12 p-0 d-flex flex-row">
				<div class="text-center navbar-brand-wrapper d-flex align-items-center justify-content-center">
					<a class="navbar-brand brand-logo mr-2" href="${home}"><h1 class="site-logo">
						<img src="${contextPath}/images/cheil.png" />
					</h1></a>
					<a class="navbar-brand brand-logo-mini" href="${home}"><h1 class="site-logo">
						<img src="${contextPath}/images/Favicon.png" />
					</h1></a>
				</div>
				<div class="navbar-menu-wrapper d-flex align-items-center justify-content-end hidden">
					<button class="navbar-toggler navbar-toggler align-self-center" type="button" data-toggle="minimize">
						<span  class="icon-menu"></span>
					</button>
					<button class="navbar-toggler navbar-toggler-right d-lg-none align-self-center" type="button" data-toggle="offcanvas">
						<span id="navbar" class="icon-menu"></span>
					</button>
				</div> 
			</nav>
		<!-- partial -->
	<div class="container-fluid page-body-wrapper">
			<!-- Left Sidebar -->
			<%@ include file="sideMenu.jsp" %>
			<!-- partial -->
			<div class="main-panel">
				<div class="content-wrapper">
				        <p id="actionMessage" style="text-align:center;color:green;font-weight:800;"></p>
						<div class="col-xs-12 col-md-12 col-sm-12" id="appContent">
						    <c:if test="${defaultHomePage}">
								<div class="row" style="margin-top:50px;">
                                    <div class="col-sm-12">
                                         <table id="tableData" class="table table-striped table-bordered table-sm" cellspacing="0" width="100%" data-tableName="Stock Data">
                                              <input type="hidden" id="rowSelectorId" name="rowSelectorId" value="">
                                              <thead>
                                                    <tr>
                                                      <th class="th-sm">Last Run-Time</th>
                                                      <th class="th-sm">Subsidiaries</th>
                                                      <th class="th-sm">Schedulers</th>
                                                      <th class="th-sm">Result</th>
                                                      <th class="th-sm">Issued Cases</th>
                                                      <th class="th-sm">Email to</th>
                                                    </tr>
                                              </thead>
                                              <tbody>
                                                  <c:forEach items="${cronJobs}" var="model">
                                                        <td class="td-sm">${model.jobTime}</td>
                                                        <td class="td-sm">${model.subsidiary}</td>
                                                        <td class="td-sm">${model.scheduler}</td>
                                                        <td class="td-sm">${model.cronStatus}</td>
                                                        <td class="td-sm">${model.processedSkus} SKUS</td>
                                                        <td class="td-sm">${model.email}</td>
                                                        </tr>
                                                 </c:forEach>
                                             </tbody>
                                         </table>
                                    </div>
                                  </div>
                             </c:if>
                        </div>
				</div>
					<!-- footer -->
					<!--%@ include file="footer.jsp" %-->
					<div class="modal" id="loading">
						<p>In Progress</p>
					</div>
					<!-- partial -->
				<!-- partial -->
			</div>
			<!-- main-panel ends -->
		</div>
		<!-- page-body-wrapper ends -->
	</div>

	<!-- plugins:js -->
	<script src="${contextPath}/vendors/js/vendor.bundle.base.js"></script>
	<script src="js/off-canvas.js"></script>
	<script src="js/hoverable-collapse.js"></script>
	<script type="text/javascript" src="DataTables/datatables.min.js"></script>

	<script src="https://cdn.rawgit.com/rainabba/jquery-table2excel/1.1.0/dist/jquery.table2excel.min.js"></script>
    <script type="text/javascript" language="javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.1.3/jszip.min.js"></script>
    <script type="text/javascript" language="javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.53/pdfmake.min.js"></script>
    <script type="text/javascript" language="javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.53/vfs_fonts.js"></script>
    <script src="${contextPath}/js/dashboard.js"></script>
	<script src="${contextPath}/js/custom.js"></script>
	<script src="${contextPath}/js/choices.js"></script>
	<script src="${contextPath}/js/quartz-cron-formatter.js"></script>
	<script src="${contextPath}/js/cron-picker.js"></script>
	<script src="${contextPath}/js/standard-cron-formatter.js"></script>
	<script src="${contextPath}/js/cron.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.18.1/moment.min.js"></script>
	<link rel="stylesheet" href="https://cdn.datatables.net/1.13.1/css/jquery.dataTables.min.css">
	<link rel="stylesheet" href="https://cdn.datatables.net/responsive/2.4.0/css/responsive.dataTables.min.css">
	<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.2/themes/smoothness/jquery-ui.css" />
	<link rel="stylesheet" href="https://cdn.datatables.net/rowreorder/1.3.3/css/rowReorder.dataTables.min.css" />
	<script type="text/javascript" language="javascript" src="https://cdn.datatables.net/rowreorder/1.3.3/js/dataTables.rowReorder.min.js"></script>
    <script src="http://code.jquery.com/ui/1.10.2/jquery-ui.js"></script>
    <!-- Include the plugin's CSS and JS: -->
    <script type="text/javascript" src="${contextPath}/js/bootstrap-multiselect.js"></script>
    <link rel="stylesheet" href="${contextPath}/css/bootstrap-multiselect.css" type="text/css"/>
    <link href="${contextPath}/css/lightbox.css" rel="stylesheet" />
    <link href="${contextPath}/css/summernote.css" rel="stylesheet" />
    <script type="text/javascript" src="${contextPath}/js/lightbox.js"></script>
    <script type="text/javascript" src="${contextPath}/js/summernote.js"></script>


	<!-- endinject -->

</body>
</html>