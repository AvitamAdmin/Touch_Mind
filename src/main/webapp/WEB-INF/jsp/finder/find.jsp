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
                      <th class="th-sm">ID</th>
                      <th class="th-sm">Description</th>
                      <th class="th-sm">Type</th>
                      <th class="th-sm">System Path</th>
                      <th class="th-sm">Subsidiaries</th>
                  </tr>
            </thead>
            <tbody>
                <c:forEach items="${libraries}" var="model">
                  <tr id="${model.id}">
                      <td class="td-sm"><a class="nav-link" style="color:blue;" id="firstmenu" href="#" onclick="javascript:fire_ajax_submit('/admin/library/edits?id=${model.id}&libId=')">${model.id}</td>
                      <td class="td-sm">${model.description}</td>
                      <td class="td-sm">${model.type}</td>
                      <td class="td-sm">${model.errorMsg}</td>
                      <td class="td-sm">${model.subsidiaries}</td>
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
               <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/finder/find" class="handle-upload" modelAttribute="editForm" >
                  <div class="row">
                     <div class="col-sm-12">
                    <select class="cheil-select" name="subsidiary" id="subsidiary" placeholder="Select Subsidiary" >
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
                                       <div class="col-sm-12">
                   <select class="cheil-select" name="type" id="type" placeholder="Select Error Type" >
                   <span>Select Error Type</span>
                   <option value="" >Select Error Type</option>
                      <c:forEach items="${errorTypes}" var="child">
                         <option value="${child}" >${child}</option>
                      </c:forEach>
                   </select>
                   </div>
                                     </div>
                  </br></br>
                  <div class="row">
                        <div class="col-sm-12">
                            <input id="errorMsg" name="errorMsg" class="inputbox-cheil" placeholder="Enter Search Text" />
                            <span class="searchtext">Enter Search Text</span>
                        </div>
                     </div>
                     </br></br>
                     <div class="row">
                        <div id="submitButton" class="col-sm-12">
                            <button class="btn btn-primary add-more" style="float:right;"  aria-controls="tableData" onclick="submitFormById('#editForm');"  type="button"><i class="glyphicon glyphicon-add"></i>Submit</button>
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
             var multipleCancelButton4 = new Choices('#type', {
                             removeItemButton: true,
                             maxItemCount:-1,
                             searchResultLimit:20,
                             renderChoiceLimit:-1
                           });
   });

   $("#import").click(function(){
    $("#myModal").modal('show');
   });

</script>
</div>