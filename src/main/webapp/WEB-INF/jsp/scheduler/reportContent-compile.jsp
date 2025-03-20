<%@ include file="../include.jsp" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<div class="main-content">
    <div class="row">
          <div class="col-sm-5">
             <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
          </div>
          <div class="col-sm-7"></div>
    </div>
    <p id="url" style="display:none">${url}</p>

<div class="row">
    <div class="col-sm-12">
        <form:form method="POST" id="editForm" enctype="multipart/form-data" action="${url}" class="handle-upload" modelAttribute="editForm" >
           <div class="dt-buttons">
               <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" aria-controls="tableData" type="submit" title="Download">Download</button>
           </div>
        </form:form>
       <table id="tableData" class="table table-striped table-bordered table-sm" cellspacing="0" width="100%" data-tableName="Stock Data">
         <thead>
           <tr>

                <c:forEach var="name" items="${staticColumns}">
                    <th class="th-sm">${name}</th>
                </c:forEach>
                <c:forEach var="name" items="${columnNames}">
                     <th class="th-sm">${name}</th>
                </c:forEach>
           </tr>
         </thead>
         <tbody>
           <c:forEach items="${resultsMap}" var="resultMap">
             <tr>
                 <c:set var="keysMap" value="${fn:split(resultMap.key, '§')}"/>
                 <c:forEach var="keyVal" items="${keysMap}">
                 <td class="td-sm">${keyVal}</td>
                 </c:forEach>
                 <c:set var="valuesMap" value="${resultMap.value}"/>
                 <c:forEach var="name" items="${columnNames}">
                   <td class="td-sm">${valuesMap[name]!=null ? valuesMap[name] : 0}</td>
                 </c:forEach>
              </tr>
          </c:forEach>
       </tbody>
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
<%@ include file="tableActions-compile.jsp" %>
