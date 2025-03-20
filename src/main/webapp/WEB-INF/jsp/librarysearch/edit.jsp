<%@ include file="../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-12">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
    </div>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/library/edit" class="handle-upload" modelAttribute="editForm" >
        <div class="row">
            <div class="col-sm-12">
                <div class="dt-buttons">
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/library')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" title="Save" type="submit">Save</button>
                </div>
            </div>
        </div>
        <%@ include file="../commonFields.jsp" %>
        <form:input path="recordId" class="inputbox-cheil-small" placeholder="Record Id" style="display:none" type="hidden"/>
        <input type="hidden" id="rowSelectorIdAction" name="actions" value="">
        <input type="hidden" id="rowSelectorAction" name="" value="">
        <input type="hidden" id="rowSelectorIdSubLibrary" name="subLibraries" value="">
        <input type="hidden" id="rowSelectorSubLibrary" name="" value="">
        <input type="hidden" id="rowSelectorIdMedia" name="medias" value="">
        <input type="hidden" id="actionRemarks" name="remarks" value="">

        <div class="row">
            <div class="col-sm-3">
                <form:input path="identifier" id="identifier" class="inputbox-cheil" placeholder="Identifier" />
                <span>Identifier</span>
                <form:errors path="identifier" class="text-danger"></form:errors>
            </div>
             <div class="col-sm-3">
                <form:input path="shortDescription" class="inputbox-cheil-long" placeholder="Input a short description" required="required"/>
                <span>Short description</span>
                <form:errors path="shortDescription" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                <form:input type="email" path="picEmail" class="inputbox-cheil" placeholder="Enter PIC email"/>
                <span>PIC email</span>
                <form:errors path="errorMsg" class="text-danger"></form:errors>
            </div>
        </div>
            <br/>
            <br/>
        <div class="row">
             <div class="col-sm-12">
                 <select name="type" class="3col active cheil-select" >
                  <option value="">Select Type</option>
                     <c:forEach items="${types}" var="child">
                         <c:choose>
                             <c:when test="${editForm.type eq child }">
                               <option value="${child}" selected>${child}</option>
                             </c:when>
                             <c:otherwise>
                                <option value="${child}" >${child}</option>
                             </c:otherwise>
                         </c:choose>
                     </c:forEach>
                 </select>
             </div>
        </div>
           <br/>
            <br/>
        <div class="col-sm-12">
                  <select name="subAndSites[]" placeholder="Select subsidiaries" multiple="multiple" id="selectpicker5">
                    <c:forEach items="${subsidiaries}" var="child">
                    <optgroup label="${child.key}">
                      <c:forEach items="${child.value}" var="site">
                      <c:choose>
                            <c:when test="${fn:contains( editForm.sites, site.recordId ) && fn:contains( editForm.subsidiaries, child.key )}">
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
        </div>
        <br/>
        <br/>
        <div class="row">
                <div class="col-sm-6">
                        <p style="font-size:22px;font-weight:bolder;">Media</p><form:input type="file" name="files" path="files" multiple="multiple" />
                    </div>
                </div>
                <br/>
        <%@ include file="medias.jsp" %>

       <c:if test="${not empty message}">
           <div class="alert alert-danger" role="alert" id="errorMessage">
               <spring:message code="${message}" />
           </div>
       </c:if>
    </div>
  </div>
</div>
<br/><br/>
<%@ include file="sublibraries.jsp" %>

<br/><br/>
<%@ include file="action.jsp" %>
       </form:form>
<script type="text/javascript">
    $(document).ready(function() {
         $('#selectpicker5').multiselect({
                    enableClickableOptGroups: true,
                    enableCollapsibleOptGroups: true,
                    collapseOptGroupsByDefault:true,
                    enableFiltering: true,
                    includeSelectAllOption: true,
                    buttonWidth: '100%',
                    includeResetOption: true,
                    includeResetDivider: true,
                    enableFiltering: true,
                    resetText: "Reset all",
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
    $("#addBtnAction").click(function(){
       $("#myActionModal").modal('show');
       });
       $("#addBtnLib").click(function(){
        $("#myLibraryModal").modal('show');
      });

</script>

<div id="myLibraryModal" class="modal" tabindex="-1">
      <div class="modal-dialog-full">
         <div class="modal-content" style="height:65%;margin-left:20px !important;">
            <div class="modal-header" style="border-bottom:none;">
                Add Library
               <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
               <%@ include file="sublibrariespopup.jsp" %>
            </div>
         </div>
      </div>
   </div>

   <div id="myActionModal" class="modal" tabindex="-1">
         <div class="modal-dialog-full">
            <div class="modal-content" style="height:65%;margin-left:20px !important">
               <div class="modal-header" style="border-bottom:none;">
                   Add Action
                  <button type="button" class="close" data-dismiss="modal">&times;</button>
               </div>
               <div class="modal-body">
                  <%@ include file="actionpopup.jsp" %>
               </div>
            </div>
         </div>
      </div>