<%@ include file="../include.jsp" %>
  <div class="row">
    <div class="col-sm-12">
        <c:if test="${fileName!=null}">
           <p style="text-align:center;">Data uploaded successfully for details check the log <a href="${contextPath}/reports/${fileName}" style="color:blue;" target="_blank">${fileName}</a></p>
        </c:if>
    </div>
  </div>
<div class="main-content">
<div class="row">
    <div class="col-sm-12">
        <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
    </div>
</div>
  <div class="row">
    <div class="col-sm-12">
         <table id="tableData" class="table table-striped table-bordered table-sm" cellspacing="0" width="100%" data-tableName="Stock Data">
              <input type="hidden" id="rowSelectorId" name="rowSelectorId" value="">
              <thead>
                    <tr>
                      <th class="th-sm">ID</th>
                      <th class="th-sm">Date</th>
                      <th class="th-sm">site</th>
                      <th class="th-sm">subsidiary</th>
                      <th class="th-sm">Variant</th>
                    </tr>
              </thead>
              <tbody>
                  <c:forEach items="${models}" var="model">
                    <tr id="${model.id}">
                        <td class="td-sm">${model.id}</td>
                        <td class="td-sm">${model.creationTime}</td>
                        <td class="td-sm">${model.site}</td>
                        <td class="td-sm">${model.subsidiary}</td>
                        <td class="td-sm">${model.variant}</td>
                     </tr>
                 </c:forEach>
             </tbody>
         </table>
    </div>
  </div>
</div>
<%@ include file="../tableActionsWithUpload.jsp" %>