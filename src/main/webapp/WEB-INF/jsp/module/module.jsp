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
                      <th class="th-sm">ID</th>
                      <th class="th-sm">Identifier</th>
                      <th class="th-sm">ShortDescription</th>
                      <th class="th-sm">System Link</th>
                      <th class="th-sm">System Path</th>
                      <th class="th-sm">System</th>
                  </tr>
            </thead>
            <tbody>
                <c:forEach items="${models}" var="model">
                  <tr id="${model.id}">
                      <td class="td-sm">${model.recordId}</td>
                       <td class="td-sm">${model.identifier}</td>
                      <td class="td-sm">${model.shortDescription}</td>
                      <td class="td-sm">${model.systemLink}</td>
                      <td class="td-sm" >${model.systemPath}</td>
                      <td class="td-sm">${model.system.shortDescription}</td>
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
               <form:form method="POST" id="uploadForm" enctype="multipart/form-data" action="/admin/module/upload" class="handle-upload" modelAttribute="uploadForm">
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