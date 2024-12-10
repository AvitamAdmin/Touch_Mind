<%@ include file="../include.jsp" %>
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
                      <th class="th-sm">PK</th>
                      <th class="th-sm">Email</th>
                      <th class="th-sm">Status</th>
                      <th class="th-sm">Subsidiary</th>
                      <th class="th-sm">Role</th>

                    </tr>
              </thead>
              <tbody>
                  <c:forEach items="${userList}" var="model">
                    <tr id="${model.id}">
                        <td class="td-sm">${model.id}</td>
                        <td class="td-sm">${model.username}</td>
                        <c:choose>
                            <c:when test="${model.status}">
                                 <c:set var="varChecked" value="Active"></c:set>
                             </c:when>
                             <c:otherwise>
                                 <c:set var="varChecked" value="Inactive"></c:set>
                             </c:otherwise>
                        </c:choose>
                        <td class="td-sm">${varChecked}</td>
                        <td class="td-sm">
                            <c:forEach items="${model.subsidiaries}" var="child">
                                 ${child.identifier},
                            </c:forEach>
                        </td>
                        <td class="td-sm">
                            <c:forEach items="${model.roles}" var="child">
                                 ${child.identifier},
                            </c:forEach>
                        </td>
                     </tr>
                 </c:forEach>
             </tbody>
         </table>
    </div>
  </div>
</div>
<div id="myModalUpload" class="modal fade" tabindex="-1">
      <div class="modal-dialog" style="max-width: 50%; margin-top:120px; margin-left: 350px;">
         <div class="modal-content">
            <div class="modal-header" style="border-bottom:none;">
            Upload
               <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
               <form:form method="POST" id="uploadForm" enctype="multipart/form-data" action="/admin/user/upload" class="handle-upload" modelAttribute="uploadForm">
                    <div class="form-group files">
                    <input class="file-input" type="file" name="file" path="file" accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel">
                    </div>
               <div style="text-align:center;">
                   <input type="submit" value="Upload" onClick="ajaxformSubmit('uploadForm');"/>
               </div>
               </form:form>
            </div>
         </div>
      </div>
   </div>
<%@ include file="../tableActionsWithUpload.jsp" %>