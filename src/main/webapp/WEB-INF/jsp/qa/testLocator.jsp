<%@ include file="../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="row">
            <div class="col-sm-12">
                <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-sm-12">
            <table id="tableData" class="table table-striped table-bordered table-sm" cellspacing="0" width="100%" data-tableName="Test Locator Data">
                <input type="hidden" id="rowSelectorId" name="rowSelectorId" value="">
                <thead>
                <tr>
                    <th class="th-sm">recordId</th>
                    <th class="th-sm">identifier</th>
                    <th class="th-sm">description</th>
                    <th class="th-sm">methodName</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${models}" var="model">
                    <tr id="${model.id}">
                        <td class="td-sm">${model.recordId}</td>
                        <td class="td-sm">${model.identifier}</td>
                        <td class="td-sm">${model.description}</td>
                        <td class="td-sm">${model.methodName}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>
<%@ include file="../tableActionsQaWithUpload.jsp" %>
<div id="myModalEdit" class="modal fade" tabindex="-1">
      <div class="modal-dialog-full" style="margin-left:200px;margin-top:100px;">
         <div class="modal-content" style="height:65%;margin-left:20px !important">
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
               <form:form method="POST" id="uploadForm" enctype="multipart/form-data" action="/admin/locator/upload" class="handle-upload" modelAttribute="uploadForm">
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