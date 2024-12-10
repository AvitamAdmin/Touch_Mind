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
                      <th class="th-sm">Server Node</th>
                      <th class="th-sm">Subs</th>
                      <th class="th-sm">Site</th>
                      <th class="th-sm">Test Plan</th>
                      <th class="th-sm">Test case</th>
                      <th class="th-sm">SKU</th>
                      <th class="th-sm">Status</th>
                      <th class="th-sm">Error</th>
                      <th class="th-sm">Runner</th>
                      <th class="th-sm">Timestamp</th>
                    </tr>
              </thead>
              <tbody>

             </tbody>
         </table>
    </div>
  </div>

<%@ include file="../tableAction-qaentry.jsp" %>