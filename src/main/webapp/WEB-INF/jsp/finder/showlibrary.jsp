<%@ include file="../include.jsp" %>
<div class="main-content">
   <div class="row">
      <div class="col-sm-5">
         <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
      </div>
      <div class="col-sm-7"></div>
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
                    <select class="cheil-select" name="subsidiary" id="subsidiary" placeholder="Select Subsidiary" required="required">
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
                   <select class="cheil-select" name="type" id="type" placeholder="Select Error Type" required="required">
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
                            <span class="searchtext">Search Text</span>
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