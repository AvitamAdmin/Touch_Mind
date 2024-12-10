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
                      <th class="th-sm">Sso Link</th>
                      <th class="th-sm">Link</th>
                      <th class="th-sm">Affiliate id</th>
                      <th class="th-sm">Timestamp</th>
                      <th class="th-sm">Hash</th>
                    </tr>
              </thead>
              <tbody>
                  <c:forEach items="${epps}" var="epp">
                    <tr id="${model.id}">
                        <td class="td-sm"><a href='${epp.ssoLink}' target='_blank' style='color:blue;'>SSO Link</a></td>
                        <td class="td-sm">${epp.disabledLink}</td>
                        <td class="td-sm">${epp.affiliateId}</td>
                        <td class="td-sm">${epp.timestamp}</td>
                        <td class="td-sm">${epp.hash}</td>
                     </tr>
                 </c:forEach>
             </tbody>
         </table>
    </div>
  </div>
</div>
<%@ include file="tableActioneppsso.jsp" %>
<div id="myModal" class="modal fade" tabindex="-1">
      <div class="modal-dialog">
         <div class="modal-content">
            <div class="modal-header">
                EPP SSO-Link Generator
               <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
               <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/eppsso/generate" class="handle-upload" modelAttribute="editForm" >
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
                     <div class="col-sm-12" id="siteDiv">
                        <select class="cheil-select" name="sites[]" placeholder="Select Sites" multiple id="selectpicker">
                        <span>Select Sites</span>
                        </select>
                     </div>
                  </div>
                  <br/>
                    <br/>
                    <div class="row">
                       <div class="col-sm-12" id="siteDiv">
                          <input type="datetime-local" name="ssoDate" class="inputbox-cheil" placeholder="Input date"  autocomplete="off"/>
                          <span>Select date</span>
                          </select>
                       </div>
                    </div>
                  </br></br>
                     <div class="row">
                        <div style="display:none;" id="submitButton" class="col-sm-12">
                            <button class="btn btn-primary add-more" style="float:right;"  aria-controls="tableData" onclick="submitFormById('#editForm');"  type="button"><i class="glyphicon glyphicon-add"></i>Submit</button>
                        </div>
                         <input id="timeZone" style="display:none;" name="timeZone" class="inputbox-cheil" placeholder="Enter Voucher Code" />
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
   });

   $("#import").click(function(){
    $("#myModal").modal('show');
   });

   $("#subsidiary").change(function(){
   $("#timeZone").val(Intl.DateTimeFormat().resolvedOptions().timeZone);
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
                            "value" : data[i].id,
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
</script>
</div>