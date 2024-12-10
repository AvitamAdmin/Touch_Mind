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
                      <th class="th-sm">Id</th>
                      <th class="th-sm">Identifier</th>
                      <th class="th-sm">Short Desc</th>
                      <th class="th-sm">Test Data Type</th>
                      <th class="th-sm">Subsidiary</th>
                    </tr>
              </thead>
              <tbody>
                  <c:forEach items="${models}" var="model">
                    <tr id="${model.id}">
                        <td class="td-sm">${model.id}</td>
                        <td class="td-sm">${model.identifier}</td>
                        <td class="td-sm">${model.shortDescription}</td>
                        <c:set var="matchFound" value="false" />
                        <c:forEach items="${testDataTypes}" var="child">
                            <c:choose>
                                <c:when test="${model.testDataType == child.id}">
                                    <td class="td-sm">${child.identifier}</td>
                                    <c:set var="matchFound" value="true" />
                                </c:when>
                            </c:choose>
                        </c:forEach>
                        <c:if test="${!matchFound}">
                            <td class="td-sm"></td>
                        </c:if>
                        <td class="td-sm">${model.subsidiaries}</td>
                     </tr>
                 </c:forEach>
             </tbody>
         </table>
    </div>
  </div>
</div>
<%@ include file="../tableActions.jsp" %>