<%@ include file="../../include.jsp" %>
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
                    <th class="th-sm">Id</th>
                    <th class="th-sm">Identifier</th>
                    <th class="th-sm">Description</th>
                    <th class="th-sm">Test Plan</th>
                    <th class="th-sm">Failure condition</th>
                    <th class="th-sm">Status</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${models}" var="model">
                    <tr id="${model.id}">
                        <td class="td-sm">${model.recordId}</td>
                        <td class="td-sm">${model.identifier}</td>
                        <td class="td-sm">${model.description}</td>
                        <td class="td-sm">${model.testPlanId}</td>
                        <td class="td-sm">${model.percentFailure}</td>
                        <td class="td-sm">${model.status}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>
<%@ include file="../../tableActions.jsp" %>
<div id="myModalEdit" class="modal fade" tabindex="-1">
      <div class="modal-dialog-full" style="margin-left:200px;margin-top:100px;">
         <div class="modal-content" style="height:65%;margin-left:20px !important">
            <div class="modal-body" id="editModelContent">

            </div>
         </div>
      </div>
</div>
