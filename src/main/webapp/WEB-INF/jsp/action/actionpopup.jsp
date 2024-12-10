
  <div class="row">
    <div class="col-sm-12">
         <table id="tableData3popup" class="table table-striped table-bordered table-sm" cellspacing="0" width="100%" data-tableName="Stock Data">
                <input type="hidden" id="rowSelectorRelatedAddAction" name="rowSelectorId" value="">
              <thead>
                  <tr>
                      <th class="th-sm">ID</th>
                      <th class="th-sm">Action</th>
                      <th class="th-sm">System</th>
                      <th class="th-sm">Subsidiary</th>
                  </tr>
            </thead>
            <tbody>
                <c:forEach items="${actions}" var="model">
                  <tr id="${model.id}">
                      <td class="td-sm">${model.id}</td>
                      <td class="td-sm">${model.shortDescription}</td>
                      <td class="td-sm">${model.system.id}</td>
                      <td class="td-sm">${model.subsidiaries}</td>
                   </tr>
               </c:forEach>
           </tbody>
         </table>
    </div>
  </div>
<br/>
<br/>
<%@ include file="tableActions2popup.jsp" %>