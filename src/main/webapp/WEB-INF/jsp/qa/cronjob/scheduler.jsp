<%@ include file="../../include.jsp" %>
<div class="main-content">
<div class="row">
    <div class="col-sm-12">
        <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
    </div>
</div>
  <div class="row">
    <div class="col-sm-12">
         <table id="tableData" class="table table-striped table-bordered table-sm schedulertable" cellspacing="0" width="100%" data-tableName="Stock Data">
              <input type="hidden" id="rowSelectorId" name="rowSelectorId" value="">
              <thead>
                    <tr>
                      <th class="th-sm">PK</th>
                      <th class="th-sm">Scheduler id</th>
                      <th class="th-sm">Cron Expression</th>
                      <th class="th-sm">Node</th>
                      <th class="th-sm">Campaign</th>
                      <th class="th-sm">Status</th>
                      <th class="th-sm">Action</th>
                    </tr>
              </thead>
              <tbody>
                  <c:forEach items="${models}" var="model">
                    <tr id="${model.id}">
                        <td class="td-sm">${model.id}</td>
                        <td class="td-sm">${model.identifier}</td>
                        <td class="td-sm">${model.cronExpression}</td>
                        <td class="td-sm">${model.node}</td>
                        <td class="td-sm">${model.shopCampaign}</td>
                        <td class="td-sm">${model.jobStatus}</td>
                        <c:choose>
                            <c:when test="${model.jobStatus=='Stopped'}">
                                <td class="td-sm"><button style="background: green !important;" class="btn btn-primary btn-cheil" onclick="fire_ajax_submit('/admin/qaCronJob/startJob?id=${model.id}', 'false')" type="button">Start</button></td>
                            </c:when>
                            <c:otherwise>
                                <td class="td-sm"><button style="background: red !important;" class="btn btn-primary btn-cheil" onclick="fire_ajax_submit('/admin/qaCronJob/stopJob?id=${model.id}&delete=false', 'false')" type="button">Stop</button></td>
                              </c:otherwise>
                         </c:choose>
                    </tr>
                 </c:forEach>
             </tbody>
         </table>
    </div>
  </div>
</div>
<%@ include file="../../tableActions.jsp" %>