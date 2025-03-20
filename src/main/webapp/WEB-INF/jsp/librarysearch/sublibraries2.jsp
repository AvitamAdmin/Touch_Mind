<%@ include file="../include.jsp" %>
<div class="main-content collapsed" id="collapseSubLib">
<div class="row" >
    <div class="col-sm-12">
        <p style="font-size:20px;font-weight:bolder;">Related Library</p>
    </div>
</div>
  <div class="row">
    <div class="col-sm-12">
         <table id="tableData1sub" class="table table-striped table-bordered table-sm" cellspacing="0" width="100%" data-tableName="Stock Data">
              <input type="hidden" id="rowSelectorId" name="rowSelectorId" value="">
              <thead>
                    <tr>
                        <th class="th-sm">ID</th>
                        <th class="th-sm">Description</th>
                        <th class="th-sm">Library Type</th>
                        <th class="th-sm">Subsidiaries</th>
                    </tr>
              </thead>
              <tbody>
                  <c:forEach items="${subLibrariesList}" var="model">
                    <tr id="${model.id}">
                        <td class="td-sm"><a class="nav-link" style="color:blue;" id="firstmenu" href="#" onclick="javascript:fire_ajax_submit('/admin/library/edits?id=${model.id}&libId=${editForm.id}')">${model.id}</td>
                        <td class="td-sm">${model.description}</td>
                        <td class="td-sm">${model.type}</td>
                        <td class="td-sm">${model.subsidiaries}</td>
                     </tr>
                 </c:forEach>
             </tbody>
         </table>
    </div>
  </div>
</div>
<%@ include file="tableActionsedit.jsp" %>

