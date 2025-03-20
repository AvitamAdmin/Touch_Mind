<div class="row">
    <div class="col-sm-12">
        <p style="font-size:22px;font-weight:bolder;">Pre-condition actions</p>
    </div>
</div>
  <div class="row">
    <div class="col-sm-12">
         <table id="tableDatarelated" class="table table-striped table-bordered table-sm" cellspacing="0" width="100%" data-tableName="Stock Data">
              <input type="hidden" id="rowSelectorAction" name="" value="">
              <thead>
                  <tr>
                      <th class="th-sm">ID</th>
                      <th class="th-sm">action</th>
                      <th class="th-sm">system</th>
                      <th class="th-sm">system path</th>
                      <th class="th-sm">subsidiary</th>
                  </tr>
            </thead>
            <tbody>
                <c:forEach items="${actionsList}" var="model">
                  <tr id="${model.id}">
                      <td class="td-sm">${model.id}</td>
                      <td class="td-sm">${model.shortDescription}</td>
                      <td class="td-sm">${model.system.shortDescription}</td>
                      <td class="td-sm">${model.systemPath}</td>
                      <td class="td-sm">${model.subsidiaries}</td>
                   </tr>
               </c:forEach>
           </tbody>
         </table>
    </div>
  </div>
<br/>
<br/>
<%@ include file="tableActionsrelated.jsp" %>