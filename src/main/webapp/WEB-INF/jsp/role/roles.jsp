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
                      <th class="th-sm">Id</th>
                      <th class="th-sm">Name</th>
                      <th class="th-sm">Creater</th>
                      <th class="th-sm">Creation Time</th>
                      <th class="th-sm">Last Modified</th>
                    </tr>
              </thead>
              <tbody>
                  <c:forEach items="${roles}" var="role">
                    <tr id="${role.id}">
                        <td class="td-sm">${role.recordId}</td>
                        <td class="td-sm">${role.identifier}</td>
                        <td class="td-sm">${role.creator}</td>
                        <td class="td-sm">${role.creationTime}</td>
                        <td class="td-sm">${role.lastModified}</td>
                     </tr>
                 </c:forEach>
             </tbody>
         </table>
    </div>
  </div>
</div>
<div id="myModalEdit" class="modal fade" tabindex="-1">
      <div class="modal-dialog-full" style="margin-left:200px;margin-top:100px;">
         <div class="modal-content" style="height:65%;margin-left:20px !important">
            <div class="modal-header">
                Edit
               <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body" id="editModelContent">

            </div>
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
               <form:form method="POST" id="uploadForm" enctype="multipart/form-data" action="/admin/role/upload" class="handle-upload" modelAttribute="uploadForm">
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
