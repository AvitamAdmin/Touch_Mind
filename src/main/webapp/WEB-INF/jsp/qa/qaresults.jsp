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
                      <th class="th-sm">Timestamp</th>
                      <th class="th-sm">TestID</th>
                      <th class="th-sm">Test group</th>
                      <th class="th-sm">Runner</th>
                      <th class="th-sm">Test passed</th>
                      <th class="th-sm">Result</th>
                      <th class="th-sm">Order Number</th>
                    </tr>
              </thead>
              <tbody>

             </tbody>
         </table>
    </div>
  </div>
  <div id="myModal" class="modal fade" tabindex="-1">
        <div class="modal-dialog">
             <div class="modal-content">
                <div class="modal-header">
                    Run Test
                   <button type="button" class="close" data-dismiss="modal">&times;</button>
                </div>
                <div class="modal-body">
                   <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/qa/testPlans" class="handle-upload" modelAttribute="editForm" >
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
                      <div class="row">
                      <div class="col-sm-12">
                           <select class="cheil-select" id="siteIsoCode" name="siteIsoCode" placeholder="Select Site">
                           <span>Select Site</span>
                           <option value="" >Select Site</option>
                               <c:forEach items="${sites}" var="child">
                                  <option value="${child.siteId}" >${child.siteId}</option>
                               </c:forEach>
                           </select>
                      </div>
                      </div>
                      <br/>
                      <div class="row">
                        <div class="col-sm-12">
                             <select class="cheil-select" id="selectProfile" name="testProfile" placeholder="Select Site">
                             span>Select test profile</span>
                             <option value="" >Select test profile</option>
                                 <c:forEach items="${profiles}" var="child">
                                    <option value="${child.recordId}" >${child.identifier}</option>
                                 </c:forEach>
                             </select>
                        </div>
                        </div>
                        <br/>
                      <div class="row">
                         <div class="col-sm-12">
                        <select class="cheil-select" name="environment" id="environment" placeholder="Select Environment" required="required">
                        <span>Select Environment</span>
                        <option value="" >Select Environment</option>
                           <c:forEach items="${environments}" var="child">
                              <option value="${child.recordId}" >${child.identifier}</option>
                           </c:forEach>
                        </select>
                         </div>
                      </div>
                      <br/>
                      <div class="row">
                           <div class="col-sm-12">
                          <select class="cheil-select" name="testPlan" id="selectPlan" placeholder="Select Test Plan" required="required">
                          <span>Select Test Plan</span>
                          <option value="" >Select Test Plan</option>
                             <c:forEach items="${testPlans}" var="child">
                                <option value="${child.recordId}" >${child.identifier}</option>
                             </c:forEach>
                          </select>
                           </div>
                        </div>
                        <br/>
                      <div class="row">
                         <div class="col-sm-12" id="siteDiv" style="display:none;">
                            <select class="cheil-select" name="sites" placeholder="Select Sites" multiple id="selectpicker" required="required">
                            <span>Select Sites</span>
                            </select>
                         </div>
                      </div>
                      </br>
                        Select from the default SKU list
                        <div class="row">
                        <div class="col-sm-6">
                        <label class="switch">
                          <input type="checkbox" name="defaultSku" id="defaultSku">
                          <span class="slider round"></span>
                        </label>
                        </div>
                        <div class="col-sm-6"></div>
                        </div>
                        </br>

                        <div class="row">
                        <div class="col-sm-6">
                         Enable Debug? <form:checkbox style= "width: 40px;height: 20px;" path="isDebug"  id="isDebug"/>
                          </div>
                          <div class="col-sm-6"></div>
                          </div>
                          </br></br>
                        <div class="row" id="enterSku">
                             <div class="col-sm-7">
                                <textarea id="skus" class="inputbox-cheil-textarea-report" name="skus" rows="10" cols="53" placeholder="Please enter SKU's"></textarea>
                                <span class="searchtext">Please enter SKU's</span>
                                <form:errors path="skus" class="text-danger"></form:errors>
                             </div>
                             <div class="col-sm-5"></div>
                          </div>
                      <div class="row" id="shortcutDiv" style="display:none;">
                         <div class="col-sm-12">
                            <select name="shortcuts" class="expanded" placeholder="Select Models" multiple id="shortcutsVal">

                            </select>
                         </div>
                      </div>
                      <br/>
                      <br/>
                      <input id="isCascadePlan" name="isCascadePlan" value="false" style="display:none;">
                         <div class="row">
                            <div style="display:none;" id="submitButton" class="col-sm-12">
                                <button class="btn btn-primary add-more" style="float:right;"  aria-controls="tableData" onclick="submitFormById('#editForm');"  type="button"><i class="glyphicon glyphicon-add"></i>Submit</button>
                            </div>
                      </div>
                   </form:form>
             </div>
          </div>
     </div>
  </div>

  <script type="text/javascript">
  var subsidiary = '';
  var siteIsoCode = '';
  var selectProfile = '';
  var environment = '';
  var selectPlan = '';
     $(document).ready(function() {
     $(".modal-backdrop").removeClass('modal-backdrop fade show');
        $('.content-wrapper').addClass('toolkit');
        subsidiary = new Choices('#subsidiary', {
                 removeItemButton: true,
                 maxItemCount:-1,
                 searchResultLimit:20,
                 renderChoiceLimit:-1
               });
          siteIsoCode = new Choices('#siteIsoCode', {
                 removeItemButton: true,
                 maxItemCount:-1,
                 searchResultLimit:20,
                 renderChoiceLimit:-1
               });
           selectProfile = new Choices('#selectProfile', {
                    removeItemButton: true,
                    maxItemCount:-1,
                    searchResultLimit:20,
                    renderChoiceLimit:-1
                  });
          environment = new Choices('#environment', {
                  removeItemButton: true,
                  maxItemCount:-1,
                  searchResultLimit:20,
                  renderChoiceLimit:-1
                });
         selectPlan = new Choices('#selectPlan', {
                 removeItemButton: true,
                 maxItemCount:-1,
                 searchResultLimit:20,
                 renderChoiceLimit:-1
               });
     });

     $("#defaultSku").on( "click", function() {
       // for a specific one, but you can add a foreach cycle if you need more
       if($('#defaultSku').is(":checked")){
           $("#shortcutDiv").show();
           $("#enterSku").hide();
       }else {
           $("#shortcutDiv").hide();
           $("#enterSku").show();
       }
     });

     $("#import").click(function(){
        $("#myModal").modal('show');
     });
      $("#selectPlan").change(function(){
                  var planId = $(this).val();
                  $('#submitButton').hide();
                  if(planId!=''){
                    $('#submitButton').show();
                  }
              });

     $("#subsidiary").change(function(){
               var categoryId = $(this).val();
               $.ajax({
                   type: 'GET',
                   url: "/toolkit/import/getSitesForSubsidiary/" + categoryId,
                   datatype: "json",
                   success: function(data){
                   var jsonData = [];
                  for(var i=0; i<data.length; i++){
                       jsonData.push({
                          "value" : data[i].siteId,
                          "label" : data[i].identifier
                      });
                  }
                  siteIsoCode.setChoices(jsonData,'value','label', true);

                   },
                   error:function(e){
                       console.log(e.statusText);
                   }
               });
           });

         $("#subsidiary").change(function(){
             var categoryId = $(this).val();
             $.ajax({
                 type: 'GET',
                 url: "/qa/getTestPlanForSubsidiary/" + categoryId,
                 datatype: "json",
                 success: function(data){
                 $('#submitButton').show();
                 var jsonData = [];
                   for(var i=0; i<data.length; i++){
                        jsonData.push({
                           "value" : data[i].recordId,
                           "label" : data[i].identifier
                       });
                   }
               selectPlan.setChoices(jsonData,'value','label', true);
                 },
                 error:function(e){
                     console.log(e.statusText);
                 }
             });
         });

         $("#subsidiary").change(function(){
                      var categoryId = $(this).val();
                      $.ajax({
                          type: 'GET',
                          url: "/qa/getEnvironmentsForSubsidiary/" + categoryId,
                          datatype: "json",
                          success: function(data){
                          var jsonData = [];
                             for(var i=0; i<data.length; i++){
                                  jsonData.push({
                                     "value" : data[i].recordId,
                                     "label" : data[i].identifier
                                 });
                             }
                         environment.setChoices(jsonData,'value','label', true);
                          },
                          error:function(e){
                              console.log(e.statusText);
                          }
                      });
                  });

         $("#subsidiary").change(function(){
              var categoryId = $(this).val();
              $.ajax({
                  type: 'GET',
                  url: "/qa/getTestProfileForSubsidiary/" + categoryId,
                  datatype: "json",
                  success: function(data){
                  var jsonData = [];
                         for(var i=0; i<data.length; i++){
                              jsonData.push({
                                 "value" : data[i].recordId,
                                 "label" : data[i].identifier
                             });
                         }
                     selectProfile.setChoices(jsonData,'value','label', true);
                  },
                  error:function(e){
                      console.log(e.statusText);
                  }
              });
          });
  </script>
</div>
<%@ include file="../tableAction-qa.jsp" %>