<%@ include file="../include.jsp" %>
<div class="main-content">
<div class="row">
    <div class="col-sm-12">
        <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
    </div>
</div>
   <div class="row" id="cheil-row">
      <div class="col-sm-5">
         <div class="dt-buttons">
            <button class="btn btn-primary btn-icon btn-icon-small" title="Import" id="import" tabindex="0" aria-controls="tableData" type="button">Find</button>
            <button class="btn btn-primary btn-icon btn-icon-small" title="Refresh" tabindex="0" aria-controls="tableData" type="button">Refresh</button>
         </div>
      </div>
      <div class="col-sm-7"></div>
   </div>
  <div class="row">
    <div class="col-sm-12">
         <table id="tableData" class="table table-striped table-bordered table-sm" cellspacing="0" width="100%" data-tableName="Stock Data">
              <input type="hidden" id="rowSelectorId" name="rowSelectorId" value="">
              <thead>
                  <tr>
                      <th class="th-sm">Promotion ID</th>
                      <th class="th-sm">Version</th>
                      <th class="th-sm">Condition</th>
                      <th class="th-sm">Actions</th>
                  </tr>
            </thead>
            <tbody>
                <c:forEach items="${promotions}" var="model">
                  <tr id="${model.id}">
                      <td class="td-sm">${model.id}</td>
                      <td class="td-sm">${model.version}</td>
                      <td class="td-sm">${model.condition}</td>
                      <td class="td-sm">${model.actions}</td>
                   </tr>
               </c:forEach>
           </tbody>
         </table>
    </div>
  </div>
</div>
<%@ include file="tableAction.jsp" %>
<div id="myModal" class="modal fade" tabindex="-1">
      <div class="modal-dialog">
         <div class="modal-content">
            <div class="modal-header">
                Finder
               <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
               <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/finder/findpromotion" class="handle-upload" modelAttribute="editForm" >
                  <div class="row">
                                    <div class="col-sm-12">
                                        <select class="cheil-select" name="subsidiary" id="subsidiary"  placeholder="Select Subsidiary" >
                                        <span>Select Subsidiary</span>
                                        <option value="" >Select Subsidiary</option>
                                           <c:forEach items="${subsidiaries}" var="child">
                                              <option value="${child.id}" >${child.identifier}</option>
                                           </c:forEach>
                                        </select>
                                       </div>
                                    </div>
                                    <br/>
                                    <br/>
                                    <div class="row">
                                       <div class="col-sm-12" id="siteDiv">
                                          <select class="cheil-select" name="sites[]" placeholder="Select Sites" multiple id="selectpicker">
                                          <span>Select Sites</span>

                                          </select>
                                       </div>
                                    </div>
                                    </br></br>
                                    <div class="row">
                                       <div class="col-sm-7">
                                          <textarea id="skus" name="skus" rows="10" cols="53" class="inputbox-cheil-textarea-report" placeholder="Please enter SKU's"></textarea>
                                          <span class="searchtext">Please enter SKU's</span>
                                          <form:errors path="skus" class="text-danger"></form:errors>
                                       </div>
                                       <div class="col-sm-5"></div>
                                    </div>
                                    </br></br>
                                    <div class="row">
                                       <div class="col-sm-12">
                                          <form:input type="text" path="fromDate" id="datepicker" class="inputbox-cheil" placeholder="From Date"  />
                                          <span class="searchtext">From Date</span>
                                       </div>
                                    </div>
                                    </br></br>
                                     <div class="row">
                                       <div class="col-sm-12">
                                          <form:input type="text" path="toDate" id="datepicker2" class="inputbox-cheil" placeholder="To Date"  />
                                          <span class="searchtext">To Date</span>
                                       </div>
                                    </div>
                                    </br></br>
                                    <div class="row">
                                       <div style="display:none;" id="submitButton" class="col-sm-12"><button class="btn btn-primary add-more" style="float:right;"  aria-controls="tableData"  onclick="submitFormById('#editForm');"  type="button"><i class="glyphicon glyphicon-add"></i>Submit</button>
                                       </div>
                                    </div>
                              </div>
               </form:form>
            </div>
         </div>
      </div>
   </div>
<script type="text/javascript">
var sitesData = '';
var sitesData2 = '';
   $(document).ready(function() {
   $("#datepicker").datepicker({dateFormat:"yy-mm-dd"});
   $("#datepicker2").datepicker({dateFormat:"yy-mm-dd"});
   $(".modal-backdrop").removeClass('modal-backdrop fade show');
      $('.content-wrapper').addClass('toolkit');
        sitesData = new Choices('#selectpicker', {
                       removeItemButton: true,
                       maxItemCount:-1,
                       searchResultLimit:20,
                       renderChoiceLimit:-1
                     });
       sitesData2 = new Choices('#selectpicker2', {
               removeItemButton: true,
               maxItemCount:-1,
               searchResultLimit:20,
               renderChoiceLimit:-1
             });
       var multipleCancelButton3 = new Choices('#subsidiary', {
                       removeItemButton: true,
                       maxItemCount:-1,
                       searchResultLimit:20,
                       renderChoiceLimit:-1
                     });
   });

   $("#subsidiary").change(function(){
             var categoryId = $(this).val();
             $.ajax({
                 type: 'GET',
                 url: "/toolkit/import/getSitesForSubsidiary/" + categoryId,
                 datatype: "json",
                 success: function(data){
                 $('#submitButton').show();
                 var jsonData = [];
                   for(var i=0; i<data.length; i++){
                       jsonData.push({
                               "value" : data[i].siteId,
                               "label" : data[i].siteId
                           });
                   }
                   sitesData.clearStore();
                   sitesData.setChoices(jsonData,'value','label', true);
                 },
                 error:function(e){
                     console.log(e.statusText);
                 }
             });
         });

   $("#import").click(function(){
    $("#myModal").modal('show');
   });

</script>
</div>