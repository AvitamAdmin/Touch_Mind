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
                      <th class="th-sm">Identifier</th>
                      <th class="th-sm">Short Description</th>
                    </tr>
              </thead>
              <tbody>
                  <c:forEach items="${models}" var="model">
                    <tr id="${model.id}">
                        <td class="td-sm">${model.id}</td>
                        <td class="td-sm">${model.creationTime}</td>
                        <td class="td-sm">${model.identifier}</td>
                        <td class="td-sm">${model.shortDescription}</td>
                     </tr>
                 </c:forEach>
             </tbody>
         </table>
    </div>
  </div>
</div>
<%@ include file="../tableActionsWithUpload.jsp" %>