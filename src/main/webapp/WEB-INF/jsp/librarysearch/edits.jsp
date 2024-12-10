<%@ include file="../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-12">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
        <c:if test="${not empty backUrl}">
        <div class="row">
                <div class="col-sm-12">
                    <div class="dt-buttons">
                        <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('${backUrl}')" aria-controls="tableData" title="Back" type="button">Back</button>
                    </div>
                </div>
            </div>
        </c:if>
        <c:if test="${empty backUrl}">
        <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/finder/find" class="handle-upload" modelAttribute="sessionLibrary" >
                <div class="row">
                    <div class="col-sm-12">
                        <div class="dt-buttons">
                            <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" title="Back" type="submit">Back</button>
                        </div>
                    </div>
                </div>
                <input type="hidden" name="subsidiary" value="${sessionLibrary.subsidiary.id}">
                <input type="hidden" name="type" value="${sessionLibrary.type}">
                <input type="hidden" name="errorMsg" value="${sessionLibrary.errorMsg}">
        </form:form>
        </c:if>
    </div>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/library/edits" class="handle-upload" modelAttribute="editForm" >

        <%@ include file="../commonElasticFields.jsp" %>
        <div class="row">
            <div class="col-sm-3">
                <form:input path="id" class="inputbox-cheil-small" placeholder="Enter ID without space" readonly="true"/>
                <span>Lib ID</span>
                <form:errors path="errorType" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                <form:input path="identifier" id="identifier" class="inputbox-cheil" placeholder="Identifier" />
                <span>Category Id</span>
                <form:errors path="identifier" class="text-danger"></form:errors>
                        </div>
             <div class="col-sm-3">
                <form:input path="shortDescription" class="inputbox-cheil" placeholder="shortDescription" readonly="true"/>
                <span>shortDescription</span>
                <form:errors path="errorType" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                <form:input type="email" path="picEmail" class="inputbox-cheil" placeholder="Enter Pic Email" readonly="true"/>
                <span>Pic Email</span>
                <form:errors path="errorMsg" class="text-danger"></form:errors>
            </div>
            <br/>
            <br/>
             <div class="col-sm-3">
                 <select name="type" class="3col active cheil-select" disabled="disabled">
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
        <div class="row">
                    <div class="col-sm-12">
                          <select name="subAndSites[]" placeholder="Select Subsidiaries" multiple="multiple" id="selectpicker7" disabled="disabled">
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
      $('#selectpicker7').multiselect({
          enableClickableOptGroups: true,
          enableCollapsibleOptGroups: true,
          collapseOptGroupsByDefault:true,
          enableFiltering: true,
          includeSelectAllOption: true,
          buttonWidth: '100%',
          enableFiltering: true,
          includeResetOption: true,
          includeResetDivider: true,
          resetText: "Reset all",
          dropUp: true,
          enableHTML: true,
          buttonText: function(options, select) {
                  var $select = $(select);
                  var $optgroups = $('optgroup', $select);
                          if (options.length === 0) {
                              return 'Select Subsidiaries';
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
<%@ include file="sublibraries2.jsp" %>
<br/><br/>

<%@ include file="medias2.jsp" %>
<br/><br/>
<%@ include file="action2.jsp" %>
       </form:form>