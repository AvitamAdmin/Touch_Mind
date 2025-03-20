<%@ include file="../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-12">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
    </div>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/action/edit" class="handle-upload" modelAttribute="editForm" >
        <div class="row">
            <div class="col-sm-12">
                <div class="dt-buttons">
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/action')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" title="Save" type="submit">Save</button>
                </div>
            </div>
        </div>
        <%@ include file="../commonFields.jsp" %>
        <form:input path="recordId" class="inputbox-cheil-small" placeholder="Record Id" style="display:none" type="hidden"/>
        <input type="hidden" id="rowSelectorIdRelatedAction" name="relatedActions" value="">
        <div class="row">
            <div class="col-sm-3">
                <form:input path="id" class="inputbox-cheil-small" placeholder="Enter ID without space" required="required"/>
                <span>Action ID</span>
                <form:errors path="errorType" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                <form:input path="identifier" class="inputbox-cheil-small" placeholder="Enter Identifier" required="required"/>
                <span>Identifier</span>
                <form:errors path="errorType" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                <form:input path="ShortDescription" class="inputbox-cheil-long" placeholder="Enter a short description" required="required" />
                <span>Short description</span>
                <form:errors path="errorType" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                <form:input type="email" path="picEmail" class="inputbox-cheil" placeholder="Enter PIC email" />
                <span>PIC email</span>
                <form:errors path="errorType" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                 <select class="cheil-select" name="toolkitId" id="selectpicker">
                    <option value="">Select toolkits</option>
                    <c:forEach items="${nodes}" var="child">
                        <c:choose>
                            <c:when test="${editForm.toolkitId eq child.id}">
                              <option value="${child.id}" selected>${child.identifier}</option>
                            </c:when>
                            <c:otherwise>
                               <option value="${child.id}" >${child.identifier}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
            </div>

        </div>
        <br/>
        <br/>
        <div class="row">
                    <div class="col-sm-12">
                          <select name="subAndSites[]" placeholder="Select subsidiaries" multiple="multiple" id="selectpicker6">
                            <c:forEach items="${subsidiaries}" var="child">
                            <optgroup label="${child.key}">
                              <c:forEach items="${child.value}" var="site">
                              <c:choose>
                                    <c:when test="${fn:contains( editForm.sites, site.recordId ) && fn:contains( editForm.subsidiaries, child.key ) }">
                                      <option value="${child.key}-${site.recordId}" selected>${site.identifier}</option>
                                    </c:when>
                                    <c:otherwise>
                                       <option value="${child.key}-${site.recordId}" >${site.identifier}</option>
                                    </c:otherwise>
                                </c:choose>
                              </c:forEach>
                            </optgroup>
                               </c:forEach>
                          </select>
                      </div>
                </div>
        <br/>
        <br/>
        <div class="row">
            <div class="col-sm-3">
                  <select name="systemId"  id="systemId" placeholder="Select System" required="required">
                  <option value="">Select system</option>
                     <c:forEach items="${systems}" var="child">
                         <c:choose>
                             <c:when test="${editForm.systemId eq child.recordId}">
                               <option value="${child.recordId}" selected>${child.shortDescription}</option>
                             </c:when>
                             <c:otherwise>
                                <option value="${child.recordId}" >${child.shortDescription}</option>
                             </c:otherwise>
                         </c:choose>
                     </c:forEach>
                 </select>
              </div>
               <div class="col-sm-3">
                  <select class="cheil-select" id="roleId" name="role" placeholder="Select Role">
                  <option value="">Select role </option>
                      <c:forEach items="${roles}" var="role">
                          <c:choose>
                              <c:when test="${editForm.role == role.recordId}">
                                <option value="${role.recordId}" selected>${role.shortDescription}</option>
                              </c:when>
                              <c:otherwise>
                                 <option value="${role.recordId}" >${role.shortDescription}</option>
                              </c:otherwise>
                          </c:choose>
                      </c:forEach>
                  </select>
              </div>
            <div class="col-sm-3">
                 <select name="catalogId" id="catalogId" placeholder="Select Catalog">
                 <option value="">Select catalog</option>
                    <c:forEach items="${catalogs}" var="child">
                        <c:choose>
                            <c:when test="${editForm.catalogId eq child.recordId}">
                              <option value="${child.recordId}" selected>${child.shortDescription}</option>
                            </c:when>
                            <c:otherwise>
                               <option value="${child.recordId}" >${child.shortDescription}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
             </div>

              <div class="col-sm-3">
                   <select name="moduleId" id="moduleId" placeholder="Select Module">
                   <option value="">Select module</option>
                      <c:forEach items="${modules}" var="child">
                          <c:choose>
                              <c:when test="${editForm.moduleId eq child.recordId}">
                                <option value="${child.recordId}" selected>${child.shortDescription}</option>
                              </c:when>
                              <c:otherwise>
                                 <option value="${child.recordId}" >${child.shortDescription}</option>
                              </c:otherwise>
                          </c:choose>
                      </c:forEach>
                  </select>
               </div>
        </div>
        <br/>
        <br/>
        <div class="row">
            <div class="col-sm-12">
                <form:textarea id="summernote" rows="5" path="systemPath" class="inputbox-cheil-textarea" placeholder="Enter the system path" />
                <form:errors path="errorType" class="text-danger"></form:errors>
            </div>
        </div>
        <br/>
        <br/>
        <div class="row">
                    <div class="col-sm-12 col-sm-full">
                        <form:textarea rows="5" path="longDescription" class="inputbox-cheil-textarea" placeholder="Enter the long description or notes or remarks" />
                        <span>Long description</span>
                        <form:errors path="errorType" class="text-danger"></form:errors>
                    </div>

                </div>
                <br/>
                        <br/>

  <%@ include file="relatedaction.jsp" %>
                                <br/>
