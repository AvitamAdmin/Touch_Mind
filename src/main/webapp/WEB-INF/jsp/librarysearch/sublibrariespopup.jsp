
  <div class="row">
    <div class="col-sm-12">
         <table id="tableDatalibpopup" class="table table-striped table-bordered table-sm" cellspacing="0" width="100%" data-tableName="Stock Data">
              <input type="hidden" id="rowSelectorIdAddSubLibrary" name="subLibraries" value="">
              <thead>
                    <tr>
                        <th class="th-sm">ID</th>
                        <th class="th-sm">Description</th>
                        <th class="th-sm">Library Type</th>
                        <th class="th-sm">Subsidiaries</th>
                    </tr>
              </thead>
              <tbody>
                  <c:forEach items="${subLibraries}" var="model">
                    <tr id="${model.id}">
                        <td class="td-sm">${model.id}</td>
                        <td class="td-sm">${model.shortDescription}</td>
                        <td class="td-sm">${model.type}</td>
                        <td class="td-sm">${model.subsidiaries}</td>
                     </tr>
                 </c:forEach>
             </tbody>
         </table>
    </div>
  </div>
<%@ include file="tableActionspopup.jsp" %>
