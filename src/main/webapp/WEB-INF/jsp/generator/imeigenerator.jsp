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
                      <th class="th-sm">Imei Number</th>
                    </tr>
              </thead>
              <tbody>
              <c:forEach items="${imeiNumbers}" var="imeiNumber">
                <tr id="${imeiNumber}">
                    <td class="td-sm">${imeiNumber}</td>
                 </tr>
             </c:forEach>

             </tbody>
         </table>
    </div>
  </div>
</div>
<%@ include file="tableActionimei.jsp" %>

</div>