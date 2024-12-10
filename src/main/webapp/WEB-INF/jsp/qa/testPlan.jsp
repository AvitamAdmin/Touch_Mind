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
            <table id="tableData" class="table table-striped table-bordered table-sm" cellspacing="0" width="100%" data-tableName="Test Plans Data">
                <input type="hidden" id="rowSelectorId" name="rowSelectorId" value="">
                <thead>
                <tr>
                    <th class="th-sm">Id</th>
                    <th class="th-sm">Identifier</th>
                    <th class="th-sm">Short Desc</th>
                    <th class="th-sm">Subsidiary</th>
                    <th class="th-sm">Status</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${models}" var="model">
                    <tr id="${model.id}">
                        <td class="td-sm">${model.recordId}</td>
                        <td class="td-sm">${model.identifier}</td>
                        <td class="td-sm">${model.shortDescription}</td>
                        <td class="td-sm">${model.subsidiary}</td>
                        <c:choose>
                            <c:when test="${model.status}">
                                 <c:set var="varChecked" value="Active"></c:set>
                             </c:when>
                             <c:otherwise>
                                 <c:set var="varChecked" value="Inactive"></c:set>
                             </c:otherwise>
                        </c:choose>
                        <td class="td-sm">${varChecked}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>
<%@ include file="../tableActions.jsp" %>
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
