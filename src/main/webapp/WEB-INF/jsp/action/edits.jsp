<%@ include file="../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-12">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
    </div>
    <div class="row">
        <div class="col-sm-12">
            <div class="dt-buttons">
                <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('${backUrl}')" aria-controls="tableData" title="Back" type="button">Back</button>
            </div>
        </div>
    </div>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/action/edit" class="handle-upload" modelAttribute="editForm" >
        
        <%@ include file="../commonElasticFields.jsp" %>

        <div class="row">
            <div class="col-sm-3">
                <form:input path="id" class="inputbox-cheil-small" placeholder="Enter ID without space" readonly="true"/>
                <span>Action ID</span>
                <form:errors path="errorType" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                <form:input path="identifier" class="inputbox-cheil-small" placeholder="Enter Identifier" readonly="true"/>
                <span>Identifier</span>
                <form:errors path="errorType" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                <form:input path="shortDescription" class="inputbox-cheil" placeholder="Enter Short Description" readonly="true" />
                <span>Short Description</span>
                <form:errors path="errorType" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                <form:input type="email" path="picEmail" class="inputbox-cheil" placeholder="Enter PIC Email" readonly="true"/>
                <span>PIC Email</span>
                <form:errors path="errorType" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                 <select class="cheil-select" name="toolkitId" disabled="disabled" id="selectpicker">
                    <option value="">Select Node</option>
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
                          <select name="subAndSites[]" placeholder="Select subsidiaries" multiple="multiple" id="selectpicker8" disabled="disabled">
                            <c:forEach items="${subsidiaries}" var="child">
                            <optgroup label="${child.key}">
                              <c:forEach items="${child.value}" var="site">
                              <c:choose>
                                    <c:when test="${fn:contains( editForm.sites, site.recordId ) }">
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
                  <select name="systemId"  placeholder="Select Systems" disabled="disabled">
                  <option value="">Select System</option>
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
                              <select class="cheil-select" name="role" placeholder="Select Role" disabled="disabled" id="selectpicker2">
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
                 <select name="catalogId"  placeholder="Select Catalog" disabled="disabled">
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
                   <select name="moduleId"  placeholder="Select Module" disabled="disabled">
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
                <form:textarea id="summernote" rows="5" path="systemPath" class="inputbox-cheil-textarea" placeholder="System Path" readonly="true"/>
                <form:errors path="errorType" class="text-danger"></form:errors>
            </div>
        </div>
        <br/>
        <br/>
        <div class="row">
                    <div class="col-sm-12 col-sm-full">
                        <form:textarea rows="5" path="longDescription" class="inputbox-cheil-textarea" placeholder="Enter Long Description" readonly="true"/>
                        <span>Long Description</span>
                        <form:errors path="errorType" class="text-danger"></form:errors>
                    </div>

                </div>
                <br/>
                        <br/>
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
           placeholder: 'System Path',
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
$('#selectpicker8').multiselect({
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
   </script>
   <br/><br/>
           <div class="row">
           <div class="col-sm-6">
                   <p style="font-size:22px;font-weight:bolder;">Medias</p>
               </div>

           </div>
   <%@ include file="medias.jsp" %>
   <script type="text/javascript">
         var multipleCancelButton = new Choices('#selectpicker', {
             removeItemButton: true,
             maxItemCount:-1,
             searchResultLimit:20,
             renderChoiceLimit:-1
           });
           var multipleCancelButton2 = new Choices('#selectpicker2', {
                        removeItemButton: true,
                        maxItemCount:-1,
                        searchResultLimit:20,
                        renderChoiceLimit:-1
                      });
   </script>