<div class="row">
        <div class="col-sm-6">
                <p style="font-size:22px;font-weight:bolder;">Media</p><form:input type="file" name="files" path="files" multiple="multiple" />
            </div>

        </div>
       </form:form>
       <c:if test="${not empty message}">
           <div class="alert alert-danger" role="alert" id="errorMessage">
               <spring:message code="${message}" />
           </div>
       </c:if>
    </div>
  </div>
</div>
<script type="text/javascript">
   $(document).ready(function() {
      $('#summernote').summernote({
           placeholder: 'Enter the system path',
           tabsize: 2,
           toolbar: [
                       [ 'style', [ 'style' ] ],
                       [ 'font', [ 'bold', 'italic', 'underline', 'clear'] ],
                       [ 'fontname', [ 'fontname' ] ],
                       [ 'fontsize', [ 'fontsize' ] ],
                       [ 'color', [ 'color' ] ],
                       [ 'para', [ 'ol', 'ul', 'paragraph', 'height' ] ],
                       [ 'table', [ 'table' ] ],
                   ]
         });
                     $("#addBtnRelatedAction").click(function(){
                            $("#myRelatedActionModal").modal('show');
                            });

$('#selectpicker6').multiselect({
                    enableClickableOptGroups: true,
                    enableCollapsibleOptGroups: true,
                    collapseOptGroupsByDefault:true,
                    enableFiltering: true,
                    includeSelectAllOption: true,
                    buttonWidth: '100%',
                    resetText: "Reset all",
                    includeResetOption: true,
                    includeResetDivider: true,
                    enableFiltering: true,
                    dropUp: true,
                    enableHTML: true,
                    buttonText: function(options, select) {
                            var $select = $(select);
                            var $optgroups = $('optgroup', $select);
                                    if (options.length === 0) {
                                        return 'Select subsidiaries';
                                    }
                                     else {
                                        var text='';
                                         $optgroups.each(function() {
                                              var $selectedOptions = $('option:selected', this);
                                              var $options = $('option', this);
                                              if ($selectedOptions.length == $options.length) {
                                                  text += '<span class="boot-choices">' + $(this).attr('label') + '|' + 'All Sites ' + '</span>';
                                              }
                                              else if($selectedOptions.length > 0) {
                                                  text += '<span class="boot-choices">' + $(this).attr('label') + '|';
                                                  $selectedOptions.each(function() {
                                                      text += $(this).text() + ',';
                                                  });
                                                  text = text.substr(0, text.length - 1);
                                                  text += '</span>';
                                              }
                                          });
                                          return text;
                                     }
                                }
                });
   });
   $("#systemId").change(function(){
         var categoryId = $(this).val();
             $.ajax({
                 type: 'GET',
                 url: "/admin/action/getCatalogsForSystem/" + categoryId,
                 datatype: "json",
                 success: function(data){
                 var toAppend='<option value="">Select Catalog</option>>';
                   for(var i=0; i<data.length; i++){
                          toAppend+='<option value="'+data[i].id+'">'+ data[i].shortDescription +'</option>>'
                   }
                   $("#catalogId").empty().append(toAppend);
                 },
                 error:function(e){
                     console.log(e.statusText);
                 }
             });
         });
    $("#systemId").change(function(){
         var categoryId = $(this).val();
             $.ajax({
                 type: 'GET',
                 url: "/admin/action/getModulesForSystem/" + categoryId,
                 datatype: "json",
                 success: function(data){
                 var toAppend='<option value="">Select Module</option>>';
                   for(var i=0; i<data.length; i++){
                          toAppend+='<option value="'+data[i].id+'">'+ data[i].shortDescription +'</option>>'
                   }
                 $("#moduleId").empty().append(toAppend);
                 },
                 error:function(e){
                     console.log(e.statusText);
                 }
             });
         });
         $("#systemId").change(function(){
                  var categoryId = $(this).val();
                      $.ajax({
                          type: 'GET',
                          url: "/admin/action/getRolesForSystem/" + categoryId,
                          datatype: "json",
                          success: function(data){
                          var toAppend='<option value="">Select Role</option>>';
                            for(var i=0; i<data.length; i++){
                                   toAppend+='<option value="'+data[i].id+'">'+ data[i].shortDescription +'</option>>'
                            }
                          $("#roleId").empty().append(toAppend);
                          },
                          error:function(e){
                              console.log(e.statusText);
                          }
                      });
                  });

   </script>
      <br/>
      <%@ include file="medias.jsp" %>


      <div id="myRelatedActionModal" class="modal" tabindex="-1">
            <div class="modal-dialog-full">
               <div class="modal-content" style="height:65%;margin-left:20px !important">
                  <div class="modal-header" style="border-bottom:none;">
                      Add Related Actions
                     <button type="button" class="close" data-dismiss="modal">&times;</button>
                  </div>
                  <div class="modal-body">
                     <%@ include file="actionpopup.jsp" %>
                  </div>
               </div>
            </div>
         </div>
         <script type="text/javascript">
               var multipleCancelButton = new Choices('#selectpicker', {
                   removeItemButton: true,
                   maxItemCount:-1,
                   searchResultLimit:20,
                   renderChoiceLimit:-1
                 });
                 var multipleCancelButton2 = new Choices('#roleId', {
                    removeItemButton: true,
                    maxItemCount:-1,
                    searchResultLimit:20,
                    renderChoiceLimit:-1
                  });
                  var multipleCancelButton3 = new Choices('#systemId', {
                                      removeItemButton: true,
                                      maxItemCount:-1,
                                      searchResultLimit:20,
                                      renderChoiceLimit:-1
                                    });
                  var multipleCancelButton4 = new Choices('#catalogId', {
                                        removeItemButton: true,
                                        maxItemCount:-1,
                                        searchResultLimit:20,
                                        renderChoiceLimit:-1
                                      });
                  var multipleCancelButton5 = new Choices('#moduleId', {
                                      removeItemButton: true,
                                      maxItemCount:-1,
                                      searchResultLimit:20,
                                      renderChoiceLimit:-1
                                    });
         </script>