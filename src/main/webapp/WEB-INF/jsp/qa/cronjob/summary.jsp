<%@ include file="../include.jsp" %>
<div class="main-content">
<div class="row">
    <div class="col-sm-12">
        <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
    </div>
</div>
  <div class="row">
    <div class="col-sm-12">
    <div><p style="color: blue;font-weight: bold; font-size: 18px;"> Cronjob Performance </p></div>
        </br>
         <table id="tableData" class="table table-striped table-bordered table-sm" cellspacing="0" width="100%" data-tableName="Stock Data">
              <input type="hidden" id="rowSelectorId" name="rowSelectorId" value="">
              <thead>
                    <tr>
                      <th class="th-sm">Last Run-Time</th>
                      <th class="th-sm">Subsidiaries</th>
                      <th class="th-sm">Schedulers</th>
                      <th class="th-sm">Result</th>
                      <th class="th-sm">Issued Cases</th>
                      <th class="th-sm">Email to</th>
                    </tr>
              </thead>
              <tbody>
                  <c:forEach items="${cronJobs}" var="model">
                        <td class="td-sm">${model.jobTime}</td>
                        <td class="td-sm">${model.subsidiary}</td>
                        <td class="td-sm">${model.scheduler}</td>
                        <td class="td-sm">${model.cronStatus}</td>
                        <td class="td-sm">${model.processedSkus} SKUS</td>
                        <td class="td-sm">${model.email}</td>
                        </tr>
                 </c:forEach>
             </tbody>
         </table>
    </div>
  </div>
</div>
<%@ include file="tableAction.jsp" %>
