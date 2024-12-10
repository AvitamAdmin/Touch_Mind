
<div class="row">
    <div class="col-sm-12">
        <p style="font-size:22px;font-weight:bolder;">Related Library</p>
    </div>
</div>
  <div class="row">
    <div class="col-sm-12">
         <table id="tableData1" class="table table-striped table-bordered table-sm" cellspacing="0" width="100%" data-tableName="Stock Data">
              <input type="hidden" id="rowSelectorIdSubLibrary" name="subLibraries" value="">
              <thead>
                    <tr>
                        <th class="th-sm">ID</th>
                        <th class="th-sm">description</th>
                        <th class="th-sm">library type</th>
                        <th class="th-sm">subsidiaries</th>
                    </tr>
              </thead>
              <tbody>
                  <c:forEach items="${subLibrariesList}" var="model">
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
<%@ include file="tableActions.jsp" %>
