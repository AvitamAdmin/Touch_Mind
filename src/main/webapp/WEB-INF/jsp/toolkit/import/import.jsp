<%@ include file="../../include.jsp" %>
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
            <button class="btn btn-primary btn-icon btn-icon-small" title="Shortcuts" id="import" tabindex="0" aria-controls="tableData" type="button">Shortcuts</button>
            <button class="btn btn-primary btn-icon btn-icon-small" title="Import SKUs" id="modalBtn" tabindex="0" aria-controls="tableData" type="button">Import</button>
            <button class="btn btn-primary btn-icon btn-icon-small" title="Refresh" tabindex="0" aria-controls="tableData" type="button">Refresh</button>
            <button class="btn btn-primary btn-icon btn-icon-small" title="Save" tabindex="0" aria-controls="tableData" type="button">Save</button>
         </div>
      </div>
      <div class="col-sm-7"></div>
   </div>
   <div id="myModal" class="modal fade" tabindex="-1">
      <div class="modal-dialog">
         <div class="modal-content">
            <div class="modal-header">
                Shortcuts
               <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
               <form:form method="POST" id="editForm" enctype="multipart/form-data" action="${reportUrl}" class="handle-upload" modelAttribute="editForm" >
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
                     <div class="col-sm-12" id="siteDiv">
                        <select class="cheil-select" name="sites[]" placeholder="Select Sites" multiple id="selectpicker" required="required">
                        <span>Select Sites</span>
                        </select>
                     </div>
                  </div>
                  </br></br>
                  <c:if test="${enableCategory}">
                   <div class="row">
                        <div class="col-sm-12" id="categoryDiv" style="display:none;">
                       <select class="cheil-select" name="category" id="category" placeholder="Select Category" >
                        </select>
                        </div>
                     </div>
                   </br></br>
               </c:if>
                  <c:if test="${enableVoucher}">
                       <div class="row">
                          <div class="col-sm-12" id="voucher">
                              <input id="voucherBox" name="voucherCode" class="inputbox-cheil" placeholder="Enter Voucher Code" />
                              <span class="searchtext">Voucher Code</span>
                          </div>
                       </div>
                       </br></br>
                   </c:if>
                   <c:if test="${enableCurrentPage}">
                      <div class="row">
                         <div class="col-sm-12" id="currentPage">
                             <input id="voucherBox" name="currentPage" class="inputbox-cheil" placeholder="Enter current page" />
                             <span class="searchtext">Current page</span>
                         </div>
                      </div>
                      </br></br>
                  </c:if>
                     <c:if test="${enableSkus}">
                          <div class="row">
                             <div class="col-sm-7">
                                 <textarea id="skus2" name="skus2" rows="10" cols="53" class="inputbox-cheil-textarea" placeholder="please enter teamVariant2"></textarea>
                                 <span class="searchtext">Please enter temvariant2</span>
                                 <form:errors path="skus2" class="text-danger"></form:errors>
                              </div>
                              <div class="col-sm-5"></div>
                          </div>
                          </br></br>
                      </c:if>
                   <c:if test="${enableVariant}">
                  <div class="row">
                     <div class="col-sm-12" id="shortcutDiv" style="display:none;">
                        <select name="shortcuts[]" class="expanded" placeholder="Select Models" multiple id="shortcutsVal">

                        </select>
                     </div>
                  </div>
                  </c:if>
                  <br/>
                  <br/>
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
   <div id="myModal3" class="modal fade" tabindex="-1">
      <div class="modal-dialog">
         <div class="modal-content">
            <div class="modal-header">
            Upload
               <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
               <form:form method="POST" id="uploadForm" enctype="multipart/form-data" action="/toolkit/stockReport/upload" class="handle-upload" modelAttribute="uploadForm">
                  <input type="file" name="file" /><br/><br/>
                  <input type="submit" value="Upload" onClick="ajaxformSubmit('uploadForm');"/>
               </form:form>
            </div>
         </div>
      </div>
   </div>
   <div id="myModal2" class="modal fade" tabindex="-1">
      <div class="modal-dialog">
         <div class="modal-content">
            <div class="modal-header">
                Import SKUs
               <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
               <form:form method="POST" id="editFormImport" enctype="multipart/form-data" action="${reportUrl}" class="handle-upload" modelAttribute="editForm" >
               <div class="row">
                  <div class="col-sm-12">
                      <select class="cheil-select" name="subsidiary" id="subsidiary2"  placeholder="Select Subsidiary" >
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
                     <div class="col-sm-12" id="site2Div">
                        <select class="cheil-select" name="sites[]" placeholder="Select Sites" multiple id="selectpicker2">
                        <span>Select Sites</span>

                        </select>
                     </div>
                  </div>
                  </br></br>
                  <c:if test="${enableToggle}">
                  Bundle
                  <div class="row">


                  <div class="col-sm-6">
                  <label class="switch">
                    <input type="checkbox" name="bundle">
                    <span class="slider round"></span>
                  </label>
                  </div>
                  <div class="col-sm-6"></div>
                  </div>
                  </br></br>
                  </c:if>
                  <c:if test="${enableCategory}">
                   <div class="row">
                        <div class="col-sm-12" id="categoryDiv2" style="display:none;">
                       <select class="cheil-select" name="category" id="category2" placeholder="Select Category" >
                        </select>
                        </div>
                     </div>
                     </br></br>
               </c:if>

                  <c:if test="${enableVoucher}">
                     <div class="row">
                        <div class="col-sm-12" id="voucher">
                            <input id="voucherBox" name="voucherCode" class="inputbox-cheil" placeholder="Enter Voucher Code" />
                            <span class="searchtext">Voucher Code</span>
                        </div>
                     </div>
                     </br></br>
                 </c:if>
                 <c:if test="${enableCurrentPage}">
                    <div class="row">
                       <div class="col-sm-12" id="currentPage">
                           <input id="voucherBox" name="currentPage" class="inputbox-cheil" placeholder="Enter current page" />
                           <span class="searchtext">Current page</span>
                       </div>
                    </div>
                    </br></br>
                </c:if>
                <c:if test="${enableVariant}">
                  <div class="row">
                     <div class="col-sm-7">
                        <textarea id="skus" class="inputbox-cheil-textarea-report" name="skus" rows="10" cols="53" placeholder="Please enter SKU's"></textarea>
                        <span class="searchtext">Please enter SKU's</span>
                        <form:errors path="skus" class="text-danger"></form:errors>
                     </div>
                     <div class="col-sm-5"></div>
                  </div>
                  </c:if>
                  </br></br>
                   <c:if test="${enableSkus}">
                        <div class="row">
                           <div class="col-sm-7">
                               <textarea id="skus2" class="inputbox-cheil-textarea" name="skus2" rows="10" cols="53" placeholder="Please enter teamVariant2"></textarea>
                               <span class="searchtext">Please enter temvariant2</span>
                               <form:errors path="skus2" class="text-danger"></form:errors>
                            </div>
                            <div class="col-sm-5"></div>
                        </div>
                        </br></br>
                    </c:if>
                  <div class="row">
                     <div style="display:none;" id="submitButton2" class="col-sm-12"><button class="btn btn-primary add-more" style="float:right;"  aria-controls="tableData"  onclick="submitFormById('#editFormImport');"  type="button"><i class="glyphicon glyphicon-add"></i>Submit</button>
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
   });

   $("#import").click(function(){
   $("#myModal").modal('show');
   });
   $("#modalBtn").click(function(){
    $("#myModal2").modal('show');
   });
   $("#uploadBtn").click(function(){
       $("#myModal3").modal('show');
   });

   $("#selectpicker").change(function(){
   $('#submitButton').show();
   });
   $("#selectpicker2").change(function(){
      $('#submitButton2').show();
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
      $("#subsidiary2").change(function(){
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
                                "label" : data[i].siteId
                            });
                    }
                    sitesData2.clearStore();
                    sitesData2.setChoices(jsonData,'value','label', true);
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
                    url: "/toolkit/import/getCategoriesForSubsidiary/" + categoryId,
                    datatype: "json",
                    success: function(data){
                    $('#categoryDiv').show();
                    var categoryData=$('#category'), option="";
                    categoryData.empty();
                      for(var i=0; i<data.length; i++){
                          option = option + "<option value='"+data[i].categoryId + "'>"+data[i].shortDescription + "</option>";
                      }
                  categoryData.append(option);
                    },
                    error:function(e){
                        console.log(e.statusText);
                    }
                });
            });
$("#subsidiary2").change(function(){
    var categoryId = $(this).val();
    $.ajax({
        type: 'GET',
        url: "/toolkit/import/getCategoriesForSubsidiary/" + categoryId,
        datatype: "json",
        success: function(data){
        $('#categoryDiv2').show();
        var categoryData=$('#category2'), option="";
        categoryData.empty();
          for(var i=0; i<data.length; i++){
              option = option + "<option value='"+data[i].categoryId + "'>"+data[i].shortDescription + "</option>";
          }
      categoryData.append(option);
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
                    url: "/toolkit/import/getModelForSubsidiary/" + categoryId,
                    datatype: "json",
                    success: function(data){
                    $('#shortcutDiv').show();
                    var slctSubcat = $('#shortcutsVal'), options="";
                    slctSubcat.empty();
                      for(var i=0; i<data.length; i++){
                          options = options + "<option value='"+data[i].id + "'>"+data[i].shortDescription + "</option>";
                      }
                  slctSubcat.append(options);
                    },
                    error:function(e){
                        console.log(e.statusText);
                    }
                });
            });

</script>
</div>