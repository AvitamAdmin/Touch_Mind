<%@ include file="../include.jsp" %>
<div class="main-content">
<div class="row">
    <div class="col-sm-12">
        <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
    </div>
</div>
<p id="fileNameId" style="display:none;">${fileName}</p>
  <div class="row">
    <div class="col-sm-12">
         <table id="tableData" class="table table-striped table-bordered table-sm" cellspacing="0" width="100%" data-tableName="Stock Data">
              <input type="hidden" id="rowSelectorId" name="rowSelectorId" value="">
              <thead>
                    <tr>
                      <c:forEach items="${columnHeaders}" var="headVal">
                      <th class="th-sm">${headVal}</th>
                      </c:forEach>
                    </tr>
              </thead>
              <tbody>
               <c:forEach items="${valuesMap}" var="result">
                     <tr>
                     <c:forEach items="${result.value}" var="model">
                        <td class="td-sm">${model}</td>
                     </c:forEach>
                     </tr>
               </c:forEach>
             </tbody>

         </table>
    </div>
  </div>
<%@ include file="tableAction.jsp" %>

</div>