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
                      <th class="th-sm">Name</th>
                      <th class="th-sm">Description</th>
                      <th class="th-sm">Assignee</th>
                      <th class="th-sm">Action</th>
                  </tr>
            </thead>
            <tbody>
                <c:forEach items="${tasks}" var="model">
                  <tr id="${model.id}">
                      <td class="td-sm">${model.id}</td>
                      <td class="td-sm">${model.name}</td>
                      <td class="td-sm">${model.description}</td>
                      <td class="td-sm">${model.assignee}</td>
                      <td class="td-sm"><button style="background: green !important;" class="btn btn-primary btn-cheil" onclick="javascript:fire_ajax_submit('/workflow/completetask/${model.id}')" type="button">Complete Task</button></td>
                   </tr>
               </c:forEach>
           </tbody>
         </table>
    </div>
  </div>
</div>
<%@ include file="tableActions.jsp" %>