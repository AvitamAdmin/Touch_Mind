<%@ include file="../include.jsp" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<div class="main-content">
    <div class="row">
          <div class="col-sm-5">
             <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
          </div>
          <div class="col-sm-7"></div>
    </div>
    <p id="reportId" style="display:none">${reportId}</p>
<div class="row">
    <div class="col-sm-12">
       <table id="tableData" class="table table-striped table-bordered table-sm" cellspacing="0" width="100%" data-tableName="Stock Data">
         <thead>
           <tr>
                <c:forEach var="name" items="${columnNames}">
                     <th class="th-sm">${name}</th>
                </c:forEach>
           </tr>
         </thead>
       </table>
        <c:if test="${not empty message}">
            <div class="alert alert-danger" role="alert">
                <spring:message code="${message}" />
            </div>
        </c:if>
    </div>
  </div>
</div>
<script>
$(document).ready(function () {
$('.content-wrapper').addClass('toolkit');
});
</script>
<%@ include file="tableActions.jsp" %>
