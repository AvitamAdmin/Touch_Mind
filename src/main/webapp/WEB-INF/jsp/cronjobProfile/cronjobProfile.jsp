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
            <table id="tableData" class="table table-striped table-bordered table-sm" cellspacing="0" width="100%" data-tableName="Cron job profiles">
                <input type="hidden" id="rowSelectorId" name="rowSelectorId" value="">
                <thead>
                <tr>
                    <th class="th-sm">Id</th>
                    <th class="th-sm">Identifier</th>
                    <th class="th-sm">Recipients</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${models}" var="model">
                    <tr id="${model.id}">
                        <td class="td-sm">${model.recordId}</td>
                        <td class="td-sm">${model.identifier}</td>
                        <td class="td-sm">${model.recipients}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>
<%@ include file="../tableActions.jsp" %>
