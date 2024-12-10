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
              <input type="hidden" id="roleId" name="roleId" value="">
              <thead>
                    <tr>
                      <th class="th-sm">Id</th>
                      <th class="th-sm">Name</th>
                      <th class="th-sm">Creater</th>
                      <th class="th-sm">Type</th>
                      <th class="th-sm">Creation Time</th>
                      <th class="th-sm">Last Modified</th>
                    </tr>
              </thead>
              <tbody>
                  <c:forEach items="${models}" var="model">
                    <tr id="${role.id}">
                        <td class="td-sm">${model.id}</td>
                        <td class="td-sm">${model.name}</td>
                        <td class="td-sm">${model.creator}</td>
                        <td class="td-sm">${model.type}</td>
                        <td class="td-sm">${model.creationTime}</td>
                        <td class="td-sm">${model.lastModified}</td>
                     </tr>
                 </c:forEach>
             </tbody>
         </table>
    </div>
  </div>
</div>
<%@ include file="../tableActions.jsp" %>