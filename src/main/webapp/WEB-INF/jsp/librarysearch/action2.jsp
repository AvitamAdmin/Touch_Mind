<%@ include file="../include.jsp" %>
<div class="main-content collapsed" id="collapseAction">
<div class="row">
    <div class="col-sm-12">
        <p style="font-size:20px;font-weight:bolder;">Actions</p>
    </div>
</div>
  <div class="row">
    <div class="col-sm-12">
         <table id="tableData3edit" class="table table-striped table-bordered table-sm" cellspacing="0" width="100%" data-tableName="Stock Data">
              <input type="hidden" id="rowSelectorId" name="rowSelectorId" value="">
              <thead>
                  <tr>
                      <th class="th-sm">Action</th>
                      <th class="th-sm">System</th>
                      <th class="th-sm">Library</th>
                      <th class="th-sm">Remarks</th>
                      <th class="th-sm">System path</th>
                      <th class="th-sm">Subsidiary</th>
                  </tr>
            </thead>
            <tbody>
                <c:forEach items="${actionsList}" var="model">
                  <tr id="${model.id}">
                      <td class="td-sm"><a class="nav-link" style="color:blue;" id="firstmenu" href="#" onclick="javascript:fire_ajax_submit('/admin/action/edits?id=${model.id}&libId=${editForm.id}')">${model.id}</td>
                      <td class="td-sm">${model.system.shortDescription}</td>
                      <c:set var="hasValue" value="false"></c:set>
                      <c:forEach var="mapData" items="${actionLibMap}" varStatus="status">
                        <c:if test="${mapData.key == model.id}">
                            <c:set var="hasValue" value="true"></c:set>
                            <td class="td-sm">
                            <c:forEach var="library" items="${mapData.value}" varStatus="status">
                              <a class="nav-link" style="color:blue;" href="#" onclick="javascript:fire_ajax_submit('/admin/library/edit?id=${library.id}')">${library.id}</a>
                              <br/>
                            </c:forEach>
                            </td>
                        </c:if>
                      </c:forEach>
                      <c:if test="${hasValue == false}" >
                        <td class="td-sm"></td>
                      </c:if>
                      <td class="td-sm">${model.remarks}</td>
                      <td class="td-sm" >${model.systemPath}</td>
                      <td class="td-sm">${model.subsidiaries}</td>
                   </tr>
               </c:forEach>
           </tbody>
         </table>
    </div>
  </div>
</div>
<br/>
<br/>
<%@ include file="tableActions2edit.jsp" %>